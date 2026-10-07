package com.sams.model;

/**
 * Lecturer model representing a teaching faculty member.
 */
public class Lecturer {

    private int id;
    private String name;
    private String email;
    private String phone;
    private int userId;
    private String username; // Associated user account username

    public Lecturer() {
    }

    public Lecturer(int id, String name, String email, String phone, int userId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userId = userId;
    }

    public Lecturer(String name, String email, String phone, int userId) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userId = userId;
    }

    public Lecturer(int id, String name, String email, String phone, int userId, String username) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userId = userId;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return name;
    }
}
