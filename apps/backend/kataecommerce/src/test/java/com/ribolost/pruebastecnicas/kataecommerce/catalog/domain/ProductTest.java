package com.ribolost.pruebastecnicas.kataecommerce.catalog.domain;

import com.ribolost.pruebastecnicas.kataecommerce.catalog.domain.Product.ProductCategory;
import com.ribolost.pruebastecnicas.kataecommerce.shared.error.InsufficientStockException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Invariante de arquitectura.md 1.6/1.8: stock >= 0 tras decremento, "Product
 * (Aggregate Root): rechaza cualquier decremento que deje el stock en
 * negativo". Product.decreaseStock es la única operación de dominio que
 * modifica el stock.
 */
class ProductTest {

    private Product product(int stock) {
        return new Product("p1", "Laptop", "desc", new BigDecimal("100.00"), ProductCategory.TECNOLOGIA, stock);
    }

    @Test
    void decreasesStockByRequestedQuantityWhenStockIsSufficient() {
        Product product = product(10);

        product.decreaseStock(4);

        assertThat(product.getStock()).isEqualTo(6);
    }

    @Test
    void allowsDecreasingStockDownToExactlyZero() {
        Product product = product(5);

        product.decreaseStock(5);

        assertThat(product.getStock()).isZero();
    }

    @Test
    void rejectsDecrementThatWouldLeaveStockNegative() {
        Product product = product(1);

        assertThatThrownBy(() -> product.decreaseStock(2))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("p1")
                .hasMessageContaining("disponible 1")
                .hasMessageContaining("solicitado 2");
        // El stock no debe quedar modificado ante un decremento rechazado.
        assertThat(product.getStock()).isEqualTo(1);
    }

    @Test
    void rejectsZeroOrNegativeQuantityAsInvalidArgument() {
        Product product = product(10);

        assertThatThrownBy(() -> product.decreaseStock(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> product.decreaseStock(-1)).isInstanceOf(IllegalArgumentException.class);
        assertThat(product.getStock()).isEqualTo(10);
    }
}
