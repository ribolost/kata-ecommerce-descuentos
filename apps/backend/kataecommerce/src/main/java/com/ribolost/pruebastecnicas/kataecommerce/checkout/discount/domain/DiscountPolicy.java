package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain;

import java.util.ArrayList;
import java.util.List;

public class DiscountPolicy {

    private String id;
    private List<DiscountRuleDefinition> rules = new ArrayList<>();

    public DiscountPolicy() {
    }

    public DiscountPolicy(String id, List<DiscountRuleDefinition> rules) {
        this.id = id;
        this.rules = rules;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<DiscountRuleDefinition> getRules() {
        return rules;
    }

    public void setRules(List<DiscountRuleDefinition> rules) {
        this.rules = rules;
    }
}
