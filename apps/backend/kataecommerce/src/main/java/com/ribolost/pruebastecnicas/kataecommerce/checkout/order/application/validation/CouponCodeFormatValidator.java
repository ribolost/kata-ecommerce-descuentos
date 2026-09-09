package com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class CouponCodeFormatValidator implements ConstraintValidator<ValidCouponCode, String> {

    private static final Pattern COUPON_CODE_PATTERN = Pattern.compile("^[A-Za-z0-9]{4,20}$");

    @Override
    public void initialize(ValidCouponCode constraintAnnotation) {
        // Sin estado que inicializar.
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return COUPON_CODE_PATTERN.matcher(value.trim()).matches();
    }
}
