package com.godutch.web.group.dto;

import java.util.ArrayList;
import java.util.List;

public class GroupResponse {
    private final String id;
    private final String name;
    private final String description;
    private final List<MemberResponse> members;

    public GroupResponse(String id, String name, String description, List<MemberResponse> members) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.members = new ArrayList<>(members);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<MemberResponse> getMembers() {
        return new ArrayList<>(members);
    }

    public static class MemberResponse {
        private final String id;
        private final String name;

        public MemberResponse(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
