package com.godutch.web.payment.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentEventPageResponse(
        List<PaymentEventResponse> events,
        int page,
        @JsonProperty("per_page") int perPage,
        @JsonProperty("last_page") int lastPage,
        long total) {
}
