package com.ribolost.pruebastecnicas.kataecommerce.catalog.application;

import com.ribolost.pruebastecnicas.kataecommerce.catalog.domain.Product.ProductCategory;
import com.ribolost.pruebastecnicas.kataecommerce.catalog.infrastructure.mapper.ProductMapper;
import com.ribolost.pruebastecnicas.kataecommerce.catalog.infrastructure.repository.ProductDocument;
import com.ribolost.pruebastecnicas.kataecommerce.catalog.infrastructure.repository.ProductRepository;
import com.ribolost.pruebastecnicas.kataecommerce.shared.error.InsufficientStockException;
import com.ribolost.pruebastecnicas.kataecommerce.shared.error.InvalidCartItemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * RN-06 (arquitectura.md): validación de stock, exclusiva de PlaceOrder. Se
 * usa el ProductMapper real (sin dependencias propias, mapeo puro sin
 * comportamiento) para no sobre-especificar el mock en cada prueba; lo que
 * se protege aquí es la lógica de ProductService, no el mapeo.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, new ProductMapper());
    }

    private ProductDocument document(String id, int stock) {
        return new ProductDocument(id, "Producto " + id, "desc", new BigDecimal("10.00"), ProductCategory.OTRO, stock);
    }

    @Test
    void ensureAvailabilityPassesSilentlyWhenStockCoversAllRequestedQuantities() {
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(document("p1", 5), document("p2", 3)));

        Map<String, Integer> requested = new LinkedHashMap<>();
        requested.put("p1", 2);
        requested.put("p2", 3);

        productService.ensureAvailability(requested);
        // No debe lanzar excepción alguna.
    }

    @Test
    void ensureAvailabilityRejectsWhenAnyLineExceedsAvailableStock() {
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(document("p1", 1)));

        Map<String, Integer> requested = Map.of("p1", 2);

        assertThatThrownBy(() -> productService.ensureAvailability(requested))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("p1")
                .hasMessageContaining("disponible 1")
                .hasMessageContaining("solicitado 2");
    }

    @Test
    void ensureAvailabilityRejectsWhenRequestedProductDoesNotExist() {
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of());

        Map<String, Integer> requested = Map.of("unknown", 1);

        assertThatThrownBy(() -> productService.ensureAvailability(requested))
                .isInstanceOf(InvalidCartItemException.class)
                .hasMessageContaining("unknown");
    }

    @Test
    void decrementStockPersistsUpdatedStockForEachRequestedProduct() {
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(document("p1", 5), document("p2", 3)));

        Map<String, Integer> requested = new LinkedHashMap<>();
        requested.put("p1", 2);
        requested.put("p2", 1);

        productService.decrementStock(requested);

        ArgumentCaptor<List<ProductDocument>> captor = ArgumentCaptor.forClass(List.class);
        verify(productRepository).saveAll(captor.capture());
        Map<String, Integer> stockById = new LinkedHashMap<>();
        captor.getValue().forEach(doc -> stockById.put(doc.getId(), doc.getStock()));

        assertThat(stockById).containsEntry("p1", 3).containsEntry("p2", 2);
    }

    @Test
    void decrementStockDoesNotPersistAnythingWhenAnyLineHasInsufficientStock() {
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(document("p1", 1)));

        Map<String, Integer> requested = Map.of("p1", 5);

        assertThatThrownBy(() -> productService.decrementStock(requested))
                .isInstanceOf(InsufficientStockException.class);

        verify(productRepository, never()).saveAll(any());
    }

    @Test
    void decrementStockRejectsWhenRequestedProductDoesNotExist() {
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of());

        Map<String, Integer> requested = Map.of("unknown", 1);

        assertThatThrownBy(() -> productService.decrementStock(requested))
                .isInstanceOf(InvalidCartItemException.class);
        verify(productRepository, never()).saveAll(any());
    }
}
