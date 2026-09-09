package com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CouponCodeFormatValidator.class)
public @interface ValidCouponCode {

    String message() default "Invalid coupon code format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
