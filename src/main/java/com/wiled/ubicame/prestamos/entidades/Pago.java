/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamos.entidades;

import java.util.Date;

/**
 *
 * @author Edgar Garcia
 */
public interface Pago {
    public Date getFecha();
    public double getMora();
    public double getMonto();
    public String getTipoPago();
}
