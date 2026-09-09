import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { catchError, of } from 'rxjs';
import { Product } from '../../data-access/product.model';
import { ProductService } from '../../data-access/product.service';

export const productCatalogResolver: ResolveFn<Product[] | null> = () => {
  const productService = inject(ProductService);

  return productService.getProducts().pipe(catchError(() => of(null)));
};
