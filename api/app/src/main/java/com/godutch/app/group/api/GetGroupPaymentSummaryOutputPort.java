package com.godutch.app.group.api;

import java.util.Map;

import com.godutch.common.Amount;
import com.godutch.group.MemberId;

public interface GetGroupPaymentSummaryOutputPort {
    void result(Amount totalPaidAmount, Map<MemberId, Amount> totalUsedAmounts);
    void failure(RuntimeException cause);
}
