/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamos.datalayer;

import org.quartz.JobKey;
import org.slf4j.Logger;
import com.wiled.ubicame.prestamos.entidades.Usuario;
import org.quartz.Trigger;
import com.wiled.ubicame.prestamos.utils.PrestamoException;
import com.wiled.ubicame.prestamos.entidades.Abono;
import com.wiled.ubicame.prestamos.entidades.Cliente;
import com.wiled.ubicame.prestamos.entidades.FormaPago;
import com.wiled.ubicame.prestamos.entidades.PagoInteres;
import com.wiled.ubicame.prestamos.entidades.Prestamo;
import com.wiled.ubicame.prestamos.schedules.PrestamoJob;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.JOptionPane;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.LoggerFactory;

import static org.quartz.TriggerBuilder.*;
import static org.quartz.JobBuilder.*;
import static org.quartz.DateBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;
import static org.quartz.CalendarIntervalScheduleBuilder.*;

/**
 *
 * @author edgar
 */
public class Controller {
    private EntityManagerFactory emf;
    private EntityManager em;
    private static Controller controller;
    private Scheduler scheduler;
    private final Logger log;
    
    private Controller(String persistenceUnit) {
        emf = Persistence.createEntityManagerFactory(persistenceUnit);
        em = emf.createEntityManager();
        log = LoggerFactory.getLogger(Controller.class);
        
        try {
            scheduler  = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        } catch (SchedulerException sex) {
            JOptionPane.showMessageDialog(null, sex.getMessage(), "ERROR SCHEDULER", JOptionPane.ERROR_MESSAGE);
        }                
    }

    public boolean validateUser(String user, char[] password) {
        log.info("********************** Validando informacion de usuario: " + user + " y password: " + new String(password));
        
        Query q = em.createNamedQuery("Usuario.buscarUsuario");
        q.setParameter("usuario", user);
        q.setParameter("password", new String(password));
        
        Usuario usuario = (Usuario) q.getSingleResult();
        
        if(usuario != null) return true;
        return false;                
    }
    
    public Scheduler getScheduler() {
        return scheduler;
    }
        
    public static Controller getInstance(String persistenceUnit) {
        if(controller == null) {
            controller = new Controller(persistenceUnit);
        } 
        
        return controller;
    }
        
    public void persist(Object obj) {
        em.getTransaction().begin();
        em.persist(obj);
        em.flush();
        em.getTransaction().commit();   
    }
    
    public void merge(Object obj) {
        em.getTransaction().begin();
        em.merge(obj);
        //em.flush();
        em.getTransaction().commit();
    }
    
    public void eliminarJob(JobKey jk) throws SchedulerException {
        scheduler.deleteJob(jk);
    }
        
    public void refresh(Object obj) {
        em.refresh(obj);
    }
    
    public void remove(Object obj) throws Exception {
        em.getTransaction().begin();                
        em.remove(obj);
        em.getTransaction().commit();
    }
    
    public void removeAbono(Abono abono) {
        Abono a = (Abono) em.find(Abono.class, abono.getId());
        em.remove(a);
    }
    
    public void removePagoInteres(PagoInteres interes) {
        PagoInteres i = (PagoInteres) em.find(PagoInteres.class, interes.getId());
        em.remove(i);
    }
        
    public List<Cliente> buscarClientePorNombre(String nombre) {
        Query q = em.createNamedQuery("Cliente.buscarNombre");
        q.setParameter("nombre", nombre);
        
        return q.getResultList();
    }
    
    public List<Cliente> buscarClientePorApellido(String apellido) {
        Query q = em.createNamedQuery("Cliente.buscarApellido");
        q.setParameter("apellido", apellido);
        
        return q.getResultList();
    }
    
    public List<Cliente> buscarClientePorCedula(String cedula) {
        Query q = em.createNamedQuery("Cliente.buscarCedula");
        q.setParameter("cedula", cedula);
        
        return q.getResultList();
    }
    
