package com.godutch.app.group.api;

import com.godutch.common.Amount;
import com.godutch.group.Group;

public interface GetGroupByIdOutputPort {
    public void result(Group group, Amount totalPaidAmount);
    public void failure(RuntimeException cause);
}
