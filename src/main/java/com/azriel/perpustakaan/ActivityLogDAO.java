package com.azriel.perpustakaan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDAO {
    public void insert(ActivityLog log) {
        String sql = "INSERT INTO activity_logs (username, action, details) VALUES (?, ?, ?)";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, log.getUsername());
            statement.setString(2, log.getAction());
            statement.setString(3, log.getDetails());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mencatat aktivitas", e);
        }
    }

    public List<ActivityLog> listAll() {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT id, username, action, details, timestamp FROM activity_logs ORDER BY timestamp DESC";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                logs.add(new ActivityLog(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("action"),
                        rs.getString("details"),
                        rs.getTimestamp("timestamp").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil log aktivitas", e);
        }
        return logs;
    }
}
