package com.ribolost.pruebastecnicas.kataecommerce.checkout.order.infrastructure.mappers;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.dto.out.OrderItemResponse;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.dto.out.OrderResponse;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.domain.Order;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.domain.OrderLine;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.infrastructure.persistence.OrderDocument;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.infrastructure.persistence.OrderLineDocument;
import com.ribolost.pruebastecnicas.kataecommerce.shared.validation.ResponseValidator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    private final ResponseValidator responseValidator;

    public OrderMapper(ResponseValidator responseValidator) {
        this.responseValidator = responseValidator;
    }

    public OrderDocument toDocument(Order order) {
        List<OrderLineDocument> lines = order.getLines().stream()
                .map(this::toDocument)
                .toList();

        return new OrderDocument(
                order.getId(),
                order.getCreatedAt(),
                lines,
                order.getSubtotal(),
                order.getTotalDiscountAmount(),
                order.getTotal(),
                order.getCouponCode(),
                order.getAppliedDiscounts(),
                order.getDiscountBreakdown()
        );
    }

    public Order toDomain(OrderDocument document) {
        List<OrderLine> lines = document.getLines().stream()
                .map(this::toDomain)
                .toList();

        return new Order(
                document.getId(),
                document.getCreatedAt(),
                lines,
                document.getSubtotal(),
                document.getTotalDiscountAmount(),
                document.getTotal(),
                document.getCouponCode(),
                document.getAppliedDiscounts(),
                document.getDiscountBreakdown()
        );
    }

    public OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getLines().stream()
                .map(line -> new OrderItemResponse(
                        line.getProductId(),
                        line.getProductName(),
                        line.getUnitPrice(),
                        line.getQuantity(),
                        line.getSubtotal(),
                        line.getDiscount(),
                        line.getTotal()
                ))
                .toList();

        OrderResponse response = new OrderResponse(
                order.getId(),
                order.getCreatedAt(),
                items,
                order.getSubtotal(),
                order.getTotalDiscountAmount(),
                order.getTotal(),
                order.getCouponCode(),
                order.getAppliedDiscounts(),
                order.getDiscountBreakdown()
        );
        return responseValidator.validate(response);
    }

    private OrderLineDocument toDocument(OrderLine line) {
        return new OrderLineDocument(
                line.getProductId(),
                line.getProductName(),
                line.getUnitPrice(),
                line.getQuantity(),
                line.getSubtotal(),
                line.getDiscount(),
                line.getTotal()
        );
    }

    private OrderLine toDomain(OrderLineDocument document) {
        return new OrderLine(
                document.getProductId(),
                document.getProductName(),
                document.getUnitPrice(),
                document.getQuantity(),
                document.getSubtotal(),
                document.getDiscount(),
                document.getTotal()
        );
    }
}
