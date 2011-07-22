/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamos.datalayer;

import org.slf4j.Logger;
import com.wiled.ubicame.prestamos.entidades.Usuario;
import com.wiled.ubicame.prestamos.utils.PrestamoException;
import com.wiled.ubicame.prestamos.entidades.Abono;
import com.wiled.ubicame.prestamos.entidades.Cliente;
import com.wiled.ubicame.prestamos.entidades.FormaPago;
import com.wiled.ubicame.prestamos.entidades.PagoInteres;
import com.wiled.ubicame.prestamos.entidades.Prestamo;
import com.wiled.ubicame.prestamos.entidades.Renegociacion;
import com.wiled.ubicame.prestamos.utils.PrestamoConstants;
import com.wiled.ubicame.prestamos.utils.PrestamoUtils;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.slf4j.LoggerFactory;

/**
 *
 * @author edgar
 */
public class Controller {
    private EntityManagerFactory emf;
    private EntityManager em;
    private static Controller controller;
    private final Logger log;
    
    private Controller(String persistenceUnit) {
        emf = Persistence.createEntityManagerFactory(persistenceUnit);
        em = emf.createEntityManager();
        log = LoggerFactory.getLogger(Controller.class);             
    }

    private Controller() {
        emf = Persistence.createEntityManagerFactory(PrestamoConstants.TEST_PU);
        em = emf.createEntityManager();
        log = LoggerFactory.getLogger(Controller.class);     
    }
        
    public boolean validateUser(String user, char[] password) {        
        Query q = em.createNamedQuery("Usuario.buscarUsuario");
        q.setParameter("usuario", user);
        q.setParameter("password", new String(password));
        
        Usuario usuario = (Usuario) q.getSingleResult();
        
        if(usuario != null) return true;
        return false;                
    }
        
    public static Controller getInstance(String persistenceUnit) {
        if(controller == null) {
            controller = new Controller(persistenceUnit);
        } 
        
        return controller;
    }
       
    public static Controller getInstance() {
        if(controller == null) {
            controller = new Controller();
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
        q.setParameter("nombre", nombre.toUpperCase());
        
        return q.getResultList();
    }
    
    public List<Cliente> buscarClientePorApellido(String apellido) {
        Query q = em.createNamedQuery("Cliente.buscarApellido");
        q.setParameter("apellido", apellido.toUpperCase());
        
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

        PagoInteres pago = new PagoInteres();
        pago.setMonto(monto);
        pago.setMora(mora);
        pago.setPrestamo(prestamo);
        pago.setFecha(fecha);

        persist(pago);
        
        prestamo.getPagos().add(pago);
        merge(prestamo);

        refresh(pago);
        
        if(pago.getId() != null)
            return true;
        return false;                       
    }
    
    public List<Cliente> getClientes() {
        Query q = em.createNamedQuery("Cliente.getAll");
        return q.getResultList();
    }
    
    public boolean saldarPrestamo(Prestamo prestamo, Date fecha, double monto)  throws PrestamoException {
        double interesAcumulado = PrestamoUtils.getInteresAcumulado(prestamo);
        
        if(interesAcumulado > 0)
            throw new PrestamoException("El Usuario aun posee RD$" + interesAcumulado + " en intereses pendientes");
        
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
           
    public boolean aplicarAbonoPrestamo(Prestamo p, Date fecha, double monto)  throws PrestamoException {
        if (monto < 0) throw new PrestamoException("Valor del 'monto' es menor que cero (0)") ;
        
        Abono abono = new Abono();
        abono.setMonto(monto);
        abono.setFecha(fecha);
        abono.setPrestamo(p);
        
        p.getAbonos().add(abono);
        
        merge(p);

        return true;
    }

    public void modificarPrestamo(Renegociacion renegociacion) {
        //Crear una renegociacion                
        persist(renegociacion);
        
        Prestamo actual = renegociacion.getPrestamo();
        actual.getRenegociaciones().add(renegociacion);        
        merge(actual);
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
    
    
    public Prestamo crearPrestamo(Cliente cliente, String comentario, Date fecha, FormaPago formaPago, double monto, float tasa) throws PrestamoException {
        if(monto < 0) throw new PrestamoException("Valor del 'monto' es menor que cero (0)");
         
        Prestamo prestamo = new Prestamo();
        prestamo.setCliente(cliente);
        prestamo.setComentario(comentario);
        
        Calendar c = Calendar.getInstance();
        //hora
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int minuto = c.get(Calendar.MINUTE);
                
        c.setTime(fecha);
        c.set(Calendar.HOUR_OF_DAY, hora);
        c.set(Calendar.MINUTE, minuto);
        
        
        prestamo.setFecha(c.getTime());
        prestamo.setFormaPago(formaPago);
        prestamo.setMonto(monto);
        prestamo.setTasa(tasa);
        
        persist(prestamo);
          
        if(prestamo.getId() != null)
            return prestamo;
        return null;
    }
}
