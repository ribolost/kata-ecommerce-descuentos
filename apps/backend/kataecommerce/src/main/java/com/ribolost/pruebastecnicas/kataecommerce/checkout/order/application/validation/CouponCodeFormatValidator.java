package com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CouponCodeFormatValidator implements ConstraintValidator<ValidCouponCode, String> {

    @Override
    public void initialize(ValidCouponCode constraintAnnotation) {
        // Sin estado que inicializar.
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // TODO: definir y aplicar el formato esperado del código de cupón (RN-09).
        return true;
    }
}
