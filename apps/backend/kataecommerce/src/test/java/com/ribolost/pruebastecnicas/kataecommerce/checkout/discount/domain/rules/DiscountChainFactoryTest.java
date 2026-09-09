package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.rules;

import com.ribolost.pruebastecnicas.kataecommerce.catalog.domain.Product;
import com.ribolost.pruebastecnicas.kataecommerce.catalog.domain.Product.ProductCategory;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.Coupon;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountContext;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountPolicy;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition.DiscountType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ADR-03 (Chain of Responsibility + Factory) y ADR-04 (política configurable
 * como Aggregate persistido): DiscountChainFactory debe construir la cadena
 * respetando el campo "order" de cada DiscountRuleDefinition -- no el orden
 * en que las reglas aparecen en la lista -- y dejar siempre a MaxDiscountCapRule
 * (DiscountType.TOTAL) como último eslabón, sin importar el "order" que tenga
 * asignado esa definición.
 */
class DiscountChainFactoryTest {

    private final DiscountChainFactory factory = new DiscountChainFactory();

    @Test
    void buildsChainRespectingOrderFieldRegardlessOfListOrder() {
        // La lista se entrega desordenada respecto a "order" y con TOTAL primero,
        // a propósito, para verificar que la fábrica reordena por "order" y coloca
        // el tope al final.
        DiscountRuleDefinition capDefinition = new DiscountRuleDefinition(4, DiscountType.TOTAL, new BigDecimal("0.35"), null);
        DiscountRuleDefinition couponDefinition = new DiscountRuleDefinition(3, DiscountType.COUPON, new BigDecimal("0.15"),
                new Coupon("WELCOME2026", true, false));
        DiscountRuleDefinition volumeDefinition = new DiscountRuleDefinition(2, DiscountType.VOLUME, new BigDecimal("0.05"), null);
        DiscountRuleDefinition categoryDefinition = new DiscountRuleDefinition(1, DiscountType.CATEGORY, new BigDecimal("0.10"), null);

        DiscountPolicy policy = new DiscountPolicy("policy-1",
                List.of(capDefinition, couponDefinition, volumeDefinition, categoryDefinition));

        DiscountRule chain = factory.buildChain(policy);

        Product techProduct = new Product("p1", "Laptop", "desc", new BigDecimal("1000.00"), ProductCategory.TECNOLOGIA, 10);
        Map<Product, Integer> items = new LinkedHashMap<>();
        items.put(techProduct, 1);
        DiscountContext context = new DiscountContext(new BigDecimal("1000.00"), items, "WELCOME2026");

        chain.apply(context);

        assertThat(context.getAppliedDiscounts())
                .containsExactly(DiscountType.CATEGORY, DiscountType.VOLUME, DiscountType.COUPON);
    }

    @Test
    void placesMaxDiscountCapRuleLastEvenWhenItsOrderValueIsTheSmallest() {
        // El "order" del tope es intencionalmente el más bajo: la fábrica debe
        // ignorarlo para el tope y aplicarlo siempre al final de la cadena.
        DiscountRuleDefinition capDefinition = new DiscountRuleDefinition(0, DiscountType.TOTAL, new BigDecimal("0.35"), null);
        DiscountRuleDefinition categoryDefinition = new DiscountRuleDefinition(1, DiscountType.CATEGORY, new BigDecimal("0.40"), null);

        DiscountPolicy policy = new DiscountPolicy("policy-2", List.of(categoryDefinition, capDefinition));

        DiscountRule chain = factory.buildChain(policy);

        Product techProduct = new Product("p1", "Laptop", "desc", new BigDecimal("100.00"), ProductCategory.TECNOLOGIA, 10);
        Map<Product, Integer> items = new LinkedHashMap<>();
        items.put(techProduct, 1);
        DiscountContext context = new DiscountContext(new BigDecimal("100.00"), items, null);

        chain.apply(context);

        // category=40% de 100 = 40, supera el tope de 35% (35) -> se trunca a 35 y se marca TOTAL al final.
        assertThat(context.getAppliedDiscounts()).containsExactly(DiscountType.CATEGORY, DiscountType.TOTAL);
        assertThat(context.getTotalDiscountAmount()).isEqualByComparingTo("35.00");
    }

    @Test
    void buildsChainWithoutCapRuleWhenPolicyDoesNotDefineOne() {
        DiscountRuleDefinition categoryDefinition = new DiscountRuleDefinition(1, DiscountType.CATEGORY, new BigDecimal("0.10"), null);
        DiscountPolicy policy = new DiscountPolicy("policy-3", List.of(categoryDefinition));

        DiscountRule chain = factory.buildChain(policy);

        assertThat(chain).isInstanceOf(CategoryDiscountRule.class);
        assertThat(((CategoryDiscountRule) chain).next()).isNull();
    }
}
