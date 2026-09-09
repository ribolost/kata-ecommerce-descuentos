package com.ribolost.pruebastecnicas.kataecommerce.checkout.order.infrastructure.adapter.in.web;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.dto.in.CartRequest;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.dto.out.OrderResponse;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.port.in.GetOrderUseCase;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.port.in.PlaceOrderUseCase;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.domain.Order;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.infrastructure.mappers.OrderMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/orders")
public class CheckoutController {

    private final PlaceOrderUseCase placeOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final OrderMapper orderMapper;

    public CheckoutController(PlaceOrderUseCase placeOrderUseCase,
                               GetOrderUseCase getOrderUseCase,
                               OrderMapper orderMapper) {
        this.placeOrderUseCase = placeOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.orderMapper = orderMapper;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CartRequest cartRequest) {
        Order order = placeOrderUseCase.placeOrder(cartRequest);
        OrderResponse response = orderMapper.toResponse(order);

        return ResponseEntity.created(URI.create("/api/orders/" + order.getId())).body(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String orderId) {
        Order order = getOrderUseCase.getOrder(orderId);
        return ResponseEntity.ok(orderMapper.toResponse(order));
    }
}
