/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamos.entidades;

/**
 *
 * @author Edgar Garcia
 */
public enum TipoPago {

    ABONO("Abono"), PAGO_INTERES("Pago Intereses");
    String tipoPago;

    TipoPago(String tipo) {
        tipoPago = tipo;
    }

    public String getTipoPago() {
        return tipoPago;
    }
}
