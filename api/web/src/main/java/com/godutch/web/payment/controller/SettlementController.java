package com.godutch.web.payment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.godutch.app.payment.api.GetWholeSettlements;
import com.godutch.web.payment.dto.SettlementResponse;

@RestController
@RequestMapping("/groups/{groupId}/settlement")
public class SettlementController {
    private final GetWholeSettlements getWholeSettlements;

    public SettlementController(GetWholeSettlements getWholeSettlements) {
        this.getWholeSettlements = getWholeSettlements;
    }

    @GetMapping
    public ResponseEntity<List<SettlementResponse>> getWholeSettlements(@PathVariable String groupId) {
        var input = new GetWholeSettlementsHttpRequestHandler(groupId);
        var output = new GetWholeSettlementsHttpPresenter();
        getWholeSettlements.execute(input, output);
        return output.present();
    }
}
