package com.godutch.web.payment.validator.impl;

import com.godutch.web.payment.dto.PaymentEventRequest;
import com.godutch.web.payment.validator.api.ValidPaymentEventRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PaymentEventRequestValidator implements ConstraintValidator<ValidPaymentEventRequest, PaymentEventRequest> {

    @Override
    public boolean isValid(PaymentEventRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true; // Null requests are valid, handled by @NotNull
        }

        // Validate title
        if (request.getTitle() == null || request.getTitle().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("タイトルは必須です").addPropertyNode("title").addConstraintViolation();
            return false;
        }

        // Validate creditors
        if (request.getCreditors() == null || request.getCreditors().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("債権者は必須です").addPropertyNode("creditors").addConstraintViolation();
            return false;
        }

        // Validate debtors
        if (request.getDebtors() == null || request.getDebtors().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("債務者は必須です").addPropertyNode("debtors").addConstraintViolation();
            return false;
        }

        // 立替金額と債務者の金額が一致するかチェック
        double totalCredit = request.getCreditors().stream()
                .mapToDouble(creditor -> Double.parseDouble(creditor.getAmount()))
                .sum();
        double totalDebit = request.getDebtors().stream()
                .mapToDouble(debtor -> Double.parseDouble(debtor.getAmount()))
                .sum();
        if (totalCredit != totalDebit) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("立替金額と債務者の金額が一致しません")
                    .addPropertyNode("creditors").addConstraintViolation();
            return false;
        }

        return true; // All validations passed
    }
    
}
