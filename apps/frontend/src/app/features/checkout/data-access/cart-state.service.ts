import { toObservable, takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Injectable, computed, inject, signal } from '@angular/core';
import { EMPTY, catchError, debounceTime, switchMap } from 'rxjs';
import { Product } from '../../catalog/data-access/product.model';
import { ApiError } from '../../../core/models/api-error.model';
import { CartItem, CartItemRequest } from './cart.model';
import { DiscountCalculationResult } from './discount-breakdown.model';
import { DiscountService } from './discount.service';

const RECALCULATION_DEBOUNCE_MS = 400;

@Injectable({ providedIn: 'root' })
export class CartStateService {
  private readonly discountService = inject(DiscountService);

  private readonly products = signal<Map<string, Product>>(new Map());

  readonly items = signal<CartItem[]>([]);
  readonly couponCode = signal<string | null>(null);
  readonly discountBreakdown = signal<DiscountCalculationResult | null>(null);
  readonly discountError = signal<ApiError | null>(null);
  readonly discountLoading = signal(false);

  readonly cartQuantities = computed(() => new Map(this.items().map((item) => [item.productId, item.quantity])));

  readonly subtotal = computed(() =>
    this.items().reduce((sum, item) => sum + this.unitPriceOf(item.productId) * item.quantity, 0),
  );

  readonly isCapped = computed(() =>
    (this.discountBreakdown()?.appliedDiscounts ?? []).includes('TOTAL'),
  );

  readonly hasItems = computed(() => this.items().length > 0);

  private readonly retryTick = signal(0);

  private readonly cartSnapshot = computed(() => ({
    items: this.items(),
    couponCode: this.couponCode(),
    retryTick: this.retryTick(),
  }));

  constructor() {
    toObservable(this.cartSnapshot)
      .pipe(
        debounceTime(RECALCULATION_DEBOUNCE_MS),
        switchMap(({ items, couponCode }) => {
          if (items.length === 0) {
            this.discountBreakdown.set(null);
            this.discountError.set(null);
            this.discountLoading.set(false);
            return EMPTY;
          }

          const requestItems: CartItemRequest[] = items.map(({ productId, quantity }) => ({
            productId,
            quantity,
          }));

          this.discountLoading.set(true);

          return this.discountService.calculate(requestItems, couponCode).pipe(
            catchError((error: ApiError) => {
              this.discountLoading.set(false);
              this.discountError.set(error);
              return EMPTY;
            }),
          );
        }),
        takeUntilDestroyed(),
      )
      .subscribe((result) => {
        this.discountLoading.set(false);
        this.discountError.set(null);
        this.discountBreakdown.set(result);
      });
  }

  setProducts(products: Product[]): void {
    this.products.set(new Map(products.map((product) => [product.id, product])));
  }

  quantityOf(productId: string): number {
    return this.cartQuantities().get(productId) ?? 0;
  }

  stockOf(productId: string): number {
    return this.products().get(productId)?.stock ?? 0;
  }

  productOf(productId: string): Product | undefined {
    return this.products().get(productId);
  }

  addItem(productId: string): void {
    if (this.stockOf(productId) < 1) {
      return;
    }
    this.items.update((items) => [...items, { productId, quantity: 1 }]);
  }

  incrementItem(productId: string): void {
    const stock = this.stockOf(productId);
    this.items.update((items) =>
      items.map((item) =>
        item.productId === productId && item.quantity < stock
          ? { ...item, quantity: item.quantity + 1 }
          : item,
      ),
    );
  }

  decrementItem(productId: string): void {
    this.items.update((items) =>
      items
        .map((item) =>
          item.productId === productId ? { ...item, quantity: item.quantity - 1 } : item,
        )
        .filter((item) => item.quantity > 0),
    );
  }

  applyCoupon(code: string | null): void {
    this.couponCode.set(code);
  }

  retryDiscountCalculation(): void {
    this.retryTick.update((tick) => tick + 1);
  }

  resetCart(): void {
    this.items.set([]);
    this.couponCode.set(null);
    this.discountBreakdown.set(null);
    this.discountError.set(null);
  }

  private unitPriceOf(productId: string): number {
    return this.products().get(productId)?.unitPrice ?? 0;
  }
}
