package com.godutch.app.group.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.godutch.app.group.api.GetGroupByIdInputPort;
import com.godutch.app.group.api.GetGroupByIdOutputPort;
import com.godutch.group.Group;
import com.godutch.group.GroupId;
import com.godutch.group.GroupRepository;

class GetGroupByIdImplTest {

    private static final GroupId GROUP_ID =
        new GroupId("123e4567-e89b-12d3-a456-426614174000");

    @Test
    void returnsGroup() {
        Group group = new Group(GROUP_ID, "旅行", "", List.of());
        CapturingOutput output = new CapturingOutput();

        new GetGroupByIdImpl(new StubGroupRepository(Optional.of(group)))
            .execute(input(), output);

        assertEquals(group, output.group);
    }

    @Test
    void returnsNullWhenGroupDoesNotExist() {
        CapturingOutput output = new CapturingOutput();

        new GetGroupByIdImpl(new StubGroupRepository(Optional.empty()))
            .execute(input(), output);

        assertNull(output.group);
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

    private static class CapturingOutput implements GetGroupByIdOutputPort {
        private Group group;

        @Override
        public void result(Group group) {
            this.group = group;
        }

        @Override
        public void failure(RuntimeException cause) {
            throw cause;
        }
    }
}
