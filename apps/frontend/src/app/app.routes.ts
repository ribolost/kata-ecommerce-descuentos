import { Routes } from '@angular/router';
import { productCatalogResolver } from './features/catalog/pages/product-catalog-page/product-catalog.resolver';
import { orderResultResolver } from './features/checkout/pages/order-result-page/order-result.resolver';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./features/catalog/pages/product-catalog-page/product-catalog-page').then(
        (m) => m.ProductCatalogPage,
      ),
    resolve: { products: productCatalogResolver },
  },
  {
    path: 'orders/:orderId',
    loadComponent: () =>
      import('./features/checkout/pages/order-result-page/order-result-page').then(
        (m) => m.OrderResultPage,
      ),
    resolve: { order: orderResultResolver },
  },
  {
    path: '**',
    redirectTo: '',
  },
];
