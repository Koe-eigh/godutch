package com.godutch.web.payment.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class GetAllPaymentsByGroupIdHttpResponsePresenterTest {

    @Test
    void presentsPaginationMetadataForPageBeyondLastPage() throws Exception {
        var presenter = new GetAllPaymentsByGroupIdHttpResponsePresenter();
        presenter.result(List.of(), 3, 10, 11);

        var response = presenter.present().getBody();

        assertEquals(List.of(), response.events());
        assertEquals(3, response.page());
        assertEquals(10, response.perPage());
        assertEquals(2, response.lastPage());
        assertEquals(11, response.total());

        String json = new ObjectMapper().writeValueAsString(response);
        assertTrue(json.contains("\"per_page\":10"));
        assertTrue(json.contains("\"last_page\":2"));
    }
}
