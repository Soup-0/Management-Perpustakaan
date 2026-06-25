package com.azriel.perpustakaan;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanTransactionDAO {
    public void insert(LoanTransaction transaction) {
        String sql = "INSERT INTO loan_transactions (transaction_code, book_id, book_kode, book_title, member_name, borrow_date, due_date, status, fine) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, transaction.getTransactionCode());
            statement.setInt(2, transaction.getBookId());
            statement.setString(3, transaction.getBookKode());
            statement.setString(4, transaction.getBookTitle());
            statement.setString(5, transaction.getMemberName());
            statement.setDate(6, Date.valueOf(transaction.getBorrowDate()));
            statement.setDate(7, Date.valueOf(transaction.getDueDate()));
            statement.setString(8, transaction.getStatus());
            statement.setDouble(9, transaction.getFine());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menyimpan transaksi peminjaman", e);
        }
    }

    public void update(LoanTransaction transaction) {
        String sql = "UPDATE loan_transactions SET status = ?, return_date = ?, fine = ? WHERE id = ?";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, transaction.getStatus());
            statement.setDate(2, transaction.getReturnDate() == null ? null : Date.valueOf(transaction.getReturnDate()));
            statement.setDouble(3, transaction.getFine());
            statement.setInt(4, transaction.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal memperbarui transaksi peminjaman", e);
        }
    }

    public LoanTransaction findActiveByTransactionCode(String transactionCode) {
        String sql = "SELECT * FROM loan_transactions WHERE transaction_code = ? AND status = 'dipinjam'";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, transactionCode);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return buildTransaction(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil transaksi", e);
        }
        return null;
    }

    public LoanTransaction findActiveByBookKode(String bookKode) {
        String sql = "SELECT * FROM loan_transactions WHERE book_kode = ? AND status = 'dipinjam' ORDER BY borrow_date DESC LIMIT 1";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, bookKode);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return buildTransaction(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil transaksi", e);
        }
        return null;
    }

    public List<LoanTransaction> searchActive(String keyword) {
        List<LoanTransaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM loan_transactions WHERE status = 'dipinjam' AND (LOWER(transaction_code) LIKE ? OR LOWER(book_kode) LIKE ? OR LOWER(book_title) LIKE ? OR LOWER(member_name) LIKE ?) ORDER BY borrow_date";
        String searchValue = "%" + (keyword == null ? "" : keyword.toLowerCase()) + "%";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, searchValue);
            statement.setString(2, searchValue);
            statement.setString(3, searchValue);
            statement.setString(4, searchValue);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    transactions.add(buildTransaction(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mencari transaksi peminjaman", e);
        }
        return transactions;
    }

    public List<LoanTransaction> listByStatus(String status) {
        List<LoanTransaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM loan_transactions WHERE status = ? ORDER BY borrow_date";
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    transactions.add(buildTransaction(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil transaksi berdasarkan status", e);
        }
        return transactions;
    }

    private LoanTransaction buildTransaction(ResultSet rs) throws SQLException {
        LocalDate borrowDate = rs.getDate("borrow_date") == null ? null : rs.getDate("borrow_date").toLocalDate();
        LocalDate dueDate = rs.getDate("due_date") == null ? null : rs.getDate("due_date").toLocalDate();
        LocalDate returnDate = rs.getDate("return_date") == null ? null : rs.getDate("return_date").toLocalDate();
        return new LoanTransaction(
                rs.getInt("id"),
                rs.getString("transaction_code"),
                rs.getInt("book_id"),
                rs.getString("book_kode"),
                rs.getString("book_title"),
                rs.getString("member_name"),
                borrowDate,
                dueDate,
                returnDate,
                rs.getString("status"),
                rs.getDouble("fine")
        );
    }
}
