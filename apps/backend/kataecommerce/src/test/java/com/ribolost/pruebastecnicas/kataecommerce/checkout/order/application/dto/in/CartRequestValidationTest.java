package com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.dto.in;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RN-08 (arquitectura.md): carrito vacío o con una línea que referencia un
 * producto inexistente o cantidad <= 0 -> 400 antes de cualquier cálculo, en
 * ambas operaciones (/discounts y /orders). RN-09: formato de cupón inválido.
 *
 * La arquitectura documenta EmptyCartException como la excepción de negocio
 * para carrito vacío/inválido (y GlobalExceptionHandler la mapea a 400), pero
 * el código real nunca la lanza explícitamente: el rechazo ocurre por Bean
 * Validation (@NotEmpty/@NotBlank/@Min) sobre CartRequest en el borde de la
 * API, resuelto por Spring como MethodArgumentNotValidException antes de
 * llegar a cualquier caso de uso. Esta discrepancia se documenta (no se
 * resuelve silenciosamente) y estas pruebas validan el comportamiento real:
 * las restricciones declaradas sobre el propio contrato de entrada, usando
 * un Validator real de Bean Validation sin necesidad de levantar contexto
 * Spring (no es una prueba de integración: no hay red, ni base de datos, ni
 * beans gestionados).
 */
class CartRequestValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    void rejectsCartWithNoItems() {
        CartRequest request = new CartRequest(List.of(), null);

        Set<ConstraintViolation<CartRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("items"));
    }

    @Test
    void rejectsCartItemReferencingBlankProductId() {
        CartRequest request = new CartRequest(List.of(new CartItemRequest("", 1)), null);

        Set<ConstraintViolation<CartRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void rejectsCartItemWithZeroOrNegativeQuantity() {
        CartRequest zeroQuantity = new CartRequest(List.of(new CartItemRequest("p1", 0)), null);
        CartRequest negativeQuantity = new CartRequest(List.of(new CartItemRequest("p1", -3)), null);

        assertThat(validator.validate(zeroQuantity)).isNotEmpty();
        assertThat(validator.validate(negativeQuantity)).isNotEmpty();
    }

    @Test
    void acceptsCartWithAtLeastOneValidItemAndNoCoupon() {
        CartRequest request = new CartRequest(List.of(new CartItemRequest("p1", 1)), null);

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void rejectsMalformedCouponCode() {
        CartRequest request = new CartRequest(List.of(new CartItemRequest("p1", 1)), "ab");

        Set<ConstraintViolation<CartRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("couponCode"));
    }
}
