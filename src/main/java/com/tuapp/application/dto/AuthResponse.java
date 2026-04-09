package com.tuapp.application.dto;

public class AuthResponse {
    private String token;
    private Long userId;
    private String username;
    private String email;
    private String role;

    public AuthResponse(String token, String username, String role) {
        this(token, null, username, null, role);
    }

    public AuthResponse(String token, Long userId, String username, String email, String role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}