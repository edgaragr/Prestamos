/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamos.datalayer;

import com.wiled.ubicame.prestamo.utils.PrestamoException;
import com.wiled.ubicame.prestamos.entidades.Abono;
import com.wiled.ubicame.prestamos.entidades.Cliente;
import com.wiled.ubicame.prestamos.entidades.FormaPago;
import com.wiled.ubicame.prestamos.entidades.PagoInteres;
import com.wiled.ubicame.prestamos.entidades.Prestamo;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author edgar
 */
public class Controller {
    private EntityManagerFactory emf;
    private EntityManager em;
    private static Controller controller;
    
    private Controller(String persistenceUnit) {
        emf = Persistence.createEntityManagerFactory(persistenceUnit);
        em = emf.createEntityManager();
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
        em.flush();
        em.getTransaction().commit();
    }
    
    public void refresh(Object obj) {
        em.refresh(obj);
    }
    
    public void remove(Object obj) throws Exception {
        em.getTransaction().begin();
        em.remove(obj);
        em.getTransaction().commit();
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
                pago.setMora(0);
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
            pago.setMora(0);
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
        
        double ultimoAbono = p.getAbonos().get(p.getAbonos().size() - 1).getMonto();
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
    
    public Prestamo crearPrestamo(Cliente cliente, String comentario, Date fecha, FormaPago formaPago, double monto, float tasa) throws PrestamoException {
        if(monto < 0) throw new PrestamoException("Valor del 'monto' es menor que cero (0)");
         
        Prestamo prestamo = new Prestamo();
        prestamo.setCliente(cliente);
        prestamo.setComentario(comentario);
        prestamo.setFecha(fecha);
        prestamo.setFormaPago(formaPago);
        prestamo.setMonto(monto);
        prestamo.setTasa(tasa);
        
        persist(prestamo);
        
        if(prestamo.getId() != null)
            return prestamo;
        return null;
    }
}
