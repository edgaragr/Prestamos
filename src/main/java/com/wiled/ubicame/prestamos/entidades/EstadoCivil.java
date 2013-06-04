/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamos.entidades;

/**
 *
 * @author egarcia
 */
public enum EstadoCivil {
    SOLTERO("Soltero"), CASADO("Casado"), DIVORCIADO("Divorciado"), VIUDO("Viudo");
    
    String estadoCivil;

    private EstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    @Override
    public String toString() {
        return estadoCivil;
    }
}
