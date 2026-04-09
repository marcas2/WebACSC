package com.tuapp.presentation.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public class LoginApiRequest {

    @NotBlank(message = "nombreUsuario es requerido")
    @JsonAlias({"username", "usuario"})
    private String nombreUsuario;

    @NotBlank(message = "contrasena es requerida")
    @JsonAlias({"password"})
    private String contrasena;

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}