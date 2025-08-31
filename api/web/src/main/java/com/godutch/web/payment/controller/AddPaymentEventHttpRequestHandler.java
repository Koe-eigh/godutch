package com.godutch.web.payment.controller;

import com.godutch.app.payment.api.AddPaymentEventInputPort;
import com.godutch.common.Amount;
import com.godutch.group.GroupId;
import com.godutch.group.MemberId;
import com.godutch.payment.Credit;
import com.godutch.payment.Debit;
import com.godutch.payment.PaymentEvent;
import com.godutch.web.payment.dto.PaymentEventRequest;

public class AddPaymentEventHttpRequestHandler implements AddPaymentEventInputPort {
    private final PaymentEvent paymentEvent;

    public AddPaymentEventHttpRequestHandler(String groupId, PaymentEventRequest request) {
        this.paymentEvent = new PaymentEvent(
            new GroupId(groupId),
            request.getTitle(),
            request.getMemo(),
            request.getCreditors().stream()
                .map(c -> new Credit(new MemberId(c.getMemberId()), new Amount(Long.parseLong(c.getAmount()))))
                .toList(),
            request.getDebtors().stream()
                .map(d -> new Debit(new MemberId(d.getMemberId()), new Amount(Long.parseLong(d.getAmount()))))
                .toList()

        );
    }

    @Override
    public PaymentEvent paymentEvent() {
        return this.paymentEvent;
    }
}
