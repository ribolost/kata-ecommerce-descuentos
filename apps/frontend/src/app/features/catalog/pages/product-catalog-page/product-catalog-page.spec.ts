import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { computed, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { of, throwError } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ApiError } from '../../../../core/models/api-error.model';
import { CartStateService } from '../../../checkout/data-access/cart-state.service';
import { CartItem } from '../../../checkout/data-access/cart.model';
import { DiscountCalculationResult } from '../../../checkout/data-access/discount-breakdown.model';
import { Order } from '../../../checkout/data-access/order.model';
import { OrderService } from '../../../checkout/data-access/order.service';
import { CartSidebar } from '../../../../shared/ui/organisms/cart-sidebar/cart-sidebar';
import { ProductGrid } from '../../../../shared/ui/organisms/product-grid/product-grid';
import { Product } from '../../data-access/product.model';
import { ProductService } from '../../data-access/product.service';
import { ProductCatalogPage } from './product-catalog-page';

function createCartStateStub() {
  const items = signal<CartItem[]>([]);
  const couponCode = signal<string | null>(null);
  const discountBreakdown = signal<DiscountCalculationResult | null>(null);
  const discountLoading = signal(false);
  const discountError = signal<ApiError | null>(null);
  const cartQuantities = computed(() => new Map(items().map((item) => [item.productId, item.quantity])));
  const isCapped = computed(() => (discountBreakdown()?.appliedDiscounts ?? []).includes('TOTAL'));
  const hasItems = computed(() => items().length > 0);

  return {
    items,
    couponCode,
    discountBreakdown,
    discountLoading,
    discountError,
    isCapped,
    hasItems,
    subtotal: signal(0),
    cartQuantities,
    quantityOf: (productId: string) => cartQuantities().get(productId) ?? 0,
    setProducts: vi.fn(),
    addItem: vi.fn((productId: string) => items.update((xs) => [...xs, { productId, quantity: 1 }])),
    incrementItem: vi.fn(),
    decrementItem: vi.fn(),
    applyCoupon: vi.fn((code: string | null) => couponCode.set(code)),
    resetCart: vi.fn(),
    retryDiscountCalculation: vi.fn(),
  };
}

type CartStateStub = ReturnType<typeof createCartStateStub>;

function fakeActivatedRoute(data: Record<string, unknown>, queryParams: Record<string, string> = {}): ActivatedRoute {
  return {
    snapshot: {
      data,
      queryParamMap: convertToParamMap(queryParams),
    },
  } as unknown as ActivatedRoute;
}

