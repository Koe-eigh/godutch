package com.godutch.app.payment.api;

import com.godutch.group.GroupId;

public interface GetAllPaymentEventsByGroupIdInputPort {
    public GroupId groupId();

    public int page();

    public int perPage();
}
