package com.ribolost.pruebastecnicas.kataecommerce.checkout.order.infrastructure.adapter.in.web;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.dto.in.CartRequest;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.dto.out.OrderResponse;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.port.in.PlaceOrderUseCase;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.port.out.OrderRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Controlador REST del subdominio Proceso de Checkout (Core).
 *
 * Contrato: POST /api/orders, GET /api/orders/{orderId}
 * (ver arquitectura.md, sección "Contratos REST").
 */
@RestController
@RequestMapping("/api/orders")
public class CheckoutController {

    private final PlaceOrderUseCase placeOrderUseCase;
    private final OrderRepository orderRepository;

    public CheckoutController(PlaceOrderUseCase placeOrderUseCase, OrderRepository orderRepository) {
        this.placeOrderUseCase = placeOrderUseCase;
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CartRequest cartRequest) {
        // TODO: construir la URI de ubicación del recurso creado (Location header).
        OrderResponse orderResponse = placeOrderUseCase.placeOrder(cartRequest);
        return ResponseEntity.created(URI.create("/api/orders/")).body(orderResponse);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String orderId) {
        // TODO: mapear Order -> OrderResponse y devolver 404 (OrderNotFoundException) si no existe.
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}
