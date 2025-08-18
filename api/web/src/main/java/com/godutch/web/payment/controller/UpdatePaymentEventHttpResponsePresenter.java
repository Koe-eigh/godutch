package com.godutch.web.payment.controller;

import org.springframework.http.ResponseEntity;

import com.godutch.app.payment.api.UpdatePaymentEventOutputPort;
import com.godutch.payment.PaymentEvent;
import com.godutch.web.payment.dto.PaymentEventResponse;

public class UpdatePaymentEventHttpResponsePresenter implements UpdatePaymentEventOutputPort {
    private PaymentEvent paymentEvent;
    @Override
    public void result(PaymentEvent paymentEvent) {
        this.paymentEvent = paymentEvent;
    }

    public ResponseEntity<PaymentEventResponse> present() {
        if (paymentEvent == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(new PaymentEventResponse(
                paymentEvent.getId().getValue(),
                paymentEvent.getGroupId().getValue(),
                paymentEvent.getTitle(),
                paymentEvent.getMemo(),
                paymentEvent.getCredits().stream()
                        .map(c -> new PaymentEventResponse.CreditOrDebit(c.getCreditorId().getId(), c.getAmount().toString()))
                        .toList(),
                paymentEvent.getDebits().stream()
                        .map(d -> new PaymentEventResponse.CreditOrDebit(d.getDebtorId().getId(), d.getAmount().toString()))
                        .toList()));
    }
}
