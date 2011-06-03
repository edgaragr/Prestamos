/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamos.entidades;

/**
 *
 * @author edgar
 */
public enum CriterioBusqueda {
    CEDULA("Cedula"), TELEFONO("Telefono"), NOMBRE("Nombre"), APELLIDO("Apellido");
    
    String criterioBusqueda;
    CriterioBusqueda(String forma) {
        criterioBusqueda = forma;
    }

    public String getCriterioBusqueda() {
        return criterioBusqueda;
    } 
}
