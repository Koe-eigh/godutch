package com.godutch.app.payment.api;

import java.util.List;

import com.godutch.payment.Settlement;

public interface GetWholeSettlementsOutputPort {
    public void output(List<Settlement> settlements);
}
