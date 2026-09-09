package com.ribolost.pruebastecnicas.kataecommerce.shared.validation;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountBreakdown;
import com.ribolost.pruebastecnicas.kataecommerce.shared.error.InvalidResponseException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ResponseValidator es el último punto de control antes de devolver al
 * cliente una respuesta construida por el dominio (DiscountService), y su
 * contrato real (arquitectura.md ADR-17: Money/Percentage se validan por
 * Bean Validation, no como Value Objects propios) es: si la respuesta viola
 * su propio contrato anotado (@PositiveOrZero, @DecimalMax, etc.), lanza
 * InvalidResponseException; si lo cumple, la devuelve intacta. Se usa un
 * Validator real de Bean Validation (no un mock) porque lo que se protege
 * aquí es precisamente la traducción de violaciones -> InvalidResponseException,
 * que solo se puede observar con una validación real.
 */
class ResponseValidatorTest {

    private static ValidatorFactory validatorFactory;
    private static Validator jakartaValidator;
    private ResponseValidator responseValidator;

    @BeforeAll
    static void setUpValidatorFactory() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        jakartaValidator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    private DiscountBreakdown validBreakdown() {
        return new DiscountBreakdown(new BigDecimal("5.00"), BigDecimal.ZERO, BigDecimal.ZERO,
                new BigDecimal("5.00"), new BigDecimal("0.1000"));
    }

    private DiscountBreakdown breakdownExceedingCap() {
        // effectiveDiscountPercentage > 0.35 viola @DecimalMax de DiscountBreakdown.
        return new DiscountBreakdown(new BigDecimal("50.00"), BigDecimal.ZERO, BigDecimal.ZERO,
                new BigDecimal("50.00"), new BigDecimal("0.5000"));
    }

    @Test
    void returnsTheSameResponseUnchangedWhenItSatisfiesItsOwnContract() {
        responseValidator = new ResponseValidator(jakartaValidator);
        DiscountBreakdown breakdown = validBreakdown();

        DiscountBreakdown result = responseValidator.validate(breakdown);

        assertThat(result).isSameAs(breakdown);
    }

    @Test
    void throwsInvalidResponseExceptionWhenResponseViolatesItsOwnContract() {
        responseValidator = new ResponseValidator(jakartaValidator);
        DiscountBreakdown breakdown = breakdownExceedingCap();

        assertThatThrownBy(() -> responseValidator.validate(breakdown))
                .isInstanceOf(InvalidResponseException.class)
                .hasMessageContaining("effectiveDiscountPercentage");
    }

    @Test
    void validateAllReturnsTheListUnchangedWhenEveryElementIsValid() {
        responseValidator = new ResponseValidator(jakartaValidator);
        List<DiscountBreakdown> breakdowns = List.of(validBreakdown(), validBreakdown());

        List<DiscountBreakdown> result = responseValidator.validateAll(breakdowns);

        assertThat(result).isSameAs(breakdowns);
    }

    @Test
    void validateAllThrowsWhenAnyElementInTheListViolatesItsContract() {
        responseValidator = new ResponseValidator(jakartaValidator);
        List<DiscountBreakdown> breakdowns = List.of(validBreakdown(), breakdownExceedingCap());

        assertThatThrownBy(() -> responseValidator.validateAll(breakdowns))
                .isInstanceOf(InvalidResponseException.class);
    }
}
