package com.octobyte.dto;

import jakarta.validation.constraints.NotBlank;

public class OperationModeDto {

    @NotBlank
    private String nombre;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
