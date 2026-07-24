package com.godutch.database.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import com.godutch.group.GroupId;
import com.godutch.payment.PaymentEvent;

class JdbcPaymentEventRepositoryTest {

    private static final String GROUP_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String FIRST_EVENT_ID = "123e4567-e89b-12d3-a456-426614174002";
    private static final String SECOND_EVENT_ID = "123e4567-e89b-12d3-a456-426614174001";

    @Test
    void retrievesPageInQueryOrder() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement eventStatement = mock(PreparedStatement.class);
        PreparedStatement creditStatement = mock(PreparedStatement.class);
        PreparedStatement debitStatement = mock(PreparedStatement.class);
        ResultSet eventResults = mock(ResultSet.class);
        ResultSet creditResults = mock(ResultSet.class);
        ResultSet debitResults = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(contains("FROM tbl_payment_events")))
            .thenReturn(eventStatement);
        when(connection.prepareStatement(contains("tbl_payment_event_creditors")))
            .thenReturn(creditStatement);
        when(connection.prepareStatement(contains("tbl_payment_event_debtors")))
            .thenReturn(debitStatement);
        when(eventStatement.executeQuery()).thenReturn(eventResults);
        when(creditStatement.executeQuery()).thenReturn(creditResults);
        when(debitStatement.executeQuery()).thenReturn(debitResults);
        when(eventResults.next()).thenReturn(true, true, false);
        when(eventResults.getString("id")).thenReturn(FIRST_EVENT_ID, SECOND_EVENT_ID);
        when(eventResults.getString("group_id")).thenReturn(GROUP_ID);
        when(eventResults.getString("title")).thenReturn("newer", "older");
        when(eventResults.getString("memo")).thenReturn("");
        when(creditResults.next()).thenReturn(false);
        when(debitResults.next()).thenReturn(false);

        List<PaymentEvent> events =
            new JdbcPaymentEventRepository(dataSource)
                .findAllBy(new GroupId(GROUP_ID), 2, 10)
                .orElseThrow();

        assertEquals(
            List.of(FIRST_EVENT_ID, SECOND_EVENT_ID),
            events.stream().map(event -> event.getId().getValue()).toList());
        verify(eventStatement).setInt(2, 10);
        verify(eventStatement).setLong(3, 10L);
        verify(connection).prepareStatement(argThat(sql ->
            sql.contains("ORDER BY event_date DESC, id DESC")
                && sql.contains("LIMIT ? OFFSET ?")));
    }

    @Test
    void countsEventsByGroup() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(contains("COUNT(*)"))).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(results);
        when(results.next()).thenReturn(true);
        when(results.getLong("event_count")).thenReturn(11L);

        long total =
            new JdbcPaymentEventRepository(dataSource).countBy(new GroupId(GROUP_ID));

        assertEquals(11L, total);
        verify(statement).setString(1, GROUP_ID);
    }
}
