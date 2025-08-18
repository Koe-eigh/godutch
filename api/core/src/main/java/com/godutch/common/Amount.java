package com.godutch.common;

import java.math.BigDecimal;

public class Amount {
    public static final Amount ZERO = new Amount(0);

    private final long value;

    public Amount(long value) {
        this.value = value;
    }
    
    public Amount(BigDecimal value) {
        this.value = value.longValue();
    }

    public long toLong() {
        return value;
    }
    
    public BigDecimal toBigDecimal() {
        return BigDecimal.valueOf(value);
    }

    public Amount add(Amount other) {
        return new Amount(this.value + other.value);
    }

    public Amount subtract(Amount other) {
        return new Amount(this.value - other.value);
    }

    public Amount multiply(Amount factor) {
        return new Amount(this.value * factor.value);
    }

    public Amount divide(Amount divisor) {
        if (divisor.isZero()) {
            throw new IllegalArgumentException("Divisor cannot be zero");
        }
        return new Amount(this.value / divisor.value);
    }

    public Amount abs() {
        return new Amount(Math.abs(this.value));
    }

    public boolean isMinus() {
        return this.value < 0;
    }

    public boolean isPlus() {
        return this.value > 0;
    }

    public boolean isLessThan(Amount other) {
        return this.value < other.value;
    }

    public boolean isGreaterThan(Amount other) {
        return this.value > other.value;
    }

    public boolean isZero() {
        return this.value == 0;
    }

    public Amount negate() {
        return new Amount(-this.value);
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Amount)) return false;
        Amount amount = (Amount) o;
        return value == amount.value;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }
}
