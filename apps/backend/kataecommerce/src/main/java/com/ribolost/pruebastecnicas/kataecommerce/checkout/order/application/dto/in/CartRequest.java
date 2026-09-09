package com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.dto.in;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.validation.ValidCouponCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CartRequest(

        @NotEmpty(message = "items no debe estar vacío")
        @Valid
        List<CartItemRequest> items,

        @ValidCouponCode
        String couponCode
) {
}
