/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamos.datalayer;

import com.wiled.ubicame.prestamos.entidades.Cliente;
import com.wiled.ubicame.prestamos.entidades.FormaPago;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import junit.framework.TestCase;

/**
 *
 * @author edgar
 */
public class PersistenceUnitTest extends TestCase {
    
   private static final Logger logger = Logger.getLogger(PersistenceUnitTest.class.getName());
   
    private EntityManagerFactory emFactory;
    private EntityManager em;

    private Connection connection;
    private Cliente cliente;
    
    public PersistenceUnitTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        try {
            logger.info("Starting in-memory HSQL database for unit tests");
            Class.forName("org.hsqldb.jdbcDriver");
            connection = DriverManager.getConnection("jdbc:hsqldb:mem:unit-testing-jpa", "sa", "");
        } catch (Exception ex) {
            fail("Exception during HSQL database startup.");
        }
        try {
            logger.info("Building JPA EntityManager for unit tests");
            emFactory = Persistence.createEntityManagerFactory("testPU");
            em = emFactory.createEntityManager();
        } catch (Exception ex) {
            fail("Exception during JPA EntityManager instanciation.");
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        logger.info("Shuting down Hibernate JPA layer.");
        if (em != null) {
            em.close();
        }
        if (emFactory != null) {
            emFactory.close();
        }
        logger.info("Stopping in-memory HSQL database.");
        try {
            connection.createStatement().execute("SHUTDOWN");
        } catch (Exception ex) {}
    }

    public void testCrearCliente() {
        logger.info("crearCliente");
        String nombre = "Edgar";
        String apellido = "Garcia";
        int cedula = 00116600107;
        String telefono = "8095958378";
        Controller instance = Controller.getInstance("prestamosPU");
        cliente = instance.crearCliente(nombre, apellido, cedula, telefono);
        
        assertNotNull(cliente);
    }
    
    public void testCrearPrestamo() {
        logger.info("crearPrestamo");
        String comentario = "Comentario de prueba";
        Date fecha = getDate();
        FormaPago formaPago = FormaPago.MENSUAL;
        double monto = 10000.0;
        float tasa = 5.0F;
        Controller instance = Controller.getInstance("prestamosPU");
        boolean expResult = true;
        boolean result = instance.crearPrestamo(cliente, comentario, fecha, formaPago, monto, tasa);
        assertEquals(expResult, result);
    }
    
    private Date getDate() {
        Calendar c = Calendar.getInstance();
        
        return c.getTime();
    }
}
