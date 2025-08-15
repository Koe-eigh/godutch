package com.godutch.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.godutch.common.Amount;
import com.godutch.group.GroupId;
import com.godutch.group.MemberId;

public class PaymentEventServiceSimpleImplTest {

    @Test
    public void testCalculateSettlementWithFiveMembers() {
        // Given
        PaymentEventService service = new PaymentEventServiceSimpleImpl();
        
        // メンバーIDの作成（UUID）
        MemberId m1 = new MemberId("123e4567-e89b-12d3-a456-426614174001");
        MemberId m2 = new MemberId("123e4567-e89b-12d3-a456-426614174002");
        MemberId m3 = new MemberId("123e4567-e89b-12d3-a456-426614174003");
        MemberId m4 = new MemberId("123e4567-e89b-12d3-a456-426614174004");
        MemberId m5 = new MemberId("123e4567-e89b-12d3-a456-426614174005");

        // メンバーのリスト
        List<MemberId> memberIds = Arrays.asList(m1, m2, m3, m4, m5);

        // 支払いイベントの作成（M1が2000円、M2が3000円立て替え）
        List<Credit> credits = Arrays.asList(
            new Credit(m1, new Amount(2000)),
            new Credit(m2, new Amount(3000))
        );
        List<Debit> debits = Arrays.asList(
            new Debit(m1, new Amount(1500)),
            new Debit(m2, new Amount(900)),
            new Debit(m3, new Amount(1000)),
            new Debit(m4, new Amount(700)),
            new Debit(m5, new Amount(900))
        );
        
        PaymentEvent event = new PaymentEvent(
            new PaymentEventId("123e4567-e89b-12d3-a456-426614174000"),
            new GroupId("123e4567-e89b-12d3-a456-426614174999"),
            "テスト支払い",
            "テストメモ",
            credits,
            debits
        );

        // When
        List<Settlement> settlements = service.calculateSettlement(Arrays.asList(event));

        memberIds.forEach(memberId -> {
            var creditAmount = credits.stream().filter(c -> c.getCreditorId().equals(memberId))
                .map(Credit::getAmount).findFirst().orElse(Amount.ZERO);
            var debitAmount = debits.stream().filter(d -> d.getDebtorId().equals(memberId))
                .map(Debit::getAmount).findFirst().orElse(Amount.ZERO);
            
            // 支払い、または受け取りの額を計算
            // settlements からそのメンバーが関与する清算を抽出し
            // 受け取った額はそのまま、支払った額はマイナスにして合計
            Amount settlementAmount = settlements.stream()
                .filter(s -> s.getPayerId().equals(memberId) || s.getPayeeId().equals(memberId))
                .map(s -> s.getPayerId().equals(memberId) ? s.getAmount().negate() : s.getAmount())
                .reduce(Amount.ZERO, Amount::add);
            System.out.println("Member: " + memberId.getId() + 
                ", Credit: " + creditAmount + 
                ", Debit: " + debitAmount + 
                ", Settlement: " + settlementAmount);
            assertEquals(creditAmount.subtract(debitAmount), settlementAmount);
        });
    }

    @Test
    public void testCalculateSettlementWithMultipleEvents() {
        // Given
        PaymentEventService service = new PaymentEventServiceSimpleImpl();

        // メンバーIDの作成（UUID）
        MemberId m1 = new MemberId("123e4567-e89b-12d3-a456-426614174001");
        MemberId m2 = new MemberId("123e4567-e89b-12d3-a456-426614174002");
        MemberId m3 = new MemberId("123e4567-e89b-12d3-a456-426614174003");
        MemberId m4 = new MemberId("123e4567-e89b-12d3-a456-426614174004");
        MemberId m5 = new MemberId("123e4567-e89b-12d3-a456-426614174005");

        // Event1（前テストと同様）
        List<Credit> credits1 = Arrays.asList(
            new Credit(m1, new Amount(2000)),
            new Credit(m2, new Amount(3000))
        );
        List<Debit> debits1 = Arrays.asList(
            new Debit(m1, new Amount(1500)),
            new Debit(m2, new Amount(900)),
            new Debit(m3, new Amount(1000)),
            new Debit(m4, new Amount(700)),
            new Debit(m5, new Amount(900))
        );
        PaymentEvent event1 = new PaymentEvent(
            new PaymentEventId("123e4567-e89b-12d3-a456-426614174010"),
            new GroupId("123e4567-e89b-12d3-a456-426614174999"),
            "イベント1",
            "メモ1",
            credits1,
            debits1
        );

        // Event2（別の買い物 4000円）
        List<Credit> credits2 = Arrays.asList(
            new Credit(m3, new Amount(1000)),
            new Credit(m4, new Amount(3000))
        );
        List<Debit> debits2 = Arrays.asList(
            new Debit(m1, new Amount(1000)),
            new Debit(m2, new Amount(1500)),
            new Debit(m3, new Amount(500)),
            new Debit(m4, new Amount(800)),
            new Debit(m5, new Amount(200))
        );
        PaymentEvent event2 = new PaymentEvent(
            new PaymentEventId("123e4567-e89b-12d3-a456-426614174011"),
            new GroupId("123e4567-e89b-12d3-a456-426614174999"),
            "イベント2",
            "メモ2",
            credits2,
            debits2
        );

        // When
        List<Settlement> settlements = service.calculateSettlement(Arrays.asList(event1, event2));

        // Then: 各メンバーの最終支払額（全イベントの credit 合計 + 清算支払額 - 清算受取額）が、全イベントの debit 合計に一致
        List<MemberId> members = Arrays.asList(m1, m2, m3, m4, m5);
        members.forEach(memberId -> {
            Amount creditAmount = Arrays.asList(event1, event2).stream()
                .flatMap(e -> e.getCredits().stream())
                .filter(c -> c.getCreditorId().equals(memberId))
                .map(Credit::getAmount)
                .reduce(Amount.ZERO, Amount::add);
            Amount debitAmount = Arrays.asList(event1, event2).stream()
                .flatMap(e -> e.getDebits().stream())
                .filter(d -> d.getDebtorId().equals(memberId))
                .map(Debit::getAmount)
                .reduce(Amount.ZERO, Amount::add);

            Amount settlementAmount = settlements.stream()
                .filter(s -> s.getPayerId().equals(memberId) || s.getPayeeId().equals(memberId))
                .map(s -> s.getPayerId().equals(memberId) ? s.getAmount().negate() : s.getAmount())
                .reduce(Amount.ZERO, Amount::add);

            assertEquals(creditAmount.subtract(debitAmount), settlementAmount, "member=" + memberId.getId());
        });
    }
}
