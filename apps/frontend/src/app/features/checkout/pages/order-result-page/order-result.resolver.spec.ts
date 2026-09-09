import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, RouterStateSnapshot, convertToParamMap } from '@angular/router';
import { Observable, of, throwError } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { Order } from '../../data-access/order.model';
import { OrderService } from '../../data-access/order.service';
import { orderResultResolver } from './order-result.resolver';

describe('orderResultResolver', () => {
  let getOrder: ReturnType<typeof vi.fn>;
  const state = {} as unknown as RouterStateSnapshot;

  beforeEach(() => {
    getOrder = vi.fn();

    TestBed.configureTestingModule({
      providers: [{ provide: OrderService, useValue: { getOrder } }],
    });
  });

  function routeWithOrderId(orderId: string | null): ActivatedRouteSnapshot {
    return {
      paramMap: convertToParamMap(orderId === null ? {} : { orderId }),
    } as unknown as ActivatedRouteSnapshot;
  }

  function resolve(orderId: string | null): Observable<Order | null> {
    return TestBed.runInInjectionContext(
      () => orderResultResolver(routeWithOrderId(orderId), state) as Observable<Order | null>,
    );
  }

  const sampleOrder: Order = {
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

  it('should request the order identified by the :orderId route param', () =>
    new Promise<void>((done) => {
      getOrder.mockReturnValue(of(sampleOrder));

      resolve('order-1').subscribe((resolved) => {
        expect(getOrder).toHaveBeenCalledWith('order-1');
        expect(resolved).toEqual(sampleOrder);
        done();
      });
    }));

  it('should resolve with null instead of propagating the error when the order request fails', () =>
    new Promise<void>((done) => {
      getOrder.mockReturnValue(throwError(() => new Error('not found')));

      resolve('missing-order').subscribe((resolved) => {
        expect(resolved).toBeNull();
        done();
      });
    }));

  it('should request an empty orderId when the route param is absent, without throwing', () =>
    new Promise<void>((done) => {
      getOrder.mockReturnValue(of(sampleOrder));

      resolve(null).subscribe(() => {
        expect(getOrder).toHaveBeenCalledWith('');
        done();
      });
    }));
});
