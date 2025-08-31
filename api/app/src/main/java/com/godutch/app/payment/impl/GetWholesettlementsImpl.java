package com.godutch.app.payment.impl;

import java.util.List;

import com.godutch.app.payment.api.GetWholeSettlements;
import com.godutch.app.payment.api.GetWholeSettlementsInputPort;
import com.godutch.app.payment.api.GetWholeSettlementsOutputPort;
import com.godutch.payment.PaymentEventRepository;
import com.godutch.payment.PaymentEventService;

public class GetWholesettlementsImpl implements GetWholeSettlements {

    private final PaymentEventRepository paymentEventRepository;
    private final PaymentEventService paymentEventService;

    public GetWholesettlementsImpl(
            PaymentEventRepository paymentEventRepository, PaymentEventService paymentEventService) {
        this.paymentEventRepository = paymentEventRepository;
        this.paymentEventService = paymentEventService;
    }

    @Override
    public void execute(GetWholeSettlementsInputPort input, GetWholeSettlementsOutputPort output) {
        var groupId = input.groupId();
        var paymentEvents = paymentEventRepository.findAllBy(groupId).get();
        if (paymentEvents.isEmpty()) {
            output.output(List.of());
            return;
        }
        var settlements = paymentEventService.calculateSettlement(paymentEvents);
        output.output(settlements);
    }
}
