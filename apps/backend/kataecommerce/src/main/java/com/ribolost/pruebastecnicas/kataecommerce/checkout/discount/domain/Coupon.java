package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain;

public class Coupon {

    private String code;
    private boolean active;
    private boolean used;

    private DiscountRuleDefinition discountRule;

    public Coupon() {
    }

    public Coupon(String code, boolean active, boolean used, DiscountRuleDefinition discountRule) {
        this.code = code;
        this.active = active;
        this.used = used;
        this.discountRule = discountRule;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public DiscountRuleDefinition getDiscountRule() {
        return discountRule;
    }

    public void setDiscountRule(DiscountRuleDefinition discountRule) {
        this.discountRule = discountRule;
    }
}
