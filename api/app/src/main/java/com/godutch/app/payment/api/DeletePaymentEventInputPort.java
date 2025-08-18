package com.godutch.app.payment.api;

import com.godutch.payment.PaymentEventId;

public interface DeletePaymentEventInputPort {
    public PaymentEventId paymentEventId();
}
