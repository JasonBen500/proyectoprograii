package com.proyecto.veterinaria.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "historial")
@Getter
@Setter
@NoArgsConstructor
public class Historial implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_HISTORIAL")
    private Integer idHistorial;

    @Column(name = "FECHA")
    private Date fecha;

    @Column(name = "USUARIO")
    private String usuario;

    @Column(name = "ACCION")
    private String accion;

    @Column(name = "ENTIDAD")
    private String entidad;

    @Column(name = "DESCRIPCION")
    private String descripcion;
}
