import { TestBed } from '@angular/core/testing';
import { Subject, of, throwError } from 'rxjs';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { ApiError } from '../../../core/models/api-error.model';
import { Product } from '../../catalog/data-access/product.model';
import { CartStateService } from './cart-state.service';
import { DiscountCalculationResult } from './discount-breakdown.model';
import { DiscountService } from './discount.service';

/**
 * `toObservable()` synchronizes the cart snapshot signal into the debounce/HTTP pipeline
 * through Angular's effect scheduler. `TestBed.tick()` flushes that pending effect
 * synchronously so the emission is queued before we advance the fake debounce timer.
 */
function flush(): void {
  TestBed.tick();
}

describe('CartStateService', () => {
  let service: CartStateService;
  let calculate: ReturnType<typeof vi.fn>;

  const products: Product[] = [
    { id: 'p1', name: 'Producto Tecnología', description: 'd', unitPrice: 50, category: 'TECNOLOGIA', stock: 2 },
    { id: 'p2', name: 'Producto sin stock extra', description: 'd', unitPrice: 20, category: 'OTRO', stock: 1 },
  ];

  function breakdown(overrides: Partial<DiscountCalculationResult> = {}): DiscountCalculationResult {
    return {
      items: [],
      subtotal: 100,
      totalDiscountAmount: 0,
      total: 100,
      appliedDiscounts: [],
      discountBreakdown: {
        categoryDiscountAmount: 0,
        volumeDiscountAmount: 0,
        couponDiscountAmount: 0,
        totalDiscountAmount: 0,
        effectiveDiscountPercentage: 0,
      },
      ...overrides,
    };
  }

  beforeEach(() => {
    vi.useFakeTimers();
    calculate = vi.fn().mockReturnValue(of(breakdown()));

    TestBed.configureTestingModule({
      providers: [{ provide: DiscountService, useValue: { calculate } }],
    });

    service = TestBed.inject(CartStateService);
    service.setProducts(products);
    flush();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('should not add a product to the cart when it has no available stock', () => {
    service.addItem('unknown-product');
    expect(service.items()).toEqual([]);
  });

  it('should add a product with quantity 1 when it has available stock', () => {
    service.addItem('p1');
    expect(service.items()).toEqual([{ productId: 'p1', quantity: 1 }]);
  });

  it('should not increment a line past the product stock ceiling', () => {
    service.addItem('p2');
    service.incrementItem('p2');
    expect(service.quantityOf('p2')).toBe(1);
  });

  it('should increment a line while under the stock ceiling', () => {
    service.addItem('p1');
    service.incrementItem('p1');
    expect(service.quantityOf('p1')).toBe(2);
  });

  it('should remove the line once its quantity is decremented to zero', () => {
    service.addItem('p1');
    service.decrementItem('p1');
    expect(service.items()).toEqual([]);
  });

  it('should derive the subtotal from cart quantities and catalog prices', () => {
    service.addItem('p1');
    service.incrementItem('p1');
    service.addItem('p2');
    expect(service.subtotal()).toBe(50 * 2 + 20);
  });

  it('should not trigger a discount recalculation while the cart is empty', () => {
    flush();
    vi.advanceTimersByTime(500);
    expect(calculate).not.toHaveBeenCalled();
    expect(service.discountBreakdown()).toBeNull();
  });

  it('should recalculate discounts 400ms after a cart change, with the mapped cart items', () => {
    service.addItem('p1');
    flush();
    vi.advanceTimersByTime(399);
    expect(calculate).not.toHaveBeenCalled();

    vi.advanceTimersByTime(1);
    expect(calculate).toHaveBeenCalledWith([{ productId: 'p1', quantity: 1 }], null);
  });

  it('should collapse rapid successive cart changes into a single recalculation request', () => {
    service.addItem('p1');
    flush();
    vi.advanceTimersByTime(100);

    service.incrementItem('p1');
    flush();
    vi.advanceTimersByTime(100);

    service.addItem('p2');
    flush();
    vi.advanceTimersByTime(400);

    expect(calculate).toHaveBeenCalledTimes(1);
    expect(calculate).toHaveBeenCalledWith(
      [
        { productId: 'p1', quantity: 2 },
        { productId: 'p2', quantity: 1 },
      ],
      null,
    );
  });

  it('should update discountBreakdown and clear loading/error state on a successful recalculation', () => {
    calculate.mockReturnValue(of(breakdown({ total: 90 })));

    service.addItem('p1');
    flush();
    vi.advanceTimersByTime(400);

    expect(service.discountLoading()).toBe(false);
    expect(service.discountError()).toBeNull();
    expect(service.discountBreakdown()?.total).toBe(90);
  });

  it('should report discountLoading as true while the recalculation request is in flight', () => {
    const pending = new Subject<DiscountCalculationResult>();
    calculate.mockReturnValue(pending);

    service.addItem('p1');
    flush();
    vi.advanceTimersByTime(400);

    expect(service.discountLoading()).toBe(true);

    pending.next(breakdown());
    pending.complete();

    expect(service.discountLoading()).toBe(false);
  });

  it('should set discountError on a failed recalculation and recover on the next successful one', () => {
    const apiError: ApiError = { type: 'about:blank', title: 'Error inesperado', status: 500 };
    calculate.mockReturnValueOnce(throwError(() => apiError));

    service.addItem('p1');
    flush();
    vi.advanceTimersByTime(400);

    expect(service.discountLoading()).toBe(false);
    expect(service.discountError()).toEqual(apiError);
    expect(service.discountBreakdown()).toBeNull();

    calculate.mockReturnValue(of(breakdown({ total: 42 })));
    service.incrementItem('p1');
    flush();
    vi.advanceTimersByTime(400);

    expect(service.discountError()).toBeNull();
    expect(service.discountBreakdown()?.total).toBe(42);
  });

  it('should report isCapped as true only when appliedDiscounts includes TOTAL, at the exact 35% edge', () => {
    calculate.mockReturnValue(
      of(
        breakdown({
          appliedDiscounts: ['CATEGORY', 'VOLUME', 'COUPON', 'TOTAL'],
          totalDiscountAmount: 35,
          discountBreakdown: {
            categoryDiscountAmount: 10,
            volumeDiscountAmount: 5,
            couponDiscountAmount: 20,
            totalDiscountAmount: 35,
            effectiveDiscountPercentage: 0.35,
          },
        }),
      ),
    );

    service.addItem('p1');
    flush();
    vi.advanceTimersByTime(400);

    expect(service.isCapped()).toBe(true);
  });

  it('should not report isCapped when TOTAL is absent, even at the same 35% discount amount', () => {
    calculate.mockReturnValue(
      of(
        breakdown({
          appliedDiscounts: ['CATEGORY', 'VOLUME', 'COUPON'],
          totalDiscountAmount: 35,
          discountBreakdown: {
            categoryDiscountAmount: 10,
            volumeDiscountAmount: 5,
            couponDiscountAmount: 20,
            totalDiscountAmount: 35,
            effectiveDiscountPercentage: 0.35,
          },
        }),
      ),
    );

    service.addItem('p1');
    flush();
    vi.advanceTimersByTime(400);

    expect(service.isCapped()).toBe(false);
  });

  it('should recalculate with the applied coupon code once a coupon is activated', () => {
    service.addItem('p1');
    flush();
    vi.advanceTimersByTime(400);
    calculate.mockClear();

    service.applyCoupon('WELCOME2026');
    flush();
    vi.advanceTimersByTime(400);

    expect(calculate).toHaveBeenCalledWith([{ productId: 'p1', quantity: 1 }], 'WELCOME2026');
  });

  it('should trigger a fresh recalculation on retryDiscountCalculation without changing the cart items', () => {
    service.addItem('p1');
    flush();
    vi.advanceTimersByTime(400);
    calculate.mockClear();

    service.retryDiscountCalculation();
    flush();
    vi.advanceTimersByTime(400);

    expect(calculate).toHaveBeenCalledTimes(1);
    expect(service.items()).toEqual([{ productId: 'p1', quantity: 1 }]);
  });

  it('should clear items, coupon, discount breakdown and error on resetCart', () => {
    const apiError: ApiError = { type: 'about:blank', title: 'Error inesperado', status: 500 };
    calculate.mockReturnValue(throwError(() => apiError));

    service.addItem('p1');
    service.applyCoupon('WELCOME2026');
    flush();
    vi.advanceTimersByTime(400);

    service.resetCart();

    expect(service.items()).toEqual([]);
    expect(service.couponCode()).toBeNull();
    expect(service.discountBreakdown()).toBeNull();
    expect(service.discountError()).toBeNull();

    // Flush the recalculation scheduled by the reset itself (empty cart branch)
    // so no pending fake timers leak into the next test.
    flush();
    vi.advanceTimersByTime(400);
  });
});
