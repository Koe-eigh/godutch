package com.godutch.app.group.impl;

import com.godutch.app.group.api.GetGroupById;
import com.godutch.app.group.api.GetGroupByIdInputPort;
import com.godutch.app.group.api.GetGroupByIdOutputPort;
import com.godutch.group.GroupRepository;

public class GetGroupByIdImpl implements GetGroupById {

    private final GroupRepository groupRepository;

    public GetGroupByIdImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public void execute(GetGroupByIdInputPort input, GetGroupByIdOutputPort output) {
       this.groupRepository.findById(input.groupId())
            .ifPresentOrElse(
                group -> output.result(group),
                () -> output.result(null)
            );
    }
    
}
