package com.azriel.perpustakaan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:h2:file:./data/perpustakaan_db;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    static {
        try {
            initDatabase();
        } catch (SQLException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static void initDatabase() throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "id IDENTITY PRIMARY KEY,"
                    + "username VARCHAR(100) UNIQUE NOT NULL,"
                    + "password VARCHAR(255) NOT NULL,"
                    + "role VARCHAR(50) NOT NULL,"
                    + "photo_path VARCHAR(255)"
                    + ")");
            statement.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS photo_path VARCHAR(255)");
            statement.execute("CREATE TABLE IF NOT EXISTS activity_logs ("
                    + "id IDENTITY PRIMARY KEY,"
                    + "username VARCHAR(100) NOT NULL,"
                    + "action VARCHAR(200) NOT NULL,"
                    + "details VARCHAR(1000),"
                    + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")");
            statement.execute("INSERT INTO users (username, password, role, photo_path) "
                    + "SELECT 'eden', 'eden123', 'manager', NULL "
                    + "WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'eden')");
            statement.execute("INSERT INTO users (username, password, role, photo_path) "
                    + "SELECT 'admin', 'admin123', 'admin', NULL "
                    + "WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin')");
            statement.execute("INSERT INTO users (username, password, role, photo_path) "
                    + "SELECT 'staff', 'staff123', 'staff', NULL "
                    + "WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'staff')");

            statement.execute("CREATE TABLE IF NOT EXISTS person ("
                    + "id IDENTITY PRIMARY KEY,"
                    + "name VARCHAR(255) NOT NULL,"
                    + "age INT NOT NULL,"
                    + "email VARCHAR(255)"
                    + ")");

            statement.execute("CREATE TABLE IF NOT EXISTS books ("
                    + "id IDENTITY PRIMARY KEY,"
                    + "kode VARCHAR(100) UNIQUE NOT NULL,"
                    + "judul VARCHAR(255) NOT NULL,"
                    + "pengarang VARCHAR(255) NOT NULL,"
                    + "penerbit VARCHAR(255) NOT NULL,"
                    + "tahun_terbit INT NOT NULL,"
                    + "kategori VARCHAR(100) NOT NULL,"
                    + "stok INT NOT NULL,"
                    + "status VARCHAR(50) NOT NULL DEFAULT 'tersedia'"
                    + ")");
            statement.execute("ALTER TABLE books ADD COLUMN IF NOT EXISTS status VARCHAR(50) NOT NULL DEFAULT 'tersedia'");

            statement.execute("CREATE TABLE IF NOT EXISTS loan_transactions ("
                    + "id IDENTITY PRIMARY KEY,"
                    + "transaction_code VARCHAR(50) UNIQUE NOT NULL,"
                    + "book_id INT NOT NULL,"
                    + "book_kode VARCHAR(100) NOT NULL,"
                    + "book_title VARCHAR(255) NOT NULL,"
                    + "member_name VARCHAR(255) NOT NULL,"
                    + "borrow_date DATE NOT NULL,"
                    + "due_date DATE NOT NULL,"
                    + "return_date DATE,"
                    + "status VARCHAR(50) NOT NULL,"
                    + "fine DOUBLE DEFAULT 0"
                    + ")");

            statement.execute("INSERT INTO books (kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status) "
                    + "SELECT 'B001', 'To Kill a Mockingbird', 'Harper Lee', 'J. B. Lippincott & Co.', 1960, 'Fiksi', 5, 'tersedia' "
                    + "WHERE NOT EXISTS (SELECT 1 FROM books WHERE kode = 'B001')");
            statement.execute("INSERT INTO books (kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status) "
                    + "SELECT 'B002', '1984', 'George Orwell', 'Secker & Warburg', 1949, 'Dystopia', 4, 'dipinjam' "
                    + "WHERE NOT EXISTS (SELECT 1 FROM books WHERE kode = 'B002')");
            statement.execute("INSERT INTO books (kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status) "
                    + "SELECT 'B003', 'Pride and Prejudice', 'Jane Austen', 'T. Egerton', 1813, 'Romantis', 3, 'tersedia' "
                    + "WHERE NOT EXISTS (SELECT 1 FROM books WHERE kode = 'B003')");
            statement.execute("INSERT INTO books (kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status) "
                    + "SELECT 'B004', 'The Great Gatsby', 'F. Scott Fitzgerald', 'Charles Scribner''s Sons', 1925, 'Klasik', 4, 'tersedia' "
                    + "WHERE NOT EXISTS (SELECT 1 FROM books WHERE kode = 'B004')");
            statement.execute("INSERT INTO books (kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status) "
                    + "SELECT 'B005', 'Harry Potter and the Sorcerer''s Stone', 'J.K. Rowling', 'Bloomsbury', 1997, 'Fantasi', 6, 'dipinjam' "
                    + "WHERE NOT EXISTS (SELECT 1 FROM books WHERE kode = 'B005')");
            statement.execute("INSERT INTO books (kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status) "
                    + "SELECT 'B006', 'The Hobbit', 'J.R.R. Tolkien', 'George Allen & Unwin', 1937, 'Fantasi', 5, 'tersedia' "
                    + "WHERE NOT EXISTS (SELECT 1 FROM books WHERE kode = 'B006')");
            statement.execute("INSERT INTO books (kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status) "
                    + "SELECT 'B007', 'The Catcher in the Rye', 'J.D. Salinger', 'Little, Brown and Company', 1951, 'Fiksi', 3, 'rusak' "
                    + "WHERE NOT EXISTS (SELECT 1 FROM books WHERE kode = 'B007')");
            statement.execute("INSERT INTO books (kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status) "
                    + "SELECT 'B008', 'The Lord of the Rings', 'J.R.R. Tolkien', 'George Allen & Unwin', 1954, 'Fantasi', 4, 'tersedia' "
                    + "WHERE NOT EXISTS (SELECT 1 FROM books WHERE kode = 'B008')");
            statement.execute("INSERT INTO books (kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status) "
                    + "SELECT 'B009', 'Moby-Dick', 'Herman Melville', 'Richard Bentley', 1851, 'Petualangan', 2, 'tersedia' "
                    + "WHERE NOT EXISTS (SELECT 1 FROM books WHERE kode = 'B009')");
            statement.execute("INSERT INTO books (kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status) "
                    + "SELECT 'B010', 'War and Peace', 'Leo Tolstoy', 'The Russian Messenger', 1869, 'Sejarah', 2, 'tersedia' "
                    + "WHERE NOT EXISTS (SELECT 1 FROM books WHERE kode = 'B010')");
            statement.execute("INSERT INTO books (kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status) "
                    + "SELECT 'B011', 'The Diary of a Young Girl', 'Anne Frank', 'Contact Publishing', 1947, 'Biografi', 3, 'dipinjam' "
                    + "WHERE NOT EXISTS (SELECT 1 FROM books WHERE kode = 'B011')");
            statement.execute("INSERT INTO books (kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status) "
                    + "SELECT 'B012', 'The Alchemist', 'Paulo Coelho', 'HarperTorch', 1988, 'Inspirasi', 5, 'tersedia' "
                    + "WHERE NOT EXISTS (SELECT 1 FROM books WHERE kode = 'B012')");
            statement.execute("INSERT INTO books (kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status) "
                    + "SELECT 'B013', 'Crime and Punishment', 'Fyodor Dostoevsky', 'The Russian Messenger', 1866, 'Klasik', 3, 'tersedia' "
                    + "WHERE NOT EXISTS (SELECT 1 FROM books WHERE kode = 'B013')");
            statement.execute("INSERT INTO books (kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status) "
                    + "SELECT 'B014', 'The Little Prince', 'Antoine de Saint-Exupéry', 'Reynal & Hitchcock', 1943, 'Dongeng', 5, 'dipinjam' "
                    + "WHERE NOT EXISTS (SELECT 1 FROM books WHERE kode = 'B014')");
            statement.execute("INSERT INTO books (kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status) "
                    + "SELECT 'B015', 'The Da Vinci Code', 'Dan Brown', 'Doubleday', 2003, 'Thriller', 4, 'tersedia' "
                    + "WHERE NOT EXISTS (SELECT 1 FROM books WHERE kode = 'B015')");
            statement.execute("INSERT INTO books (kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status) "
                    + "SELECT 'B016', 'The Chronicles of Narnia', 'C.S. Lewis', 'Geoffrey Bles', 1950, 'Fantasi', 4, 'tersedia' "
                    + "WHERE NOT EXISTS (SELECT 1 FROM books WHERE kode = 'B016')");
            statement.execute("INSERT INTO books (kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status) "
                    + "SELECT 'B017', 'The Kite Runner', 'Khaled Hosseini', 'Riverhead Books', 2003, 'Drama', 3, 'tersedia' "
                    + "WHERE NOT EXISTS (SELECT 1 FROM books WHERE kode = 'B017')");
            statement.execute("INSERT INTO books (kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status) "
                    + "SELECT 'B018', 'Sapiens', 'Yuval Noah Harari', 'Harvill Secker', 2011, 'Nonfiksi', 4, 'dipinjam' "
                    + "WHERE NOT EXISTS (SELECT 1 FROM books WHERE kode = 'B018')");
            statement.execute("INSERT INTO books (kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status) "
                    + "SELECT 'B019', 'The Girl with the Dragon Tattoo', 'Stieg Larsson', 'Norstedts Förlag', 2005, 'Thriller', 3, 'tersedia' "
                    + "WHERE NOT EXISTS (SELECT 1 FROM books WHERE kode = 'B019')");
            statement.execute("INSERT INTO books (kode, judul, pengarang, penerbit, tahun_terbit, kategori, stok, status) "
                    + "SELECT 'B020', 'The Hunger Games', 'Suzanne Collins', 'Scholastic Press', 2008, 'Fantasi', 5, 'tersedia' "
                    + "WHERE NOT EXISTS (SELECT 1 FROM books WHERE kode = 'B020')");
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("H2 JDBC driver tidak ditemukan", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
