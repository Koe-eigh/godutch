package com.godutch.app.payment.api;

import com.godutch.payment.PaymentEventId;

public interface GetPaymentEventByIdInputPort {
    public PaymentEventId paymentEventId();
}
