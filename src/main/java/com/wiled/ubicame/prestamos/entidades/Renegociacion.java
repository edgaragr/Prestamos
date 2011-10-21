/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamos.entidades;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;

/**
 *
 * @author edgar
 */
@Entity
public class Renegociacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(strategy = GenerationType.TABLE, generator = "Renegociacion")
    @TableGenerator(name = "Renegociacion", table = "HIBERNATE_SEQUENCES", allocationSize = 1, initialValue = 0, pkColumnName = "SEQUENCE_NAME", valueColumnName = "SEQUENCE_NEXT_HI_VALUE", pkColumnValue = "Renegociacion")
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fecha;
    private double montoAgregado;
    private float nuevaTasa;
    private FormaPago nuevaFormaPago;
    @ManyToOne
    private Prestamo prestamo;

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
    }
    
    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getMontoAgregado() {
        return montoAgregado;
    }

    public void setMontoAgregado(double montoAgregado) {
        this.montoAgregado = montoAgregado;
    }

    public FormaPago getNuevaFormaPago() {
        return nuevaFormaPago;
    }

    public void setNuevaFormaPago(FormaPago nuevaFormaPago) {
        this.nuevaFormaPago = nuevaFormaPago;
    }

    public float getNuevaTasa() {
        return nuevaTasa;
    }

    public void setNuevaTasa(float nuevaTasa) {
        this.nuevaTasa = nuevaTasa;
    }

        
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Renegociacion)) {
            return false;
        }
        Renegociacion other = (Renegociacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.wiled.ubicame.prestamos.entidades.Renegociacion[ id=" + id + " ]";
    }
    
}
