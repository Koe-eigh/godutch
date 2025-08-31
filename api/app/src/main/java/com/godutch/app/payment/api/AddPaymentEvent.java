package com.godutch.app.payment.api;

public interface AddPaymentEvent {
    public void execute(AddPaymentEventInputPort input, AddPaymentEventOutputPort output);
}
