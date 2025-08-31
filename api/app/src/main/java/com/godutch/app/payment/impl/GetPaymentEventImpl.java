package com.godutch.app.payment.impl;

import com.godutch.app.payment.api.GetPaymentEventById;
import com.godutch.app.payment.api.GetPaymentEventByIdInputPort;
import com.godutch.app.payment.api.GetPaymentEventByIdOutputPort;
import com.godutch.payment.PaymentEventRepository;

public class GetPaymentEventImpl implements GetPaymentEventById {

    private final PaymentEventRepository paymentEventRepository;

    public GetPaymentEventImpl(PaymentEventRepository paymentEventRepository) {
        this.paymentEventRepository = paymentEventRepository;
    }

    @Override
    public void execute(GetPaymentEventByIdInputPort input, GetPaymentEventByIdOutputPort output) {
        paymentEventRepository.findById(input.paymentEventId())
            .ifPresentOrElse(
                paymentEvent -> output.result(paymentEvent),
                () -> {
                    output.result(null);
                }
            );;
    }
    
}
