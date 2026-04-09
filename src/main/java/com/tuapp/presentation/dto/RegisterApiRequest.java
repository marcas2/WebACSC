package com.tuapp.presentation.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterApiRequest {

    @NotBlank(message = "nombreUsuario es requerido")
    @Size(min = 3, max = 50, message = "nombreUsuario debe tener entre 3 y 50 caracteres")
    @JsonAlias("username")
    private String nombreUsuario;

    @NotBlank(message = "email es requerido")
    @Email(message = "email invalido")
    private String email;

    @NotBlank(message = "contrasena es requerida")
    @Size(min = 8, message = "contrasena debe tener al menos 8 caracteres")
    @JsonAlias("password")
    private String contrasena;

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}