package com.godutch.web.group.controller;

import com.godutch.app.group.api.GetGroupByIdInputPort;
import com.godutch.group.GroupId;

public class GetGroupByIdHttpRequestHandler implements GetGroupByIdInputPort {
    private final GroupId groupId;
    public GetGroupByIdHttpRequestHandler(String groupId) {
        this.groupId = new GroupId(groupId);
    }

    @Override
    public GroupId groupId() {
        return this.groupId;
    }
}
