package com.example.api.Models;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Medicion\"")
public class MedicionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idMedicion\"")
    private Integer idMedicion;

    @Column(name = "\"viento\"", nullable = false)
    private Integer viento;

    @Column(name = "\"temperatura\"", nullable = false)
    private Integer temperatura;

    @Column(name = "\"lumens\"", nullable = false)
    private Integer lumens;

    public MedicionModel() {
    }

    public Integer getIdMedicion() { return idMedicion; }
    public void setIdMedicion(Integer idMedicion) { this.idMedicion = idMedicion; }

    public Integer getViento() { return viento; }
    public void setViento(Integer viento) { this.viento = viento; }

    public Integer getTemperatura() { return temperatura; }
    public void setTemperatura(Integer temperatura) { this.temperatura = temperatura; }

    public Integer getLumens() { return lumens; }
    public void setLumens(Integer lumens) { this.lumens = lumens; }
}
