/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PagoForm.java
 *
 * Created on May 21, 2011, 12:02:07 PM
 */
package com.wiled.ubicame.prestamos.forms;

import com.wiled.ubicame.prestamos.utils.PrestamoConstants;
import com.wiled.ubicame.prestamos.utils.PrestamoException;
import com.wiled.ubicame.prestamos.datalayer.Controller;
import com.wiled.ubicame.prestamos.entidades.Abono;
import com.wiled.ubicame.prestamos.entidades.Cliente;
import com.wiled.ubicame.prestamos.entidades.FormaPago;
import com.wiled.ubicame.prestamos.entidades.Pago;
import com.wiled.ubicame.prestamos.entidades.PagoInteres;
import com.wiled.ubicame.prestamos.entidades.Prestamo;
import com.wiled.ubicame.prestamos.entidades.TipoPago;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.print.PrintException;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import static com.wiled.ubicame.prestamos.utils.PrestamoUtils.*;
import org.quartz.SchedulerException;
/**
 *
 * @author edgar
 */
public class PagoForm extends javax.swing.JDialog {

    private Frame jFrame;
    private Prestamo prestamo;
    private Controller controller;
    private final String GUARDAR_CAMBIOS = "guardarCambios";
    private final String NUEVO_PRESTAMO = "nuevoPrestamo";
    private final String RENEGOCIAR = "renegociar";
    
    
    /** Creates new form PagoForm */
    public PagoForm(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jFrame = parent; 
    }
   
    private void activarEstadoRenegociar(boolean encendido) {
        crearPrestamoBtn.setText("RENEGOCIAR PRESTAMO");
        crearPrestamoBtn.setActionCommand(RENEGOCIAR);
        
        if(encendido) {
            fechaTxt.setEnabled(true);
            fechaTxt.setEditable(true);

            montoTxt.setEditable(true);
            montoTxt.setEnabled(true);

            formaPagoCBox.setEditable(true);
            formaPagoCBox.setEnabled(true);

            tasaTxt.setEditable(true);
            tasaTxt.setEnabled(true);
        } else {
            fechaTxt.setEnabled(false);
            fechaTxt.setEditable(false);

            montoTxt.setEditable(false);
            montoTxt.setEnabled(false);

            formaPagoCBox.setEditable(false);
            formaPagoCBox.setEnabled(false);

            tasaTxt.setEditable(false);
            tasaTxt.setEnabled(false);
        }             

    }
    
    private void activarEstadoCrearPrestamo() {
        aplicarPagoBtn.setEnabled(false);
        crearPrestamoBtn.setText("CREAR NUEVO PRESTAMO");
        crearPrestamoBtn.setActionCommand(NUEVO_PRESTAMO);
        crearPrestamoBtn.setVisible(true);
    }
    
