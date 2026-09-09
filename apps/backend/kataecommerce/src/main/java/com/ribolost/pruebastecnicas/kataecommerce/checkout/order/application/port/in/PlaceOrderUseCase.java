package com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.port.in;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.dto.in.CartRequest;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.dto.out.OrderResponse;

public interface PlaceOrderUseCase {

    OrderResponse placeOrder(CartRequest cartRequest);
}
