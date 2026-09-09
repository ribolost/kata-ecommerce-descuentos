import { Component, computed, input, output } from '@angular/core';
import { IconButton } from '../../atoms/icon-button/icon-button';
import { QuantityValue } from '../../atoms/quantity-value/quantity-value';

@Component({
  selector: 'ui-quantity-stepper',
  imports: [IconButton, QuantityValue],
  templateUrl: './quantity-stepper.html',
  styleUrl: './quantity-stepper.scss',
})
export class QuantityStepper {
  readonly quantity = input(0);
  readonly max = input(Infinity);

  readonly increment = output<void>();
  readonly decrement = output<void>();

  readonly isEmpty = computed(() => this.quantity() === 0);
  readonly canIncrement = computed(() => this.quantity() < this.max());
}
