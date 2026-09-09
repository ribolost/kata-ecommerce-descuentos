export type DiscountType = 'CATEGORY' | 'VOLUME' | 'COUPON' | 'TOTAL';

export interface DiscountBreakdown {
  categoryDiscountAmount: number;
  volumeDiscountAmount: number;
  couponDiscountAmount: number;
  totalDiscountAmount: number;
  effectiveDiscountPercentage: number;
}

export interface DiscountItem {
  productId: string;
  quantity: number;
  subtotal: number;
  discount: number;
  total: number;
}

export interface DiscountCalculationResult {
  items: DiscountItem[];
  subtotal: number;
  totalDiscountAmount: number;
  total: number;
  appliedDiscounts: DiscountType[];
  discountBreakdown: DiscountBreakdown;
}