describe('ProductCatalogPage', () => {
  let getProducts: ReturnType<typeof vi.fn>;
  let placeOrder: ReturnType<typeof vi.fn>;
  let navigate: ReturnType<typeof vi.fn>;
  let cartState: CartStateStub;

  const products: Product[] = [
    { id: 'p1', name: 'Producto 1', description: 'd', unitPrice: 50, category: 'TECNOLOGIA', stock: 3 },
    { id: 'p2', name: 'Producto 2', description: 'd', unitPrice: 20, category: 'OTRO', stock: 1 },
  ];

  function setup(
    routeData: Record<string, unknown> = { products },
    queryParams: Record<string, string> = {},
  ): ComponentFixture<ProductCatalogPage> {
    getProducts = vi.fn();
    placeOrder = vi.fn();
    navigate = vi.fn();
    cartState = createCartStateStub();

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: fakeActivatedRoute(routeData, queryParams) },
        { provide: Router, useValue: { navigate } },
        { provide: ProductService, useValue: { getProducts } },
        { provide: OrderService, useValue: { placeOrder } },
        { provide: CartStateService, useValue: cartState },
      ],
    });

    const fixture = TestBed.createComponent(ProductCatalogPage);
    fixture.detectChanges();
    return fixture;
  }

  function grid(fixture: ComponentFixture<ProductCatalogPage>): ProductGrid {
    return fixture.debugElement.query(By.directive(ProductGrid)).componentInstance as ProductGrid;
  }

  function sidebar(fixture: ComponentFixture<ProductCatalogPage>): CartSidebar {
    return fixture.debugElement.query(By.directive(CartSidebar)).componentInstance as CartSidebar;
  }

  const sampleOrder: Order = {
    id: 'order-1',
    createdAt: '2026-01-01T00:00:00Z',
    items: [],
    subtotal: 50,
    totalDiscountAmount: 0,
    total: 50,
    couponCode: null,
    appliedDiscounts: [],
    discountBreakdown: {
      categoryDiscountAmount: 0,
      volumeDiscountAmount: 0,
      couponDiscountAmount: 0,
      totalDiscountAmount: 0,
      effectiveDiscountPercentage: 0,
    },
  };

  it('should render the resolved products and forward the catalog to CartStateService', () => {
    const fixture = setup({ products });

    expect(grid(fixture).products()).toEqual(products);
    expect(cartState.setProducts).toHaveBeenCalledWith(products);
    expect(fixture.nativeElement.querySelector('.product-catalog-page__error')).toBeNull();
  });

  it('should show the load-error state when the resolver could not fetch the catalog', () => {
    const fixture = setup({ products: null });
    expect(fixture.nativeElement.querySelector('.product-catalog-page__error')).not.toBeNull();
  });

  it('should show the order-not-found notice when navigated back with orderNotFound=true', () => {
    const fixture = setup({ products }, { orderNotFound: 'true' });
    expect(fixture.nativeElement.textContent).toContain('La orden solicitada no existe');
  });

  it('should not show the order-not-found notice on a normal visit', () => {
    const fixture = setup({ products }, {});
    expect(fixture.nativeElement.textContent).not.toContain('La orden solicitada no existe');
  });

  it('should reload the catalog and clear the error state when retrying succeeds', () => {
    const fixture = setup({ products: null });
    getProducts.mockReturnValue(of(products));

    const retryButton = fixture.nativeElement.querySelector('.product-catalog-page__error button') as HTMLButtonElement;
    retryButton.click();
    fixture.detectChanges();

    expect(getProducts).toHaveBeenCalled();
    expect(fixture.nativeElement.querySelector('.product-catalog-page__error')).toBeNull();
    expect(grid(fixture).products()).toEqual(products);
  });

  it('should keep the load-error state when retrying fails again', () => {
    const fixture = setup({ products: null });
    getProducts.mockReturnValue(throwError(() => new Error('network error')));

    const retryButton = fixture.nativeElement.querySelector('.product-catalog-page__error button') as HTMLButtonElement;
    retryButton.click();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.product-catalog-page__error')).not.toBeNull();
  });

  it('should add a product to the cart the first time it is incremented from the catalog', () => {
    const fixture = setup({ products });
    grid(fixture).increment.emit('p1');

    expect(cartState.addItem).toHaveBeenCalledWith('p1');
    expect(cartState.incrementItem).not.toHaveBeenCalled();
  });

  it('should increment an existing cart line instead of re-adding it', () => {
    const fixture = setup({ products });
    cartState.addItem('p1');
    cartState.addItem.mockClear();
    fixture.detectChanges();

    grid(fixture).increment.emit('p1');

    expect(cartState.incrementItem).toHaveBeenCalledWith('p1');
    expect(cartState.addItem).not.toHaveBeenCalled();
  });

  it('should decrement a cart line when requested from the catalog', () => {
    const fixture = setup({ products });
    grid(fixture).decrement.emit('p1');
    expect(cartState.decrementItem).toHaveBeenCalledWith('p1');
  });

  it('should apply the coupon code activated from the cart sidebar', () => {
    const fixture = setup({ products });
    sidebar(fixture).activateCoupon.emit('WELCOME2026');
    expect(cartState.applyCoupon).toHaveBeenCalledWith('WELCOME2026');
  });

  it('should ignore a pay attempt when the cart has no items', () => {
    const fixture = setup({ products });
    sidebar(fixture).pay.emit();
    expect(placeOrder).not.toHaveBeenCalled();
  });

  it('should place the order, reset the cart and navigate to the order result page on success', () => {
    const fixture = setup({ products });
    cartState.addItem('p1');
    fixture.detectChanges();
    placeOrder.mockReturnValue(of(sampleOrder));

    sidebar(fixture).pay.emit();

    expect(placeOrder).toHaveBeenCalledWith([{ productId: 'p1', quantity: 1 }], null);
    expect(cartState.resetCart).toHaveBeenCalled();
    expect(navigate).toHaveBeenCalledWith(['/orders', 'order-1']);
  });

  it.each([
    [409, 'No hay stock suficiente'],
    [400, 'datos inválidos'],
    [500, 'No se pudo procesar el pago'],
  ])('should show a specific message for a %i payment error', (status, expectedFragment) => {
    const fixture = setup({ products });
    cartState.addItem('p1');
    fixture.detectChanges();

    const apiError: ApiError = { type: 'about:blank', title: 'Error', status };
    placeOrder.mockReturnValue(throwError(() => apiError));

    sidebar(fixture).pay.emit();
    fixture.detectChanges();

    expect(sidebar(fixture).payError()).toContain(expectedFragment);
  });

  it('should use the discount-adjusted line total once a discount result is available', () => {
    const fixture = setup({ products });
    cartState.addItem('p1');
    cartState.discountBreakdown.set({
      items: [{ productId: 'p1', quantity: 1, subtotal: 50, discount: 5, total: 45 }],
      subtotal: 50,
      totalDiscountAmount: 5,
      total: 45,
      appliedDiscounts: ['CATEGORY'],
      discountBreakdown: {
        categoryDiscountAmount: 5,
        volumeDiscountAmount: 0,
        couponDiscountAmount: 0,
        totalDiscountAmount: 5,
        effectiveDiscountPercentage: 0.1,
      },
    });
    fixture.detectChanges();

    const line = sidebar(fixture)
      .lines()
      .find((candidate) => candidate.productId === 'p1');

    expect(line?.lineTotal).toBe(45);
    expect(line?.maxQuantity).toBe(3);
  });

  it('should fall back to unitPrice * quantity before a discount result exists', () => {
    const fixture = setup({ products });
    cartState.addItem('p1');
    fixture.detectChanges();

    const line = sidebar(fixture)
      .lines()
      .find((candidate) => candidate.productId === 'p1');

    expect(line?.lineTotal).toBe(50);
  });

  it('should flag couponNotApplied when a coupon is set but COUPON is absent from appliedDiscounts', () => {
    const fixture = setup({ products });
    cartState.addItem('p1');
    cartState.couponCode.set('EXPIRED2024');
    cartState.discountBreakdown.set({
      items: [],
      subtotal: 50,
      totalDiscountAmount: 0,
      total: 50,
      appliedDiscounts: [],
      discountBreakdown: {
        categoryDiscountAmount: 0,
        volumeDiscountAmount: 0,
        couponDiscountAmount: 0,
        totalDiscountAmount: 0,
        effectiveDiscountPercentage: 0,
      },
    });
    fixture.detectChanges();

    expect(sidebar(fixture).couponNotApplied()).toBe(true);
  });

  it('should not flag couponNotApplied once the coupon has been applied successfully', () => {
    const fixture = setup({ products });
    cartState.addItem('p1');
    cartState.couponCode.set('WELCOME2026');
    cartState.discountBreakdown.set({
      items: [],
      subtotal: 50,
      totalDiscountAmount: 7.5,
      total: 42.5,
      appliedDiscounts: ['COUPON'],
      discountBreakdown: {
        categoryDiscountAmount: 0,
        volumeDiscountAmount: 0,
        couponDiscountAmount: 7.5,
        totalDiscountAmount: 7.5,
        effectiveDiscountPercentage: 0.15,
      },
    });
    fixture.detectChanges();

    expect(sidebar(fixture).couponNotApplied()).toBe(false);
  });

  it('should not flag couponNotApplied while the discount cap has been reached', () => {
    const fixture = setup({ products });
    cartState.addItem('p1');
    cartState.couponCode.set('WELCOME2026');
    cartState.discountBreakdown.set({
      items: [],
      subtotal: 200,
      totalDiscountAmount: 70,
      total: 130,
      appliedDiscounts: ['CATEGORY', 'VOLUME', 'COUPON', 'TOTAL'],
      discountBreakdown: {
        categoryDiscountAmount: 20,
        volumeDiscountAmount: 10,
        couponDiscountAmount: 40,
        totalDiscountAmount: 70,
        effectiveDiscountPercentage: 0.35,
      },
    });
    fixture.detectChanges();

    expect(sidebar(fixture).couponNotApplied()).toBe(false);
  });
});
