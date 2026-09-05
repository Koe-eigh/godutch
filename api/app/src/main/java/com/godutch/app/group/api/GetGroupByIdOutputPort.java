package com.godutch.app.group.api;

import com.godutch.group.Group;

public interface GetGroupByIdOutputPort {
    public void result(Group group);
    public void failure(RuntimeException cause);
}
