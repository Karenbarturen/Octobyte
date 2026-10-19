package com.octobyte.model;

import jakarta.persistence.*;

@Entity
@Table(name = "operativo")
public class Operativo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "operativo_seq")
    @SequenceGenerator(name = "operativo_seq", sequenceName = "operativo_id_operativo_seq", allocationSize = 1)
    @Column(name = "id_operativo")
    private Integer idOperativo;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "activo", nullable = false)
    private boolean activo = false;

    public Operativo() {}

    public Integer getIdOperativo() {
        return idOperativo;
    }

    public void setIdOperativo(Integer idOperativo) {
        this.idOperativo = idOperativo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
