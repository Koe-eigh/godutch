package com.godutch.app.payment.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.godutch.app.payment.api.GetAllPaymentEventsByGroupIdInputPort;
import com.godutch.app.payment.api.GetAllPaymentEventsByGroupIdOutputPort;
import com.godutch.group.GroupId;
import com.godutch.payment.PaymentEvent;
import com.godutch.payment.PaymentEventId;
import com.godutch.payment.PaymentEventRepository;

class GetAllPaymentEventsByGroupIdImplTest {

    private static final GroupId GROUP_ID =
        new GroupId("123e4567-e89b-12d3-a456-426614174000");

    @Test
    void retrievesRequestedPage() {
        PaymentEvent event = new PaymentEvent(
            new PaymentEventId("123e4567-e89b-12d3-a456-426614174001"),
            GROUP_ID,
            "event",
            "");
        StubPaymentEventRepository repository =
            new StubPaymentEventRepository(Optional.of(List.of(event)));
        CapturingOutput output = new CapturingOutput();

        new GetAllPaymentEventsByGroupIdImpl(repository)
            .execute(input(2, 25), output);

        assertEquals(List.of(event), output.paymentEvents);
        assertEquals(GROUP_ID, repository.requestedGroupId);
        assertEquals(2, repository.requestedPage);
        assertEquals(25, repository.requestedPerPage);
        assertEquals(51, output.total);
    }

    @Test
    void returnsEmptyListWhenRepositoryHasNoResult() {
        StubPaymentEventRepository repository =
            new StubPaymentEventRepository(Optional.empty());
        CapturingOutput output = new CapturingOutput();

        new GetAllPaymentEventsByGroupIdImpl(repository)
            .execute(input(3, 10), output);

        assertEquals(List.of(), output.paymentEvents);
    }

    private GetAllPaymentEventsByGroupIdInputPort input(int page, int perPage) {
        return new GetAllPaymentEventsByGroupIdInputPort() {
            @Override
            public GroupId groupId() {
                return GROUP_ID;
            }

            @Override
            public int page() {
                return page;
            }

            @Override
            public int perPage() {
                return perPage;
            }
        };
    }

    private static class CapturingOutput implements GetAllPaymentEventsByGroupIdOutputPort {
        private List<PaymentEvent> paymentEvents;
        private long total;

        @Override
        public void result(List<PaymentEvent> paymentEvents, int page, int perPage, long total) {
            this.paymentEvents = paymentEvents;
            this.total = total;
        }
    }

    private static class StubPaymentEventRepository implements PaymentEventRepository {
        private final Optional<List<PaymentEvent>> result;
        private GroupId requestedGroupId;
        private int requestedPage;
        private int requestedPerPage;

        StubPaymentEventRepository(Optional<List<PaymentEvent>> result) {
            this.result = result;
        }

        @Override
        public Optional<List<PaymentEvent>> findAllBy(
                GroupId groupId, int page, int perPage) {
            requestedGroupId = groupId;
            requestedPage = page;
            requestedPerPage = perPage;
            return result;
        }

        @Override
        public long countBy(GroupId groupId) {
            return 51;
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
}
