package com.godutch.group;

public class Member {
    private final MemberId id;
    private final String name;

    public Member(String id, String name) {
        this.id = new MemberId(id);
        this.name = name;
    }

    public Member(String name) {
        this.id = MemberId.newId();
        this.name = name;
    }

    public String getId() {
        return id.toString();
    }

    public String getName() {
        return name;
    }
}
