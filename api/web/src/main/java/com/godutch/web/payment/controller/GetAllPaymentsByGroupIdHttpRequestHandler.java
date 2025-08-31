package com.godutch.web.payment.controller;

import com.godutch.app.payment.api.GetAllPaymentEventsByGroupIdInputPort;
import com.godutch.group.GroupId;

public class GetAllPaymentsByGroupIdHttpRequestHandler implements GetAllPaymentEventsByGroupIdInputPort {

    private final GroupId groupId;

    public GetAllPaymentsByGroupIdHttpRequestHandler(String groupId) {
        this.groupId = new GroupId(groupId);
    }

    @Override
    public GroupId groupId() {
        return this.groupId;
    }
    
}
