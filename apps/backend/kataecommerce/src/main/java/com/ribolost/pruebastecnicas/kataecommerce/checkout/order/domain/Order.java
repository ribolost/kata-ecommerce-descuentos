package com.ribolost.pruebastecnicas.kataecommerce.checkout.order.domain;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountBreakdown;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition.DiscountType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate Root del subdominio Proceso de Checkout (Core).
 * Representa una compra confirmada, con su desglose de descuentos y el
 * precio congelado por línea.
 *
 * Invariantes (ver arquitectura.md, sección 1.6):
 * - total = subtotal - totalDiscountAmount.
 * - totalDiscountAmount <= 0.35 * subtotal.
 * - Toda Order tiene al menos una OrderLine.
 */
public class Order {

    private String id;
    private Instant createdAt;
    private List<OrderLine> lines = new ArrayList<>();
    private BigDecimal subtotal;
    private BigDecimal totalDiscountAmount;
    private BigDecimal total;
    private String couponCode;
    private List<DiscountType> appliedDiscounts = new ArrayList<>();
    private DiscountBreakdown discountBreakdown;

    public Order() {
    }

    public Order(String id, Instant createdAt, List<OrderLine> lines, BigDecimal subtotal,
                 BigDecimal totalDiscountAmount, BigDecimal total, String couponCode,
                 List<DiscountType> appliedDiscounts, DiscountBreakdown discountBreakdown) {
        this.id = id;
        this.createdAt = createdAt;
        this.lines = lines;
        this.subtotal = subtotal;
        this.totalDiscountAmount = totalDiscountAmount;
        this.total = total;
        this.couponCode = couponCode;
        this.appliedDiscounts = appliedDiscounts;
        this.discountBreakdown = discountBreakdown;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderLine> getLines() {
        return lines;
    }

    public void setLines(List<OrderLine> lines) {
        this.lines = lines;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotalDiscountAmount() {
        return totalDiscountAmount;
    }

    public void setTotalDiscountAmount(BigDecimal totalDiscountAmount) {
        this.totalDiscountAmount = totalDiscountAmount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public List<DiscountType> getAppliedDiscounts() {
        return appliedDiscounts;
    }

    public void setAppliedDiscounts(List<DiscountType> appliedDiscounts) {
        this.appliedDiscounts = appliedDiscounts;
    }

    public DiscountBreakdown getDiscountBreakdown() {
        return discountBreakdown;
    }

    public void setDiscountBreakdown(DiscountBreakdown discountBreakdown) {
        this.discountBreakdown = discountBreakdown;
    }
}
