package com.godutch.app.payment.api;

import com.godutch.payment.PaymentEvent;

public interface AddPaymentEventOutputPort {
    public void result(PaymentEvent paymentEvent);
}
