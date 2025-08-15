package com.godutch.app.payment.api;

import com.godutch.payment.PaymentEvent;

public interface AddPaymentEventInputPort {
    public PaymentEvent paymentEvent();
}