    public List<Cliente> buscarClientePorTelefono(String telefono) {
        Query q = em.createNamedQuery("Cliente.buscarTelefono");
        q.setParameter("telefono", telefono);
        
        return q.getResultList();
    }
    
    public boolean aplicarPagoIntereses(Prestamo prestamo, Date fecha, final double monto, final double mora)  throws PrestamoException {
        if ((monto < 0) || (mora < 0)) throw new PrestamoException("Valor del 'monto' o la 'mora' es menor que cero (0)") ;
        
        //verificar si tiene intereses acumulados
        if(prestamo.getInteresAcumulado() > 0) {
            //Aplicar pago a interes acumulado
            double interesAcumulado = prestamo.getInteresAcumulado();
            double restante = interesAcumulado - (monto + mora);
            
            if(restante < 0) {
                prestamo.setInteresAcumulado(0);
                
                PagoInteres pago = new PagoInteres();
                pago.setMonto(Math.abs(restante));
                pago.setMora(mora);
                pago.setPrestamo(prestamo);
                pago.setFecha(fecha);
                            
                prestamo.getPagos().add(pago);
                merge(prestamo);
                
                if(pago.getId() != null)
                    return true;
                return false;
            } else if (restante > 0) {
                prestamo.setInteresAcumulado(restante);
                merge(prestamo);
                
                return true;
            } else {
                prestamo.setInteresAcumulado(0);
                merge(prestamo);
                
                return true;
            }
        } else if (prestamo.getInteresAcumulado() == 0){
            //Aplicar un pago normal
            PagoInteres pago = new PagoInteres();
            pago.setMonto(monto);
            pago.setMora(mora);
            pago.setPrestamo(prestamo);
            pago.setFecha(fecha);
    
            persist(pago);
            
            prestamo.getPagos().add(pago);
            merge(prestamo);

            if(pago.getId() != null)
                return true;
            return false;
        }
        
        return false;
    }
    
    public List<Cliente> getClientes() {
        Query q = em.createNamedQuery("Cliente.getAll");
        return q.getResultList();
    }
    
    public boolean saldarPrestamo(Prestamo prestamo, Date fecha, double monto)  throws PrestamoException {
        if(prestamo.getInteresAcumulado() > 0)
            throw new PrestamoException("El Usuario aun posee RD$" + prestamo.getInteresAcumulado() + " en intereses pendientes");
        
        PagoInteres pago = new PagoInteres();
        pago.setMonto(monto);
        pago.setMora(0);
        pago.setPrestamo(prestamo);
        pago.setFecha(fecha);
        
        prestamo.getPagos().add(pago);
        merge(prestamo);
        
        if(pago.getId() != null)
            return true;
        return false;
    }
    
    public double getCapitalAdeudado(Prestamo p) {
        if(p.getAbonos().isEmpty()) {
            return p.getMonto();
        }
        
        //Sumatoria de abonos
        double totalAbonado = getTotalAbonado(p.getAbonos());
        double capital = p.getMonto();
        double capitalAdeudado = capital - totalAbonado;
        
        return capitalAdeudado;
    }
        
    public double getInteresesPendientes(Prestamo p) {        
        return p.getInteresAcumulado();
    }
    
    public boolean aplicarAbonoPrestamo(Prestamo p, Date fecha, double monto)  throws PrestamoException {
        if (monto < 0) throw new PrestamoException("Valor del 'monto' es menor que cero (0)") ;
        
        Abono abono = new Abono();
        abono.setMonto(monto);
        abono.setFecha(fecha);
        abono.setPrestamo(p);
        
        p.getAbonos().add(abono);
        
        merge(p);
        
        renegociarPrestamo(p);
        return true;
    }
    
    public void renegociarPrestamo(Prestamo p) {
        refresh(p);                
        double ultimoAbono = 0;
        
        if(!p.getAbonos().isEmpty()) {
            ultimoAbono = p.getAbonos().get(p.getAbonos().size() - 1).getMonto();
        }
       
        double montoPrestado = p.getMonto();
        double nuevoBalance = montoPrestado - ultimoAbono;
        
        p.setMonto(nuevoBalance);
        
        merge(p);                
    }
    
