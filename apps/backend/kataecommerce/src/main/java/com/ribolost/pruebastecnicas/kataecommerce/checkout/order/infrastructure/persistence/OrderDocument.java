package com.ribolost.pruebastecnicas.kataecommerce.checkout.order.infrastructure.persistence;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountBreakdown;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition.DiscountType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Documento MongoDB de persistencia para el Aggregate Root Order.
 * El Aggregate completo (líneas y desglose de descuentos) se persiste como
 * una única unidad coherente en un solo documento.
 */
@Document(collection = "orders")
public class OrderDocument {

    @Id
    private String id;

    @Field("created_at")
    private Instant createdAt;

    @Field("lines")
    private List<OrderLineDocument> lines = new ArrayList<>();

    @Field("subtotal")
    private BigDecimal subtotal;

    @Field("total_discount_amount")
    private BigDecimal totalDiscountAmount;

    @Field("total")
    private BigDecimal total;

    @Field("coupon_code")
    private String couponCode;

    @Field("applied_discounts")
    private List<DiscountType> appliedDiscounts = new ArrayList<>();

    @Field("discount_breakdown")
    private DiscountBreakdown discountBreakdown;

    public OrderDocument() {
    }

    public OrderDocument(String id, Instant createdAt, List<OrderLineDocument> lines, BigDecimal subtotal,
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

    public List<OrderLineDocument> getLines() {
        return lines;
    }

    public void setLines(List<OrderLineDocument> lines) {
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
