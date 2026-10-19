package com.octobyte.model;

import jakarta.persistence.*;

@Entity
@Table(name = "medicion_total")
public class MedicionTotal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "medicion_total_seq")
    @SequenceGenerator(name = "medicion_total_seq", sequenceName = "medicion_total_id_medicion_total_seq", allocationSize = 1)
    @Column(name = "id_medicion_total")
    private Integer idMedicionTotal;

    @OneToOne
    @JoinColumn(name = "id_bateria", nullable = false)
    private Bateria bateria;

    @OneToOne
    @JoinColumn(name = "id_electricidad", nullable = false)
    private Electricidad electricidad;

    @OneToOne
    @JoinColumn(name = "id_medicion", nullable = false)
    private Medicion medicion;

    public MedicionTotal() {}

    public Integer getIdMedicionTotal() {
        return idMedicionTotal;
    }

    public void setIdMedicionTotal(Integer idMedicionTotal) {
        this.idMedicionTotal = idMedicionTotal;
    }

    public Bateria getBateria() {
        return bateria;
    }

    public void setBateria(Bateria bateria) {
        this.bateria = bateria;
    }

    public Electricidad getElectricidad() {
        return electricidad;
    }

    public void setElectricidad(Electricidad electricidad) {
        this.electricidad = electricidad;
    }

    public Medicion getMedicion() {
        return medicion;
    }

    public void setMedicion(Medicion medicion) {
        this.medicion = medicion;
    }
}
