/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * IngresosPorDiaForm.java
 *
 * Created on Oct 13, 2011, 4:30:31 PM
 */
package com.wiled.ubicame.prestamos.forms;

import com.wiled.ubicame.prestamos.datalayer.Controller;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author egarcia
 */
public class IngresosPorDiaForm extends javax.swing.JDialog {

    /** Creates new form IngresosPorDiaForm */
    public IngresosPorDiaForm(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        fechaDatePicker = new org.jdesktop.swingx.JXDatePicker();
        generarReporteBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ingresos Por Dia");
        setResizable(false);

        jLabel1.setText("Fecha:");

        generarReporteBtn.setText("Generar Reporte");
        generarReporteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generarReporteBtnActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fechaDatePicker, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(generarReporteBtn)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(fechaDatePicker, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(generarReporteBtn))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void generarReporteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generarReporteBtnActionPerformed
        Date fecha = fechaDatePicker.getDate();
        
        if(fecha == null){
            JOptionPane.showMessageDialog(this, "Debe seleccionar una fecha para generar el reporte.",
                    "Erro de validacion", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Map parameters = new HashMap();
        parameters.put("fechaActual", fecha);
        
        
        try {
            JasperReport report = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("/reports/EntradasPorDia.jasper"));
           
            JasperPrint print = JasperFillManager.fillReport(report, parameters, Controller.getInstance().getJDBCConnection());
            JasperViewer.viewReport(print, false);
            
            this.dispose();
            
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Jasper Report Exception", JOptionPane.ERROR_MESSAGE);
        }
        
        
        /*try {
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Exception", JOptionPane.ERROR_MESSAGE);
        }*/
    }
        // TODO add your handling code here:}//GEN-LAST:event_generarReporteBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXDatePicker fechaDatePicker;
    private javax.swing.JButton generarReporteBtn;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}