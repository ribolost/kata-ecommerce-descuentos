package com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.port.in;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.dto.in.CartRequest;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.domain.Order;

public interface PlaceOrderUseCase {

    Order placeOrder(CartRequest cartRequest);
}
