package com.ribolost.pruebastecnicas.kataecommerce.catalog.infrastructure.controller;

import com.ribolost.pruebastecnicas.kataecommerce.catalog.application.ProductService;
import com.ribolost.pruebastecnicas.kataecommerce.catalog.domain.Product;
import com.ribolost.pruebastecnicas.kataecommerce.shared.error.ProductNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> listProducts() {
        return ResponseEntity.ok(productService.getProducts());
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable String productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping("/stocks/{productId}")
    public ResponseEntity<StockView> getProductStock(@PathVariable String productId) {
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(StockView.from(product));
    }

    @GetMapping("/stocks")
    public ResponseEntity<List<StockView>> getProductsStock(@RequestParam("productIds") List<String> productIds) {
        List<Product> products = productService.getProductsByIds(productIds);

        Set<String> foundIds = products.stream().map(Product::getId).collect(Collectors.toSet());
        List<String> missingIds = productIds.stream().filter(id -> !foundIds.contains(id)).toList();
        if (!missingIds.isEmpty()) {
            throw new ProductNotFoundException("Productos no encontrados: " + missingIds);
        }

        List<StockView> response = products.stream().map(StockView::from).toList();
        return ResponseEntity.ok(response);
    }

    public record StockView(String productId, int stock) {
        static StockView from(Product product) {
            return new StockView(product.getId(), product.getStock());
        }
    }
}
