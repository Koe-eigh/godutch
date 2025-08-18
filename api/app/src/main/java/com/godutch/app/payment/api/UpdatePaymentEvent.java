package com.godutch.app.payment.api;

public interface UpdatePaymentEvent {
    public void execute(UpdatePaymentEventInputPort input, UpdatePaymentEventOutputPort output);
}
