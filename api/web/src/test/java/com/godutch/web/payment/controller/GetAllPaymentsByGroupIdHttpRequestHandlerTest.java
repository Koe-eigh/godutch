package com.godutch.web.payment.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class GetAllPaymentsByGroupIdHttpRequestHandlerTest {

    private static final String GROUP_ID = "123e4567-e89b-12d3-a456-426614174000";

    @ParameterizedTest
    @CsvSource({
        "-1, 10, 1, 10",
        "0, 0, 1, 1",
        "2, 101, 2, 100",
        "3, 25, 3, 25"
    })
    void normalizesPaginationParameters(
            int page, int perPage, int expectedPage, int expectedPerPage) {
        var input = new GetAllPaymentsByGroupIdHttpRequestHandler(GROUP_ID, page, perPage);

        assertEquals(expectedPage, input.page());
        assertEquals(expectedPerPage, input.perPage());
    }
}
