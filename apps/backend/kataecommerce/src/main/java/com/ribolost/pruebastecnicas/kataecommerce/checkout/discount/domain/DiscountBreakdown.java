package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;


public record DiscountBreakdown(
        @NotNull(message = "categoryDiscountAmount es obligatorio")
        @PositiveOrZero(message = "categoryDiscountAmount no puede ser negativo")
        BigDecimal categoryDiscountAmount,

        @NotNull(message = "volumeDiscountAmount es obligatorio")
        @PositiveOrZero(message = "volumeDiscountAmount no puede ser negativo")
        BigDecimal volumeDiscountAmount,

        @NotNull(message = "couponDiscountAmount es obligatorio")
        @PositiveOrZero(message = "couponDiscountAmount no puede ser negativo")
        BigDecimal couponDiscountAmount,

        @NotNull(message = "totalDiscountAmount es obligatorio")
        @PositiveOrZero(message = "totalDiscountAmount no puede ser negativo")
        BigDecimal totalDiscountAmount,

        @NotNull(message = "effectiveDiscountPercentage es obligatorio")
        @DecimalMin(value = "0.0", message = "effectiveDiscountPercentage no puede ser negativo")
        @DecimalMax(value = "0.35", message = "effectiveDiscountPercentage no puede superar 0.35")
        BigDecimal effectiveDiscountPercentage) {

}
