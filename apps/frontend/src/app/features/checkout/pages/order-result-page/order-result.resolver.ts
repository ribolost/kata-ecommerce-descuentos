import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { catchError, of } from 'rxjs';
import { Order } from '../../data-access/order.model';
import { OrderService } from '../../data-access/order.service';

export const orderResultResolver: ResolveFn<Order | null> = (route) => {
  const orderService = inject(OrderService);
  const orderId = route.paramMap.get('orderId') ?? '';

  return orderService.getOrder(orderId).pipe(catchError(() => of(null)));
};
