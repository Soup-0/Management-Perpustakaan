package com.azriel.perpustakaan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    public List<Book> listAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status FROM books ORDER BY id";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                books.add(buildBook(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal memuat data buku", e);
        }
        return books;
    }

    public Book findByKode(String kode) {
        String sql = "SELECT id, kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status FROM books WHERE kode = ?";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, kode);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return buildBook(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil buku berdasarkan kode", e);
        }
        return null;
    }

    public List<Book> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return listAll();
        }
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status "
                + "FROM books WHERE LOWER(kode) LIKE ? OR LOWER(judul) LIKE ? OR LOWER(pengarang) LIKE ? OR LOWER(kategori) LIKE ? ORDER BY id";
        String searchValue = "%" + keyword.toLowerCase() + "%";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, searchValue);
            statement.setString(2, searchValue);
            statement.setString(3, searchValue);
            statement.setString(4, searchValue);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    books.add(buildBook(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mencari data buku", e);
        }
        return books;
    }

    public void insert(Book book) {
        String sql = "INSERT INTO books (kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, book.getKode());
            statement.setString(2, book.getJudul());
            statement.setString(3, book.getPengarang());
            statement.setString(4, book.getPenerbit());
            statement.setInt(5, book.getTahunTerbit());
            statement.setString(6, book.getKategori());
            statement.setInt(7, book.getStok());
            statement.setString(8, book.getStatus());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menambahkan buku", e);
        }
    }

    public void update(Book book) {
        String sql = "UPDATE books SET kode = ?, judul = ?, pengarang = ?, penerbit = ?, tahun_terbit = ?, kategori = ?, stok = ?, status = ? WHERE id = ?";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, book.getKode());
            statement.setString(2, book.getJudul());
            statement.setString(3, book.getPengarang());
            statement.setString(4, book.getPenerbit());
            statement.setInt(5, book.getTahunTerbit());
            statement.setString(6, book.getKategori());
            statement.setInt(7, book.getStok());
            statement.setString(8, book.getStatus());
            statement.setInt(9, book.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal memperbarui buku", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menghapus buku", e);
        }
    }

    private Book buildBook(ResultSet resultSet) throws SQLException {
        return new Book(
                resultSet.getInt("id"),
                resultSet.getString("kode"),
                resultSet.getString("judul"),
                resultSet.getString("pengarang"),
                resultSet.getString("penerbit"),
                resultSet.getInt("tahun_terbit"),
                resultSet.getString("kategori"),
                resultSet.getInt("stok"),
                resultSet.getString("status")
        );
    }
}
