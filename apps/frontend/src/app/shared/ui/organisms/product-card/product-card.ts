import { Component, computed, input, output } from '@angular/core';
import { Product } from '../../../../features/catalog/data-access/product.model';
import { PriceTag } from '../../atoms/price-tag/price-tag';
import { QuantityStepper } from '../../molecules/quantity-stepper/quantity-stepper';

@Component({
  selector: 'ui-product-card',
  imports: [PriceTag, QuantityStepper],
  templateUrl: './product-card.html',
  styleUrl: './product-card.scss',
})
export class ProductCard {
  readonly product = input.required<Product>();
  readonly quantity = input(0);

  readonly increment = output<void>();
  readonly decrement = output<void>();

  readonly isInCart = computed(() => this.quantity() > 0);
}
