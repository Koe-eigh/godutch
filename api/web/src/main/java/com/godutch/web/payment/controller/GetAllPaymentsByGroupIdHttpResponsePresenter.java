package com.godutch.web.payment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.godutch.app.payment.api.GetAllPaymentEventsByGroupIdOutputPort;
import com.godutch.payment.PaymentEvent;
import com.godutch.web.payment.dto.PaymentEventPageResponse;
import com.godutch.web.payment.dto.PaymentEventResponse;

public class GetAllPaymentsByGroupIdHttpResponsePresenter implements GetAllPaymentEventsByGroupIdOutputPort {
    private List<PaymentEvent> paymentEvents;
    private int page;
    private int perPage;
    private long total;

    @Override
    public void result(List<PaymentEvent> paymentEvents, int page, int perPage, long total) {
        this.paymentEvents = paymentEvents;
        this.page = page;
        this.perPage = perPage;
        this.total = total;
    }

    public ResponseEntity<PaymentEventPageResponse> present() {
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
        int lastPage = Math.max(1, (int) Math.ceil((double) total / perPage));
        return ResponseEntity.ok(
                new PaymentEventPageResponse(responses, page, perPage, lastPage, total));
    }
}
