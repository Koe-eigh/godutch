package com.godutch.group;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

public class GroupTest {
    
    @Test
    void testUniqueMemberNames() {
        // テストケース: メンバー名が重複している場合、IllegalArgumentExceptionを投げる
        assertThrows(IllegalArgumentException.class, () -> {
            new Group(GroupId.newId(), "Test Group", "", List.of(
                new Member(MemberId.newId(), "Alice"),
                new Member(MemberId.newId(), "Alice") // 重複する名前
            ));
        });
    }
}
