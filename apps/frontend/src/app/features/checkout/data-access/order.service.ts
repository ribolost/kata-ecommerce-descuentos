import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../core/config/api-base-url.token';
import { CartItemRequest, CartRequest } from './cart.model';
import { Order } from './order.model';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = inject(API_BASE_URL);

  placeOrder(items: CartItemRequest[], couponCode: string | null): Observable<Order> {
    const body: CartRequest = { items, couponCode };
    return this.http.post<Order>(`${this.baseUrl}/orders`, body);
  }

  getOrder(orderId: string): Observable<Order> {
    return this.http.get<Order>(`${this.baseUrl}/orders/${orderId}`);
  }
}
