package com.godutch.app.payment.api;

import com.godutch.payment.PaymentEvent;

public interface UpdatePaymentEventInputPort {
    public PaymentEvent paymentEvent();
}
