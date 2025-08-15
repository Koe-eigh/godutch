package com.godutch.web.payment.controller;

import com.godutch.app.payment.api.GetPaymentEventByIdInputPort;
import com.godutch.payment.PaymentEventId;

public class GetPaymentEventByIdHttpRequestHandler implements GetPaymentEventByIdInputPort {
    private final PaymentEventId paymentEventId;

    public GetPaymentEventByIdHttpRequestHandler(String paymentEventId) {
        this.paymentEventId = new PaymentEventId(paymentEventId);
    }

    @Override
    public PaymentEventId paymentEventId() {
        return paymentEventId;
    }
}
