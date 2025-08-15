package com.godutch.payment;

import java.util.UUID;

public class PaymentEventId {
    // UUID
    private final UUID value;

    public static PaymentEventId newId() {
        return new PaymentEventId(UUID.randomUUID().toString());
    }

    public PaymentEventId(String value) {
        this.value = UUID.fromString(value);
    }

    public String getValue() {
        return value.toString();
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentEventId)) return false;
        PaymentEventId that = (PaymentEventId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
