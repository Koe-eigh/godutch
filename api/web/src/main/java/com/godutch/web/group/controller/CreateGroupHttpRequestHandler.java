package com.godutch.web.group.controller;

import com.godutch.app.group.api.CreateGroupInoutPort;
import com.godutch.group.Group;
import com.godutch.web.group.dto.CreateGroupRequest;

public class CreateGroupHttpRequestHandler implements CreateGroupInoutPort {
    private final Group group;
    public CreateGroupHttpRequestHandler(CreateGroupRequest request) {
        this.group = new Group(request.getGroupName(), request.getDescription());
        for (var member : request.getMembers()) {
            group.addMember(member.getName());
        }
    }

    @Override
    public Group group() {
        return this.group;
    }
}
