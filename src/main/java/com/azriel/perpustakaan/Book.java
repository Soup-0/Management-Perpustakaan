package com.azriel.perpustakaan;

public class Book {
    private Integer id;
    private String kode;
    private String judul;
    private String pengarang;
    private String penerbit;
    private int tahunTerbit;
    private String kategori;
    private int stok;
    private String status;

    public Book() {
    }

    public Book(Integer id, String kode, String judul, String pengarang, String penerbit, int tahunTerbit, String kategori, int stok, String status) {
        this.id = id;
        this.kode = kode;
        this.judul = judul;
        this.pengarang = pengarang;
        this.penerbit = penerbit;
        this.tahunTerbit = tahunTerbit;
        this.kategori = kategori;
        this.stok = stok;
        this.status = status;
    }

    public Book(String kode, String judul, String pengarang, String penerbit, int tahunTerbit, String kategori, int stok, String status) {
        this(null, kode, judul, pengarang, penerbit, tahunTerbit, kategori, stok, status);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getPengarang() {
        return pengarang;
    }

    public void setPengarang(String pengarang) {
        this.pengarang = pengarang;
    }

    public String getPenerbit() {
        return penerbit;
    }

    public void setPenerbit(String penerbit) {
        this.penerbit = penerbit;
    }

    public int getTahunTerbit() {
        return tahunTerbit;
    }

    public void setTahunTerbit(int tahunTerbit) {
        this.tahunTerbit = tahunTerbit;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
