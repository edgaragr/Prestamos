/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamo.utils;

/**
 *
 * @author edgar
 */
public class PrestamoUtils {
    public static float amortizarPrestamo(double monto, float tasa) {
        return 0f;
    }
    
    public static boolean containsOnlyNumbers(String str) {
        if (str == null || str.length() == 0)
            return false;
        
        //Replace '-'
        str = str.replaceAll("-", "");
        
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i)))
                return false;
        }
        
        return true;
    }
    
    public static  boolean isCedulaSizeValid(String cedula) {
        if(cedula.length() == 11) return true;
        return false;
    }
    
    public static boolean isTelefonoSizeValid(String telefono) {
        if(telefono.length() == 10) return true;
        return false;
    }
}
