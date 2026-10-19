package com.example.api.DTO;

import java.time.LocalDateTime;

public class ultimoTotalDTO {
    private Integer idTotal;
    private Long cantidad;
    private Integer numAlerta;
    private LocalDateTime fecha;
    private Integer idMedicionTotal;
    private Integer idFuente;
    private String nombreFuente;
    private String codigo;
    private String descripcion;
    private String unidad;
    private String estado;
    private String tipo;
    private String observacion;
    private Boolean activo;
    private String version;

    public ultimoTotalDTO() {
    }

    public ultimoTotalDTO(Integer idTotal, Long cantidad, Integer numAlerta, LocalDateTime fecha,
                         Integer idMedicionTotal, Integer idFuente, String nombreFuente, String codigo,
                         String descripcion, String unidad, String estado, String tipo,
                         String observacion, Boolean activo, String version) {
        this.idTotal = idTotal;
        this.cantidad = cantidad;
        this.numAlerta = numAlerta;
        this.fecha = fecha;
        this.idMedicionTotal = idMedicionTotal;
        this.idFuente = idFuente;
        this.nombreFuente = nombreFuente;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.unidad = unidad;
        this.estado = estado;
        this.tipo = tipo;
        this.observacion = observacion;
        this.activo = activo;
        this.version = version;
    }

    public Integer getIdTotal() { return idTotal; }
    public void setIdTotal(Integer idTotal) { this.idTotal = idTotal; }

    public Long getCantidad() { return cantidad; }
    public void setCantidad(Long cantidad) { this.cantidad = cantidad; }

    public Integer getNumAlerta() { return numAlerta; }
    public void setNumAlerta(Integer numAlerta) { this.numAlerta = numAlerta; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public Integer getIdMedicionTotal() { return idMedicionTotal; }
    public void setIdMedicionTotal(Integer idMedicionTotal) { this.idMedicionTotal = idMedicionTotal; }

    public Integer getIdFuente() { return idFuente; }
    public void setIdFuente(Integer idFuente) { this.idFuente = idFuente; }

    public String getNombreFuente() { return nombreFuente; }
    public void setNombreFuente(String nombreFuente) { this.nombreFuente = nombreFuente; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
}
