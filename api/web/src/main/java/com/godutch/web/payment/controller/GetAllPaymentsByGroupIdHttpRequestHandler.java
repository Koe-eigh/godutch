package com.godutch.web.payment.controller;

import com.godutch.app.payment.api.GetAllPaymentEventsByGroupIdInputPort;
import com.godutch.group.GroupId;

public class GetAllPaymentsByGroupIdHttpRequestHandler implements GetAllPaymentEventsByGroupIdInputPort {

    private final GroupId groupId;
    private final int page;
    private final int perPage;

    public GetAllPaymentsByGroupIdHttpRequestHandler(String groupId, int page, int perPage) {
        this.groupId = new GroupId(groupId);
        this.page = Math.max(page, 1);
        this.perPage = Math.min(Math.max(perPage, 1), 100);
    }

    @Override
    public GroupId groupId() {
        return this.groupId;
    }

    @Override
    public int page() {
        return this.page;
    }

    @Override
    public int perPage() {
        return this.perPage;
    }
}
