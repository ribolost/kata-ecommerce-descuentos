package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.rules;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountContext;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition.DiscountType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record MaxDiscountCapRule(BigDecimal maxPercentage) implements DiscountRule {

    @Override
    public DiscountContext apply(DiscountContext context) {
        BigDecimal maxDiscountAllowed = context.getSubtotal().multiply(maxPercentage).setScale(2, RoundingMode.HALF_UP);

        if (context.getTotalDiscountAmount().compareTo(maxDiscountAllowed) > 0) {
            BigDecimal excess = context.getTotalDiscountAmount().subtract(maxDiscountAllowed);
            context.setCurrentAmount(context.getCurrentAmount().add(excess));
            context.setTotalDiscountAmount(maxDiscountAllowed);
            context.getAppliedDiscounts().add(DiscountType.TOTAL);
        }

        return context;
    }
}
