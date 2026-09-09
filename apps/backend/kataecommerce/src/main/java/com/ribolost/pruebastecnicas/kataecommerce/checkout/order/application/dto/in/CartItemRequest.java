package com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.dto.in;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada: línea de carrito (ver contrato OpenAPI: CartItemRequest).
 */
public record CartItemRequest(

        @NotBlank(message = "productId es obligatorio")
        String productId,

        @Min(value = 1, message = "quantity debe ser mayor o igual a 1")
        int quantity
) {
}
