package com.azriel.perpustakaan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public User findByUsername(String username) {
        String sql = "SELECT id, username, password, role, photo_path FROM users WHERE username = ?";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                            resultSet.getInt("id"),
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getString("role"),
                            resultSet.getString("photo_path")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil data user", e);
        }
        return null;
    }

    public void insert(User user) {
        String sql = "INSERT INTO users (username, password, role, photo_path) VALUES (?, ?, ?, ?)";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole());
            statement.setString(4, user.getPhotoPath());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menambahkan user", e);
        }
    }

    public List<User> listAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password, role, photo_path FROM users ORDER BY id";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("role"),
                        resultSet.getString("photo_path")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil daftar user", e);
        }
        return users;
    }

    public void update(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, role = ?, photo_path = ? WHERE id = ?";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole());
            statement.setString(4, user.getPhotoPath());
            statement.setInt(5, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal memperbarui user", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menghapus user", e);
        }
    }
}
