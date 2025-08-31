package com.godutch.web.payment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.godutch.app.payment.api.AddPaymentEventOutputPort;
import com.godutch.payment.PaymentEvent;
import com.godutch.web.payment.dto.PaymentEventResponse;

public class AddPaymentEventHttpResponsePresenter implements AddPaymentEventOutputPort {
    private PaymentEvent paymentEvent;
    @Override
    public void result(PaymentEvent paymentEvent) {
        this.paymentEvent = paymentEvent;
    }

    public ResponseEntity<PaymentEventResponse> present() {
        PaymentEventResponse response = new PaymentEventResponse(
            paymentEvent.getId().getValue(),
            paymentEvent.getGroupId().getValue(),
            paymentEvent.getTitle(),
            paymentEvent.getMemo(),
            paymentEvent.getCredits().stream()
                .map(c -> new PaymentEventResponse.CreditOrDebit(c.getCreditorId().getId(), c.getAmount().toString()))
                .toList(),
            paymentEvent.getDebits().stream()
                .map(d -> new PaymentEventResponse.CreditOrDebit(d.getDebtorId().getId(), d.getAmount().toString()))
                .toList()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
