/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamos.schedules;

import com.wiled.ubicame.prestamo.utils.PrestamoConstants;
import com.wiled.ubicame.prestamo.utils.PrestamoException;
import com.wiled.ubicame.prestamos.datalayer.Controller;
import com.wiled.ubicame.prestamos.entidades.Prestamo;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author Edgar Garcia
 */
public class PrestamoJob implements Job {
    public static String PRESTAMO = "prestamo";
    
    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        JobDetail jobDetail = jec.getJobDetail();
        
        JobDataMap map = jobDetail.getJobDataMap();
        int prestamoId = map.getInt(PRESTAMO);
        
        Controller database = Controller.getInstance(PrestamoConstants.PROD_PU);
        Prestamo prestamo = database.buscarPrestamo(prestamoId);
        
        //calcular cuota
        double cuota = 0;
        
        try {            
            cuota = database.amortizarPrestamo(prestamo.getMonto(), prestamo.getTasa());
        } catch (PrestamoException ex) {
            Logger.getLogger(PrestamoJob.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        double interesesAcumulados = prestamo.getInteresAcumulado();        
        prestamo.setInteresAcumulado(interesesAcumulados + cuota);
        
        database.merge(prestamo);
    }    
}
