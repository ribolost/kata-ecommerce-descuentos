package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.application.dto.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;


public record DiscountCalculationRequest(
        @NotEmpty(message = "items no debe estar vacío")
        @Valid
        List<CartItem> items,
        String couponCode
) {
    public record CartItem(
            @NotBlank(message = "productId es obligatorio")
            String productId,

            @Min(value = 1, message = "quantity debe ser mayor o igual a 1")
            int quantity
    ) {
    }
}
