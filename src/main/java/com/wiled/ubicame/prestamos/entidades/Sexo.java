/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamos.entidades;

/**
 *
 * @author egarcia
 */
public enum Sexo {
    MASCULINO("Masculino"), FEMENINO("Femenino");
    
    String sexo;

    private Sexo(String sexo) {
        this.sexo = sexo;
    }

    public String getSexo() {
        return sexo;
    }

    @Override
    public String toString() {
        return sexo;
    }
}
