import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiError } from '../../../../core/models/api-error.model';
import { CartLineViewModel, CartSidebar } from '../../../../shared/ui/organisms/cart-sidebar/cart-sidebar';
import { ProductGrid } from '../../../../shared/ui/organisms/product-grid/product-grid';
import { CatalogPageTemplate } from '../../../../shared/ui/templates/catalog-page-template/catalog-page-template';
import { LoadingIndicator } from '../../../../shared/ui/atoms/loading-indicator/loading-indicator';
import { Button } from '../../../../shared/ui/atoms/button/button';
import { CartStateService } from '../../../checkout/data-access/cart-state.service';
import { OrderService } from '../../../checkout/data-access/order.service';
import { Product } from '../../data-access/product.model';
import { ProductService } from '../../data-access/product.service';

@Component({
  selector: 'app-product-catalog-page',
  imports: [CatalogPageTemplate, ProductGrid, CartSidebar, LoadingIndicator, Button],
  templateUrl: './product-catalog-page.html',
  styleUrl: './product-catalog-page.scss',
})
export class ProductCatalogPage {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly productService = inject(ProductService);
  private readonly orderService = inject(OrderService);
  protected readonly cartState = inject(CartStateService);

  protected readonly products = signal<Product[]>([]);
  protected readonly loadError = signal(false);
  protected readonly loadingProducts = signal(false);
  protected readonly payPending = signal(false);
  protected readonly payError = signal<string | null>(null);
  protected readonly orderNotFoundNotice = signal(
    this.route.snapshot.queryParamMap.get('orderNotFound') === 'true',
  );

  protected readonly cartLines = computed<CartLineViewModel[]>(() => {
    const breakdown = this.cartState.discountBreakdown();
    const discountItems = new Map((breakdown?.items ?? []).map((item) => [item.productId, item]));

    return this.cartState.items().map((item) => {
      const product = this.products().find((candidate) => candidate.id === item.productId);
      const discountItem = discountItems.get(item.productId);

      return {
        productId: item.productId,
        name: product?.name ?? '',
        unitPrice: product?.unitPrice ?? 0,
        quantity: item.quantity,
        lineTotal: discountItem?.total ?? (product?.unitPrice ?? 0) * item.quantity,
        maxQuantity: product?.stock ?? 0,
      };
    });
  });

  protected readonly couponNotApplied = computed(() => {
    const breakdown = this.cartState.discountBreakdown();
    const couponCode = this.cartState.couponCode();

    if (!couponCode || !breakdown || this.cartState.isCapped()) {
      return false;
    }

    return !breakdown.appliedDiscounts.includes('COUPON');
  });

  constructor() {
    const resolvedProducts = this.route.snapshot.data['products'] as Product[] | null;

    if (resolvedProducts) {
      this.setProducts(resolvedProducts);
    } else {
      this.loadError.set(true);
    }
  }

  retryLoadProducts(): void {
    this.loadingProducts.set(true);
    this.loadError.set(false);

    this.productService.getProducts().subscribe({
      next: (products) => {
        this.setProducts(products);
        this.loadingProducts.set(false);
      },
      error: () => {
        this.loadError.set(true);
        this.loadingProducts.set(false);
      },
    });
  }

  onIncrement(productId: string): void {
    if (this.cartState.quantityOf(productId) === 0) {
      this.cartState.addItem(productId);
    } else {
      this.cartState.incrementItem(productId);
    }
  }

  onDecrement(productId: string): void {
    this.cartState.decrementItem(productId);
  }

  onActivateCoupon(code: string): void {
    this.cartState.applyCoupon(code);
  }

  onPay(): void {
    if (!this.cartState.hasItems() || this.payPending()) {
      return;
    }

    this.payPending.set(true);
    this.payError.set(null);

    const items = this.cartState.items().map(({ productId, quantity }) => ({ productId, quantity }));

    this.orderService.placeOrder(items, this.cartState.couponCode()).subscribe({
      next: (order) => {
        this.payPending.set(false);
        this.cartState.resetCart();
        this.router.navigate(['/orders', order.id]);
      },
      error: (error: ApiError) => {
        this.payPending.set(false);
        this.payError.set(this.toPayErrorMessage(error));
      },
    });
  }

  private setProducts(products: Product[]): void {
    this.products.set(products);
    this.cartState.setProducts(products);
  }

  private toPayErrorMessage(error: ApiError): string {
    if (error.status === 409) {
      return 'No hay stock suficiente para completar la compra. Ajusta las cantidades e inténtalo de nuevo.';
    }

    if (error.status === 400) {
      return 'El carrito contiene datos inválidos. Revisa los productos seleccionados.';
    }

    return 'No se pudo procesar el pago. Inténtalo nuevamente en unos segundos.';
  }
}
