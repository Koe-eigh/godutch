package com.godutch.web.payment.dto;

import java.util.List;

public class PaymentEventResponse {
    private String id;
    private String groupId;
    private String title;
    private String memo;
    private List<CreditOrDebit> creditors;
    private List<CreditOrDebit> debtors;

    public PaymentEventResponse(String id, String groupId, String title, String memo,
                                List<CreditOrDebit> creditors, List<CreditOrDebit> debtors) {
        this.id = id;
        this.groupId = groupId;
        this.title = title;
        this.memo = memo;
        this.creditors = creditors;
        this.debtors = debtors;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public List<CreditOrDebit> getCreditors() {
        return creditors;
    }

    public void setCreditors(List<CreditOrDebit> creditors) {
        this.creditors = creditors;
    }

    public List<CreditOrDebit> getDebtors() {
        return debtors;
    }

    public void setDebtors(List<CreditOrDebit> debtors) {
        this.debtors = debtors;
    }

    public static class CreditOrDebit {
        private final String memberId;
        private final String amount;

        public CreditOrDebit(String memberId, String amount) {
            this.memberId = memberId;
            this.amount = amount;
        }

        public String getMemberId() {
            return memberId;
        }

        public String getAmount() {
            return amount;
        }
    }
}
