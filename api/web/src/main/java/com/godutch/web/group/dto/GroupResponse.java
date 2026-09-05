package com.godutch.web.group.dto;

import java.util.ArrayList;
import java.util.List;

public class GroupResponse {
    private final String id;
    private final String name;
    private final String description;
    private final List<MemberResponse> members;
    private final String totalPaidAmount;

    public GroupResponse(String id, String name, String description, List<MemberResponse> members,
            String totalPaidAmount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.members = new ArrayList<>(members);
        this.totalPaidAmount = totalPaidAmount;
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

    public String getTotalPaidAmount() {
        return totalPaidAmount;
    }

    public static class MemberResponse {
        private final String id;
        private final String name;
        private final String totalUsedAmount;

        public MemberResponse(String id, String name, String totalUsedAmount) {
            this.id = id;
            this.name = name;
            this.totalUsedAmount = totalUsedAmount;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getTotalUsedAmount() {
            return totalUsedAmount;
        }
    }
}
