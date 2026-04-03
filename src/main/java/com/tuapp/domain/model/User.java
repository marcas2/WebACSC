package com.tuapp.domain.model;

import java.time.LocalDateTime;

public class User {

    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private Long roleId;
    private String roleName;
    private LocalDateTime createdAt;

    public User(Long id, String username, String email, String passwordHash,
                Long roleId, String roleName, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roleId = roleId;
        this.roleName = roleName;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Long getRoleId() { return roleId; }
    public String getRoleName() { return roleName; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}