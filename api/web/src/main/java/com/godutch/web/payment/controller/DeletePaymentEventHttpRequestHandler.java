package com.godutch.web.payment.controller;

import com.godutch.app.payment.api.DeletePaymentEventInputPort;
import com.godutch.payment.PaymentEventId;

public class DeletePaymentEventHttpRequestHandler implements DeletePaymentEventInputPort {

    private final PaymentEventId paymentEventId;

    public DeletePaymentEventHttpRequestHandler(String paymentEventId) {
        this.paymentEventId = new PaymentEventId(paymentEventId);
    }

    @Override
    public PaymentEventId paymentEventId() {
        return paymentEventId;
    }
    
}
