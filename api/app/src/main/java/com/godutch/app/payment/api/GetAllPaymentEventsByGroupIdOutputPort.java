package com.godutch.app.payment.api;

import java.util.List;

import com.godutch.payment.PaymentEvent;

public interface GetAllPaymentEventsByGroupIdOutputPort {
    public void result(List<PaymentEvent> paymentEvents, int page, int perPage, long total);
}
