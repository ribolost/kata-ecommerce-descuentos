import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../core/config/api-base-url.token';
import { CartItemRequest, CartRequest } from './cart.model';
import { DiscountCalculationResult } from './discount-breakdown.model';

@Injectable({ providedIn: 'root' })
export class DiscountService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = inject(API_BASE_URL);

  calculate(items: CartItemRequest[], couponCode: string | null): Observable<DiscountCalculationResult> {
    const body: CartRequest = { items, couponCode };
    return this.http.post<DiscountCalculationResult>(`${this.baseUrl}/discounts`, body);
  }
}
