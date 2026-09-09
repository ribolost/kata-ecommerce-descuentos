package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.rules;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountContext;

public interface DiscountRule {

    DiscountContext apply(DiscountContext context);
}
