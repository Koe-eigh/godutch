package com.godutch.web.group.controller;

import org.springframework.http.ResponseEntity;

import com.godutch.app.group.api.GetGroupByIdOutputPort;
import com.godutch.app.group.api.GetGroupPaymentSummaryOutputPort;
import com.godutch.common.Amount;
import com.godutch.group.Group;
import com.godutch.group.MemberId;
import com.godutch.web.group.dto.GroupResponse;
import java.util.Map;

public class GetGroupByIdHttpResponsePresenter
        implements GetGroupByIdOutputPort, GetGroupPaymentSummaryOutputPort {

    private Group group;
    private Amount totalPaidAmount;
    private Map<MemberId, Amount> totalUsedAmounts;
    private RuntimeException failure;

    @Override
    public void result(Group group) {
        this.group = group;
    }

    @Override
    public void result(Amount totalPaidAmount, Map<MemberId, Amount> totalUsedAmounts) {
        this.totalPaidAmount = totalPaidAmount;
        this.totalUsedAmounts = totalUsedAmounts;
    }

    @Override
    public void failure(RuntimeException cause) {
        this.failure = cause;
    }

    public ResponseEntity<GroupResponse> present() {
        if (failure != null) {
            throw failure;
        }

        if (group == null) {
            return ResponseEntity.notFound().build();
        }
        
        GroupResponse response = new GroupResponse(
            group.getId(),
            group.getName(),
            group.getDescription(),
            group.getMembers().stream()
                .map(member -> new GroupResponse.MemberResponse(
                    member.getId(),
                    member.getName(),
                    totalUsedAmounts.getOrDefault(new MemberId(member.getId()), Amount.ZERO).toString()
                ))
                .toList(),
            totalPaidAmount.toString()
        );
        
        return ResponseEntity.ok(response);
    }

}
