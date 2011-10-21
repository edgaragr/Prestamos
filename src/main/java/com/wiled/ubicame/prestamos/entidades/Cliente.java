/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamos.entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.TableGenerator;

/**
 *
 * @author edgar
 */
@Entity
@NamedQueries({
    @NamedQuery(name="Cliente.buscarNombre", query="Select c from Cliente c where UPPER(c.nombre) like :nombre"),
    @NamedQuery(name="Cliente.buscarApellido", query="Select c from Cliente c where UPPER(c.apellido) like :apellido"),
    @NamedQuery(name="Cliente.buscarCedula", query="Select c from Cliente c where c.cedula = :cedula"),
    @NamedQuery(name="Cliente.buscarTelefono", query="Select c from Cliente c where c.telefono = :telefono"),
    @NamedQuery(name="Cliente.getAll", query="Select c from Cliente c")
})
public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(strategy = GenerationType.TABLE, generator = "Cliente")
    @TableGenerator(name = "Cliente", table = "HIBERNATE_SEQUENCES", allocationSize = 1, initialValue = 0, pkColumnName = "SEQUENCE_NAME", valueColumnName = "SEQUENCE_NEXT_HI_VALUE", pkColumnValue = "Cliente")
    private Long id;
    private String nombre;
    private String apellido;
    private String cedula;
    private String telefono;
    @OneToMany(mappedBy = "cliente", cascade= CascadeType.ALL)
    private List<Prestamo> prestamos;

    public Cliente() {
        prestamos = new ArrayList<Prestamo>();
    }
    
    public List<Prestamo> getPrestamos() {
        return prestamos;
    }

    public void setPrestamos(List<Prestamo> prestamos) {
        this.prestamos = prestamos;
    }
        
    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
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
        if (!(object instanceof Cliente)) {
            return false;
        }
        Cliente other = (Cliente) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return (nombre + " " + apellido).toUpperCase();
    }
    
}
