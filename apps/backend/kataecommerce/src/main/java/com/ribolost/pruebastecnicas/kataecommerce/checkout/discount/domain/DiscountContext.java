package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain;

import com.ribolost.pruebastecnicas.kataecommerce.catalog.domain.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DiscountContext {

    private final BigDecimal subtotal;
    private final Map<Product, Integer> items;
    private final String couponCode;

    private BigDecimal currentAmount;
    private BigDecimal categoryDiscountAmount = BigDecimal.ZERO;
    private BigDecimal volumeDiscountAmount = BigDecimal.ZERO;
    private BigDecimal couponDiscountAmount = BigDecimal.ZERO;
    private BigDecimal totalDiscountAmount = BigDecimal.ZERO;
    private final List<DiscountRuleDefinition.DiscountType> appliedDiscounts = new ArrayList<>();

    public DiscountContext(BigDecimal subtotal, Map<Product, Integer> items, String couponCode) {
        this.subtotal = subtotal;
        this.currentAmount = subtotal;
        this.items = items;
        this.couponCode = couponCode;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public Map<Product, Integer> getItems() {
        return items;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public BigDecimal getCategoryDiscountAmount() {
        return categoryDiscountAmount;
    }

    public void setCategoryDiscountAmount(BigDecimal categoryDiscountAmount) {
        this.categoryDiscountAmount = categoryDiscountAmount;
    }

    public BigDecimal getVolumeDiscountAmount() {
        return volumeDiscountAmount;
    }

    public void setVolumeDiscountAmount(BigDecimal volumeDiscountAmount) {
        this.volumeDiscountAmount = volumeDiscountAmount;
    }

    public BigDecimal getCouponDiscountAmount() {
        return couponDiscountAmount;
    }

    public void setCouponDiscountAmount(BigDecimal couponDiscountAmount) {
        this.couponDiscountAmount = couponDiscountAmount;
    }

    public BigDecimal getTotalDiscountAmount() {
        return totalDiscountAmount;
    }

    public void setTotalDiscountAmount(BigDecimal totalDiscountAmount) {
        this.totalDiscountAmount = totalDiscountAmount;
    }

    public List<DiscountRuleDefinition.DiscountType> getAppliedDiscounts() {
        return appliedDiscounts;
    }
}
