package com.godutch.payment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.godutch.common.Amount;
import com.godutch.group.MemberId;

public class PaymentEventServiceSimpleImpl implements PaymentEventService {

    @Override
    public List<Settlement> calculateSettlement(List<PaymentEvent> paymentEvents) {
        Map<MemberId, Amount> creditMap = new HashMap<>();
        Map<MemberId, Amount> debitMap = new HashMap<>();

        for (var paymentEvent : paymentEvents) {
            // 立替額の集計
            for (Credit credit : paymentEvent.getCredits()) {
                creditMap.put(credit.getCreditorId(),
                        creditMap.getOrDefault(credit.getCreditorId(), Amount.ZERO).add(credit.getAmount()));
            }

            // 負担額の集計
            for (Debit debit : paymentEvent.getDebits()) {
                debitMap.put(debit.getDebtorId(),
                        debitMap.getOrDefault(debit.getDebtorId(), Amount.ZERO).add(debit.getAmount()));
            }
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

        // // 並び順を安定化
        // // 受取側(payees): 金額の昇順。同額の場合は MemberId の文字列昇順
        // payees.sort((a, b) -> {
        //     if (a.getValue().isLessThan(b.getValue())) return -1;
        //     if (b.getValue().isLessThan(a.getValue())) return 1;
        //     return a.getKey().getId().compareTo(b.getKey().getId());
        // });

        // 貪欲に清算
        List<Settlement> settlements = new ArrayList<>();
        int i = 0, j = 0;
        while (i < payees.size() && j < payers.size()) {
            var payee = payees.get(i);
            var payer = payers.get(j);

            Amount settlementAmount = payer.getValue().isLessThan(payee.getValue())
                    ? payer.getValue()
                    : payee.getValue();

            settlements.add(new Settlement(payer.getKey(), payee.getKey(), settlementAmount));

            // 差額を更新
            payers.set(j, Map.entry(payer.getKey(), payer.getValue().subtract(settlementAmount)));
            payees.set(i, Map.entry(payee.getKey(), payee.getValue().subtract(settlementAmount)));

            // 差額がゼロになった場合、次の要素に進む
            if (payers.get(j).getValue().isZero()) {
                j++;
            }
            if (payees.get(i).getValue().isZero()) {
                i++;
            }
        }

        return settlements;
    }

}
