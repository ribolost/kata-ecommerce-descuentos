package com.ribolost.pruebastecnicas.kataecommerce.catalog.infrastructure.mapper;

import com.ribolost.pruebastecnicas.kataecommerce.catalog.domain.Product;
import com.ribolost.pruebastecnicas.kataecommerce.catalog.infrastructure.repository.ProductDocument;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

    public Product toDomain(ProductDocument document) {
        if (document == null) {
            return null;
        }
        return new Product(
                document.getId(),
                document.getName(),
                document.getDescription(),
                document.getUnitPrice(),
                document.getCategory(),
                document.getStock());
    }

    public ProductDocument toDocument(Product product) {
        if (product == null) {
            return null;
        }
        return new ProductDocument(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getUnitPrice(),
                product.getCategory(),
                product.getStock());
    }

    public List<Product> toDomainList(List<ProductDocument> documents) {
        return documents.stream().map(this::toDomain).toList();
    }

    public List<ProductDocument> toDocumentList(List<Product> products) {
        return products.stream().map(this::toDocument).toList();
    }
}
