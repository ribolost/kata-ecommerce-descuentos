import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { API_BASE_URL } from '../../../core/config/api-base-url.token';
import { Product } from './product.model';
import { ProductService } from './product.service';

describe('ProductService', () => {
  let service: ProductService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: API_BASE_URL, useValue: '/api' },
      ],
    });

    service = TestBed.inject(ProductService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should request the catalog with a GET to {baseUrl}/products', () => {
    const mockProducts: Product[] = [
      {
        id: '1',
        name: 'Producto Tecnología',
        description: 'Un producto de categoría Tecnología',
        unitPrice: 50,
        category: 'TECNOLOGIA',
        stock: 3,
      },
    ];
    let received: Product[] | undefined;

    service.getProducts().subscribe((products) => (received = products));

    const req = httpMock.expectOne('/api/products');
    expect(req.request.method).toBe('GET');

    req.flush(mockProducts);

    expect(received).toEqual(mockProducts);
  });

  it('should issue exactly one request per subscription, without transforming the response body', () => {
    const mockProducts: Product[] = [];

    service.getProducts().subscribe();

    httpMock.expectOne('/api/products').flush(mockProducts);
  });
});
