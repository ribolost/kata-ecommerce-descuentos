package com.ribolost.pruebastecnicas.kataecommerce.catalog.domain;

import com.ribolost.pruebastecnicas.kataecommerce.shared.error.InsufficientStockException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class Product {

    @NotBlank(message = "id es obligatorio")
    private String id;

    @NotBlank(message = "name es obligatorio")
    private String name;

    private String description;

    @NotNull(message = "unitPrice es obligatorio")
    @Positive(message = "unitPrice debe ser mayor que 0")
    private BigDecimal unitPrice;

    @NotNull(message = "category es obligatoria")
    private ProductCategory category;

    @PositiveOrZero(message = "stock no puede ser negativo")
    private int stock;

    public Product() {
    }

    public Product(String id, String name, String description, BigDecimal unitPrice,
                   ProductCategory category, int stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.category = category;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad a decrementar debe ser positiva");
        }
        if (this.stock < quantity) {
            throw new InsufficientStockException(
                    "Stock insuficiente para el producto " + id + ": disponible " + this.stock
                            + ", solicitado " + quantity);
        }
        this.stock -= quantity;
    }

    public enum ProductCategory {
        TECNOLOGIA,
        OTRO
    }
}
