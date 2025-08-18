package com.godutch.app.payment.impl;

import com.godutch.app.payment.api.UpdatePaymentEvent;
import com.godutch.app.payment.api.UpdatePaymentEventInputPort;
import com.godutch.app.payment.api.UpdatePaymentEventOutputPort;
import com.godutch.payment.PaymentEvent;
import com.godutch.payment.PaymentEventRepository;

public class UpdatePaymentEventImpl implements UpdatePaymentEvent {
    private final PaymentEventRepository paymentEventRepository;

    public UpdatePaymentEventImpl(PaymentEventRepository paymentEventRepository) {
        this.paymentEventRepository = paymentEventRepository;
    }

    @Override
    public void execute(UpdatePaymentEventInputPort input, UpdatePaymentEventOutputPort output) {
        PaymentEvent paymentEvent = input.paymentEvent();
        paymentEventRepository.save(paymentEvent);
        output.result(paymentEvent);
    }
    
}
