package com.azriel.perpustakaan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PersonDAO {
    public List<Person> listAll() {
        List<Person> persons = new ArrayList<>();
        String sql = "SELECT id, name, age, email FROM person ORDER BY id";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                persons.add(new Person(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("age"),
                        resultSet.getString("email")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal memuat data", e);
        }
        return persons;
    }

    public void insert(Person person) {
        String sql = "INSERT INTO person (name, age, email) VALUES (?, ?, ?)";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, person.getName());
            statement.setInt(2, person.getAge());
            statement.setString(3, person.getEmail());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menambahkan data", e);
        }
    }

    public void update(Person person) {
        String sql = "UPDATE person SET name = ?, age = ?, email = ? WHERE id = ?";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, person.getName());
            statement.setInt(2, person.getAge());
            statement.setString(3, person.getEmail());
            statement.setInt(4, person.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal memperbarui data", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM person WHERE id = ?";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menghapus data", e);
        }
    }
}
