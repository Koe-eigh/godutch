package com.godutch.database.repository;

import com.godutch.payment.*;
import com.godutch.common.Amount;
import com.godutch.group.GroupId;
import com.godutch.group.MemberId;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * Plain JDBC PaymentEventRepository 実装 (credits/debitsを含む)
 */
public class JdbcPaymentEventRepository implements PaymentEventRepository {

    private final DataSource dataSource;

    public JdbcPaymentEventRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public PaymentEvent save(PaymentEvent paymentEvent) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                boolean exists = exists(paymentEvent.getId());
                if (exists) {
                    try (PreparedStatement ps = conn.prepareStatement("UPDATE tbl_payment_events SET title=?, memo=? WHERE id=?")) {
                        ps.setString(1, paymentEvent.getTitle());
                        ps.setString(2, paymentEvent.getMemo());
                        ps.setString(3, paymentEvent.getId().toString());
                        ps.executeUpdate();
                    }
                    // 既存の credits/debits 削除
                    try (PreparedStatement del1 = conn.prepareStatement("DELETE FROM tbl_payment_event_creditors WHERE event_id=?")) {
                        del1.setString(1, paymentEvent.getId().toString());
                        del1.executeUpdate();
                    }
                    try (PreparedStatement del2 = conn.prepareStatement("DELETE FROM tbl_payment_event_debtors WHERE event_id=?")) {
                        del2.setString(1, paymentEvent.getId().toString());
                        del2.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = conn.prepareStatement("INSERT INTO tbl_payment_events(id,group_id,title,memo,event_date) VALUES(?,?,?,?,CURRENT_DATE)")) {
                        ps.setString(1, paymentEvent.getId().toString());
                        ps.setString(2, paymentEvent.getGroupId().toString());
                        ps.setString(3, paymentEvent.getTitle());
                        ps.setString(4, paymentEvent.getMemo());
                        ps.executeUpdate();
                    }
                }
                // credits 挿入
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO tbl_payment_event_creditors(event_id,member_id,amount) VALUES(?,?,?)")) {
                    for (Credit c : paymentEvent.getCredits()) {
                        ps.setString(1, paymentEvent.getId().toString());
                        ps.setString(2, c.getCreditorId().getId());
                        ps.setLong(3, c.getAmount().toLong());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                // debits 挿入
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO tbl_payment_event_debtors(event_id,member_id,amount) VALUES(?,?,?)")) {
                    for (Debit d : paymentEvent.getDebits()) {
                        ps.setString(1, paymentEvent.getId().toString());
                        ps.setString(2, d.getDebtorId().getId());
                        ps.setLong(3, d.getAmount().toLong());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                conn.commit();
                return paymentEvent;
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Failed to save payment event", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<PaymentEvent> findById(PaymentEventId id) {
        try (Connection conn = dataSource.getConnection()) {
            // イベント本体を取得
            String eventSql = "SELECT id, group_id, title, memo FROM tbl_payment_events WHERE id = ?";
            PaymentEvent pe = null;
            try (PreparedStatement ps = conn.prepareStatement(eventSql)) {
                ps.setString(1, id.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        GroupId groupId = new GroupId(rs.getString("group_id"));
                        String title = rs.getString("title");
                        String memo = rs.getString("memo");
                        pe = new PaymentEvent(id, groupId, title, memo);
                    }
                }
            }

            if (pe == null) {
                return Optional.empty();
            }

            // credits を別クエリで取得
            String creditSql = "SELECT member_id, amount FROM tbl_payment_event_creditors WHERE event_id = ?";
            try (PreparedStatement cps = conn.prepareStatement(creditSql)) {
                cps.setString(1, id.toString());
                try (ResultSet crs = cps.executeQuery()) {
                    while (crs.next()) {
                        MemberId creditorId = new MemberId(crs.getString("member_id"));
                        Amount amount = new Amount(crs.getLong("amount"));
                        pe.addCredit(creditorId, amount);
                    }
                }
            }

            // debits を別クエリで取得
            String debitSql = "SELECT member_id, amount FROM tbl_payment_event_debtors WHERE event_id = ?";
            try (PreparedStatement dps = conn.prepareStatement(debitSql)) {
                dps.setString(1, id.toString());
                try (ResultSet drs = dps.executeQuery()) {
                    while (drs.next()) {
                        MemberId debtorId = new MemberId(drs.getString("member_id"));
                        Amount amount = new Amount(drs.getLong("amount"));
                        pe.addDebit(debtorId, amount);
                    }
                }
            }

            return Optional.of(pe);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<List<PaymentEvent>> findAllBy(GroupId groupId) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = """
                        SELECT id, group_id, title, memo
                        FROM tbl_payment_events
                        WHERE group_id = ?;
                        """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, groupId.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    Map<PaymentEventId, PaymentEvent> eventMap = new HashMap<>();
                    
                    while (rs.next()) {
                        PaymentEventId eventId = new PaymentEventId(rs.getString("id"));
                        GroupId gId = new GroupId(rs.getString("group_id"));
                        String title = rs.getString("title");
                        String memo = rs.getString("memo");

                        PaymentEvent event = eventMap.computeIfAbsent(eventId, id -> new PaymentEvent(id, gId, title, memo));
                        
                        String creditSql = """
                                            SELECT member_id, amount
                                            FROM tbl_payment_event_creditors
                                            WHERE event_id = ?;
                                            """;
                        
                        try (PreparedStatement creditPs = conn.prepareStatement(creditSql)) {
                            creditPs.setString(1, eventId.toString());

                            try (ResultSet creditRs = creditPs.executeQuery()) {
                                while (creditRs.next()) {
                                    MemberId creditorId = new MemberId(creditRs.getString("member_id"));
                                    Amount amount = new Amount(creditRs.getLong("amount"));
                                    event.addCredit(creditorId, amount);
                                }
                            }
                        }

                        String debitSql = """
                                            SELECT member_id, amount
                                            FROM tbl_payment_event_debtors
                                            WHERE event_id = ?;
                                            """;
                        try (PreparedStatement debitPs = conn.prepareStatement(debitSql)) {
                            debitPs.setString(1, eventId.toString());
                            try (ResultSet debitRs = debitPs.executeQuery()) {
                                while (debitRs.next()) {
                                    MemberId debtorId = new MemberId(debitRs.getString("member_id"));
                                    Amount amount = new Amount(debitRs.getLong("amount"));
                                    event.addDebit(debtorId, amount);
                                }
                            }
                        }
                    }
                    return Optional.of(new ArrayList<>(eventMap.values()));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PaymentEvent> findAll() {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id FROM tbl_payment_events")) {
            List<PaymentEvent> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    findById(new PaymentEventId(rs.getString("id"))).ifPresent(list::add);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(PaymentEventId id) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM tbl_payment_event_creditors WHERE event_id=?")) {
                    ps.setString(1, id.toString());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM tbl_payment_event_debtors WHERE event_id=?")) {
                    ps.setString(1, id.toString());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM tbl_payment_events WHERE id=?")) {
                    ps.setString(1, id.toString());
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Failed to delete payment event", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(PaymentEventId id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM tbl_payment_events WHERE id=?")) {
            ps.setString(1, id.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
