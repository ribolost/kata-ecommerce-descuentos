import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { API_BASE_URL } from '../../../core/config/api-base-url.token';
import { CartItemRequest, CartRequest } from './cart.model';
import { DiscountCalculationResult } from './discount-breakdown.model';
import { DiscountService } from './discount.service';

describe('DiscountService', () => {
  let service: DiscountService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: API_BASE_URL, useValue: '/api' },
      ],
    });

    service = TestBed.inject(DiscountService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should POST the cart items and coupon code to {baseUrl}/discounts', () => {
    const items: CartItemRequest[] = [{ productId: '1', quantity: 2 }];
    const expectedBody: CartRequest = { items, couponCode: 'WELCOME2026' };

    service.calculate(items, 'WELCOME2026').subscribe();

    const req = httpMock.expectOne('/api/discounts');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(expectedBody);

    req.flush({} as DiscountCalculationResult);
  });

  it('should send a null couponCode when no coupon has been applied', () => {
    const items: CartItemRequest[] = [{ productId: '1', quantity: 1 }];

    service.calculate(items, null).subscribe();

    const req = httpMock.expectOne('/api/discounts');
    expect(req.request.body).toEqual({ items, couponCode: null });

    req.flush({} as DiscountCalculationResult);
  });

  it('should resolve with the discount calculation returned by the backend', () => {
    const result: DiscountCalculationResult = {
      items: [],
      subtotal: 100,
      totalDiscountAmount: 10,
      total: 90,
      appliedDiscounts: ['CATEGORY'],
      discountBreakdown: {
        categoryDiscountAmount: 10,
        volumeDiscountAmount: 0,
        couponDiscountAmount: 0,
        totalDiscountAmount: 10,
        effectiveDiscountPercentage: 0.1,
      },
    };
    let received: DiscountCalculationResult | undefined;

    service.calculate([{ productId: '1', quantity: 1 }], null).subscribe((response) => (received = response));

    httpMock.expectOne('/api/discounts').flush(result);

    expect(received).toEqual(result);
  });
});
