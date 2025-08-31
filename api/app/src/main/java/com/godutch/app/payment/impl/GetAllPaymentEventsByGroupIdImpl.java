package com.godutch.app.payment.impl;

import java.util.List;

import com.godutch.app.payment.api.GetAllPaymentEventsByGroupId;
import com.godutch.app.payment.api.GetAllPaymentEventsByGroupIdInputPort;
import com.godutch.app.payment.api.GetAllPaymentEventsByGroupIdOutputPort;
import com.godutch.payment.PaymentEventRepository;

public class GetAllPaymentEventsByGroupIdImpl implements GetAllPaymentEventsByGroupId {

    private final PaymentEventRepository paymentEventRepository;

    public GetAllPaymentEventsByGroupIdImpl(PaymentEventRepository paymentEventRepository) {
        this.paymentEventRepository = paymentEventRepository;
    }

    @Override
    public void execute(GetAllPaymentEventsByGroupIdInputPort input, GetAllPaymentEventsByGroupIdOutputPort output) {
        var groupId = input.groupId();
        paymentEventRepository.findAllBy(groupId)
            .ifPresentOrElse(
                paymentEvents -> output.result(paymentEvents),
                () -> output.result(List.of()) // Return an empty list if no events found
            );
    }
}
