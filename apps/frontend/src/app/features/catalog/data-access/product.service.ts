import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { API_BASE_URL } from '../../../core/config/api-base-url.token';
import { Product } from './product.model';
import { mockProducts } from './product.mock';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = inject(API_BASE_URL);

  getProducts(): Observable<Product[]> {
    // return of(mockProducts);
    return this.http.get<Product[]>(`${this.baseUrl}/products`);
  }
}
