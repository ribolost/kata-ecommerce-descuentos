package com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.application.dto.out.DiscountCalculationResponse;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.application.dto.out.DiscountItemResponse;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.dto.in.CartRequest;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.dto.out.OrderResponse;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.port.in.PlaceOrderUseCase;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.port.out.DiscountCalculationPort;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.port.out.OrderRepository;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.port.out.ProductStockPort;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.domain.Order;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.domain.OrderLine;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

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
        // 1) validar stock (lanza InsufficientStockException si aplica)
        productStockPort.ensureAvailability(cartRequest);

        // 2) calcular descuentos
        DiscountCalculationResponse discountResponse = discountCalculationPort.calculate(cartRequest);

        // 3) construir Order domain a partir de la respuesta de descuentos
        List<OrderLine> lines = discountResponse.items().stream()
                .map(item -> new OrderLine(
                        item.productId(),
                        /* productName: intentar obtener nombre desde el port */
                        productStockPort.getProductNames(List.of(item.productId())).get(item.productId()),
                        /* unitPrice */ item.subtotal().divide(BigDecimal.valueOf(item.quantity())),
                        item.quantity(),
                        item.subtotal(),
                        item.discount(),
                        item.total()
                ))
                .toList();

        Order order = new Order(
                UUID.randomUUID().toString(),
                Instant.now(),
                lines,
                discountResponse.subtotal(),
                discountResponse.totalDiscountAmount(),
                discountResponse.total(),
                /* couponCode */ null,
                discountResponse.appliedDiscounts(),
                discountResponse.discountBreakdown()
        );

        // guardar orden
        Order saved = orderRepository.save(order);

        // decrementar stock
        productStockPort.decrementStock(cartRequest);

        return OrderResponse.from(saved);
    }
}
