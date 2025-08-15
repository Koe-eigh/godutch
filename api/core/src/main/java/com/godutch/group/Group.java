package com.godutch.group;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private final GroupId id;
    private final String name;
    private String description;
    private List<Member> members;

    public Group(String id, String name, String description, List<Member> members) {
        this.id = new GroupId(id);
        this.name = name;
        this.description = description;
        this.members = new ArrayList<>(members);
    }

    public Group(String name, String description) {
        this.id = GroupId.newId();
        this.name = name;
        this.description = description;
        this.members = new ArrayList<>();
    }

    public Group(String name) {
        this.id = GroupId.newId();
        this.name = name;
        this.description = "";
        this.members = new ArrayList<>();
    }

    public String getId() {
        return id.toString();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void addMember(String memberId, String memberName) {
        Member newMember = new Member(memberId, memberName);
        members.add(newMember);
    }

    public void addMember(String memberName) {
        Member newMember = new Member(memberName);
        members.add(newMember);
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
