/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamo.security;

import com.wiled.ubicame.prestamo.utils.PrestamoConstants;
import com.wiled.ubicame.prestamos.datalayer.Controller;
import org.jdesktop.swingx.auth.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Edgar Garcia
 */
public class PrestamoLoginService extends LoginService {
    private final Logger log = LoggerFactory.getLogger(PrestamoLoginService.class);
    
    @Override
    public boolean authenticate(String user, char[] password, String server) throws Exception {
        log.info("********************** Verificando informacion para el usuario: " + user);
        Controller database = Controller.getInstance(PrestamoConstants.PROD_PU);
        return database.validateUser(user, password);
    }    
}