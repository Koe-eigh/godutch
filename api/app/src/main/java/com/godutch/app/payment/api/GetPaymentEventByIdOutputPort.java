package com.godutch.app.payment.api;

import com.godutch.payment.PaymentEvent;

public interface GetPaymentEventByIdOutputPort {
    public void result(PaymentEvent paymentEvent);
}
