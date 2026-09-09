package com.ribolost.pruebastecnicas.kataecommerce.catalog.application;

import com.ribolost.pruebastecnicas.kataecommerce.catalog.domain.Product;
import com.ribolost.pruebastecnicas.kataecommerce.catalog.infrastructure.mapper.ProductMapper;
import com.ribolost.pruebastecnicas.kataecommerce.catalog.infrastructure.repository.ProductDocument;
import com.ribolost.pruebastecnicas.kataecommerce.catalog.infrastructure.repository.ProductRepository;
import com.ribolost.pruebastecnicas.kataecommerce.shared.error.InsufficientStockException;
import com.ribolost.pruebastecnicas.kataecommerce.shared.error.InvalidCartItemException;
import com.ribolost.pruebastecnicas.kataecommerce.shared.error.ProductNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public List<Product> getProducts() {
        return productMapper.toDomainList(productRepository.findAll());
    }

    public Product getProductById(String productId) {
        ProductDocument document = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado: " + productId));
        return productMapper.toDomain(document);
    }

    public List<Product> getProductsByIds(Collection<String> productIds) {
        return productMapper.toDomainList(productRepository.findAllByIdIn(productIds));
    }


    public void ensureAvailability(Map<String, Integer> quantitiesByProductId) {
        Map<String, ProductDocument> documentsById = indexById(quantitiesByProductId.keySet());
        for (Map.Entry<String, Integer> entry : quantitiesByProductId.entrySet()) {
            Product product = requireProduct(documentsById, entry.getKey());
            if (product.getStock() < entry.getValue()) {
                throw new InsufficientStockException(
                        "Stock insuficiente para el producto " + entry.getKey() + ": disponible "
                                + product.getStock() + ", solicitado " + entry.getValue());
            }
        }
    }


    public void decrementStock(Map<String, Integer> quantitiesByProductId) {
        Map<String, ProductDocument> documentsById = indexById(quantitiesByProductId.keySet());
        List<Product> updatedProducts = new ArrayList<>(quantitiesByProductId.size());

        for (Map.Entry<String, Integer> entry : quantitiesByProductId.entrySet()) {
            Product product = requireProduct(documentsById, entry.getKey());
            product.decreaseStock(entry.getValue());
            updatedProducts.add(product);
        }

        productRepository.saveAll(productMapper.toDocumentList(updatedProducts));
    }

    private Product requireProduct(Map<String, ProductDocument> documentsById, String productId) {
        ProductDocument document = documentsById.get(productId);
        if (document == null) {
            throw new InvalidCartItemException("El carrito referencia un producto inexistente: " + productId);
        }
        return productMapper.toDomain(document);
    }

    private Map<String, ProductDocument> indexById(Collection<String> productIds) {
        return productRepository.findAllByIdIn(productIds).stream()
                .collect(Collectors.toMap(ProductDocument::getId, document -> document));
    }
}
