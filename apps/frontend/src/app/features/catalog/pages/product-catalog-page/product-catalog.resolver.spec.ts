import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { TestBed } from '@angular/core/testing';
import { Observable, of, throwError } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { Product } from '../../data-access/product.model';
import { ProductService } from '../../data-access/product.service';
import { productCatalogResolver } from './product-catalog.resolver';

describe('productCatalogResolver', () => {
  let getProducts: ReturnType<typeof vi.fn>;
  const route = {} as unknown as ActivatedRouteSnapshot;
  const state = {} as unknown as RouterStateSnapshot;

  beforeEach(() => {
    getProducts = vi.fn();

    TestBed.configureTestingModule({
      providers: [{ provide: ProductService, useValue: { getProducts } }],
    });
  });

  function resolve(): Observable<Product[] | null> {
    return TestBed.runInInjectionContext(
      () => productCatalogResolver(route, state) as Observable<Product[] | null>,
    );
  }

  it('should resolve with the products returned by ProductService when the request succeeds', () =>
    new Promise<void>((resolve_) => {
      const products: Product[] = [
        { id: '1', name: 'Producto 1', description: 'desc', unitPrice: 10, category: 'OTRO', stock: 5 },
      ];
      getProducts.mockReturnValue(of(products));

      resolve().subscribe((resolved) => {
        expect(resolved).toEqual(products);
        resolve_();
      });
    }));

  it('should resolve with null instead of propagating the error when the request fails', () =>
    new Promise<void>((resolve_) => {
      getProducts.mockReturnValue(throwError(() => new Error('network error')));

      resolve().subscribe((resolved) => {
        expect(resolved).toBeNull();
        resolve_();
      });
    }));
});
