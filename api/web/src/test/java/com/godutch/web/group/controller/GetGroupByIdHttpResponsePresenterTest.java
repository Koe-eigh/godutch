package com.godutch.web.group.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.godutch.common.Amount;
import com.godutch.group.Group;
import com.godutch.group.GroupId;
import com.godutch.group.Member;
import com.godutch.group.MemberId;

class GetGroupByIdHttpResponsePresenterTest {

    @Test
    void presentsEachMembersTotalUsedAmountAndDefaultsMissingTotalsToZero() {
        GroupId groupId = new GroupId("123e4567-e89b-12d3-a456-426614174000");
        MemberId memberWithUsage =
            new MemberId("123e4567-e89b-12d3-a456-426614174001");
        MemberId memberWithoutUsage =
            new MemberId("123e4567-e89b-12d3-a456-426614174002");
        Group group = new Group(
            groupId,
            "旅行",
            "",
            List.of(
                new Member(memberWithUsage, "田中"),
                new Member(memberWithoutUsage, "佐藤")
            )
        );
        GetGroupByIdHttpResponsePresenter presenter = new GetGroupByIdHttpResponsePresenter();

        presenter.result(group);
        presenter.result(
            new Amount(12_345),
            Map.of(memberWithUsage, new Amount(7_500))
        );

        var response = presenter.present().getBody();
        assertEquals("12345", response.getTotalPaidAmount());
        assertEquals("7500", response.getMembers().get(0).getTotalUsedAmount());
        assertEquals("0", response.getMembers().get(1).getTotalUsedAmount());
    }
}
