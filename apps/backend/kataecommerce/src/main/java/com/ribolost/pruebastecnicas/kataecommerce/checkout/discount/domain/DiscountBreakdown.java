package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain;

import java.math.BigDecimal;


public record DiscountBreakdown(BigDecimal categoryDiscountAmount, BigDecimal volumeDiscountAmount,
                                BigDecimal couponDiscountAmount, BigDecimal totalDiscountAmount,
                                BigDecimal effectiveDiscountPercentage) {

}
