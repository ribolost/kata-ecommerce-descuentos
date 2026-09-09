package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.rules;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.Coupon;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountContext;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition.DiscountType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record CouponDiscountRule(BigDecimal value, Coupon coupon, DiscountRule next) implements DiscountRule {

    @Override
    public DiscountContext apply(DiscountContext context) {
        if (isCouponApplicable(context.getCouponCode())) {
            BigDecimal discount = context.getCurrentAmount().multiply(value).setScale(2, RoundingMode.HALF_UP);
            context.setCouponDiscountAmount(discount);
            context.setCurrentAmount(context.getCurrentAmount().subtract(discount));
            context.setTotalDiscountAmount(context.getTotalDiscountAmount().add(discount));
            context.getAppliedDiscounts().add(DiscountType.COUPON);
        }

        return next != null ? next.apply(context) : context;
    }

    private boolean isCouponApplicable(String requestedCouponCode) {
        if (coupon == null || requestedCouponCode == null || requestedCouponCode.isBlank()) {
            return false;
        }
        if (!coupon.isActive() || coupon.isUsed()) {
            return false;
        }
        return coupon.getCode() != null
                && coupon.getCode().trim().equalsIgnoreCase(requestedCouponCode.trim());
    }
}
