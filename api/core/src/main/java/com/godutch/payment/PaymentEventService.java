package com.godutch.payment;

import java.util.List;

public interface PaymentEventService {
    List<Settlement> calculateSettlement(List<PaymentEvent> paymentEvents);
}
