package com.godutch.group;

import java.util.UUID;

public class GroupId {
    // UUID
    private final UUID value;

    public static GroupId newId() {
        return new GroupId(UUID.randomUUID().toString());
    }

    public GroupId(String value) {
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
        if (!(o instanceof GroupId)) return false;
        GroupId that = (GroupId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
