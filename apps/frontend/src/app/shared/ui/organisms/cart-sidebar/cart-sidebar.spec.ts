import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { DiscountBreakdown } from '../../../../features/checkout/data-access/discount-breakdown.model';
import { CartItemRow } from '../../molecules/cart-item-row/cart-item-row';
import { CartLineViewModel, CartSidebar } from './cart-sidebar';

describe('CartSidebar', () => {
  let fixture: ComponentFixture<CartSidebar>;
  let component: CartSidebar;

  const lines: CartLineViewModel[] = [
    { productId: 'p1', name: 'Producto 1', unitPrice: 50, quantity: 2, lineTotal: 100, maxQuantity: 5 },
    { productId: 'p2', name: 'Producto 2', unitPrice: 20, quantity: 1, lineTotal: 20, maxQuantity: 1 },
  ];

  const breakdown: DiscountBreakdown = {
    categoryDiscountAmount: 5,
    volumeDiscountAmount: 0,
    couponDiscountAmount: 0,
    totalDiscountAmount: 5,
    effectiveDiscountPercentage: 0.05,
  };

  function render(overrides: Partial<{
    lines: CartLineViewModel[];
    subtotal: number;
    total: number;
    totalDiscountAmount: number;
    discountBreakdown: DiscountBreakdown | null;
    appliedDiscounts: string[];
    isCapped: boolean;
    payDisabled: boolean;
    payPending: boolean;
    payError: string | null;
  }> = {}): void {
    fixture = TestBed.createComponent(CartSidebar);
    component = fixture.componentInstance;

    fixture.componentRef.setInput('lines', overrides.lines ?? lines);
    fixture.componentRef.setInput('subtotal', overrides.subtotal ?? 120);
    fixture.componentRef.setInput('total', overrides.total ?? 115);
    fixture.componentRef.setInput('totalDiscountAmount', overrides.totalDiscountAmount ?? 5);
    fixture.componentRef.setInput('discountBreakdown', overrides.discountBreakdown ?? null);
    fixture.componentRef.setInput('appliedDiscounts', overrides.appliedDiscounts ?? []);
    fixture.componentRef.setInput('isCapped', overrides.isCapped ?? false);
    fixture.componentRef.setInput('payDisabled', overrides.payDisabled ?? false);
    fixture.componentRef.setInput('payPending', overrides.payPending ?? false);
    fixture.componentRef.setInput('payError', overrides.payError ?? null);

    fixture.detectChanges();
  }

  it('should show the empty-cart message and hide the summary/coupon/pay controls when there are no lines', () => {
    render({ lines: [] });

    const root = fixture.nativeElement as HTMLElement;
    expect(root.querySelector('.cart-sidebar__empty')?.textContent).toContain('Tu carrito está vacío');
    expect(root.querySelector('.cart-sidebar__coupon')).toBeNull();
    expect(root.querySelector('.cart-sidebar__pay-button')).toBeNull();
  });

  it('should render one cart-item-row per cart line', () => {
    render();

    const rows = fixture.debugElement.queryAll(By.directive(CartItemRow));
    expect(rows.length).toBe(2);
  });

  it('should show the discount cap alert and hide the breakdown panel when isCapped is true', () => {
    render({ isCapped: true, discountBreakdown: breakdown, appliedDiscounts: ['CATEGORY', 'TOTAL'] });

    const root = fixture.nativeElement as HTMLElement;
    expect(root.textContent).toContain('¡Enhorabuena! Has alcanzado el límite máximo de ahorro permitido (35%)');
    expect(root.querySelector('ui-discount-breakdown-panel')).toBeNull();
  });

  it('should show the discount breakdown panel when not capped and a breakdown is available', () => {
    render({ isCapped: false, discountBreakdown: breakdown, appliedDiscounts: ['CATEGORY'] });

    const root = fixture.nativeElement as HTMLElement;
    expect(root.querySelector('ui-discount-breakdown-panel')).not.toBeNull();
    expect(root.textContent).not.toContain('límite máximo de ahorro');
  });

  it('should show neither the cap alert nor the breakdown panel while no discount result is available yet', () => {
    render({ isCapped: false, discountBreakdown: null });

    const root = fixture.nativeElement as HTMLElement;
    expect(root.querySelector('ui-discount-breakdown-panel')).toBeNull();
    expect(root.textContent).not.toContain('límite máximo de ahorro');
  });

  it('should show the item count badge only when the cart has items', () => {
    render({ lines: [] });
    expect((fixture.nativeElement as HTMLElement).querySelector('.cart-sidebar__toggle-badge')).toBeNull();

    render();
    const badge = (fixture.nativeElement as HTMLElement).querySelector('.cart-sidebar__toggle-badge');
    expect(badge?.textContent?.trim()).toBe('3');
  });

  it('should toggle the open state (mobile panel) when the toggle button is clicked', () => {
    render();
    const toggle = (fixture.nativeElement as HTMLElement).querySelector('.cart-sidebar__toggle') as HTMLButtonElement;

    expect(component.isOpen()).toBe(false);
    toggle.click();
    fixture.detectChanges();
    expect(component.isOpen()).toBe(true);
    expect(toggle.getAttribute('aria-expanded')).toBe('true');
  });

  it('should close the panel when the close control is clicked', () => {
    render();
    component.toggleOpen();
    fixture.detectChanges();

    const closeButton = (fixture.nativeElement as HTMLElement).querySelector('.cart-sidebar__close') as HTMLButtonElement;
    closeButton.click();
    fixture.detectChanges();

    expect(component.isOpen()).toBe(false);
  });

  it('should disable the pay button and show a loading indicator while payPending is true', () => {
    render({ payPending: true });

    const payButton = (fixture.nativeElement as HTMLElement).querySelector(
      '.cart-sidebar__pay-button button',
    ) as HTMLButtonElement;

    expect(payButton.disabled).toBe(true);
    expect((fixture.nativeElement as HTMLElement).querySelector('ui-loading-indicator')).not.toBeNull();
  });

  it('should disable the pay button when payDisabled is true (e.g. empty or invalid cart)', () => {
    render({ payDisabled: true });

    const payButton = (fixture.nativeElement as HTMLElement).querySelector(
      '.cart-sidebar__pay-button button',
    ) as HTMLButtonElement;

    expect(payButton.disabled).toBe(true);
  });

  it('should show the payment error message when one is present', () => {
    render({ payError: 'No hay stock suficiente para completar la compra. Ajusta las cantidades e inténtalo de nuevo.' });

    const message = (fixture.nativeElement as HTMLElement).querySelector('.cart-sidebar__pay-error');
    expect(message?.textContent).toContain('No hay stock suficiente');
  });

  it('should emit pay when the pay button is clicked and enabled', () => {
    render();
    const paySpy = vi.fn();
    component.pay.subscribe(paySpy);

    const payButton = (fixture.nativeElement as HTMLElement).querySelector(
      '.cart-sidebar__pay-button button',
    ) as HTMLButtonElement;
    payButton.click();

    expect(paySpy).toHaveBeenCalled();
  });

  it('should emit incrementItem/decrementItem with the corresponding productId from a specific row', () => {
    render();
    const incrementSpy = vi.fn();
    const decrementSpy = vi.fn();
    component.incrementItem.subscribe(incrementSpy);
    component.decrementItem.subscribe(decrementSpy);

    const rows = fixture.debugElement.queryAll(By.directive(CartItemRow));
    rows[1].componentInstance.increment.emit();
    rows[1].componentInstance.decrement.emit();

    expect(incrementSpy).toHaveBeenCalledWith('p2');
    expect(decrementSpy).toHaveBeenCalledWith('p2');
  });

  it('should forward activateCoupon from the nested coupon form', () => {
    render();
    const activateSpy = vi.fn();
    component.activateCoupon.subscribe(activateSpy);

    const input = (fixture.nativeElement as HTMLElement).querySelector('.coupon-form__input input') as HTMLInputElement;
    input.value = 'welcome2026';
    input.dispatchEvent(new Event('input'));

    const form = (fixture.nativeElement as HTMLElement).querySelector('.coupon-form') as HTMLFormElement;
    form.dispatchEvent(new Event('submit', { cancelable: true }));

    expect(activateSpy).toHaveBeenCalledWith('WELCOME2026');
  });
});
