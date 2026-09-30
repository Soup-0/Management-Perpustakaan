package com.azriel.perpustakaan;

public class User {
    private Integer id;
    private String username;
    private String password;
    private String role;
    private String photoPath;

    public User() {
    }

    public User(Integer id, String username, String password, String role, String photoPath) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.photoPath = photoPath;
    }

    public User(Integer id, String username, String password, String role) {
        this(id, username, password, role, null);
    }

    public User(String username, String password, String role) {
        this(null, username, password, role, null);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
