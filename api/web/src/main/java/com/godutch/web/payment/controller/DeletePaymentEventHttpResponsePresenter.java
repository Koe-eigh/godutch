package com.godutch.web.payment.controller;

import org.springframework.http.ResponseEntity;

import com.godutch.app.payment.api.DeletePaymentEventOutputPort;
import com.godutch.payment.PaymentEvent;
import com.godutch.web.payment.dto.PaymentEventResponse;

public class DeletePaymentEventHttpResponsePresenter implements DeletePaymentEventOutputPort {

    private PaymentEvent paymentEvent;

    @Override
    public void result(PaymentEvent paymentEvent) {
        this.paymentEvent = paymentEvent;
    }

    public ResponseEntity<PaymentEventResponse> present() {
        if (paymentEvent == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
