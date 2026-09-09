package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.rules;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountContext;

import java.math.BigDecimal;

public record MaxDiscountCapRule(BigDecimal maxPercentage) implements DiscountRule {

    @Override
    public DiscountContext apply(DiscountContext context) {
        // TODO: truncar context.getTotalDiscountAmount() a maxPercentage * subtotal (RN-04).
        return context;
    }
}
