import { Component, input } from '@angular/core';

@Component({
  selector: 'ui-quantity-value',
  templateUrl: './quantity-value.html',
  styleUrl: './quantity-value.scss',
})
export class QuantityValue {
  readonly quantity = input.required<number>();
}
