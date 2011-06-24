/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamos.entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;

/**
 *
 * @author edgar
 */
@Entity
public class Prestamo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Prestamo")
    @TableGenerator(name = "Prestamo", table = "HIBERNATE_SEQUENCES", allocationSize = 1, initialValue = 0, pkColumnName = "SEQUENCE_NAME", valueColumnName = "SEQUENCE_NEXT_HI_VALUE", pkColumnValue = "Prestamo")
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fecha;
    private double monto;
    private String comentario;
    private float tasa;
    @Enumerated(EnumType.STRING)
    private FormaPago formaPago;
    @ManyToOne
    private Cliente cliente;
    @OneToMany(mappedBy = "prestamo", cascade = CascadeType.ALL)
    private List<PagoInteres> pagos;
    @OneToMany(mappedBy = "prestamo", cascade = CascadeType.ALL)
    private List<Abono> abonos;
    @OneToMany(mappedBy = "prestamo", cascade = CascadeType.ALL)
    private List<Renegociacion> renegociaciones;

    public List<Renegociacion> getRenegociaciones() {
        return renegociaciones;
    }

    public void setRenegociaciones(List<Renegociacion> renegociaciones) {
        this.renegociaciones = renegociaciones;
    }

    public Prestamo() {
        abonos = new ArrayList<Abono>();
        pagos = new ArrayList<PagoInteres>();
        renegociaciones = new ArrayList<Renegociacion>();
    }

    public FormaPago getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(FormaPago formaPago) {
        this.formaPago = formaPago;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<Abono> getAbonos() {
        return abonos;
    }

    public void setAbonos(List<Abono> abonos) {
        this.abonos = abonos;
    }

    public List<PagoInteres> getPagos() {
        return pagos;
    }

    public void setPagos(List<PagoInteres> pagos) {
        this.pagos = pagos;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public float getTasa() {
        return tasa;
    }

    public void setTasa(float tasa) {
        this.tasa = tasa;
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
        if (!(object instanceof Prestamo)) {
            return false;
        }
        Prestamo other = (Prestamo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.wiled.ubicame.prestamos.entidades.Prestamo[ id=" + id + " ]";
    }
}
