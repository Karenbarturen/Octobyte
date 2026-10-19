package com.example.api.Models;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Total\"")
public class totalModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idTotal\"")
    private Integer idTotal;

    @Column(name = "\"fecha\"", nullable = false)
    private LocalDateTime fecha;

    @PrePersist
    protected void onCreate() {
        if (this.fecha == null) {
            this.fecha = LocalDateTime.now();
        }
    }

    @Column(name = "\"numAlerta\"", nullable = false)
    private Integer numAlerta;

    @Column(name = "\"idMedicionTotal\"", nullable = false)
    private Integer idMedicionTotal;

    public totalModel() {}

    public Integer getIdTotal() { return idTotal; }
    public void setIdTotal(Integer idTotal) { this.idTotal = idTotal; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public Integer getNumAlerta() { return numAlerta; }
    public void setNumAlerta(Integer numAlerta) { this.numAlerta = numAlerta; }

    public Integer getIdMedicionTotal() { return idMedicionTotal; }
    public void setIdMedicionTotal(Integer idMedicionTotal) { this.idMedicionTotal = idMedicionTotal; }
}
