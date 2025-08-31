package com.godutch.payment;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.godutch.common.Amount;
import com.godutch.group.GroupId;
import com.godutch.group.MemberId;

public class PaymentEventTest {
    @Test
    void testTotalCreditsAndDebits() {
        // テストケース: クレジットとデビットの金額合計が一致することを確認
        
        assertThrows(IllegalArgumentException.class, () -> {
            new PaymentEvent(
                GroupId.newId(),
                "Test Event",
                "This is a test event",
                List.of(new Credit(MemberId.newId(), new Amount(100))),
                List.of(new Debit(MemberId.newId(), new Amount(200))) // 合計が一致しない
            );
        });
    }
}
