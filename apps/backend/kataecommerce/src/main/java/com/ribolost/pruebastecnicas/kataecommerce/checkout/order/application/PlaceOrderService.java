package com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.dto.in.CartRequest;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.dto.out.OrderResponse;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.port.in.PlaceOrderUseCase;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.port.out.DiscountCalculationPort;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.port.out.OrderRepository;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.port.out.ProductStockPort;
import org.springframework.stereotype.Service;

@Service
public class PlaceOrderService implements PlaceOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductStockPort productStockPort;
    private final DiscountCalculationPort discountCalculationPort;

    public PlaceOrderService(OrderRepository orderRepository,
                             ProductStockPort productStockPort,
                             DiscountCalculationPort discountCalculationPort) {
        this.orderRepository = orderRepository;
        this.productStockPort = productStockPort;
        this.discountCalculationPort = discountCalculationPort;
    }

    @Override
    public OrderResponse placeOrder(CartRequest cartRequest) {
        // TODO: RN-06 validar stock (productStockPort.ensureAvailability) ->
        // RN-01..RN-05 calcular descuentos (discountCalculationPort.calculate) ->
        // persistir la Order (orderRepository.save) -> RN-07 decrementar stock
        // y marcar el cupón como usado (productStockPort.decrementStock).
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}
