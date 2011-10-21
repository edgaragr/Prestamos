/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamos.entidades;

/**
 *
 * @author edgar
 */
public enum FormaPago {
    DIARIO("Diario"), SEMANAL("Semanal"), QUINCENAL("Quincenal"), MENSUAL("Mensual");   
    
    String formaPago;
    FormaPago(String forma) {
        formaPago = forma;
    }

    public String getFormaPago() {
        return formaPago;
    }        
}
