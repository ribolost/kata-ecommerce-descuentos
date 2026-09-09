package com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.port.in;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.domain.Order;

public interface GetOrderUseCase {

    Order getOrder(String orderId);
}
