package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain;

import java.math.BigDecimal;

public class DiscountRuleDefinition {

    private int order;
    private DiscountType type;
    private BigDecimal value;

    private Coupon coupon;

    public DiscountRuleDefinition() {
    }

    public DiscountRuleDefinition(int order, DiscountType type, BigDecimal value, Coupon coupon) {
        this.order = order;
        this.type = type;
        this.value = value;
        this.coupon = coupon;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public DiscountType getType() {
        return type;
    }

    public void setType(DiscountType type) {
        this.type = type;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    /**
     * TOTAL representa el tope máximo de descuento (RN-04). Se nombra TOTAL
     * (y no CAP) para que coincida exactamente con el valor que expone el
     * contrato OpenAPI en AppliedDiscounts/DiscountType (arquitectura.md,
     * "Contrato de API"), tanto para tipar la regla configurada en la
     * DiscountPolicy como para reportar en appliedDiscounts que el tope fue
     * alcanzado y aplicado.
     */
    public enum DiscountType {
        CATEGORY,
        VOLUME,
        COUPON,
        TOTAL
    }
}
