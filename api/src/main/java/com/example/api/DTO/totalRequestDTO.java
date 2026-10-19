package com.example.api.DTO;

import java.time.LocalDateTime;

public class totalRequestDTO {
    private Integer idMedicionTotal;
    private Integer numAlerta;
    private LocalDateTime fecha;

    private String nombreFuente;
    private Long cantidad;

    public Integer getIdMedicionTotal() { return idMedicionTotal; }
    public void setIdMedicionTotal(Integer idMedicionTotal) { this.idMedicionTotal = idMedicionTotal; }

    public Integer getNumAlerta() { return numAlerta; }
    public void setNumAlerta(Integer numAlerta) { this.numAlerta = numAlerta; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getNombreFuente() { return nombreFuente; }
    public void setNombreFuente(String nombreFuente) { this.nombreFuente = nombreFuente; }

    public Long getCantidad() { return cantidad; }
    public void setCantidad(Long cantidad) { this.cantidad = cantidad; }
}
