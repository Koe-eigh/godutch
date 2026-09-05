package com.godutch.web.group.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.godutch.app.group.api.GetGroupById;
import com.godutch.app.group.api.GetGroupByIdInputPort;
import com.godutch.app.group.api.GetGroupByIdOutputPort;
import com.godutch.app.group.api.GetGroupPaymentSummary;
import com.godutch.app.group.api.GetGroupPaymentSummaryInputPort;
import com.godutch.app.group.api.GetGroupPaymentSummaryOutputPort;
import com.godutch.common.Amount;
import com.godutch.group.Group;
import com.godutch.group.GroupId;
import com.godutch.group.Member;
import com.godutch.group.MemberId;

class GroupControllerTest {

    @Test
    void combinesGroupAndPaymentSummaryQueriesInPresenter() {
        MemberId memberId = new MemberId("123e4567-e89b-12d3-a456-426614174001");
        Group group = new Group(
            new GroupId("123e4567-e89b-12d3-a456-426614174000"),
            "旅行",
            "",
            List.of(new Member(memberId, "田中"))
        );
        StubGetGroupById getGroupById = new StubGetGroupById(group);
        StubGetGroupPaymentSummary getSummary =
            new StubGetGroupPaymentSummary(new Amount(7_500), Map.of(memberId, new Amount(7_500)));
        GroupController controller = new GroupController(
            (input, output) -> { },
            getGroupById,
            getSummary
        );

        var response = controller.get(group.getId());

        assertTrue(getGroupById.executed);
        assertTrue(getSummary.executed);
        assertEquals("7500", response.getBody().getTotalPaidAmount());
        assertEquals("7500", response.getBody().getMembers().get(0).getTotalUsedAmount());
    }

    @Test
    void propagatesPaymentSummaryFailure() {
        RuntimeException failure = new RuntimeException(
            "Failed to get group payment summary",
            new java.sql.SQLException("database unavailable")
        );
        Group group = new Group(
            new GroupId("123e4567-e89b-12d3-a456-426614174000"),
            "旅行",
            "",
            List.of()
        );
        GroupController controller = new GroupController(
            (input, output) -> { },
            (input, output) -> output.result(group),
            (input, output) -> output.failure(failure)
        );

        RuntimeException thrown = assertThrows(
            RuntimeException.class,
            () -> controller.get("123e4567-e89b-12d3-a456-426614174000")
        );

        assertSame(failure, thrown);
        assertSame(failure.getCause(), thrown.getCause());
    }

    @Test
    void skipsPaymentSummaryWhenGroupIsNotFound() {
        StubGetGroupPaymentSummary getSummary =
            new StubGetGroupPaymentSummary(Amount.ZERO, Map.of());
        GroupController controller = new GroupController(
            (input, output) -> { },
            (input, output) -> output.result(null),
            getSummary
        );

        var response = controller.get("123e4567-e89b-12d3-a456-426614174000");

        assertEquals(404, response.getStatusCode().value());
        assertFalse(getSummary.executed);
    }

    @Test
    void skipsPaymentSummaryWhenGroupQueryFails() {
        RuntimeException failure = new RuntimeException("Failed to get group");
        StubGetGroupPaymentSummary getSummary =
            new StubGetGroupPaymentSummary(Amount.ZERO, Map.of());
        GroupController controller = new GroupController(
            (input, output) -> { },
            (input, output) -> output.failure(failure),
            getSummary
        );

        RuntimeException thrown = assertThrows(
            RuntimeException.class,
            () -> controller.get("123e4567-e89b-12d3-a456-426614174000")
        );

        assertSame(failure, thrown);
        assertFalse(getSummary.executed);
    }

    private static class StubGetGroupById implements GetGroupById {
        private final Group group;
        private boolean executed;

        StubGetGroupById(Group group) {
            this.group = group;
        }

        @Override
        public void execute(GetGroupByIdInputPort input, GetGroupByIdOutputPort output) {
            executed = true;
            output.result(group);
        }
    }

    private static class StubGetGroupPaymentSummary implements GetGroupPaymentSummary {
        private final Amount totalPaidAmount;
        private final Map<MemberId, Amount> totalUsedAmounts;
        private boolean executed;

        StubGetGroupPaymentSummary(
                Amount totalPaidAmount,
                Map<MemberId, Amount> totalUsedAmounts) {
            this.totalPaidAmount = totalPaidAmount;
            this.totalUsedAmounts = totalUsedAmounts;
        }

        @Override
        public void execute(
                GetGroupPaymentSummaryInputPort input,
                GetGroupPaymentSummaryOutputPort output) {
            executed = true;
            output.result(totalPaidAmount, totalUsedAmounts);
        }
    }
}
