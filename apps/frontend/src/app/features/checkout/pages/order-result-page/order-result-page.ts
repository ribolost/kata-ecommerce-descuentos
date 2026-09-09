import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Button } from '../../../../shared/ui/atoms/button/button';
import { OrderSummary } from '../../../../shared/ui/organisms/order-summary/order-summary';
import { Order } from '../../data-access/order.model';

@Component({
  selector: 'app-order-result-page',
  imports: [OrderSummary, Button, RouterLink],
  templateUrl: './order-result-page.html',
  styleUrl: './order-result-page.scss',
})
export class OrderResultPage {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly order: Order | null;

  constructor() {
    const resolvedOrder = this.route.snapshot.data['order'] as Order | null;
    this.order = resolvedOrder;

    if (!resolvedOrder) {
      this.router.navigate(['/'], { queryParams: { orderNotFound: 'true' } });
    }
  }
}
