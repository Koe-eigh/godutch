package com.godutch.web.payment.controller;

import com.godutch.app.payment.api.UpdatePaymentEventInputPort;
import com.godutch.group.GroupId;
import com.godutch.group.MemberId;
import com.godutch.payment.Credit;
import com.godutch.payment.Debit;
import com.godutch.payment.PaymentEvent;
import com.godutch.payment.PaymentEventId;
import com.godutch.web.payment.dto.PaymentEventRequest;
import com.godutch.common.Amount;

public class UpdatePaymentEventHttpRequestHandler implements UpdatePaymentEventInputPort {
    private final PaymentEvent paymentEvent;

    public UpdatePaymentEventHttpRequestHandler(String groupId, String paymentEventId, PaymentEventRequest paymentEventRequest) {
        this.paymentEvent = new PaymentEvent(
            new PaymentEventId(paymentEventId),
            new GroupId(groupId),
            paymentEventRequest.getTitle(),
            paymentEventRequest.getMemo(),
            paymentEventRequest.getCreditors().stream()
                .map(credit -> new Credit(new MemberId(credit.getMemberId()), new Amount(Long.parseLong(credit.getAmount()))))
                .toList(),
            paymentEventRequest.getDebtors().stream()
                .map(debit -> new Debit(new MemberId(debit.getMemberId()), new Amount(Long.parseLong(debit.getAmount()))))
                .toList()
        );
    }

    @Override
    public PaymentEvent paymentEvent() {
        return this.paymentEvent;
    }
    
}
