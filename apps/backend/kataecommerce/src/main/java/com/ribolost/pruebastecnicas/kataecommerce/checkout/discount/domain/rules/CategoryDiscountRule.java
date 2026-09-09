package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.rules;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountContext;

import java.math.BigDecimal;

public record CategoryDiscountRule(BigDecimal value, DiscountRule next) implements DiscountRule {

    @Override
    public DiscountContext apply(DiscountContext context) {
        // TODO: calcular el descuento de categoría (RN-01) sobre context.
        return next != null ? next.apply(context) : context;
    }
}
