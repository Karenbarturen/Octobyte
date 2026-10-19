package com.example.api.Models;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Electricidad\"")
public class ElectricidadModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idElectricidad\"")
    private Integer idElectricidad;

    @Column(name = "\"voltaje\"", nullable = false)
    private Integer voltaje;

    @Column(name = "\"corriente\"", nullable = false)
    private Integer corriente;

    @Column(name = "\"generacionElectricidad\"", nullable = false)
    private Integer generacionElectricidad;

    @Column(name = "\"consumoElectricidad\"", nullable = false)
    private Integer consumoElectricidad;

    public ElectricidadModel() {
    }

    public Integer getIdElectricidad() { return idElectricidad; }
    public void setIdElectricidad(Integer idElectricidad) { this.idElectricidad = idElectricidad; }

    public Integer getVoltaje() { return voltaje; }
    public void setVoltaje(Integer voltaje) { this.voltaje = voltaje; }

    public Integer getCorriente() { return corriente; }
    public void setCorriente(Integer corriente) { this.corriente = corriente; }

    public Integer getGeneracionElectricidad() { return generacionElectricidad; }
    public void setGeneracionElectricidad(Integer generacionElectricidad) { this.generacionElectricidad = generacionElectricidad; }

    public Integer getConsumoElectricidad() { return consumoElectricidad; }
    public void setConsumoElectricidad(Integer consumoElectricidad) { this.consumoElectricidad = consumoElectricidad; }
}
