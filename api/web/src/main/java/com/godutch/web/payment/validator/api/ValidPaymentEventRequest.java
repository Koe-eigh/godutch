package com.godutch.web.payment.validator.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.godutch.web.payment.validator.impl.PaymentEventRequestValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { PaymentEventRequestValidator.class })
public @interface ValidPaymentEventRequest {
    String message() default "不正な支払いイベントです";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
