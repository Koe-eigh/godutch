package com.godutch.web.group.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateGroupRequest {
    @JsonProperty("name")
    private String groupName;
    private String description;
    private List<MemberRequest> members;

    public CreateGroupRequest() {
    }

    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public List<MemberRequest> getMembers() {
        return members;
    }
    public void setMembers(List<MemberRequest> members) {
        this.members = members;
    }

    public static class MemberRequest {
        private String name;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }
}
