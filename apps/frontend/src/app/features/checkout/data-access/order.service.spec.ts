import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { API_BASE_URL } from '../../../core/config/api-base-url.token';
import { CartItemRequest, CartRequest } from './cart.model';
import { Order } from './order.model';
import { OrderService } from './order.service';

describe('OrderService', () => {
  let service: OrderService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: API_BASE_URL, useValue: '/api' },
      ],
    });

    service = TestBed.inject(OrderService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should POST the cart items and coupon code to {baseUrl}/orders to place an order', () => {
    const items: CartItemRequest[] = [{ productId: '1', quantity: 2 }];
    const expectedBody: CartRequest = { items, couponCode: 'WELCOME2026' };

    service.placeOrder(items, 'WELCOME2026').subscribe();

    const req = httpMock.expectOne('/api/orders');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(expectedBody);

    req.flush({} as Order);
  });

  it('should resolve with the created order returned by the backend', () => {
    const order: Order = {
      id: 'order-1',
      createdAt: '2026-01-01T00:00:00Z',
      items: [],
      subtotal: 100,
      totalDiscountAmount: 0,
      total: 100,
      couponCode: null,
      appliedDiscounts: [],
      discountBreakdown: {
        categoryDiscountAmount: 0,
        volumeDiscountAmount: 0,
        couponDiscountAmount: 0,
        totalDiscountAmount: 0,
        effectiveDiscountPercentage: 0,
      },
    };
    let received: Order | undefined;

    service.placeOrder([{ productId: '1', quantity: 1 }], null).subscribe((response) => (received = response));

    httpMock.expectOne('/api/orders').flush(order);

    expect(received).toEqual(order);
  });

  it('should request an existing order with a GET to {baseUrl}/orders/{orderId}', () => {
    let received: Order | undefined;

    service.getOrder('order-42').subscribe((order) => (received = order));

    const req = httpMock.expectOne('/api/orders/order-42');
    expect(req.request.method).toBe('GET');

    req.flush({ id: 'order-42' } as Order);

    expect(received?.id).toBe('order-42');
  });
});