    public PagoForm(java.awt.Frame parent, boolean modal, Cliente cliente) {
        super(parent, modal);
        initComponents();
        this.cliente = cliente;
        jFrame = parent;
        
        controller = Controller.getInstance(PrestamoConstants.PROD_PU);
        tipoPagoCBox.insertItemAt(TipoPago.ABONO, 0);
        tipoPagoCBox.insertItemAt(TipoPago.PAGO_INTERES, 1);

        nameLabel.setVisible(true);
        nameLabel.setText(cliente.toString());

        int totalPrestamos = cliente.getPrestamos().size();
        prestamo = cliente.getPrestamos().get(totalPrestamos - 1);

        controller.refresh(prestamo);
        
        montoTxt.setText(String.valueOf(prestamo.getMonto()));
        montoTxt.setEditable(false);

        fechaTxt.setDate(prestamo.getFecha());
        tasaTxt.setText(String.valueOf(prestamo.getTasa()));
        tasaTxt.setEditable(false);

        formaPagoCBox.insertItemAt(prestamo.getFormaPago(), 0);
        formaPagoCBox.setSelectedIndex(0);
        formaPagoCBox.setEditable(false);

        interesesTxt.setText(String.valueOf(prestamo.getInteresAcumulado()));
        interesesTxt.setEditable(false);

        double totalAbonado = Controller.getTotalAbonado(prestamo.getAbonos());
        abonadoTxt.setText(String.valueOf(totalAbonado));
        abonadoTxt.setEditable(false);

        List<Pago> pagos = new ArrayList<Pago>();
        pagos.addAll(prestamo.getAbonos());
        pagos.addAll(prestamo.getPagos());

        pagosTable.setModel(new PagosTableModel(pagos));
        pagosTable.updateUI();

        moraPagoTxt.setText("0");
               
        pagosTable.addKeyListener(new KeyAdapter() {
            
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_DELETE) {
                    Pago pago = ((PagosTableModel) pagosTable.getModel()).pagos.get(pagosTable.getSelectedRow());
                    ((PagosTableModel) pagosTable.getModel()).pagos.remove(pagosTable.getSelectedRow());
                    pagosTable.updateUI();

                    if( pago instanceof Abono) {
                        prestamo.getAbonos().remove((Abono) pago);
                        //prestamo.getAbonos().remove();
                        controller.removeAbono((Abono) pago);                        
                        prestamo.setMonto(prestamo.getMonto() + pago.getMonto());
                        controller.merge(prestamo);
                    } else if ( pago instanceof PagoInteres) {
                        prestamo.getPagos().remove((PagoInteres) pago);
                        
                        controller.removePagoInteres((PagoInteres) pago);
                        controller.merge(prestamo);
                    }
                    
                    List<Pago> pagos = new ArrayList<Pago>();
                    pagos.addAll(prestamo.getAbonos());
                    pagos.addAll(prestamo.getPagos());

                    pagosTable.setModel(new PagosTableModel(pagos));
                    pagosTable.updateUI();
                    
                    montoTxt.setText(String.valueOf(prestamo.getMonto()));
                    interesesTxt.setText(String.valueOf(prestamo.getInteresAcumulado()));
                    
                    double totalAbonado = Controller.getTotalAbonado(prestamo.getAbonos());
                    abonadoTxt.setText(String.valueOf(totalAbonado));
                    
                    JOptionPane.showMessageDialog(rootPane, "Pago eliminado exitosamente", "Eliminacion de Pago", JOptionPane.INFORMATION_MESSAGE);
                }
            }            
        });
        
        activarEstadoRenegociar(false);
                
        if (prestamo.getInteresAcumulado() == 0 && prestamo.getMonto() == 0) {
            //Significa que el prestamo ya se pago
            activarEstadoCrearPrestamo();
        } else {
            try {
                cuotaTxt.setText(String.valueOf(controller.amortizarPrestamo(prestamo.getMonto(), prestamo.getTasa())));
            } catch (PrestamoException ex) {
                JOptionPane.showMessageDialog(parent, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class PagosTableModel extends AbstractTableModel {

        List<Pago> pagos = null;
        String[] columns = {"Tipo Pago", "Monto", "Mora", "Fecha"};

        public PagosTableModel() {
            pagos = new ArrayList<Pago>();
        }

        public PagosTableModel(List<Pago> pagos) {
            this.pagos = pagos;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public int getRowCount() {
            return pagos.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Object value = null;
            switch (columnIndex) {
                case 0:
                    value = pagos.get(rowIndex).getTipoPago();
                    break;
                case 1:
                    value = pagos.get(rowIndex).getMonto();
                    break;
                case 2:
                    value = pagos.get(rowIndex).getMora();
                    break;
                case 3:
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    value = sdf.format(pagos.get(rowIndex).getFecha());
                    break;
            }

            return value;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel5 = new javax.swing.JLabel();
        formaPagoCBox = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        montoTxt = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        interesesTxt = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        abonadoTxt = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        montoPagoTxt = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        aplicarPagoBtn = new javax.swing.JButton();
        moraPagoTxt = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        tipoPagoCBox = new javax.swing.JComboBox();
        datePicker = new org.jdesktop.swingx.JXDatePicker();
        jScrollPane1 = new javax.swing.JScrollPane();
        pagosTable = new javax.swing.JTable();
        tasaTxt = new javax.swing.JTextField();
        nameLabel = new javax.swing.JLabel();
        administrarClienteBtn = new javax.swing.JButton();
        crearPrestamoBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        cuotaTxt = new javax.swing.JTextField();
        fechaTxt = new org.jdesktop.swingx.JXDatePicker();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Pagos");
        setResizable(false);

        jLabel5.setText("Fecha:");

        formaPagoCBox.setEnabled(false);

        jLabel4.setText("Forma Pago:");

        jLabel2.setText("Monto:");

        montoTxt.setEditable(false);

        jLabel3.setText("Tasa:");

        jLabel6.setText("Intereses Acumulados:");

        interesesTxt.setEditable(false);

        jLabel7.setText("Abonado:");

        abonadoTxt.setEditable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Detalles de Pago"));

        jLabel8.setText("Monto:");

        jLabel9.setText("Mora:");

        aplicarPagoBtn.setBackground(new java.awt.Color(197, 164, 15));
        aplicarPagoBtn.setText("Aplicar Pago");
        aplicarPagoBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aplicarPagoBtnActionPerformed(evt);
            }
        });

        jLabel10.setText("Fecha:");

        jLabel11.setText("Tipo Pago:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(montoPagoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(moraPagoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addGap(4, 4, 4)
                .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tipoPagoCBox, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(aplicarPagoBtn)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(montoPagoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(aplicarPagoBtn)
                    .addComponent(jLabel9)
                    .addComponent(moraPagoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(tipoPagoCBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pagosTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Tipo Pago", "Monto", "Mora", "Fecha"
            }
        ));
        pagosTable.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(pagosTable);
        pagosTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        tasaTxt.setEditable(false);

        nameLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        nameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        nameLabel.setText("[Nombre del Cliente]");

        administrarClienteBtn.setBackground(new java.awt.Color(51, 204, 0));
        administrarClienteBtn.setText("Administrar Cliente");
        administrarClienteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                administrarClienteBtnActionPerformed(evt);
            }
        });

        crearPrestamoBtn.setBackground(new java.awt.Color(255, 204, 204));
        crearPrestamoBtn.setText("CREAR NUEVO PRESTAMO");
        crearPrestamoBtn.setActionCommand("crearNuevoPrestamo");
        crearPrestamoBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                crearPrestamoBtnActionPerformed(evt);
            }
        });

        jLabel1.setText("Cuota:");

        cuotaTxt.setEditable(false);

        fechaTxt.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(montoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jLabel3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(tasaTxt))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(jLabel6)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(interesesTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(crearPrestamoBtn))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(nameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(administrarClienteBtn))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addGap(18, 18, 18)
                                        .addComponent(abonadoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cuotaTxt))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(4, 4, 4)
                                        .addComponent(formaPagoCBox, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel5)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fechaTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))))
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(administrarClienteBtn)
                    .addComponent(nameLabel)
                    .addComponent(crearPrestamoBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(montoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(tasaTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addComponent(formaPagoCBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(fechaTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(interesesTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)
                        .addComponent(abonadoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addComponent(cuotaTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void administrarClienteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_administrarClienteBtnActionPerformed
        // TODO add your handling code here:
        AdministrarCliente adm = new AdministrarCliente(jFrame, true, cliente);
        adm.setLocationRelativeTo(null);
        adm.setVisible(true);
        
        if(adm.isClienteEliminado()) {
            dispose();
        }
    }//GEN-LAST:event_administrarClienteBtnActionPerformed

    private void reloadPagoTable() {
        controller.refresh(prestamo);

        List<Pago> pagos = new ArrayList<Pago>();
        pagos.addAll(prestamo.getAbonos());
        pagos.addAll(prestamo.getPagos());

        PagosTableModel tableModel = (PagosTableModel) pagosTable.getModel();
        tableModel.pagos.clear();
        tableModel.pagos.addAll(pagos);

        pagosTable.updateUI();
        montoTxt.setText(String.valueOf(prestamo.getMonto()));

        double totalAbonado = Controller.getTotalAbonado(prestamo.getAbonos());
        abonadoTxt.setText(String.valueOf(totalAbonado));

        interesesTxt.setText(String.valueOf(prestamo.getInteresAcumulado()));
        interesesTxt.updateUI();
        
        montoTxt.updateUI();
        abonadoTxt.updateUI();

        moraPagoTxt.setText("0");
        montoPagoTxt.setText("");

        montoPagoTxt.grabFocus();

        montoPagoTxt.setBackground(Color.WHITE);
        montoPagoTxt.setForeground(Color.BLACK);
    }

    private void aplicarPagoBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aplicarPagoBtnActionPerformed
        // TODO add your handling code here:
        if (montoPagoTxt.getText().isEmpty() || !containsOnlyNumbers(montoPagoTxt.getText())) {
            JOptionPane.showMessageDialog(jFrame, "Por favor digite un monto", "ERROR DE VALIDACION", JOptionPane.ERROR_MESSAGE);
            montoPagoTxt.grabFocus();
            montoPagoTxt.setBackground(Color.red);
            montoPagoTxt.setForeground(Color.WHITE);
            return;
        }

        if (datePicker.getDate() == null) {
            JOptionPane.showMessageDialog(jFrame, "Por favor digite una fecha", "ERROR DE VALIDACION", JOptionPane.ERROR_MESSAGE);
            datePicker.grabFocus();
            datePicker.setBackground(Color.red);
            datePicker.setForeground(Color.WHITE);
            return;
        }

        if (tipoPagoCBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(jFrame, "Por elija una opcion de pago", "ERROR DE VALIDACION", JOptionPane.ERROR_MESSAGE);
            tipoPagoCBox.grabFocus();
            return;
        }

        if (prestamo.getInteresAcumulado() == 0 && prestamo.getMonto() == 0 || prestamo.getMonto() < 0) {
            JOptionPane.showMessageDialog(rootPane, "Este prestamo ya ha sido saldado", "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean pagoAplicado = false;
        String concepto = "";
        
        if (tipoPagoCBox.getSelectedItem().equals(TipoPago.PAGO_INTERES)) {
            if (!containsOnlyNumbers(moraPagoTxt.getText())) {
                JOptionPane.showMessageDialog(jFrame, "Por favor digite un valor numerico en el campo 'Mora'", "ERROR DE VALIDACION", JOptionPane.ERROR_MESSAGE);
                moraPagoTxt.grabFocus();
                return;
            }

            try {
                pagoAplicado = controller.aplicarPagoIntereses(prestamo,
                        datePicker.getDate(),
                        Double.valueOf(montoPagoTxt.getText()),
                        Double.valueOf(moraPagoTxt.getText()));
                
                concepto = "PAGO INTERES";
                
            } catch (PrestamoException ex) {
                JOptionPane.showMessageDialog(jFrame, ex.getMessage(), "ERROR APLICANDO PAGO", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            try {
                pagoAplicado = controller.aplicarAbonoPrestamo(prestamo, datePicker.getDate(), Double.valueOf(montoPagoTxt.getText()));
                
                concepto = "ABONO";
            } catch (PrestamoException ex) {
                JOptionPane.showMessageDialog(jFrame, ex.getMessage(), "ERROR APLICANDO PAGO", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (pagoAplicado) {
            reloadPagoTable();
            
            int confirmation = JOptionPane.showConfirmDialog(rootPane, "Desea imprimir una factura ?", "IMPRESION DE FACTURA", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            
            if(confirmation == JOptionPane.YES_OPTION) {
                StringBuilder sb = new StringBuilder();
                sb.append("Sistema de Prestamos\n");
                sb.append("--------------------\n\n");
                sb.append("Cliente: ").append(prestamo.getCliente()).append("\n");
                sb.append("Monto Pagado: RD$").append(montoPagoTxt.getText()).append("\n");
                sb.append("Concepto: ").append(concepto).append("\n");

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sb.append("Fecha: ").append(sdf.format(datePicker.getDate())).append("\n\n\n\n\n");
                sb.append("--------------------\n");
                sb.append("      Firma");
                try {
                    imprimirFactura(sb.toString());
                } catch (PrintException ex) {
                    JOptionPane.showMessageDialog(jFrame, ex.getMessage(), "ERROR IMPRIMIENDO FACTURA", JOptionPane.ERROR_MESSAGE);
                }
            }           
        }
    }//GEN-LAST:event_aplicarPagoBtnActionPerformed

    private void crearPrestamoBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_crearPrestamoBtnActionPerformed
        if(evt.getActionCommand().equalsIgnoreCase(RENEGOCIAR)) {
            activarEstadoRenegociar(true);
            
            crearPrestamoBtn.setActionCommand(GUARDAR_CAMBIOS);
            crearPrestamoBtn.setText("GUARDAR CAMBIOS");
            
        } else if(evt.getActionCommand().equalsIgnoreCase(NUEVO_PRESTAMO)){
            CrearPrestamo form = new CrearPrestamo(jFrame, true, cliente);
            form.setLocationRelativeTo(null);
            form.setVisible(true);

            dispose();
        } else if (evt.getActionCommand().equalsIgnoreCase(GUARDAR_CAMBIOS)) {
            prestamo.setMonto(Double.valueOf(montoTxt.getText()));
            prestamo.setFecha(fechaTxt.getDate());
            prestamo.setFormaPago((FormaPago)formaPagoCBox.getSelectedItem());
            prestamo.setTasa(Float.valueOf(tasaTxt.getText()));
            
            try {
                controller.modificarPrestamo(prestamo);
                activarEstadoRenegociar(false);
            } catch (SchedulerException ex) {
                JOptionPane.showMessageDialog(rootPane, ex.getMessage(), "ERROR ELIMINANDO JOB", JOptionPane.ERROR_MESSAGE);                
            }
        }      
    }//GEN-LAST:event_crearPrestamoBtnActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField abonadoTxt;
    private javax.swing.JButton administrarClienteBtn;
    private javax.swing.JButton aplicarPagoBtn;
    private javax.swing.JButton crearPrestamoBtn;
    private javax.swing.JTextField cuotaTxt;
    private org.jdesktop.swingx.JXDatePicker datePicker;
    private org.jdesktop.swingx.JXDatePicker fechaTxt;
    private javax.swing.JComboBox formaPagoCBox;
    private javax.swing.JTextField interesesTxt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField montoPagoTxt;
    private javax.swing.JFormattedTextField montoTxt;
    private javax.swing.JTextField moraPagoTxt;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTable pagosTable;
    private javax.swing.JTextField tasaTxt;
    private javax.swing.JComboBox tipoPagoCBox;
    // End of variables declaration//GEN-END:variables
    private Cliente cliente;
}
