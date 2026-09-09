export interface CartItem {
  productId: string;
  quantity: number;
}

export interface CartItemRequest {
  productId: string;
  quantity: number;
}

export interface CartRequest {
  items: CartItemRequest[];
  couponCode: string | null;
}
