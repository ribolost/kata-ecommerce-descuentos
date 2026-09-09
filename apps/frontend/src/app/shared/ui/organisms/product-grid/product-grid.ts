import { Component, input, output } from '@angular/core';
import { Product } from '../../../../features/catalog/data-access/product.model';
import { ProductCard } from '../product-card/product-card';

@Component({
  selector: 'ui-product-grid',
  imports: [ProductCard],
  templateUrl: './product-grid.html',
  styleUrl: './product-grid.scss',
})
export class ProductGrid {
  readonly products = input.required<Product[]>();
  readonly cartQuantities = input<Map<string, number>>(new Map());

  readonly increment = output<string>();
  readonly decrement = output<string>();

  quantityOf(productId: string): number {
    return this.cartQuantities().get(productId) ?? 0;
  }
}
