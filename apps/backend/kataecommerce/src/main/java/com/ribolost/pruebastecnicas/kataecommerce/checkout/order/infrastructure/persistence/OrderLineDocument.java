package com.ribolost.pruebastecnicas.kataecommerce.checkout.order.infrastructure.persistence;

import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

/**
 * Sub-documento MongoDB embebido dentro de OrderDocument, correspondiente a
 * la entidad interna OrderLine.
 */
public class OrderLineDocument {

    @Field("product_id")
    private String productId;

    @Field("product_name")
    private String productName;

    @Field("unit_price")
    private BigDecimal unitPrice;

    @Field("quantity")
    private int quantity;

    @Field("subtotal")
    private BigDecimal subtotal;

    @Field("discount")
    private BigDecimal discount;

    @Field("total")
    private BigDecimal total;

    public OrderLineDocument() {
    }

    public OrderLineDocument(String productId, String productName, BigDecimal unitPrice, int quantity,
                              BigDecimal subtotal, BigDecimal discount, BigDecimal total) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.discount = discount;
        this.total = total;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
