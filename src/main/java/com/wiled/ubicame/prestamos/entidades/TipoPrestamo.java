/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamos.entidades;

/**
 *
 * @author edgar
 */
public enum TipoPrestamo {
    INTERES_CAPITAL, USURA;
  String tipoPrestamo;

    TipoPrestamo(String tipo) {
        tipoPago = tipo;
    }

    public String getTipoPrestamo() {
        return TipoPrestamo;
    }

    @Override
    public String toString() {
        return tipoPrestamo;
    }        
}
