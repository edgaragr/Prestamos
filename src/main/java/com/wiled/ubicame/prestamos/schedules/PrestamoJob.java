/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamos.schedules;

import com.wiled.ubicame.prestamos.entidades.Prestamo;
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
    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        JobDetail jobDetail = jec.getJobDetail();
        
        JobDataMap map = jobDetail.getJobDataMap();
        Prestamo prestamo = (Prestamo)map.get("Prestamo");
        
        
        throw new UnsupportedOperationException("Not supported yet.");
    }    
}
