package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.rules;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountContext;

import java.math.BigDecimal;


public record VolumeDiscountRule(BigDecimal value, DiscountRule next) implements DiscountRule {

    @Override
    public DiscountContext apply(DiscountContext context) {
        // TODO: calcular el descuento por volumen (RN-02) sobre context.
        return next != null ? next.apply(context) : context;
    }
}
