import { CurrencyPipe } from '@angular/common';
import { Component, input } from '@angular/core';

export type PriceTagSize = 'sm' | 'md' | 'lg';

@Component({
  selector: 'ui-price-tag',
  imports: [CurrencyPipe],
  templateUrl: './price-tag.html',
  styleUrl: './price-tag.scss',
})
export class PriceTag {
  readonly amount = input.required<number>();
  readonly size = input<PriceTagSize>('md');
}
