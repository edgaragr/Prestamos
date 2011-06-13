/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamos.security;

import com.wiled.ubicame.prestamos.utils.PrestamoConstants;
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
        Controller database = Controller.getInstance(PrestamoConstants.PROD_PU);
        return database.validateUser(user, password);
    }    
}