import { Component, input, output } from '@angular/core';
import { PriceTag } from '../../atoms/price-tag/price-tag';
import { QuantityStepper } from '../quantity-stepper/quantity-stepper';

@Component({
  selector: 'ui-cart-item-row',
  imports: [PriceTag, QuantityStepper],
  templateUrl: './cart-item-row.html',
  styleUrl: './cart-item-row.scss',
})
export class CartItemRow {
  readonly productName = input.required<string>();
  readonly unitPrice = input.required<number>();
  readonly quantity = input.required<number>();
  readonly lineTotal = input.required<number>();
  readonly maxQuantity = input(Infinity);

  readonly increment = output<void>();
  readonly decrement = output<void>();
}
