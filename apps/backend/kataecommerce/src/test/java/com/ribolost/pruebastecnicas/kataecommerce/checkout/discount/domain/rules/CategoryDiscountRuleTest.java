package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.rules;

import com.ribolost.pruebastecnicas.kataecommerce.catalog.domain.Product;
import com.ribolost.pruebastecnicas.kataecommerce.catalog.domain.Product.ProductCategory;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountContext;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition.DiscountType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RN-01 (arquitectura.md): 10% sobre el precio de los productos de categoría
 * TECNOLOGIA. Cubre la condición de activación (al menos un producto
 * TECNOLOGIA), el cálculo exacto del criterio de aceptación documentado
 * (producto de $50 -> descuento $5) y la propagación de la cadena de
 * responsabilidad (Chain of Responsibility, ADR-03).
 */
class CategoryDiscountRuleTest {

    private static final BigDecimal RULE_VALUE = new BigDecimal("0.10");

    private Product product(String id, BigDecimal price, ProductCategory category) {
        return new Product(id, "Producto " + id, "desc", price, category, 100);
    }

    @Test
    void appliesTenPercentOverTechnologyItemsWhenCartContainsTechnologyProduct() {
        Product tech = product("p1", new BigDecimal("50.00"), ProductCategory.TECNOLOGIA);
        Map<Product, Integer> items = new LinkedHashMap<>();
        items.put(tech, 1);

        DiscountContext context = new DiscountContext(new BigDecimal("50.00"), items, null);
        new CategoryDiscountRule(RULE_VALUE, null).apply(context);

        assertThat(context.getCategoryDiscountAmount()).isEqualByComparingTo("5.00");
        assertThat(context.getTotalDiscountAmount()).isEqualByComparingTo("5.00");
        assertThat(context.getCurrentAmount()).isEqualByComparingTo("45.00");
        assertThat(context.getAppliedDiscounts()).containsExactly(DiscountType.CATEGORY);
    }

    @Test
    void ignoresNonTechnologyItemsWhenComputingCategoryDiscount() {
        Product tech = product("p1", new BigDecimal("50.00"), ProductCategory.TECNOLOGIA);
        Product other = product("p2", new BigDecimal("200.00"), ProductCategory.OTRO);
        Map<Product, Integer> items = new LinkedHashMap<>();
        items.put(tech, 1);
        items.put(other, 1);

        DiscountContext context = new DiscountContext(new BigDecimal("250.00"), items, null);
        new CategoryDiscountRule(RULE_VALUE, null).apply(context);

        assertThat(context.getCategoryDiscountAmount()).isEqualByComparingTo("5.00");
        assertThat(context.getCurrentAmount()).isEqualByComparingTo("245.00");
    }

    @Test
    void doesNotApplyDiscountAndDoesNotMarkTypeWhenCartHasNoTechnologyProduct() {
        Product other = product("p1", new BigDecimal("80.00"), ProductCategory.OTRO);
        Map<Product, Integer> items = new LinkedHashMap<>();
        items.put(other, 1);

        DiscountContext context = new DiscountContext(new BigDecimal("80.00"), items, null);
        new CategoryDiscountRule(RULE_VALUE, null).apply(context);

        assertThat(context.getCategoryDiscountAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(context.getTotalDiscountAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(context.getCurrentAmount()).isEqualByComparingTo("80.00");
        assertThat(context.getAppliedDiscounts()).isEmpty();
    }

    @Test
    void invokesNextRuleInChainRegardlessOfWhetherCategoryDiscountApplied() {
        Product other = product("p1", new BigDecimal("80.00"), ProductCategory.OTRO);
        Map<Product, Integer> items = new LinkedHashMap<>();
        items.put(other, 1);
        DiscountContext context = new DiscountContext(new BigDecimal("80.00"), items, null);

        AtomicBoolean nextInvoked = new AtomicBoolean(false);
        DiscountRule next = ctx -> {
            nextInvoked.set(true);
            return ctx;
        };

        new CategoryDiscountRule(RULE_VALUE, next).apply(context);

        assertThat(nextInvoked).isTrue();
    }

    @Test
    void multipliesTechnologySubtotalByRuleValueWhenSeveralTechnologyItemsArePresent() {
        Product tech1 = product("p1", new BigDecimal("30.00"), ProductCategory.TECNOLOGIA);
        Product tech2 = product("p2", new BigDecimal("20.00"), ProductCategory.TECNOLOGIA);
        Map<Product, Integer> items = new LinkedHashMap<>();
        items.put(tech1, 2);
        items.put(tech2, 1);

        DiscountContext context = new DiscountContext(new BigDecimal("80.00"), items, null);
        new CategoryDiscountRule(RULE_VALUE, null).apply(context);

        // technologySubtotal = 30*2 + 20*1 = 80.00 -> 10% = 8.00
        assertThat(context.getCategoryDiscountAmount()).isEqualByComparingTo("8.00");
    }
}
