/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamos.utils;

import com.wiled.ubicame.prestamos.entidades.Abono;
import com.wiled.ubicame.prestamos.entidades.FormaPago;
import com.wiled.ubicame.prestamos.entidades.PagoInteres;
import com.wiled.ubicame.prestamos.entidades.Prestamo;
import com.wiled.ubicame.prestamos.entidades.Renegociacion;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;

/**
 *
 * @author edgar
 */
public class PrestamoUtils {

    public static float amortizarPrestamo(double monto, float tasa) {
        return (float) ((monto*tasa)/100);
    }

    public static boolean containsOnlyNumbers(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }

        //Replace '-'
        str = str.replaceAll("-", "");

        for (int i = 0; i < str.length(); i++) {
            if(str.charAt(i) == '.') continue;
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean isCedulaSizeValid(String cedula) {
        int len = cedula.length();

        if (len == 11) {
            return true;
        }
        return false;
    }

    public static double montoAdeudado(Prestamo prestamo) {
        double abonos = buscarAbonoHastaLaFecha(prestamo, PrestamoUtils.getCurrentDate());
        double totalRenegociaciones = totalRenegociaciones(prestamo.getRenegociaciones());
        double montoOriginal = prestamo.getMonto();
        
        double totalAdeudado = (montoOriginal + totalRenegociaciones) - abonos;
        
        return totalAdeudado;
    }
    
    public static Date ultimaFechaPrestamo(Prestamo p) {
        Date fecha = null;
        if(p.getRenegociaciones().isEmpty()) {
            fecha = p.getFecha();
        } else {
            fecha = p.getRenegociaciones().get(p.getRenegociaciones().size() - 1).getFecha();
        }
        
        return fecha;
    }
    
    public static FormaPago ultimaFormaPagoPrestamo(Prestamo p) {
        FormaPago formaPago = null;
        if(p.getRenegociaciones().isEmpty()) {
            formaPago = p.getFormaPago();
        } else {
            formaPago = p.getRenegociaciones().get(p.getRenegociaciones().size() - 1).getNuevaFormaPago();
        }
        
        return formaPago;
    }
    
    public static List<FormaPago> getFormasPago() {
        List<FormaPago> formasPago = new ArrayList<FormaPago>();
        formasPago.add(FormaPago.DIARIO);
        formasPago.add(FormaPago.SEMANAL);
        formasPago.add(FormaPago.QUINCENAL);
        formasPago.add(FormaPago.MENSUAL);
        
        return formasPago;
    }
    
    public static double getInteresAcumulado(Prestamo p) {   
        double monto = p.getMonto();
        Date fechaInicioPrestamo = p.getFecha();
        Date fechaActual = getCurrentDate();
        int diasEntreFechas = diasEntreFechas(fechaInicioPrestamo, fechaActual);

        System.out.println("Dias entre fechas: "  + diasEntreFechas);
        double interesGanado = 0;
        double interesPagado = totalInteresesPagados(p);
        
        for (int i = 0; i < diasEntreFechas; i++) {
            
            Calendar c = Calendar.getInstance();
            c.setTime(fechaInicioPrestamo);
            
            int j = i;
            c.add(Calendar.DAY_OF_MONTH, ++j);

            //Calcular la cuota de ese dia
            //1. Verificar si hubo algun abono
            double abono = buscarAbonoHastaLaFecha(p, c.getTime());
            float tasa = p.getTasa();
            FormaPago formaPago = p.getFormaPago();
            
            // Buscar la ultima renegociacion anterior a la fecha buscada
            List<Renegociacion> renegociaciones = buscarRenegociacionHastaLaFecha(p, c.getTime());
            if(!renegociaciones.isEmpty()) {
                monto += totalRenegociaciones(renegociaciones);
                tasa += tasaALaFecha(renegociaciones);
                formaPago = ultimaFormaPago(renegociaciones);
            }
 
            switch (formaPago) {
                case DIARIO:
                    interesGanado += ((monto - abono) * tasa) / 100;
                    break;
                case SEMANAL:
                    interesGanado += ((monto - abono) * (tasa/7)) / 100;
                    break;
                case QUINCENAL:
                    interesGanado += ((monto - abono) * (tasa/15)) / 100;
                    break;
                case MENSUAL:
                    interesGanado += ((monto - abono) * (tasa/30)) / 100;
                    break;
            }                        
        }
        
        return interesGanado - interesPagado;
    }

    public static BigDecimal redondear(double f, int scale) {
      String val = f+"";
      BigDecimal big = new BigDecimal(val);
      big = big.setScale(scale, RoundingMode.HALF_UP);
      
      return big;
    }
    
    private static FormaPago ultimaFormaPago(List<Renegociacion> renegoaciaciones) {
        int total = renegoaciaciones.size();
        Renegociacion r = renegoaciaciones.get(total - 1);
        
        return r.getNuevaFormaPago();
    }
    
    private static float tasaALaFecha(List<Renegociacion> renegoaciaciones) {
        float tasa = 0;
        
        for (Renegociacion renegociacion : renegoaciaciones) {
            tasa += renegociacion.getNuevaTasa();
        }
        
        return tasa;
    }
    
    public static boolean prestamoSaldado(Prestamo prestamo) {
        double interesesAcumulados = PrestamoUtils.getInteresAcumulado(prestamo);
        double totalRenegociaciones = PrestamoUtils.totalRenegociaciones(prestamo.getRenegociaciones());
        double montoTotal = prestamo.getMonto() + totalRenegociaciones;
        double totalAbonado = PrestamoUtils.buscarAbonoHastaLaFecha(prestamo, PrestamoUtils.getCurrentDate());
        
        if (interesesAcumulados == 0 && totalAbonado == montoTotal)
            return true;
        return false;
    }
    
    public static double totalRenegociaciones(List<Renegociacion> renegoaciaciones) {
        double montoTotal = 0;
        
        for (Renegociacion renegociacion : renegoaciaciones) {
            montoTotal += renegociacion.getMontoAgregado();
        }
        
        return montoTotal;
    }
    
    private static double totalInteresesPagados(Prestamo p) {
        double totalPagado = 0;
        for(PagoInteres pi : p.getPagos()) {
            totalPagado += pi.getMonto();
        }
        
        return totalPagado;
    }
    
    public static double buscarAbonoHastaLaFecha(Prestamo p, Date fecha) {
        System.out.println("Buscando abono en fecha: " + fecha);
        double abonos = 0;
        
        for(Abono abono : p.getAbonos()) {
             if(abono.getFecha().before(fecha)) {
                 abonos += abono.getMonto();
             }             
        }
        
        return abonos;
    }

    private static List<Renegociacion> buscarRenegociacionHastaLaFecha(Prestamo p, Date fecha) {
        List<Renegociacion> renegociaciones = new ArrayList<Renegociacion>();
        
        for(Renegociacion r : p.getRenegociaciones()) {
            if(r.getFecha().before(fecha)) {
                renegociaciones.add(r);
            } else {
                break;
            }                
        }        
        return renegociaciones;
    }
    
    public static float buscarTasaHastaLaFecha(Prestamo p, Date fecha) {
        float tasa = p.getTasa();
        
        for(Renegociacion r : p.getRenegociaciones()) {
            if(r.getFecha().before(fecha)) {
                tasa += r.getNuevaTasa();
            } else {
                break;
            }                
        }
        
        return tasa;
    }
    
    private static int diasEntreFechas(Date fechaInicial, Date fechaFinal) {
        return (int) ((fechaFinal.getTime() - fechaInicial.getTime()) / (1000 * 60 * 60 * 24));
    }

    public static boolean isTelefonoSizeValid(String telefono) {
        if (telefono.length() == 10) {
            return true;
        }
        return false;
    }

    public static Date getCurrentDate() {
        Calendar c = Calendar.getInstance();
        return c.getTime();
    }

    public static void exportDataBase(String drive) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        Connection conn = DriverManager.getConnection("jdbc:derby:"+PrestamoConstants.SYSTEM_DATABASE_NAME+";user="+PrestamoConstants.SYSTEM_USER+";password="+PrestamoConstants.SYSTEM_PASSWORD+"");
        
        
        // Get today's date as a string:
        SimpleDateFormat todaysDate = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String backupdirectory = drive + todaysDate.format((java.util.Calendar.getInstance()).getTime());

        CallableStatement cs = conn.prepareCall("CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(?)"); 
        cs.setString(1, backupdirectory);
        cs.execute(); 
        cs.close();
        
        System.out.println("backed up database to "+backupdirectory);
    }

    public static void imprimirFactura(String factura) throws PrintException {
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        if (service == null) {
            throw new PrintException("No se encontro impresora conectada");
        }

        //Le decimos el tipo de datos que vamos a enviar a la impresora
        //Tipo: bytes Subtipo: autodetectado
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        DocPrintJob pj = service.createPrintJob();
        byte[] bytes;
        bytes = factura.getBytes();
        Doc doc = new SimpleDoc(bytes, flavor, null);

        pj.print(doc, null);

    }
}
