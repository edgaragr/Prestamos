/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamos.datalayer;

import org.quartz.Trigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.TriggerBuilder.*;
import static org.quartz.JobBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class QuartzTest implements Job {

    public static void main(String[] args) {

        try {
            // Grab the Scheduler instance from the Factory 
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            // and start it off
            scheduler.start();

            // define the job and tie it to our HelloJob class
            JobDetail job = newJob(QuartzTest.class).withIdentity("job1", "group1").build();
            
            
            // Trigger the job to run now, and then repeat every 40 seconds
            Trigger trigger = newTrigger().withIdentity("trigger1", "group1").startNow().withSchedule(simpleSchedule().withIntervalInSeconds(40).repeatForever()).build();

            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(job, trigger);

            scheduler.shutdown();

            System.out.println("edgaragr".toCharArray());
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}