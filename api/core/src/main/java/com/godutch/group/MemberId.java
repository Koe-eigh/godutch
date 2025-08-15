package com.godutch.group;

import java.util.UUID;

public class MemberId {
    // UUID
    private final UUID id;

    public static MemberId newId() {
        return new MemberId(UUID.randomUUID().toString());
    }

    public MemberId(String id) {
        this.id = UUID.fromString(id);
    }

    public String getId() {
        return id.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberId memberId = (MemberId) o;
        return id.equals(memberId.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return this.id.toString();
    }
}
