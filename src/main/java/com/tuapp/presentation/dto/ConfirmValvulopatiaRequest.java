package com.tuapp.presentation.dto;

import jakarta.validation.constraints.NotNull;

public class ConfirmValvulopatiaRequest {

    @NotNull(message = "valvulopatia es obligatorio")
    private Boolean valvulopatia;

    public Boolean getValvulopatia() {
        return valvulopatia;
    }

    public void setValvulopatia(Boolean valvulopatia) {
        this.valvulopatia = valvulopatia;
    }
}