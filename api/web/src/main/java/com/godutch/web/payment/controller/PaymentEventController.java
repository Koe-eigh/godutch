package com.godutch.web.payment.controller;

import com.godutch.app.payment.api.AddPaymentEvent;
import com.godutch.app.payment.api.DeletePaymentEvent;
import com.godutch.app.payment.api.GetAllPaymentEventsByGroupId;
import com.godutch.app.payment.api.GetPaymentEventById;
import com.godutch.app.payment.api.UpdatePaymentEvent;
import com.godutch.web.payment.dto.PaymentEventRequest;
import com.godutch.web.payment.dto.PaymentEventResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups/{groupId}/events")
@Validated
public class PaymentEventController {
    private final AddPaymentEvent addPaymentEvent;
    private final GetAllPaymentEventsByGroupId getAllPaymentEventsByGroupId;
    private final GetPaymentEventById getPaymentEventById;
    private final UpdatePaymentEvent updatePaymentEvent;
    private final DeletePaymentEvent deletePaymentEvent;

    public PaymentEventController(AddPaymentEvent addPaymentEvent,
            GetAllPaymentEventsByGroupId getAllPaymentEventsByGroupId, GetPaymentEventById getPaymentEventById,
            UpdatePaymentEvent updatePaymentEvent,
            DeletePaymentEvent deletePaymentEvent) {
        this.addPaymentEvent = addPaymentEvent;
        this.getAllPaymentEventsByGroupId = getAllPaymentEventsByGroupId;
        this.getPaymentEventById = getPaymentEventById;
        this.updatePaymentEvent = updatePaymentEvent;
        this.deletePaymentEvent = deletePaymentEvent;
    }

    @GetMapping
    public ResponseEntity<List<PaymentEventResponse>> list(@PathVariable String groupId) {
        var input = new GetAllPaymentsByGroupIdHttpRequestHandler(groupId);
        var output = new GetAllPaymentsByGroupIdHttpResponsePresenter();
        getAllPaymentEventsByGroupId.execute(input, output);
        return output.present();
    }

    @PostMapping
    public ResponseEntity<PaymentEventResponse> create(@PathVariable String groupId,
            @RequestBody PaymentEventRequest request) {
        var input = new AddPaymentEventHttpRequestHandler(groupId, request);
        var output = new AddPaymentEventHttpResponsePresenter();
        addPaymentEvent.execute(input, output);
        return output.present();
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<PaymentEventResponse> get(@PathVariable String groupId, @PathVariable String eventId) {
        var input = new GetPaymentEventByIdHttpRequestHandler(eventId);
        var output = new GetPaymentEventByIdHttpResponsePresenter();
        this.getPaymentEventById.execute(input, output);
        return output.present();
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<PaymentEventResponse> update(@PathVariable String groupId,
            @PathVariable String eventId,
            @RequestBody PaymentEventRequest request) {
        var input = new UpdatePaymentEventHttpRequestHandler(groupId, eventId, request);
        var output = new UpdatePaymentEventHttpResponsePresenter();
        this.updatePaymentEvent.execute(input, output);
        return output.present();
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<PaymentEventResponse> delete(@PathVariable String groupId, @PathVariable String eventId) {
        var input = new DeletePaymentEventHttpRequestHandler(eventId);
        var output = new DeletePaymentEventHttpResponsePresenter();
        this.deletePaymentEvent.execute(input, output);
        return output.present();
    }
}
