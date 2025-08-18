package com.godutch.web.group.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.godutch.app.group.api.CreateGroupOutputPort;
import com.godutch.group.Group;
import com.godutch.web.group.dto.GroupResponse;

public class CreateGroupHttpResponsePresenter implements CreateGroupOutputPort {

    private Group group;

    @Override
    public void result(Group group) {
        this.group = group;
    }

    public ResponseEntity<GroupResponse> present() {
        return ResponseEntity.status(HttpStatus.CREATED).body(new GroupResponse(
            group.getId(),
            group.getName(),
            group.getDescription(),
            group.getMembers().stream()
                .map(member -> new GroupResponse.MemberResponse(member.getId(), member.getName()))
                .toList()
        ));
    }
}
