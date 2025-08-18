package com.godutch.app.payment.impl;

import com.godutch.app.payment.api.AddPaymentEvent;
import com.godutch.app.payment.api.AddPaymentEventInputPort;
import com.godutch.app.payment.api.AddPaymentEventOutputPort;
import com.godutch.payment.PaymentEventRepository;

public class AddPaymentEventImpl implements AddPaymentEvent {
    private final PaymentEventRepository paymentEventRepository;

    public AddPaymentEventImpl(PaymentEventRepository paymentEventRepository) {
        this.paymentEventRepository = paymentEventRepository;
    }

    @Override
    public void execute(AddPaymentEventInputPort input, AddPaymentEventOutputPort output) {
        var paymentEvent = paymentEventRepository.save(input.paymentEvent());
        output.result(paymentEvent);
    }
    
}
