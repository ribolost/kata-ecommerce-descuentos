import { Component, input } from '@angular/core';
import { PriceTag } from '../../atoms/price-tag/price-tag';

@Component({
  selector: 'ui-discount-line-item',
  imports: [PriceTag],
  templateUrl: './discount-line-item.html',
  styleUrl: './discount-line-item.scss',
})
export class DiscountLineItem {
  readonly label = input.required<string>();
  readonly amount = input.required<number>();
}
