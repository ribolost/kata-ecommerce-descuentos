package com.ribolost.pruebastecnicas.kataecommerce.checkout.order.domain;

import java.math.BigDecimal;

/**
 * Entidad interna del Aggregate Root Order.
 * Línea de producto dentro de una orden, con cantidad y precio unitario
 * congelado al momento de la compra.
 */
public class OrderLine {

    private String productId;
    private String productName;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal total;

    public OrderLine() {
    }

    public OrderLine(String productId, String productName, BigDecimal unitPrice, int quantity,
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