    public double getCuota(Prestamo p) throws Exception {
        double monto = p.getMonto();
        float tasa = p.getTasa();
       
        return amortizarPrestamo(monto, tasa);
    }
    
    public static double getTotalAbonado(List<Abono> abonos) {
        double totalAbonado = 0;
        
        for (Abono abono : abonos) {
            totalAbonado += abono.getMonto();
        }
        
        return totalAbonado;
    }
    
    public double amortizarPrestamo(final Double monto, final float tasa)  throws PrestamoException {     
        if(monto < 0) throw new PrestamoException("Valor del 'monto' es menor que cero (0)");
        
        return (monto * tasa)/100;
    }
    
    public Cliente crearCliente(String nombre, String apellido, String cedula, String telefono) {
        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setCedula(cedula);
        cliente.setTelefono(telefono);
        
        persist(cliente);
        
        if(cliente.getId() != null)
            return cliente;
        return null;
    }
    
    public Prestamo buscarPrestamo(Long id) {
        Prestamo prestamo = em.find(Prestamo.class, id);                        
        return prestamo;
    }
    
    public Prestamo crearPrestamo(Cliente cliente, String comentario, Date fecha, FormaPago formaPago, double monto, float tasa) throws PrestamoException, SchedulerException {
        if(monto < 0) throw new PrestamoException("Valor del 'monto' es menor que cero (0)");
         
        Prestamo prestamo = new Prestamo();
        prestamo.setCliente(cliente);
        prestamo.setComentario(comentario);
        prestamo.setFecha(fecha);
        prestamo.setFormaPago(formaPago);
        prestamo.setMonto(monto);
        prestamo.setTasa(tasa);
        
        persist(prestamo);
                
        // Crear el scheduler para generar los cortes de este prestamo
        JobDataMap map = new JobDataMap();
        map.put(PrestamoJob.PRESTAMO, prestamo.getId());
        
        JobDetail job = newJob(PrestamoJob.class)
                .withIdentity("job" + prestamo.getId(), "prestamos")
                .usingJobData(map)
                .requestRecovery()
                .withDescription(prestamo.getComentario())
                .build();
        
        Trigger trigger = null;
        
        switch (formaPago) {
            case DIARIO:
                trigger = newTrigger()
                    .startAt(tomorrowAt(15, 0, 0))
                    .withIdentity("trigger"+prestamo.getId(), "diarios")                    
                    .withSchedule(simpleSchedule().withIntervalInHours(24).repeatForever())
                    .build();
                break;
            case MENSUAL:                              
                trigger = newTrigger()
                    .withIdentity("trigger"+prestamo.getId(), "mensuales")
                    .startAt(futureDate(30, IntervalUnit.DAY)) 
                    .withSchedule(calendarIntervalSchedule()
                        .withIntervalInMonths(1)) // interval is set in calendar months
                    .build();
                break;
            case QUINCENAL:
                trigger = newTrigger()
                    .withIdentity("trigger"+prestamo.getId(), "quincenales")
                    .startAt(futureDate(15, IntervalUnit.DAY)) 
                    .withSchedule(calendarIntervalSchedule()
                        .withIntervalInWeeks(2))
                    .build();
                break;
            case SEMANAL:
                trigger = newTrigger()
                    .withIdentity("trigger"+prestamo.getId(), "quincenales")
                    .startAt(futureDate(7, IntervalUnit.DAY))  
                    .withSchedule(calendarIntervalSchedule()
                        .withIntervalInWeeks(1))
                    .build();
                break;
        }

        // Tell quartz to schedule the job using our trigger
        scheduler.scheduleJob(job, trigger);
        
        if(prestamo.getId() != null)
            return prestamo;
        return null;
    }
}
