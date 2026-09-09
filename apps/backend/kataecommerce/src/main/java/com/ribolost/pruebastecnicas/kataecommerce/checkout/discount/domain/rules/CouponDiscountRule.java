package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.rules;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.Coupon;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountContext;

import java.math.BigDecimal;

public record CouponDiscountRule(BigDecimal value, Coupon coupon, DiscountRule next) implements DiscountRule {

    @Override
    public DiscountContext apply(DiscountContext context) {
        // TODO: calcular el descuento de cupón (RN-03) sobre context,
        // validando que coupon.isActive() && !coupon.isUsed() (RN-05).
        return next != null ? next.apply(context) : context;
    }
}
