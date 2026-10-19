package com.octobyte.model;

import jakarta.persistence.*;

@Entity
@Table(name = "medicion")
public class Medicion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "medicion_seq")
    @SequenceGenerator(name = "medicion_seq", sequenceName = "medicion_id_medicion_seq", allocationSize = 1)
    @Column(name = "id_medicion")
    private Integer idMedicion;

    @Column(name = "viento", nullable = false)
    private Integer viento;

    @Column(name = "temperatura", nullable = false)
    private Integer temperatura;

    @Column(name = "lumens", nullable = false)
    private Integer lumens;

    public Medicion() {}

    public Integer getIdMedicion() {
        return idMedicion;
    }

    public void setIdMedicion(Integer idMedicion) {
        this.idMedicion = idMedicion;
    }

    public Integer getViento() {
        return viento;
    }

    public void setViento(Integer viento) {
        this.viento = viento;
    }

    public Integer getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Integer temperatura) {
        this.temperatura = temperatura;
    }

    public Integer getLumens() {
        return lumens;
    }

    public void setLumens(Integer lumens) {
        this.lumens = lumens;
    }
}
