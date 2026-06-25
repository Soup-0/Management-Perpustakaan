package com.azriel.perpustakaan;

import java.time.LocalDate;

public class LoanTransaction {
    private Integer id;
    private String transactionCode;
    private Integer bookId;
    private String bookKode;
    private String bookTitle;
    private String memberName;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;
    private double fine;

    public LoanTransaction() {
    }

    public LoanTransaction(Integer id, String transactionCode, Integer bookId, String bookKode, String bookTitle, String memberName, LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate, String status, double fine) {
        this.id = id;
        this.transactionCode = transactionCode;
        this.bookId = bookId;
        this.bookKode = bookKode;
        this.bookTitle = bookTitle;
        this.memberName = memberName;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.fine = fine;
    }

    public LoanTransaction(String transactionCode, Integer bookId, String bookKode, String bookTitle, String memberName, LocalDate borrowDate, LocalDate dueDate, String status) {
        this(null, transactionCode, bookId, bookKode, bookTitle, memberName, borrowDate, dueDate, null, status, 0.0);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getBookKode() {
        return bookKode;
    }

    public void setBookKode(String bookKode) {
        this.bookKode = bookKode;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }
}
