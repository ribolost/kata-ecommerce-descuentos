package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.rules;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountContext;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition.DiscountType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RN-04 (arquitectura.md): el descuento acumulado nunca supera el
 * maxPercentage configurado (0.35 en el seed real, DiscountPolicySeeder);
 * si lo excede, se trunca EXACTAMENTE a ese máximo y se marca TOTAL en
 * appliedDiscounts. Caso de prueba obligatorio: "Tope del 35% de descuento
 * superado". Se cubre explícitamente el límite exacto (no debe truncar, la
 * condición del código es estrictamente "mayor que") y el caso por encima
 * del límite (criterio de aceptación de arquitectura.md: subtotal $200,
 * descuento acumulado 40% -> se trunca a 35%, total a pagar $130).
 */
class MaxDiscountCapRuleTest {

    private static final BigDecimal MAX_PERCENTAGE = new BigDecimal("0.35");

    private DiscountContext contextWithSubtotalAndAccumulatedDiscount(String subtotal, String accumulatedDiscount) {
        DiscountContext context = new DiscountContext(new BigDecimal(subtotal), Collections.emptyMap(), null);
        // Simula el resultado que dejarían las reglas anteriores de la cadena
        // (CATEGORY/VOLUME/COUPON) antes de que MaxDiscountCapRule sea invocada.
        context.setTotalDiscountAmount(new BigDecimal(accumulatedDiscount));
        context.setCurrentAmount(context.getSubtotal().subtract(new BigDecimal(accumulatedDiscount)));
        return context;
    }

    @Test
    void truncatesAccumulatedDiscountToExactlyThirtyFivePercentWhenItExceedsTheCap() {
        // Criterio de aceptación documentado: subtotal $200; 40% acumulado ($80) -> se trunca a 35% ($70).
        DiscountContext context = contextWithSubtotalAndAccumulatedDiscount("200.00", "80.00");

        new MaxDiscountCapRule(MAX_PERCENTAGE).apply(context);

        assertThat(context.getTotalDiscountAmount()).isEqualByComparingTo("70.00");
        assertThat(context.getSubtotal().subtract(context.getTotalDiscountAmount())).isEqualByComparingTo("130.00");
        assertThat(context.getAppliedDiscounts()).containsExactly(DiscountType.TOTAL);
    }

    @Test
    void addsExcessBackToCurrentAmountWhenTruncating() {
        DiscountContext context = contextWithSubtotalAndAccumulatedDiscount("200.00", "80.00");

        new MaxDiscountCapRule(MAX_PERCENTAGE).apply(context);

        // currentAmount antes del tope era 200 - 80 = 120; el exceso truncado (10) se devuelve.
        assertThat(context.getCurrentAmount()).isEqualByComparingTo("130.00");
    }

    @Test
    void doesNotTruncateAndDoesNotMarkTotalWhenAccumulatedDiscountIsExactlyThirtyFivePercent() {
        DiscountContext context = contextWithSubtotalAndAccumulatedDiscount("100.00", "35.00");

        new MaxDiscountCapRule(MAX_PERCENTAGE).apply(context);

        assertThat(context.getTotalDiscountAmount()).isEqualByComparingTo("35.00");
        assertThat(context.getAppliedDiscounts()).isEmpty();
    }

    @Test
    void doesNotTruncateWhenAccumulatedDiscountIsBelowThirtyFivePercent() {
        DiscountContext context = contextWithSubtotalAndAccumulatedDiscount("100.00", "20.00");

        new MaxDiscountCapRule(MAX_PERCENTAGE).apply(context);

        assertThat(context.getTotalDiscountAmount()).isEqualByComparingTo("20.00");
        assertThat(context.getAppliedDiscounts()).isEmpty();
    }

    @Test
    void isAlwaysTheTerminalLinkOfTheChainAndReturnsTheSameContext() {
        DiscountContext context = contextWithSubtotalAndAccumulatedDiscount("100.00", "10.00");

        DiscountContext result = new MaxDiscountCapRule(MAX_PERCENTAGE).apply(context);

        assertThat(result).isSameAs(context);
    }
}
