package com.godutch.app.payment.api;

public interface DeletePaymentEvent {
    public void execute(DeletePaymentEventInputPort input, DeletePaymentEventOutputPort output);
}
