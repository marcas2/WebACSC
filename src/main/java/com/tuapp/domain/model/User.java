package com.tuapp.domain.model;

import java.time.LocalDateTime;

public class User {

    private Long id;
    private String nombreUsuario;
    private String email;
    private String hashContrasena;
    private Long roleId;
    private String roleName;
    private LocalDateTime creadoEn;

    public User(Long id, String nombreUsuario, String email, String hashContrasena,
                Long roleId, String roleName, LocalDateTime creadoEn) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.hashContrasena = hashContrasena;
        this.roleId = roleId;
        this.roleName = roleName;
        this.creadoEn = creadoEn;
    }

    public Long getId() { return id; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getEmail() { return email; }
    public String getHashContrasena() { return hashContrasena; }
    public Long getRoleId() { return roleId; }
    public String getRoleName() { return roleName; }
    public LocalDateTime getCreadoEn() { return creadoEn; }

    public void setId(Long id) { this.id = id; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public void setEmail(String email) { this.email = email; }
    public void setHashContrasena(String hashContrasena) { this.hashContrasena = hashContrasena; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}