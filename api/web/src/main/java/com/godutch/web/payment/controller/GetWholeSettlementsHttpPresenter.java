package com.godutch.web.payment.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.godutch.app.payment.api.GetWholeSettlementsOutputPort;
import com.godutch.payment.Settlement;
import com.godutch.web.payment.dto.SettlementResponse;

public class GetWholeSettlementsHttpPresenter implements GetWholeSettlementsOutputPort {
    private List<Settlement> settlements;

    @Override
    public void output(List<Settlement> settlements) {
        this.settlements = new ArrayList<>(settlements);
    }
    
    public ResponseEntity<List<SettlementResponse>> present() {
        List<SettlementResponse> settlementResponses = settlements.stream()
            .map(settlement -> new SettlementResponse(
                settlement.getPayerId().toString(),
                settlement.getPayeeId().toString(),
                settlement.getAmount().toString()))
            .toList();
        
        return ResponseEntity.ok(settlementResponses);
    }
}
