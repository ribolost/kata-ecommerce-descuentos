package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain;

import java.util.ArrayList;
import java.util.List;

public class DiscountPolicy {

    private String id;
    private List<DiscountRuleDefinition> rules = new ArrayList<>();
    private List<Coupon> coupons = new ArrayList<>();

    public DiscountPolicy() {
    }

    public DiscountPolicy(String id, List<DiscountRuleDefinition> rules, List<Coupon> coupons) {
        this.id = id;
        this.rules = rules;
        this.coupons = coupons;
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

    public List<Coupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<Coupon> coupons) {
        this.coupons = coupons;
    }
}
