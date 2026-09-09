package com.ribolost.pruebastecnicas.kataecommerce.catalog.application;

import com.ribolost.pruebastecnicas.kataecommerce.catalog.domain.Product;
import com.ribolost.pruebastecnicas.kataecommerce.catalog.infrastructure.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProducts() {
        // TODO: mapear List<ProductDocument> a List<Product>
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public List<Product> getProductsByIds(Collection<String> productIds) {
        // TODO: mapear List<ProductDocument> a List<Product>
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public void decrementStock(String productId, int quantity) {
        // TODO: validar stock disponible y persistir el decremento
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}
