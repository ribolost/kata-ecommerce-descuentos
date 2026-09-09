package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.rules;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountContext;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition.DiscountType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record VolumeDiscountRule(BigDecimal value, DiscountRule next) implements DiscountRule {

    private static final BigDecimal VOLUME_THRESHOLD = BigDecimal.valueOf(100);

    @Override
    public DiscountContext apply(DiscountContext context) {
        if (context.getCurrentAmount().compareTo(VOLUME_THRESHOLD) > 0) {
            BigDecimal discount = context.getCurrentAmount().multiply(value).setScale(2, RoundingMode.HALF_UP);
            context.setVolumeDiscountAmount(discount);
            context.setCurrentAmount(context.getCurrentAmount().subtract(discount));
            context.setTotalDiscountAmount(context.getTotalDiscountAmount().add(discount));
            context.getAppliedDiscounts().add(DiscountType.VOLUME);
        }

        return next != null ? next.apply(context) : context;
    }
}
