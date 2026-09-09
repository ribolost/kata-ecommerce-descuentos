import { DatePipe } from '@angular/common';
import { Component, computed, input } from '@angular/core';
import { Order } from '../../../../features/checkout/data-access/order.model';
import { DiscountCapAlert } from '../../atoms/discount-cap-alert/discount-cap-alert';
import { PriceTag } from '../../atoms/price-tag/price-tag';

@Component({
  selector: 'ui-order-summary',
  imports: [PriceTag, DiscountCapAlert, DatePipe],
  templateUrl: './order-summary.html',
  styleUrl: './order-summary.scss',
})
export class OrderSummary {
  readonly order = input.required<Order>();

  readonly isCapped = computed(() => this.order().appliedDiscounts.includes('TOTAL'));
  readonly hasCategoryDiscount = computed(() => this.order().appliedDiscounts.includes('CATEGORY'));
  readonly hasVolumeDiscount = computed(() => this.order().appliedDiscounts.includes('VOLUME'));
  readonly hasCouponDiscount = computed(() => this.order().appliedDiscounts.includes('COUPON'));
}
