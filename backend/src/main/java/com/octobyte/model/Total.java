package com.octobyte.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "total")
public class Total {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "total_seq")
    @SequenceGenerator(name = "total_seq", sequenceName = "total_id_total_seq", allocationSize = 1)
    @Column(name = "id_total")
    private Integer idTotal;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "num_alerta", nullable = false)
    private Integer numAlerta;

    @OneToOne
    @JoinColumn(name = "id_medicion_total", nullable = false)
    private MedicionTotal medicionTotal;

    @Transient
    private String source;

    public Total() {}

    public Integer getIdTotal() {
        return idTotal;
    }

    public void setIdTotal(Integer idTotal) {
        this.idTotal = idTotal;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Integer getNumAlerta() {
        return numAlerta;
    }

    public void setNumAlerta(Integer numAlerta) {
        this.numAlerta = numAlerta;
    }

    public MedicionTotal getMedicionTotal() {
        return medicionTotal;
    }

    public void setMedicionTotal(MedicionTotal medicionTotal) {
        this.medicionTotal = medicionTotal;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
