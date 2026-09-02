/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.veterinaria.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author benny
 */
@Entity
@Table(name = "detalle_cita")
@NamedQueries({
    @NamedQuery(name = "DetalleCita.findAll", query = "SELECT d FROM DetalleCita d")})
public class DetalleCita implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_DETALLE_CITA")
    private Integer idDetalleCita;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "PRECIO_APLICADO")
    private BigDecimal precioAplicado;
    @Column(name = "ESTADO")
    private Boolean estado;
    @JoinColumn(name = "ID_CITA", referencedColumnName = "ID_CITA")
    @ManyToOne
    private Cita idCita;
    @JoinColumn(name = "ID_MEDICAMENTO", referencedColumnName = "ID_MEDICAMENTO")
    @ManyToOne
    private Medicamentos idMedicamento;
    @JoinColumn(name = "ID_TRATAMIENTO", referencedColumnName = "ID_TRATAMIENTO")
    @ManyToOne
    private Tratamiento idTratamiento;

    public DetalleCita() {
    }

    public DetalleCita(Integer idDetalleCita) {
        this.idDetalleCita = idDetalleCita;
    }

    public Integer getIdDetalleCita() {
        return idDetalleCita;
    }

    public void setIdDetalleCita(Integer idDetalleCita) {
        this.idDetalleCita = idDetalleCita;
    }

    public BigDecimal getPrecioAplicado() {
        return precioAplicado;
    }

    public void setPrecioAplicado(BigDecimal precioAplicado) {
        this.precioAplicado = precioAplicado;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Cita getIdCita() {
        return idCita;
    }

    public void setIdCita(Cita idCita) {
        this.idCita = idCita;
    }

    public Medicamentos getIdMedicamento() {
        return idMedicamento;
    }

    public void setIdMedicamento(Medicamentos idMedicamento) {
        this.idMedicamento = idMedicamento;
    }

    public Tratamiento getIdTratamiento() {
        return idTratamiento;
    }

    public void setIdTratamiento(Tratamiento idTratamiento) {
        this.idTratamiento = idTratamiento;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDetalleCita != null ? idDetalleCita.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DetalleCita)) {
            return false;
        }
        DetalleCita other = (DetalleCita) object;
        if ((this.idDetalleCita == null && other.idDetalleCita != null) || (this.idDetalleCita != null && !this.idDetalleCita.equals(other.idDetalleCita))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.proyecto.veterinaria.entity.DetalleCita[ idDetalleCita=" + idDetalleCita + " ]";
    }
    
}
