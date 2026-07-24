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
        var paymentEvents = paymentEventRepository
            .findAllBy(groupId, input.page(), input.perPage())
            .orElse(List.of());
        var total = paymentEventRepository.countBy(groupId);
        output.result(paymentEvents, input.page(), input.perPage(), total);
    }
}
