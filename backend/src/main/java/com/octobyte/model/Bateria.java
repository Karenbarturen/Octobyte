package com.octobyte.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bateria")
public class Bateria {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bateria_seq")
    @SequenceGenerator(name = "bateria_seq", sequenceName = "bateria_id_bateria_seq", allocationSize = 1)
    @Column(name = "id_bateria")
    private Integer idBateria;

    @Column(name = "energia_guardada", nullable = false)
    private Integer energiaGuardada;

    @Column(name = "nivel_porcentaje", nullable = false)
    private Integer nivelPorcentaje;

    @ManyToOne
    @JoinColumn(name = "id_operativo", nullable = false)
    private Operativo operativo;

    public Bateria() {}

    public Integer getIdBateria() {
        return idBateria;
    }

    public void setIdBateria(Integer idBateria) {
        this.idBateria = idBateria;
    }

    public Integer getEnergiaGuardada() {
        return energiaGuardada;
    }

    public void setEnergiaGuardada(Integer energiaGuardada) {
        this.energiaGuardada = energiaGuardada;
    }

    public Integer getNivelPorcentaje() {
        return nivelPorcentaje;
    }

    public void setNivelPorcentaje(Integer nivelPorcentaje) {
        this.nivelPorcentaje = nivelPorcentaje;
    }

    public Operativo getOperativo() {
        return operativo;
    }

    public void setOperativo(Operativo operativo) {
        this.operativo = operativo;
    }
}
