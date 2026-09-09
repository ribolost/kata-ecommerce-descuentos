import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { describe, expect, it, vi } from 'vitest';
import { Order } from '../../data-access/order.model';
import { OrderResultPage } from './order-result-page';

function fakeActivatedRoute(order: Order | null): ActivatedRoute {
  return { snapshot: { data: { order } } } as unknown as ActivatedRoute;
}

describe('OrderResultPage', () => {
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

  function setup(order: Order | null): { fixture: ComponentFixture<OrderResultPage>; navigate: ReturnType<typeof vi.fn> } {
    const navigate = vi.fn();

    TestBed.configureTestingModule({
      providers: [
        { provide: ActivatedRoute, useValue: fakeActivatedRoute(order) },
        { provide: Router, useValue: { navigate } },
      ],
    });

    const fixture = TestBed.createComponent(OrderResultPage);
    fixture.detectChanges();
    return { fixture, navigate };
  }

  it('should render the resolved order and not navigate away', () => {
    const { fixture, navigate } = setup(sampleOrder);

    expect(fixture.nativeElement.textContent).toContain('order-1');
    expect(navigate).not.toHaveBeenCalled();
  });

  it('should redirect to the catalog with an orderNotFound notice when no order was resolved', () => {
    const { fixture, navigate } = setup(null);

    expect(navigate).toHaveBeenCalledWith(['/'], { queryParams: { orderNotFound: 'true' } });
    expect(fixture.nativeElement.querySelector('ui-order-summary')).toBeNull();
  });
});
