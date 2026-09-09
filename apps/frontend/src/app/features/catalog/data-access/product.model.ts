export type ProductCategory = 'TECNOLOGIA' | 'OTRO';

export interface Product {
  id: string;
  name: string;
  description: string;
  unitPrice: number;
  category: ProductCategory;
  stock: number;
}

export interface Stock {
  productId: string;
  stock: number;
}
