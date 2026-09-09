import { DiscountBreakdown, DiscountType } from './discount-breakdown.model';

export interface OrderItem {
  productId: string;
  name: string;
  unitPrice: number;
  quantity: number;
  subtotal: number;
  discount: number;
  total: number;
}

export interface Order {
  id: string;
  createdAt: string;
  items: OrderItem[];
  subtotal: number;
  totalDiscountAmount: number;
  total: number;
  couponCode: string | null;
  appliedDiscounts: DiscountType[];
  discountBreakdown: DiscountBreakdown;
}
