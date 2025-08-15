package com.godutch.database.repository;

import com.godutch.group.Group;
import com.godutch.group.GroupId;
import com.godutch.group.GroupRepository;
import com.godutch.group.Member;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Plain JDBC 実装: GroupRepository
 * Spring 依存なし (DataSource は web モジュールから注入)
 */
public class JdbcGroupRepository implements GroupRepository {

    private final DataSource dataSource;

    public JdbcGroupRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Group save(Group group) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            
            String updateGroupsSql = "UPDATE tbl_groups SET name=?, description=? WHERE id=?";
            String insertGroupsSql = "INSERT INTO tbl_groups(id,name,description) VALUES(?,?,?)";
            String insertMembersSql = "INSERT INTO tbl_members(id,group_id,name) VALUES(?,?,?)";

            try {
                if (exists(new GroupId(group.getId()))) {
                    try (PreparedStatement ps = conn.prepareStatement(updateGroupsSql)) {
                        ps.setString(1, group.getName());
                        ps.setString(2, group.getDescription());
                        ps.setString(3, group.getId());
                        ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement groupsPs = conn.prepareStatement(insertGroupsSql);
                        PreparedStatement membersPs = conn.prepareStatement(insertMembersSql)) {
                        groupsPs.setString(1, group.getId());
                        groupsPs.setString(2, group.getName());
                        groupsPs.setString(3, group.getDescription());
                        groupsPs.executeUpdate();

                        for (var member : group.getMembers()) {
                            membersPs.setString(1, member.getId());
                            membersPs.setString(2, group.getId());
                            membersPs.setString(3, member.getName());
                            membersPs.addBatch();
                        }
                        membersPs.executeBatch();

                        conn.commit();
                    } catch (Exception e) {
                        conn.rollback();
                        throw new RuntimeException("Failed to insert group and members", e);
                    }
                }
            } finally {
                conn.setAutoCommit(true);
            }
            return group;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Group> findById(GroupId id) {

        String sql = 
            "SELECT g.id AS group_id," +
            "g.name AS group_name," +
            "g.description AS group_description," +
            "m.id AS member_id," +
            "m.name AS member_name " +
            "FROM tbl_groups g " +
            "LEFT JOIN tbl_members m ON g.id = m.group_id " +
            "WHERE g.id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.getValue());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String groupId = rs.getString("group_id");
                    String groupName = rs.getString("group_name");
                    String groupDescription = rs.getString("group_description");

                    List<Member> members = new ArrayList<>();
                    do {
                        String memberId = rs.getString("member_id");
                        String memberName = rs.getString("member_name");
                        if (memberId != null && memberName != null) {
                            members.add(new Member(memberId, memberName));
                        }
                    } while (rs.next());

                    return Optional.of(new Group(groupId, groupName, groupDescription, members));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(GroupId id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM tbl_groups WHERE id=?")) {
            ps.setString(1, id.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Group> findAll() {
        List<Group> groups = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id,name,description FROM tbl_groups")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    groups.add(new Group(rs.getString("id"), rs.getString("name"), rs.getString("description"), new ArrayList<>()));
                }
            }
            return groups;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
