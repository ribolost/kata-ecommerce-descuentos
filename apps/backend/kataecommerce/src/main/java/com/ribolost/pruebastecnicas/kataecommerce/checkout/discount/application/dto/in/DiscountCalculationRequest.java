package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.application.dto.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;


public record DiscountCalculationRequest(
        @NotEmpty(message = "items no debe estar vacío")
        @Valid
        List<CartItem> items,
        String couponCode
) {
    public record CartItem(String productId, int quantity) {
    }
}
