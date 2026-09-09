import { Component, input } from '@angular/core';
import { DiscountBreakdown, DiscountType } from '../../../../features/checkout/data-access/discount-breakdown.model';
import { DiscountLineItem } from '../../molecules/discount-line-item/discount-line-item';

@Component({
  selector: 'ui-discount-breakdown-panel',
  imports: [DiscountLineItem],
  templateUrl: './discount-breakdown-panel.html',
  styleUrl: './discount-breakdown-panel.scss',
})
export class DiscountBreakdownPanel {
  readonly breakdown = input.required<DiscountBreakdown>();
  readonly appliedDiscounts = input.required<DiscountType[]>();

  hasType(type: DiscountType): boolean {
    return this.appliedDiscounts().includes(type);
  }
}
