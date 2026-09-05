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
        String totalPaidSql = """
            SELECT COALESCE(SUM(creditor.amount), 0) AS total_paid_amount
            FROM tbl_payment_events payment_event
            INNER JOIN tbl_payment_event_creditors creditor
                ON creditor.event_id = payment_event.id
            WHERE payment_event.group_id = ?
            """;
        String totalUsedSql = """
            SELECT debtor.member_id, SUM(debtor.amount) AS total_used_amount
            FROM tbl_payment_events payment_event
            INNER JOIN tbl_payment_event_debtors debtor
                ON debtor.event_id = payment_event.id
            WHERE payment_event.group_id = ?
            GROUP BY debtor.member_id
            """;

        try (Connection connection = dataSource.getConnection()) {
            Amount totalPaidAmount = findTotalPaidAmount(connection, totalPaidSql, input);
            Map<MemberId, Amount> totalUsedAmounts =
                findTotalUsedAmounts(connection, totalUsedSql, input);
            output.result(totalPaidAmount, totalUsedAmounts);
        } catch (SQLException cause) {
            output.failure(new RuntimeException("Failed to get group payment summary", cause));
        }
    }

    private Amount findTotalPaidAmount(
            Connection connection,
            String sql,
            GetGroupPaymentSummaryInputPort input) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, input.groupId().toString());
            try (ResultSet results = statement.executeQuery()) {
                return results.next()
                    ? new Amount(results.getLong("total_paid_amount"))
                    : Amount.ZERO;
            }
        }
    }

    private Map<MemberId, Amount> findTotalUsedAmounts(
            Connection connection,
            String sql,
            GetGroupPaymentSummaryInputPort input) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, input.groupId().toString());
            try (ResultSet results = statement.executeQuery()) {
                Map<MemberId, Amount> totalUsedAmounts = new HashMap<>();
                while (results.next()) {
                    totalUsedAmounts.put(
                        new MemberId(results.getString("member_id")),
                        new Amount(results.getLong("total_used_amount"))
                    );
                }
                return totalUsedAmounts;
            }
        }
    }
}
