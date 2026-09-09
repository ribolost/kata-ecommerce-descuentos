package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.rules;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.Coupon;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountPolicy;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition;

public class DiscountChainFactory {

    public DiscountRule buildChain(DiscountPolicy policy, String couponCode) {
        // TODO: ordenar policy.getRules() por order, resolver el Coupon aplicable
        // (si couponCode fue provisto) y encadenar las instancias de DiscountRule
        // correspondientes a cada DiscountRuleDefinition.DiscountType.
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    private Coupon resolveCoupon(DiscountPolicy policy, String couponCode) {
        // TODO: buscar en policy.getCoupons() el Coupon cuyo code coincida con couponCode.
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    private DiscountRule buildRule(DiscountRuleDefinition definition, DiscountRule next, Coupon coupon) {
        // TODO: instanciar CategoryDiscountRule / VolumeDiscountRule / CouponDiscountRule
        // / MaxDiscountCapRule según definition.getType().
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}
