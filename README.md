# Aplikasi Manajemen Buku Perpustakaan

A simple Java Swing application for managing a library book system using an embedded H2 database.

## Struktur Proyek

- `pom.xml` - konfigurasi Maven
- `src/main/java/com/azriel/perpustakaan` - kode sumber aplikasinya
- `Database.java` - koneksi dan inisialisasi tabel
- `Person.java` - model data sederhana
- `PersonDAO.java` - operasi CRUD ke database
- `PersonForm.java` - antarmuka pengguna Swing
- `LoginForm.java` - form login dan autentikasi user
- `App.java` - entry point aplikasi

## Jalankan Aplikasi

1. Pastikan Java JDK sudah terpasang.
2. Pasang Maven jika belum tersedia.
3. Di terminal direktori C:\Kuliah\Management-Perpustakaan-main\PBO, jalankan:

   ```bash
   mvn package
   java -jar target/perpustakaan-management-1.0.0.jar
   ```

Aplikasi akan menampilkan halaman login terlebih dahulu. Akun awal yang sudah tersedia:

- username: `eden`
- password: `eden123`
- role: `manager`

Jika Maven tidak tersedia, tambahkan `h2-2.3.0.jar` ke classpath dan jalankan manual:

```bash
javac -cp "path\to\h2-2.3.0.jar" src\main\java\com\azriel\perpustakaan\*.java
java -cp "path\to\h2-2.3.0.jar;src\main\java" com.azriel.perpustakaan.App
```
