import { ComponentFixture, TestBed } from '@angular/core/testing';
import { describe, expect, it } from 'vitest';
import { Order } from '../../../../features/checkout/data-access/order.model';
import { OrderSummary } from './order-summary';

describe('OrderSummary', () => {
  let fixture: ComponentFixture<OrderSummary>;

  function baseOrder(overrides: Partial<Order> = {}): Order {
    return {
      id: 'order-1',
      createdAt: '2026-01-01T10:00:00Z',
      items: [
        { productId: 'p1', name: 'Producto 1', unitPrice: 50, quantity: 2, subtotal: 100, discount: 10, total: 90 },
      ],
      subtotal: 100,
      totalDiscountAmount: 10,
      total: 90,
      couponCode: null,
      appliedDiscounts: ['CATEGORY'],
      discountBreakdown: {
        categoryDiscountAmount: 10,
        volumeDiscountAmount: 0,
        couponDiscountAmount: 0,
        totalDiscountAmount: 10,
        effectiveDiscountPercentage: 0.1,
      },
      ...overrides,
    };
  }

  function render(order: Order): void {
    fixture = TestBed.createComponent(OrderSummary);
    fixture.componentRef.setInput('order', order);
    fixture.detectChanges();
  }

  it('should show the discount cap alert instead of the discount breakdown when TOTAL was applied', () => {
    render(
      baseOrder({
        appliedDiscounts: ['CATEGORY', 'VOLUME', 'COUPON', 'TOTAL'],
        totalDiscountAmount: 35,
        total: 65,
        discountBreakdown: {
          categoryDiscountAmount: 10,
          volumeDiscountAmount: 5,
          couponDiscountAmount: 20,
          totalDiscountAmount: 35,
          effectiveDiscountPercentage: 0.35,
        },
      }),
    );

    const text = (fixture.nativeElement as HTMLElement).textContent ?? '';
    expect(text).toContain('¡Enhorabuena! Has alcanzado el límite máximo de ahorro permitido (35%)');
    expect((fixture.nativeElement as HTMLElement).querySelector('.order-summary__discounts')).toBeNull();
  });

  it('should show the discount breakdown for each applied discount type when the cap was not reached', () => {
    render(baseOrder({ appliedDiscounts: ['CATEGORY', 'VOLUME'] }));

    const rows = (fixture.nativeElement as HTMLElement).querySelectorAll('.order-summary__discount-row');
    expect(rows.length).toBe(2);
    expect((fixture.nativeElement as HTMLElement).textContent).not.toContain('límite máximo de ahorro');
  });

  it('should show the applied coupon code only when one was used', () => {
    render(baseOrder({ couponCode: 'WELCOME2026' }));
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('WELCOME2026');
  });

  it('should not show a coupon line when no coupon was applied', () => {
    render(baseOrder({ couponCode: null }));
    expect((fixture.nativeElement as HTMLElement).querySelector('.order-summary__coupon')).toBeNull();
  });

  it('should render one item row per order line', () => {
    render(
      baseOrder({
        items: [
          { productId: 'p1', name: 'Producto 1', unitPrice: 50, quantity: 1, subtotal: 50, discount: 0, total: 50 },
          { productId: 'p2', name: 'Producto 2', unitPrice: 20, quantity: 2, subtotal: 40, discount: 0, total: 40 },
        ],
      }),
    );

    expect((fixture.nativeElement as HTMLElement).querySelectorAll('.order-summary__item').length).toBe(2);
  });
});
