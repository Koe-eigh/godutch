package com.godutch.web.group.controller;

import org.springframework.http.ResponseEntity;

import com.godutch.app.group.api.GetGroupByIdOutputPort;
import com.godutch.group.Group;
import com.godutch.web.group.dto.GroupResponse;

public class GetGroupByIdHttpResponsePresenter implements GetGroupByIdOutputPort {

    private Group group;

    @Override
    public void result(Group group) {
        this.group = group;
    }

    @Override
    public void failure(RuntimeException cause) {
        return;
    }

    public ResponseEntity<GroupResponse> present() {
        if (group == null) {
            return ResponseEntity.notFound().build();
        }
        
        GroupResponse response = new GroupResponse(
            group.getId(),
            group.getName(),
            group.getDescription(),
            group.getMembers().stream()
                .map(member -> new GroupResponse.MemberResponse(member.getId(), member.getName()))
                .toList()
        );
        
        return ResponseEntity.ok(response);
    }

}
