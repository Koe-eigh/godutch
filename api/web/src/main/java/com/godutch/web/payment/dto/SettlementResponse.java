package com.godutch.web.payment.dto;

public class SettlementResponse {
    private String payerId;
    private String payeeId;
    private String amount;

    public SettlementResponse(String payerId, String payeeId, String amount) {
        this.payerId = payerId;
        this.payeeId = payeeId;
        this.amount = amount;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public String getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(String payeeId) {
        this.payeeId = payeeId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
