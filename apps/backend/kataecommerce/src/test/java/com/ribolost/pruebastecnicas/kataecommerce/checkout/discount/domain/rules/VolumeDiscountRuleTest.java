package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.rules;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountContext;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition.DiscountType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RN-02 (arquitectura.md): 5% adicional sobre el total acumulado cuando el
 * subtotal acumulado hasta ese punto de la cadena SUPERA $100 (umbral
 * estrictamente mayor, VolumeDiscountRule.VOLUME_THRESHOLD = 100,
 * comparingIf currentAmount.compareTo(100) > 0).
 */
class VolumeDiscountRuleTest {

    private static final BigDecimal RULE_VALUE = new BigDecimal("0.05");

    private DiscountContext contextWithCurrentAmount(String amount) {
        // El importe acumulado (currentAmount) es lo relevante para VolumeDiscountRule;
        // se usa como subtotal inicial porque el constructor inicializa currentAmount = subtotal,
        // simulando el resultado que dejaría CategoryDiscountRule en la cadena real.
        return new DiscountContext(new BigDecimal(amount), Collections.emptyMap(), null);
    }

    @Test
    void appliesFivePercentOverAccumulatedAmountWhenItExceedsOneHundred() {
        DiscountContext context = contextWithCurrentAmount("120.00");
        new VolumeDiscountRule(RULE_VALUE, null).apply(context);

        assertThat(context.getVolumeDiscountAmount()).isEqualByComparingTo("6.00");
        assertThat(context.getTotalDiscountAmount()).isEqualByComparingTo("6.00");
        assertThat(context.getCurrentAmount()).isEqualByComparingTo("114.00");
        assertThat(context.getAppliedDiscounts()).containsExactly(DiscountType.VOLUME);
    }

    @Test
    void doesNotApplyDiscountWhenAccumulatedAmountIsExactlyOneHundred() {
        DiscountContext context = contextWithCurrentAmount("100.00");
        new VolumeDiscountRule(RULE_VALUE, null).apply(context);

        assertThat(context.getVolumeDiscountAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(context.getAppliedDiscounts()).isEmpty();
    }

    @Test
    void doesNotApplyDiscountWhenAccumulatedAmountIsBelowOneHundred() {
        DiscountContext context = contextWithCurrentAmount("99.99");
        new VolumeDiscountRule(RULE_VALUE, null).apply(context);

        assertThat(context.getVolumeDiscountAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(context.getTotalDiscountAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(context.getAppliedDiscounts()).isEmpty();
    }

    @Test
    void invokesNextRuleInChainRegardlessOfWhetherVolumeDiscountApplied() {
        DiscountContext context = contextWithCurrentAmount("10.00");
        java.util.concurrent.atomic.AtomicBoolean nextInvoked = new java.util.concurrent.atomic.AtomicBoolean(false);
        DiscountRule next = ctx -> {
            nextInvoked.set(true);
            return ctx;
        };

        new VolumeDiscountRule(RULE_VALUE, next).apply(context);

        assertThat(nextInvoked).isTrue();
    }
}
