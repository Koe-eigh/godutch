package com.godutch.payment;

import com.godutch.common.Amount;
import com.godutch.group.MemberId;

public class Debit {
    private final MemberId debtorId;
    private final Amount amount;

    public Debit(MemberId debtorId, Amount amount) {
        this.debtorId = debtorId;
        this.amount = amount;
    }

    public MemberId getDebtorId() {
        return debtorId;
    }

    public Amount getAmount() {
        return amount;
    }
}
