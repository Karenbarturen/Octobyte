package com.example.api.Models;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Operativo\"")
public class OperativoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idOperativo\"")
    private Integer idOperativo;

    @Column(name = "\"nombre\"", nullable = false)
    private String nombre;

    public OperativoModel() {
    }

    public Integer getIdOperativo() { return idOperativo; }
    public void setIdOperativo(Integer idOperativo) { this.idOperativo = idOperativo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
