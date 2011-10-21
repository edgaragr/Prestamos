/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamos.datalayer;

import com.wiled.ubicame.prestamos.utils.PrestamoConstants;
import com.wiled.ubicame.prestamos.entidades.Cliente;
import com.wiled.ubicame.prestamos.entidades.FormaPago;
import com.wiled.ubicame.prestamos.entidades.Prestamo;
import com.wiled.ubicame.prestamos.utils.PrestamoUtils;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;
import junit.framework.TestCase;

/**
 *
 * @author edgar
 */
public class PersistenceUnitTest extends TestCase {

    private static final Logger logger = Logger.getLogger(PersistenceUnitTest.class.getName());
    private static Cliente cliente;
    private static Prestamo prestamo;
    private static Controller instance;
    
    private String nombre = "Edgar";
    private String apellido =  "Garcia";
    private String cedula = "00116600107";
    private String telefono = "8095958378";
    
    public PersistenceUnitTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        try {
            logger.info("Starting in-memory HSQL database for unit tests");
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (Exception ex) {
            fail("Exception during HSQL database startup\n" + ex.getLocalizedMessage());
        }
        try {
            logger.info("Building JPA EntityManager for unit tests");
            instance = Controller.getInstance() ;
        } catch (Exception ex) {
            fail("Exception during JPA EntityManager instanciation\n" + ex.getLocalizedMessage());
        }
    }

    public void testCrearCliente() {
        logger.info("crearCliente");
        cliente = instance.crearCliente(nombre, apellido, cedula, telefono);

        assertNotNull(cliente);
    }

    public void testCrearPrestamo() throws Exception {
        logger.info("crearPrestamo");
        String comentario = "Comentario de prueba";
        Date fecha = getDate();
        FormaPago formaPago = FormaPago.MENSUAL;
        double monto = 10000.0;
        float tasa = 5.0F;

        System.out.println("*****************************"+cliente.getCedula());
        prestamo = instance.crearPrestamo(cliente, comentario, fecha, formaPago, monto, tasa);
        assertNotNull(prestamo);
    }

    public void testGetCuota() throws Exception {
        double expResult = 500;
        double cuota = instance.getCuota(prestamo);
        
        assertEquals(expResult, cuota);
    }
    
    public void testGetInteresesPendientes() {
        double expResult = 0;
        double intereses = PrestamoUtils.getInteresAcumulado(prestamo);
        
        assertEquals(expResult, intereses);
    }
    
    public void testAmortizarPrestamo() throws Exception {
        logger.info("amortizarPrestamo");
        double expResult = 100;
        double result = instance.amortizarPrestamo(new Double(1000), 10);
        assertEquals(expResult, result);
    }
    
    public void testAplicarAbonoPrestamo() throws Exception {
        logger.info("aplicarAbonoPrestamo");
        instance.aplicarAbonoPrestamo(prestamo, getDate(), 100);
    }
    
    public void testAplicarPagoIntereses() throws Exception {
        instance.aplicarPagoIntereses(prestamo, getDate(), 300, 50);
    }
    
    public void testBuscarClienteApellido() {
        Cliente result = instance.buscarClientePorApellido(apellido).get(0);
        assertEquals(cliente, result);
    }
    
    public void testBuscarClienteNombre() {
        Cliente result = instance.buscarClientePorNombre(nombre).get(0);
        assertEquals(cliente, result);
    }
    
    public void testBuscarClienteTelefono() {
        Cliente result = instance.buscarClientePorTelefono(telefono).get(0);
        assertEquals(cliente, result);
    }
    
    public void testBuscarClienteCedula() {
        Cliente result = instance.buscarClientePorCedula(cedula).get(0);
        assertEquals(cliente, result);
    }
    
    public void testGetCapitalAdeudado() {
        double expResult = 9900;
        double capitalAdeudado = instance.getCapitalAdeudado(prestamo);
        
        assertEquals(expResult, capitalAdeudado);
    }
    
    public void testSaldarPrestamo() throws Exception {
        double capitalAdeudado = instance.getCapitalAdeudado(prestamo);        
        instance.saldarPrestamo(prestamo, getDate(), capitalAdeudado);
    }
    
    private Date getDate() {
        Calendar c = Calendar.getInstance();

        return c.getTime();
    }
}
