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
public class PagoInteres implements Serializable, Pago {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(strategy = GenerationType.TABLE, generator = "Prestamo")
    @TableGenerator(name = "Prestamo", table = "HIBERNATE_SEQUENCES", allocationSize = 1, initialValue = 0, pkColumnName = "SEQUENCE_NAME", valueColumnName = "SEQUENCE_NEXT_HI_VALUE", pkColumnValue = "Prestamo")
    private Long id;
    private double monto;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fecha;
    private double mora;
    @ManyToOne
    private Prestamo prestamo;

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
    }
        
    @Override
    public double getMora() {
        return mora;
    }

    public void setMora(double mora) {
        this.mora = mora;
    }
        
    @Override
    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @Override
    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
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
        if (!(object instanceof PagoInteres)) {
            return false;
        }
        PagoInteres other = (PagoInteres) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.wiled.ubicame.prestamos.entidades.PagoInteres[ id=" + id + " ]";
    }

    @Override
    public String getTipoPago() {
        return TipoPago.PAGO_INTERES.getTipoPago();
    }    
}
