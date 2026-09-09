package com.ribolost.pruebastecnicas.kataecommerce.catalog.infrastructure.controller;

import com.ribolost.pruebastecnicas.kataecommerce.catalog.application.ProductService;
import com.ribolost.pruebastecnicas.kataecommerce.catalog.domain.Product;
import com.ribolost.pruebastecnicas.kataecommerce.shared.error.ProductNotFoundException;
import com.ribolost.pruebastecnicas.kataecommerce.shared.validation.ResponseValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
@Validated
public class ProductController {

    private final ProductService productService;
    private final ResponseValidator responseValidator;

    public ProductController(ProductService productService, ResponseValidator responseValidator) {
        this.productService = productService;
        this.responseValidator = responseValidator;
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> listProducts() {
        return ResponseEntity.ok(responseValidator.validateAll(productService.getProducts()));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<Product> getProductById(
            @PathVariable @NotBlank(message = "productId es obligatorio") String productId) {
        return ResponseEntity.ok(responseValidator.validate(productService.getProductById(productId)));
    }

    @GetMapping("/stocks/{productId}")
    public ResponseEntity<StockView> getProductStock(
            @PathVariable @NotBlank(message = "productId es obligatorio") String productId) {
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(responseValidator.validate(StockView.from(product)));
    }

    @GetMapping("/stocks")
    public ResponseEntity<List<StockView>> getProductsStock(
            @RequestParam("productIds")
            @NotEmpty(message = "productIds no debe estar vacío")
            List<@NotBlank(message = "productIds no debe contener valores en blanco") String> productIds) {
        List<Product> products = productService.getProductsByIds(productIds);

        Set<String> foundIds = products.stream().map(Product::getId).collect(Collectors.toSet());
        List<String> missingIds = productIds.stream().filter(id -> !foundIds.contains(id)).toList();
        if (!missingIds.isEmpty()) {
            throw new ProductNotFoundException("Productos no encontrados: " + missingIds);
        }

        List<StockView> response = products.stream().map(StockView::from).toList();
        return ResponseEntity.ok(responseValidator.validateAll(response));
    }

    public record StockView(
            @NotBlank(message = "productId es obligatorio")
            String productId,

            @PositiveOrZero(message = "stock no puede ser negativo")
            int stock
    ) {
        static StockView from(Product product) {
            return new StockView(product.getId(), product.getStock());
        }
    }
}
