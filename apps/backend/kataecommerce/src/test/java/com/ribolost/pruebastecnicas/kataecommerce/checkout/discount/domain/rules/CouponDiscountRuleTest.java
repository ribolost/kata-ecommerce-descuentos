package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.rules;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.Coupon;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountContext;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition.DiscountType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RN-03 (15% adicional con cupón activo y no usado que coincide con el
 * código solicitado) y RN-05 (cupón no registrado, inactivo o ya usado: la
 * regla simplemente no se aplica y el resto del cálculo continúa con
 * normalidad, sin lanzar excepción -- ver CouponDiscountRule.isCouponApplicable).
 */
class CouponDiscountRuleTest {

    private static final BigDecimal RULE_VALUE = new BigDecimal("0.15");

    private DiscountContext contextWithAmount(String amount, String requestedCode) {
        return new DiscountContext(new BigDecimal(amount), Collections.emptyMap(), requestedCode);
    }

    @Test
    void appliesFifteenPercentWhenCouponIsActiveUnusedAndCodeMatches() {
        Coupon coupon = new Coupon("WELCOME2026", true, false);
        DiscountContext context = contextWithAmount("150.00", "WELCOME2026");

        new CouponDiscountRule(RULE_VALUE, coupon, null).apply(context);

        assertThat(context.getCouponDiscountAmount()).isEqualByComparingTo("22.50");
        assertThat(context.getTotalDiscountAmount()).isEqualByComparingTo("22.50");
        assertThat(context.getCurrentAmount()).isEqualByComparingTo("127.50");
        assertThat(context.getAppliedDiscounts()).containsExactly(DiscountType.COUPON);
    }

    @Test
    void matchingIsCaseInsensitiveAndTrimmed() {
        Coupon coupon = new Coupon("WELCOME2026", true, false);
        DiscountContext context = contextWithAmount("100.00", "  welcome2026  ");

        new CouponDiscountRule(RULE_VALUE, coupon, null).apply(context);

        assertThat(context.getAppliedDiscounts()).containsExactly(DiscountType.COUPON);
    }

    @Test
    void doesNotApplyWhenNoCouponIsConfiguredForThePolicy() {
        DiscountContext context = contextWithAmount("100.00", "WELCOME2026");

        new CouponDiscountRule(RULE_VALUE, null, null).apply(context);

        assertThat(context.getCouponDiscountAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(context.getAppliedDiscounts()).isEmpty();
    }

    @Test
    void doesNotApplyWhenRequestedCodeIsNullOrBlank() {
        Coupon coupon = new Coupon("WELCOME2026", true, false);

        DiscountContext nullCodeContext = contextWithAmount("100.00", null);
        new CouponDiscountRule(RULE_VALUE, coupon, null).apply(nullCodeContext);
        assertThat(nullCodeContext.getAppliedDiscounts()).isEmpty();

        DiscountContext blankCodeContext = contextWithAmount("100.00", "   ");
        new CouponDiscountRule(RULE_VALUE, coupon, null).apply(blankCodeContext);
        assertThat(blankCodeContext.getAppliedDiscounts()).isEmpty();
    }

    @Test
    void doesNotApplyWhenCouponIsNotRegisteredUnderRequestedCode() {
        Coupon coupon = new Coupon("WELCOME2026", true, false);
        DiscountContext context = contextWithAmount("100.00", "NOTREGISTERED");

        new CouponDiscountRule(RULE_VALUE, coupon, null).apply(context);

        assertThat(context.getCouponDiscountAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(context.getAppliedDiscounts()).isEmpty();
    }

    @Test
    void doesNotApplyWhenCouponIsInactive() {
        Coupon inactiveCoupon = new Coupon("WELCOME2026", false, false);
        DiscountContext context = contextWithAmount("100.00", "WELCOME2026");

        new CouponDiscountRule(RULE_VALUE, inactiveCoupon, null).apply(context);

        assertThat(context.getCouponDiscountAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(context.getAppliedDiscounts()).isEmpty();
    }

    @Test
    void doesNotApplyWhenCouponWasAlreadyUsed() {
        Coupon usedCoupon = new Coupon("WELCOME2026", true, true);
        DiscountContext context = contextWithAmount("100.00", "WELCOME2026");

        new CouponDiscountRule(RULE_VALUE, usedCoupon, null).apply(context);

        assertThat(context.getCouponDiscountAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(context.getAppliedDiscounts()).isEmpty();
    }

    @Test
    void invokesNextRuleInChainRegardlessOfWhetherCouponDiscountApplied() {
        DiscountContext context = contextWithAmount("100.00", "NOTREGISTERED");
        java.util.concurrent.atomic.AtomicBoolean nextInvoked = new java.util.concurrent.atomic.AtomicBoolean(false);
        DiscountRule next = ctx -> {
            nextInvoked.set(true);
            return ctx;
        };

        new CouponDiscountRule(RULE_VALUE, new Coupon("WELCOME2026", true, false), next).apply(context);

        assertThat(nextInvoked).isTrue();
    }
}
