package com.godutch.app.group.api;

public interface GetGroupPaymentSummary {
    void execute(
        GetGroupPaymentSummaryInputPort input,
        GetGroupPaymentSummaryOutputPort output
    );
}
