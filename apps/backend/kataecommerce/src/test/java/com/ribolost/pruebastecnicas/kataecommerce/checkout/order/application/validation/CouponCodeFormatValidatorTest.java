package com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RN-09 (arquitectura.md): formato de cupón inválido, evaluado en el borde de
 * la API sobre CartRequest/DiscountCalculationRequest antes de llegar a
 * cualquier caso de uso. El validador no usa el ConstraintValidatorContext
 * (ver CouponCodeFormatValidator.initialize, que no inicializa estado), por
 * lo que puede probarse como POJO puro, sin @WebMvcTest ni contexto Spring.
 */
class CouponCodeFormatValidatorTest {

    private final CouponCodeFormatValidator validator = new CouponCodeFormatValidator();

    @Test
    void treatsNullAsValidBecauseCouponCodeIsOptional() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    void treatsBlankAsValidBecauseCouponCodeIsOptional() {
        assertThat(validator.isValid("   ", null)).isTrue();
    }

    @Test
    void acceptsAlphanumericCodeWithinFourToTwentyCharacters() {
        assertThat(validator.isValid("WELCOME2026", null)).isTrue();
    }

    @Test
    void acceptsCodeAtTheMinimumBoundaryOfFourCharacters() {
        assertThat(validator.isValid("AB12", null)).isTrue();
    }

    @Test
    void acceptsCodeAtTheMaximumBoundaryOfTwentyCharacters() {
        assertThat(validator.isValid("A1234567890123456789", null)).isTrue();
    }

    @Test
    void rejectsCodeShorterThanFourCharacters() {
        assertThat(validator.isValid("ab", null)).isFalse();
    }

    @Test
    void rejectsCodeLongerThanTwentyCharacters() {
        assertThat(validator.isValid("A12345678901234567890", null)).isFalse();
    }

    @Test
    void rejectsCodeWithNonAlphanumericCharacters() {
        assertThat(validator.isValid("WELCOME-2026", null)).isFalse();
        assertThat(validator.isValid("WELCOME 2026", null)).isFalse();
    }
}
