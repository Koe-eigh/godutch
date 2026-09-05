package com.godutch.database.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.godutch.app.group.api.GetGroupPaymentSummary;
import com.godutch.app.group.api.GetGroupPaymentSummaryInputPort;
import com.godutch.app.group.api.GetGroupPaymentSummaryOutputPort;
import com.godutch.common.Amount;
import com.godutch.group.MemberId;

public class JdbcGetGroupPaymentSummary implements GetGroupPaymentSummary {

    private final DataSource dataSource;

    public JdbcGetGroupPaymentSummary(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void execute(
            GetGroupPaymentSummaryInputPort input,
            GetGroupPaymentSummaryOutputPort output) {
        String sql = """
            SELECT paid.total_paid_amount, used.member_id, used.total_used_amount
            FROM (
                SELECT COALESCE(SUM(creditor.amount), 0) AS total_paid_amount
                FROM tbl_payment_events payment_event
                INNER JOIN tbl_payment_event_creditors creditor
                    ON creditor.event_id = payment_event.id
                WHERE payment_event.group_id = ?
            ) paid
            LEFT JOIN (
                SELECT debtor.member_id, SUM(debtor.amount) AS total_used_amount
                FROM tbl_payment_events payment_event
                INNER JOIN tbl_payment_event_debtors debtor
                    ON debtor.event_id = payment_event.id
                WHERE payment_event.group_id = ?
                GROUP BY debtor.member_id
            ) used ON TRUE
            """;

        try (Connection connection = dataSource.getConnection()) {
            findPaymentSummary(connection, sql, input, output);
        } catch (SQLException cause) {
            output.failure(new RuntimeException("Failed to get group payment summary", cause));
        }
    }

    private void findPaymentSummary(
            Connection connection,
            String sql,
            GetGroupPaymentSummaryInputPort input,
            GetGroupPaymentSummaryOutputPort output) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, input.groupId().toString());
            statement.setString(2, input.groupId().toString());
            try (ResultSet results = statement.executeQuery()) {
                Amount totalPaidAmount = Amount.ZERO;
                Map<MemberId, Amount> totalUsedAmounts = new HashMap<>();

                if (results.next()) {
                    totalPaidAmount = new Amount(results.getLong("total_paid_amount"));
                    do {
                        String memberId = results.getString("member_id");
                        if (memberId != null) {
                            totalUsedAmounts.put(
                                new MemberId(memberId),
                                new Amount(results.getLong("total_used_amount"))
                            );
                        }
                    } while (results.next());
                }

                output.result(totalPaidAmount, totalUsedAmounts);
            }
        }
    }
}
