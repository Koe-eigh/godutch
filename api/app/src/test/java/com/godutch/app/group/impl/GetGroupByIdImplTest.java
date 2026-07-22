package com.godutch.app.group.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.godutch.app.group.api.GetGroupByIdInputPort;
import com.godutch.app.group.api.GetGroupByIdOutputPort;
import com.godutch.common.Amount;
import com.godutch.group.Group;
import com.godutch.group.GroupId;
import com.godutch.group.GroupRepository;
import com.godutch.payment.PaymentEvent;
import com.godutch.payment.PaymentEventId;
import com.godutch.payment.PaymentEventRepository;

class GetGroupByIdImplTest {

    private static final GroupId GROUP_ID =
        new GroupId("123e4567-e89b-12d3-a456-426614174000");

    @Test
    void returnsGroupWithTotalPaidAmount() {
        Group group = new Group(GROUP_ID, "旅行", "", List.of());
        StubPaymentEventRepository paymentRepository =
            new StubPaymentEventRepository(new Amount(12_345));
        CapturingOutput output = new CapturingOutput();

        new GetGroupByIdImpl(new StubGroupRepository(Optional.of(group)), paymentRepository)
            .execute(input(), output);

        assertEquals(group, output.group);
        assertEquals(new Amount(12_345), output.totalPaidAmount);
        assertEquals(1, paymentRepository.totalQueryCount);
    }

    @Test
    void doesNotCalculateTotalWhenGroupDoesNotExist() {
        StubPaymentEventRepository paymentRepository =
            new StubPaymentEventRepository(new Amount(12_345));
        CapturingOutput output = new CapturingOutput();

        new GetGroupByIdImpl(new StubGroupRepository(Optional.empty()), paymentRepository)
            .execute(input(), output);

        assertNull(output.group);
        assertNull(output.totalPaidAmount);
        assertEquals(0, paymentRepository.totalQueryCount);
    }

    private GetGroupByIdInputPort input() {
        return () -> GROUP_ID;
    }

    private static class StubGroupRepository implements GroupRepository {
        private final Optional<Group> group;

        StubGroupRepository(Optional<Group> group) {
            this.group = group;
        }

        @Override
        public Optional<Group> findById(GroupId id) {
            return group;
        }

        @Override
        public Group save(Group group) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean exists(GroupId id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Group> findAll() {
            throw new UnsupportedOperationException();
        }
    }

    private static class StubPaymentEventRepository implements PaymentEventRepository {
        private final Amount totalPaidAmount;
        private int totalQueryCount;

        StubPaymentEventRepository(Amount totalPaidAmount) {
            this.totalPaidAmount = totalPaidAmount;
        }

        @Override
        public Amount findTotalPaidAmountBy(GroupId groupId) {
            totalQueryCount++;
            return totalPaidAmount;
        }

        @Override
        public PaymentEvent save(PaymentEvent paymentEvent) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<PaymentEvent> findById(PaymentEventId id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<PaymentEvent> findAll() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<List<PaymentEvent>> findAllBy(GroupId groupId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteById(PaymentEventId id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean exists(PaymentEventId id) {
            throw new UnsupportedOperationException();
        }
    }

    private static class CapturingOutput implements GetGroupByIdOutputPort {
        private Group group;
        private Amount totalPaidAmount;

        @Override
        public void result(Group group, Amount totalPaidAmount) {
            this.group = group;
            this.totalPaidAmount = totalPaidAmount;
        }

        @Override
        public void failure(RuntimeException cause) {
            throw cause;
        }
    }
}
