package com.godutch.payment;

import com.godutch.common.Amount;
import com.godutch.group.MemberId;

public class Credit {
    private final MemberId creditorId;
    private final Amount amount;

    public Credit(MemberId creditorId, Amount amount) {
        this.creditorId = creditorId;
        this.amount = amount;
    }

    public MemberId getCreditorId() {
        return creditorId;
    }

    public Amount getAmount() {
        return amount;
    }
}
