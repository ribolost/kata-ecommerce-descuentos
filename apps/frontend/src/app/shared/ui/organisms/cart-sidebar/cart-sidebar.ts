import { Component, computed, input, output, signal } from '@angular/core';
import { DiscountBreakdown, DiscountType } from '../../../../features/checkout/data-access/discount-breakdown.model';
import { Button } from '../../atoms/button/button';
import { DiscountCapAlert } from '../../atoms/discount-cap-alert/discount-cap-alert';
import { LoadingIndicator } from '../../atoms/loading-indicator/loading-indicator';
import { PriceTag } from '../../atoms/price-tag/price-tag';
import { CartItemRow } from '../../molecules/cart-item-row/cart-item-row';
import { CouponForm } from '../../molecules/coupon-form/coupon-form';
import { DiscountBreakdownPanel } from '../discount-breakdown-panel/discount-breakdown-panel';

export interface CartLineViewModel {
  productId: string;
  name: string;
  unitPrice: number;
  quantity: number;
  lineTotal: number;
  maxQuantity: number;
}

@Component({
  selector: 'ui-cart-sidebar',
  imports: [CartItemRow, CouponForm, DiscountBreakdownPanel, DiscountCapAlert, PriceTag, Button, LoadingIndicator],
  templateUrl: './cart-sidebar.html',
  styleUrl: './cart-sidebar.scss',
})
export class CartSidebar {
  readonly lines = input.required<CartLineViewModel[]>();
  readonly subtotal = input.required<number>();
  readonly total = input.required<number>();
  readonly totalDiscountAmount = input.required<number>();
  readonly discountBreakdown = input<DiscountBreakdown | null>(null);
  readonly appliedDiscounts = input<DiscountType[]>([]);
  readonly isCapped = input(false);
  readonly couponPending = input(false);
  readonly couponNotApplied = input(false);
  readonly payDisabled = input(false);
  readonly payPending = input(false);
  readonly payError = input<string | null>(null);

  readonly incrementItem = output<string>();
  readonly decrementItem = output<string>();
  readonly activateCoupon = output<string>();
  readonly pay = output<void>();

  readonly isOpen = signal(false);
  readonly itemCount = computed(() => this.lines().reduce((sum, line) => sum + line.quantity, 0));
  readonly isEmpty = computed(() => this.lines().length === 0);

  toggleOpen(): void {
    this.isOpen.update((open) => !open);
  }

  close(): void {
    this.isOpen.set(false);
  }
}
