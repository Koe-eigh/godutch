package com.godutch.database.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import com.godutch.app.group.api.GetGroupPaymentSummaryOutputPort;
import com.godutch.common.Amount;
import com.godutch.group.GroupId;
import com.godutch.group.MemberId;

class JdbcGetGroupPaymentSummaryTest {

    private static final GroupId GROUP_ID =
        new GroupId("123e4567-e89b-12d3-a456-426614174000");

    @Test
    void returnsTotalPaidAmountAndTotalUsedAmountForEachMember() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement paidStatement = mock(PreparedStatement.class);
        PreparedStatement usedStatement = mock(PreparedStatement.class);
        ResultSet paidResults = mock(ResultSet.class);
        ResultSet usedResults = mock(ResultSet.class);
        MemberId firstMemberId =
            new MemberId("123e4567-e89b-12d3-a456-426614174001");
        MemberId secondMemberId =
            new MemberId("123e4567-e89b-12d3-a456-426614174002");

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(contains("total_paid_amount")))
            .thenReturn(paidStatement);
        when(connection.prepareStatement(contains("GROUP BY debtor.member_id")))
            .thenReturn(usedStatement);
        when(paidStatement.executeQuery()).thenReturn(paidResults);
        when(usedStatement.executeQuery()).thenReturn(usedResults);
        when(paidResults.next()).thenReturn(true);
        when(paidResults.getLong("total_paid_amount")).thenReturn(12_000L);
        when(usedResults.next()).thenReturn(true, true, false);
        when(usedResults.getString("member_id"))
            .thenReturn(firstMemberId.toString(), secondMemberId.toString());
        when(usedResults.getLong("total_used_amount")).thenReturn(7_500L, 4_500L);
        CapturingOutput output = new CapturingOutput();

        new JdbcGetGroupPaymentSummary(dataSource).execute(() -> GROUP_ID, output);

        assertEquals(new Amount(12_000), output.totalPaidAmount);
        assertEquals(new Amount(7_500), output.totalUsedAmounts.get(firstMemberId));
        assertEquals(new Amount(4_500), output.totalUsedAmounts.get(secondMemberId));
        assertNull(output.failure);
        verify(paidStatement).setString(1, GROUP_ID.toString());
        verify(usedStatement).setString(1, GROUP_ID.toString());
    }

    private static class CapturingOutput implements GetGroupPaymentSummaryOutputPort {
        private Amount totalPaidAmount;
        private Map<MemberId, Amount> totalUsedAmounts;
        private RuntimeException failure;

        @Override
        public void result(Amount totalPaidAmount, Map<MemberId, Amount> totalUsedAmounts) {
            this.totalPaidAmount = totalPaidAmount;
            this.totalUsedAmounts = totalUsedAmounts;
        }

        @Override
        public void failure(RuntimeException cause) {
            this.failure = cause;
        }
    }
}
