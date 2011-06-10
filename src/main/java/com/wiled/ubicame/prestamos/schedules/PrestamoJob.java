/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamos.schedules;

import com.wiled.ubicame.prestamos.utils.PrestamoConstants;
import com.wiled.ubicame.prestamos.utils.PrestamoException;
import com.wiled.ubicame.prestamos.datalayer.Controller;
import com.wiled.ubicame.prestamos.entidades.Prestamo;
import java.util.logging.Level;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Edgar Garcia
 */
public class PrestamoJob implements Job {
    public static String PRESTAMO = "prestamo";
    private static final Logger log = LoggerFactory.getLogger(PrestamoJob.class);
    
    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        
        JobDetail jobDetail = jec.getJobDetail();
        
        JobDataMap map = jobDetail.getJobDataMap();
        Long prestamoId = map.getLong(PRESTAMO);
        
        Controller database = Controller.getInstance(PrestamoConstants.PROD_PU);
        Prestamo prestamo = database.buscarPrestamo(prestamoId);
                
        double interesesAcumulados = prestamo.getInteresAcumulado();
        
        if(interesesAcumulados == 0 && prestamo.getMonto() == 0) {
            try {
                database.eliminarJob(jobDetail.getKey());                
                return;
            } catch (SchedulerException ex) {
                log.error(ex.getMessage(), ex);
            }
        }
                
        log.info("**********************Aplicando intereses al prestamo: " + prestamo.getId());

        //calcular cuota
        double cuota = 0;
        
        try {            
            cuota = database.amortizarPrestamo(prestamo.getMonto(), prestamo.getTasa());
        } catch (PrestamoException ex) {
            log.error(ex.getMessage(), ex);
        }
        
        log.info("***********************   Cuota aplicada: " + cuota);

        prestamo.setInteresAcumulado(interesesAcumulados + cuota);
        
        database.merge(prestamo);
    }    
}
