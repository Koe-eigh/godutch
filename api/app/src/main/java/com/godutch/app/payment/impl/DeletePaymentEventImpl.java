package com.godutch.app.payment.impl;

import com.godutch.app.payment.api.DeletePaymentEvent;
import com.godutch.app.payment.api.DeletePaymentEventInputPort;
import com.godutch.app.payment.api.DeletePaymentEventOutputPort;
import com.godutch.payment.PaymentEventId;
import com.godutch.payment.PaymentEventRepository;

public class DeletePaymentEventImpl implements DeletePaymentEvent {
    private final PaymentEventRepository paymentEventRepository;

    public DeletePaymentEventImpl(PaymentEventRepository paymentEventRepository) {
        this.paymentEventRepository = paymentEventRepository;
    }

    @Override
    public void execute(DeletePaymentEventInputPort input, DeletePaymentEventOutputPort output) {
        PaymentEventId paymentEventId = input.paymentEventId();
        paymentEventRepository.findById(paymentEventId).ifPresentOrElse(
            paymentEvent -> {
                paymentEventRepository.deleteById(paymentEventId);
                output.result(paymentEvent);
            },
            () -> output.result(null)
        );
    }
}
