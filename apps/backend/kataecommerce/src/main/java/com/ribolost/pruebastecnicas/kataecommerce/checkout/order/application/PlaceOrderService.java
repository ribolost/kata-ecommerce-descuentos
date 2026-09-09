package com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.application.dto.out.DiscountCalculationResponse;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.dto.in.CartRequest;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.exception.OrderNotFoundException;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.exception.OrderStockException;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.port.in.GetOrderUseCase;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.port.in.PlaceOrderUseCase;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.port.out.DiscountCalculationPort;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.port.out.OrderRepository;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.port.out.ProductStockPort;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.domain.Order;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.domain.OrderLine;
import com.ribolost.pruebastecnicas.kataecommerce.shared.error.InsufficientStockException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PlaceOrderService implements PlaceOrderUseCase, GetOrderUseCase {

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
    public Order placeOrder(CartRequest cartRequest) {
        try {
            productStockPort.ensureAvailability(cartRequest);
        } catch (InsufficientStockException ex) {
            throw new OrderStockException(ex.getMessage(), ex);
        }

        // consumeCoupon = true: a diferencia de POST /api/discounts, PlaceOrder
        // es el único flujo que debe marcar el cupón aplicado como usado (RN-07).
        DiscountCalculationResponse discountResponse = discountCalculationPort.calculate(cartRequest, true);

        Map<String, String> productNamesById = productStockPort.getProductNames(
                cartRequest.items().stream()
                        .map(item -> item.productId())
                        .collect(Collectors.toSet())
        );

        List<OrderLine> lines = discountResponse.items().stream()
                .map(item -> new OrderLine(
                        item.productId(),
                        productNamesById.get(item.productId()),
                        item.subtotal().divide(BigDecimal.valueOf(item.quantity()), 2, RoundingMode.HALF_UP),
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
                cartRequest.couponCode(),
                discountResponse.appliedDiscounts(),
                discountResponse.discountBreakdown()
        );

        // Persistir antes de decrementar stock (RN-11): si decrementStock falla,
        // la orden queda registrada y la inconsistencia queda documentada/visible,
        // en lugar de perder el registro de una compra ya calculada.
        Order savedOrder = orderRepository.save(order);

        try {
            productStockPort.decrementStock(cartRequest);
        } catch (InsufficientStockException ex) {
            throw new OrderStockException(
                    "No fue posible descontar el stock requerido para la orden",
                    ex
            );
        }

        return savedOrder;
    }

    @Override
    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Orden no encontrada: " + orderId
                ));
    }
}
