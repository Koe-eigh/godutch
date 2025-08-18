package com.godutch.web.payment.dto;

import java.util.List;

import com.godutch.web.payment.validator.api.ValidPaymentEventRequest;

@ValidPaymentEventRequest
public class PaymentEventRequest {
    private String title;
    private String memo;
    private List<CreditOrDebit> creditors;
    private List<CreditOrDebit> debtors;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
    public List<CreditOrDebit> getCreditors() { return creditors; }
    public void setCreditors(List<CreditOrDebit> creditors) { this.creditors = creditors; }
    public List<CreditOrDebit> getDebtors() { return debtors; }
    public void setDebtors(List<CreditOrDebit> debtors) { this.debtors = debtors; }

    public static class CreditOrDebit {
        private String memberId;
        private String amount;
        public String getMemberId() { return memberId; }
        public void setMemberId(String memberId) { this.memberId = memberId; }
        public String getAmount() { return amount; }
        public void setAmount(String amount) { this.amount = amount; }
    }
}
