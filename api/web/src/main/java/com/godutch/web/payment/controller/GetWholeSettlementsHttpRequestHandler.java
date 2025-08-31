package com.godutch.web.payment.controller;

import com.godutch.app.payment.api.GetWholeSettlementsInputPort;
import com.godutch.group.GroupId;

public class GetWholeSettlementsHttpRequestHandler implements GetWholeSettlementsInputPort {
    private final GroupId groupId;

    public GetWholeSettlementsHttpRequestHandler(String groupId) {
        this.groupId = new GroupId(groupId);
    }

    @Override
    public GroupId groupId() {
        return groupId;
    }
}
