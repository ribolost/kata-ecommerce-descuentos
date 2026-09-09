package com.ribolost.pruebastecnicas.kataecommerce.catalog.infrastructure.controller;

import com.ribolost.pruebastecnicas.kataecommerce.catalog.application.ProductService;
import com.ribolost.pruebastecnicas.kataecommerce.catalog.domain.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> listProducts() {
        return ResponseEntity.ok(productService.getProducts());
    }
}
