package com.godutch.web.group.controller;

import org.springframework.http.ResponseEntity;

import com.godutch.app.group.api.GetGroupByIdOutputPort;
import com.godutch.common.Amount;
import com.godutch.group.Group;
import com.godutch.web.group.dto.GroupResponse;

public class GetGroupByIdHttpResponsePresenter implements GetGroupByIdOutputPort {

    private Group group;
    private Amount totalPaidAmount;

    @Override
    public void result(Group group, Amount totalPaidAmount) {
        this.group = group;
        this.totalPaidAmount = totalPaidAmount;
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
                .toList(),
            totalPaidAmount.toString()
        );
        
        return ResponseEntity.ok(response);
    }

}
