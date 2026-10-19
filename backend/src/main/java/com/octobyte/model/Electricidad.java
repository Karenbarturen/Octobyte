package com.octobyte.model;

import jakarta.persistence.*;

@Entity
@Table(name = "electricidad")
public class Electricidad {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "electricidad_seq")
    @SequenceGenerator(name = "electricidad_seq", sequenceName = "electricidad_id_electricidad_seq", allocationSize = 1)
    @Column(name = "id_electricidad")
    private Integer idElectricidad;

    @Column(name = "voltaje", nullable = false)
    private Integer voltaje;

    @Column(name = "corriente", nullable = false)
    private Integer corriente;

    @Column(name = "generacion_electricidad", nullable = false)
    private Integer generacionElectricidad;

    @Column(name = "consumo_electricidad", nullable = false)
    private Integer consumoElectricidad;

    public Electricidad() {}

    public Integer getIdElectricidad() {
        return idElectricidad;
    }

    public void setIdElectricidad(Integer idElectricidad) {
        this.idElectricidad = idElectricidad;
    }

    public Integer getVoltaje() {
        return voltaje;
    }

    public void setVoltaje(Integer voltaje) {
        this.voltaje = voltaje;
    }

    public Integer getCorriente() {
        return corriente;
    }

    public void setCorriente(Integer corriente) {
        this.corriente = corriente;
    }

    public Integer getGeneracionElectricidad() {
        return generacionElectricidad;
    }

    public void setGeneracionElectricidad(Integer generacionElectricidad) {
        this.generacionElectricidad = generacionElectricidad;
    }

    public Integer getConsumoElectricidad() {
        return consumoElectricidad;
    }

    public void setConsumoElectricidad(Integer consumoElectricidad) {
        this.consumoElectricidad = consumoElectricidad;
    }
}
