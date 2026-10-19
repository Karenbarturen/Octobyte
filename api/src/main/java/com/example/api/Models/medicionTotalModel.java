package com.example.api.Models;

import jakarta.persistence.*;

@Entity
@Table(name = "\"medicionTotal\"")
public class medicionTotalModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idMedicionTotal\"")
    private Integer idMedicionTotal;

    @Column(name = "\"idBateria\"", nullable = false)
    private Integer idBateria;

    @Column(name = "\"idElectricidad\"", nullable = false)
    private Integer idElectricidad;

    @Column(name = "\"idMedicion\"", nullable = false)
    private Integer idMedicion;

    public medicionTotalModel() {
    }

    public Integer getIdMedicionTotal() { return idMedicionTotal; }
    public void setIdMedicionTotal(Integer idMedicionTotal) { this.idMedicionTotal = idMedicionTotal; }

    public Integer getIdBateria() { return idBateria; }
    public void setIdBateria(Integer idBateria) { this.idBateria = idBateria; }

    public Integer getIdElectricidad() { return idElectricidad; }
    public void setIdElectricidad(Integer idElectricidad) { this.idElectricidad = idElectricidad; }

    public Integer getIdMedicion() { return idMedicion; }
    public void setIdMedicion(Integer idMedicion) { this.idMedicion = idMedicion; }
}
