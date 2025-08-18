package com.godutch.payment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.godutch.common.Amount;
import com.godutch.group.GroupId;
import com.godutch.group.MemberId;

public class PaymentEvent {
    private final PaymentEventId id;
    private final GroupId groupId;
    private String title;
    private String memo;
    private List<Credit> credits;
    private List<Debit> debits;

    public PaymentEvent(PaymentEventId id, GroupId groupId) {
        this.id = id;
        this.groupId = groupId;
        this.title = "";
        this.memo = "";
        this.credits = new ArrayList<>();
        this.debits = new ArrayList<>();
    }

    public PaymentEvent(PaymentEventId id, GroupId groupId, String title, String memo) {
        this.id = id;
        this.groupId = groupId;
        this.title = title;
        this.memo = memo;
        this.credits = new ArrayList<>();
        this.debits = new ArrayList<>();
    }

    public PaymentEvent(PaymentEventId id, GroupId groupId, String title, String memo, List<Credit> credits, List<Debit> debits) {
        this.id = id;
        this.groupId = groupId;
        this.title = title;
        this.memo = memo;
        validate(credits, debits);
        this.credits = new ArrayList<>(credits);
        this.debits = new ArrayList<>(debits);
    }

    public PaymentEvent(GroupId groupId, String title, String memo, List<Credit> credits, List<Debit> debits) {
        this.id = PaymentEventId.newId();
        this.groupId = groupId;
        this.title = title;
        this.memo = memo;
        validate(credits, debits);
        this.credits = new ArrayList<>(credits);
        this.debits = new ArrayList<>(debits);
    }

    public List<Settlement> calcSettlement() {
        Map<MemberId, Amount> creditMap = new HashMap<>();
        Map<MemberId, Amount> debitMap = new HashMap<>();

        // 立替額の集計
        for (Credit credit : credits) {
            creditMap.put(credit.getCreditorId(), creditMap.getOrDefault(credit.getCreditorId(), Amount.ZERO).add(credit.getAmount()));
        }

        // 負担額の集計
        for (Debit debit : debits) {
            debitMap.put(debit.getDebtorId(), debitMap.getOrDefault(debit.getDebtorId(), Amount.ZERO).add(debit.getAmount()));
        }

        // 差額の計算
        Map<MemberId, Amount> netAmounts = new HashMap<>();
        Set<MemberId> allMemberIds = new HashSet<>();
        allMemberIds.addAll(creditMap.keySet());
        allMemberIds.addAll(debitMap.keySet());

        for (MemberId memberId : allMemberIds) {
            Amount creditAmount = creditMap.getOrDefault(memberId, Amount.ZERO);
            Amount debitAmount = debitMap.getOrDefault(memberId, Amount.ZERO);
            netAmounts.put(memberId, creditAmount.subtract(debitAmount));
        }

        // 正の差額(payee)と負の差額(payer)を分ける
        List<Map.Entry<MemberId, Amount>> payees = new ArrayList<>();
        List<Map.Entry<MemberId, Amount>> payers = new ArrayList<>();

        for (Map.Entry<MemberId, Amount> entry : netAmounts.entrySet()) {
            if (entry.getValue().isPlus()) {
                payees.add(entry);
            } else if (entry.getValue().isMinus()) {
                payers.add(Map.entry(entry.getKey(), entry.getValue().abs()));
            }
        }

        // 貪欲に清算
        List<Settlement> settlements = new ArrayList<>();
        int i = 0, j = 0;
        while (i < payees.size() && j < payers.size()) {
            var payer = payers.get(j);
            var payee = payees.get(i);

            Amount settlementAmount = payer.getValue().isLessThan(payee.getValue()) ? payer.getValue() : payee.getValue();

            settlements.add(new Settlement(payer.getKey(), payee.getKey(), settlementAmount));

            // 差額更新（正しいインデックスにセット）
            payers.set(j, Map.entry(payer.getKey(), payer.getValue().subtract(settlementAmount)));
            payees.set(i, Map.entry(payee.getKey(), payee.getValue().subtract(settlementAmount)));

            // ゼロになった方を次へ（正しいカウンタを進める）
            if (payers.get(j).getValue().isZero()) {
                j++;
            }

            if (payees.get(i).getValue().isZero()) {
                i++;
            }
        }

        return settlements;
    }

    public Amount calcNetAmount(MemberId memberId) {
        Amount totalCredit = credits.stream()
                .filter(credit -> credit.getCreditorId().equals(memberId))
                .map(Credit::getAmount)
                .reduce(Amount.ZERO, Amount::add);

        Amount totalDebit = debits.stream()
                .filter(debit -> debit.getDebtorId().equals(memberId))
                .map(Debit::getAmount)
                .reduce(Amount.ZERO, Amount::add);

        return totalCredit.subtract(totalDebit);
    }

    public PaymentEventId getId() {
        return id;
    }

    public GroupId getGroupId() {
        return groupId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public List<Credit> getCredits() {
        return credits;
    }

    public List<Debit> getDebits() {
        return debits;
    }

    public void addDebit(MemberId memberId, Amount amount) {
        this.debits.add(new Debit(memberId, amount));
    }

    public void addCredit(MemberId memberId, Amount amount) {
        this.credits.add(new Credit(memberId, amount));
    }

    public List<MemberId> getMemberIds() {
        List<MemberId> memberIds = new ArrayList<>();
        for (Credit credit : credits) {
            if (!memberIds.contains(credit.getCreditorId())) {
                memberIds.add(credit.getCreditorId());
            }
        }
        for (Debit debit : debits) {
            if (!memberIds.contains(debit.getDebtorId())) {
                memberIds.add(debit.getDebtorId());
            }
        }
        return memberIds;
    }

    public Amount getTotalAmount() {
        Amount total = Amount.ZERO;
        for (Credit credit : credits) {
            total = total.add(credit.getAmount());
        }
        return total;
    }

    private void validate(List<Credit> credits, List<Debit> debits) {
        if (credits == null || debits == null) {
            throw new IllegalArgumentException("Credits and Debits cannot be null");
        }

        // 立替金額の合計と負担金額の合計が一致することを確認
        Amount totalCredit = credits.stream()
                .map(Credit::getAmount)
                .reduce(Amount.ZERO, Amount::add);
        Amount totalDebit = debits.stream()
                .map(Debit::getAmount)
                .reduce(Amount.ZERO, Amount::add);
        if (!totalCredit.equals(totalDebit)) {
            throw new IllegalArgumentException("Total credit amount must equal total debit amount");
        }
    }
}
