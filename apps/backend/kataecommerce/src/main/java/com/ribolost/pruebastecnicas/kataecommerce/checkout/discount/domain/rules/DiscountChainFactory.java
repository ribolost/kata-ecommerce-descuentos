package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.rules;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountPolicy;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition.DiscountType;

import java.util.Comparator;
import java.util.List;

public class DiscountChainFactory {

    public DiscountRule buildChain(DiscountPolicy policy) {
        List<DiscountRuleDefinition> definitions = policy.getRules();

        DiscountRuleDefinition capDefinition = definitions.stream()
                .filter(definition -> definition.getType() == DiscountType.TOTAL)
                .findFirst()
                .orElse(null);

        List<DiscountRuleDefinition> orderedRules = definitions.stream()
                .filter(definition -> definition.getType() != DiscountType.TOTAL)
                .sorted(Comparator.comparingInt(DiscountRuleDefinition::getOrder))
                .toList();

        DiscountRule chain = capDefinition != null ? buildRule(capDefinition, null) : null;

        for (int i = orderedRules.size() - 1; i >= 0; i--) {
            chain = buildRule(orderedRules.get(i), chain);
        }

        return chain;
    }

    private DiscountRule buildRule(DiscountRuleDefinition definition, DiscountRule next) {
        return switch (definition.getType()) {
            case CATEGORY -> new CategoryDiscountRule(definition.getValue(), next);
            case VOLUME -> new VolumeDiscountRule(definition.getValue(), next);
            case COUPON -> new CouponDiscountRule(definition.getValue(), definition.getCoupon(), next);
            case TOTAL -> new MaxDiscountCapRule(definition.getValue());
        };
    }
}
