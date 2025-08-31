package com.godutch.payment;

import java.util.Objects;

import com.godutch.common.Amount;
import com.godutch.group.MemberId;

public class Settlement {
    private final MemberId payerId;     // 支払う人
    private final MemberId payeeId;     // 受け取る人
    private final Amount amount;        // 金額

    public Settlement(MemberId payerId, MemberId payeeId, Amount amount) {
        this.payerId = payerId;
        this.payeeId = payeeId;
        this.amount = amount;
    }

    public MemberId getPayerId() {
        return payerId;
    }

    public MemberId getPayeeId() {
        return payeeId;
    }

    public Amount getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Settlement that = (Settlement) o;
        return payerId.equals(that.payerId) &&
               payeeId.equals(that.payeeId) &&
               amount.equals(that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(payerId, payeeId, amount);
    }

    @Override
    public String toString() {
        return "Settlement{" +
                "payerId=" + payerId +
                ", payeeId=" + payeeId +
                ", amount=" + amount +
                '}';
    }
}
