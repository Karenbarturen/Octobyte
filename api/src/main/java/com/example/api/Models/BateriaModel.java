package com.example.api.Models;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Bateria\"")
public class BateriaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idBateria\"")
    private Integer idBateria;

    @Column(name = "\"energiaGuardada\"", nullable = false)
    private Integer energiaGuardada;

    @Column(name = "\"nivelPorcentaje\"", nullable = false)
    private Integer nivelPorcentaje;

    @Column(name = "\"idOperativo\"", nullable = false)
    private Integer idOperativo;

    public BateriaModel() {
    }

    public Integer getIdBateria() { return idBateria; }
    public void setIdBateria(Integer idBateria) { this.idBateria = idBateria; }

    public Integer getEnergiaGuardada() { return energiaGuardada; }
    public void setEnergiaGuardada(Integer energiaGuardada) { this.energiaGuardada = energiaGuardada; }

    public Integer getNivelPorcentaje() { return nivelPorcentaje; }
    public void setNivelPorcentaje(Integer nivelPorcentaje) { this.nivelPorcentaje = nivelPorcentaje; }

    public Integer getIdOperativo() { return idOperativo; }
    public void setIdOperativo(Integer idOperativo) { this.idOperativo = idOperativo; }
}
