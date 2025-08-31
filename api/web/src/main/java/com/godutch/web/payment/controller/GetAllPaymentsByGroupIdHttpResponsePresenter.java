package com.godutch.web.payment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.godutch.app.payment.api.GetAllPaymentEventsByGroupIdOutputPort;
import com.godutch.payment.PaymentEvent;
import com.godutch.web.payment.dto.PaymentEventResponse;

public class GetAllPaymentsByGroupIdHttpResponsePresenter implements GetAllPaymentEventsByGroupIdOutputPort {
    private List<PaymentEvent> paymentEvents;

    @Override
    public void result(List<PaymentEvent> paymentEvents) {
        this.paymentEvents = paymentEvents;
    }

    public ResponseEntity<List<PaymentEventResponse>> present() {
        List<PaymentEventResponse> responses = paymentEvents.stream()
                .map(paymentEvent -> new PaymentEventResponse(
                        paymentEvent.getId().getValue(),
                        paymentEvent.getGroupId().getValue(),
                        paymentEvent.getTitle(),
                        paymentEvent.getMemo(),
                        paymentEvent.getCredits().stream()
                                .map(c -> new PaymentEventResponse.CreditOrDebit(c.getCreditorId().getId(),
                                        c.getAmount().toString()))
                                .toList(),
                        paymentEvent.getDebits().stream()
                                .map(d -> new PaymentEventResponse.CreditOrDebit(d.getDebtorId().getId(),
                                        d.getAmount().toString()))
                                .toList()))
                .toList();
        return ResponseEntity.ok(responses);
    }
}
