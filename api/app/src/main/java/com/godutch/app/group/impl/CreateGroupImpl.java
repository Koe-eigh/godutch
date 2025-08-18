package com.godutch.app.group.impl;

import com.godutch.app.group.api.CreateGroup;
import com.godutch.app.group.api.CreateGroupInoutPort;
import com.godutch.app.group.api.CreateGroupOutputPort;
import com.godutch.group.Group;
import com.godutch.group.GroupRepository;

public class CreateGroupImpl implements CreateGroup {

    private final GroupRepository groupRepository;

    public CreateGroupImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public void execute(CreateGroupInoutPort input, CreateGroupOutputPort output) {
        Group group = input.group();
        
        groupRepository.save(group);

        output.result(group);
    }
}
