package Controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import Vista.VistaMenu;
import Vista.VistaReportes;

public class ControladorMenuReportes implements ActionListener, ItemListener {
    
    private VistaReportes ventana;
    private VistaMenu padre;
    private ControladorMenu menuPrincipal;
    private ControladorReportes controladorReportes;
    
    
    public ControladorMenuReportes(ControladorMenu menu) {
        this.menuPrincipal = menu;
        this.controladorReportes = new ControladorReportes();
        
        ventana = new VistaReportes();
        ventana.setTitle("Generación de Reportes");
        
        // Agregar listeners a los componentes
        ventana.getComboTipoReporte().addItemListener(this);
        ventana.getBtnGenerar().addActionListener(this);
        ventana.getBtnCancelar().addActionListener(this);
        
        // Agregar la ventana al escritorio y maximizarla
        menu.getMiVentana().getEscritorio().add(ventana);
        try {
            ventana.setMaximum(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
            
        ventana.setVisible(true);
        menuPrincipal.getMiVentana().Menus(true);
    }
    
    public JInternalFrame getVentana() {
            // Si la ventana está cerrada o no existe, crear una nueva
            if (ventana == null || ventana.isClosed()) {
                ventana = new VistaReportes();
                ventana.setTitle("Generación de Reportes");
                
                // Agregar listeners a los componentes
                ventana.getComboTipoReporte().addItemListener(this);
                ventana.getBtnGenerar().addActionListener(this);
                ventana.getBtnCancelar().addActionListener(this);
                
                // Agregar la ventana al escritorio y centrarla
                menuPrincipal.getMiVentana().getEscritorio().add(ventana);
                ventana.setLocation(
                    (menuPrincipal.getMiVentana().getEscritorio().getWidth() - ventana.getWidth()) / 2,
                    (menuPrincipal.getMiVentana().getEscritorio().getHeight() - ventana.getHeight()) / 2
                );
                
                ventana.setVisible(true);
            }
        return ventana;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ventana.getBtnGenerar()) {
            generarReporte();
        } else if (e.getSource() == ventana.getBtnCancelar()) {
            cancelar();
        }
    }
    
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == ventana.getComboTipoReporte() && e.getStateChange() == ItemEvent.SELECTED) {
            String tipoReporte = (String) ventana.getComboTipoReporte().getSelectedItem();
            ventana.mostrarPanelConfiguracion(tipoReporte);
        }
    }
    
    private void generarReporte() {
        String tipoReporte = (String) ventana.getComboTipoReporte().getSelectedItem();
        String rutaReporte = null;
        
        try {
            switch (tipoReporte) {
                case "Listado de Productos":
                    boolean incluyeStockCero = ventana.getRbTodosProductos().isSelected();
                    rutaReporte = controladorReportes.generarReporteProductos(incluyeStockCero);
                    if (rutaReporte == null) {
                        JOptionPane.showMessageDialog(ventana, 
                                "No hay productos disponibles para generar el reporte", 
                                "Sin datos", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    break;
                    
                case "Reporte de Ventas":
                    Date fechaInicio = ventana.getFechaInicioVentas().getDate();
                    Date fechaFin = ventana.getFechaFinVentas().getDate();
                    
                    if (fechaInicio == null || fechaFin == null) {
                        JOptionPane.showMessageDialog(ventana, 
                                "Debe seleccionar las fechas de inicio y fin", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (fechaFin.before(fechaInicio)) {
                        JOptionPane.showMessageDialog(ventana, 
                                "La fecha de fin no puede ser anterior a la fecha de inicio", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
                    String fechaInicioStr = formatoFecha.format(fechaInicio);
                    String fechaFinStr = formatoFecha.format(fechaFin);
                    
                    boolean incluirCanceladas = ventana.getRbTodasVentas().isSelected();
                    rutaReporte = controladorReportes.generarReporteVentas(fechaInicioStr, fechaFinStr, incluirCanceladas);
                    if (rutaReporte == null) {
                        JOptionPane.showMessageDialog(ventana, 
                                "No hay ventas registradas en el período seleccionado", 
                                "Sin datos", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    break;
                    
                case "Reporte de Caja":
                    Date fechaCaja = ventana.getFechaCaja().getDate();
                    
                    if (fechaCaja == null) {
                        JOptionPane.showMessageDialog(ventana, 
                                "Debe seleccionar una fecha para el reporte de caja", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    SimpleDateFormat formatoFechaCaja = new SimpleDateFormat("yyyy-MM-dd");
                    String fechaCajaStr = formatoFechaCaja.format(fechaCaja);
                    
                    rutaReporte = controladorReportes.generarReporteCaja(fechaCajaStr);
                    if (rutaReporte == null) {
                        JOptionPane.showMessageDialog(ventana, 
                                "No hay datos de caja registrados para la fecha seleccionada", 
                                "Sin datos", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    break;
                                            
                                        case "Productos Más Vendidos":
                                            Date fechaInicioProd = ventana.getFechaInicioProductosMasVendidos().getDate();
                                            Date fechaFinProd = ventana.getFechaFinProductosMasVendidos().getDate();
                                            
                                            if (fechaInicioProd == null || fechaFinProd == null) {
                        JOptionPane.showMessageDialog(ventana, 
                                "Debe seleccionar las fechas de inicio y fin", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                                            }
                                            
                                            if (fechaFinProd.before(fechaInicioProd)) {
                        JOptionPane.showMessageDialog(ventana, 
                                "La fecha de fin no puede ser anterior a la fecha de inicio", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                                            }
                                            
                                            // Obtener el límite de productos
                                            int limite = 10; // valor por defecto
                                            try {
                        limite = Integer.parseInt(ventana.getTxtLimiteProductos().getText());
                        if (limite <= 0) {
                            limite = 10;
                            JOptionPane.showMessageDialog(ventana, 
                                    "El límite debe ser mayor a 0. Se usará el valor por defecto (10).", 
                                    "Aviso", JOptionPane.WARNING_MESSAGE);
                        }
                                            } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(ventana, 
                                "Valor de límite inválido. Se usará el valor por defecto (10).", 
                                "Aviso", JOptionPane.WARNING_MESSAGE);
                                            }
                                            
                                            SimpleDateFormat formatoFechaProd = new SimpleDateFormat("yyyy-MM-dd");
                                            String fechaInicioProdStr = formatoFechaProd.format(fechaInicioProd);
                                            String fechaFinProdStr = formatoFechaProd.format(fechaFinProd);
                                            
                                            rutaReporte = controladorReportes.generarReporteProductosMasVendidos(fechaInicioProdStr, fechaFinProdStr, limite);
                                            if (rutaReporte == null) {
                        JOptionPane.showMessageDialog(ventana, 
                                "No hay productos vendidos en el período seleccionado", 
                                "Sin datos", JOptionPane.INFORMATION_MESSAGE);
                        return;
                                            }
                                            break;
            }
            
            if (rutaReporte != null) {
                int opcion = JOptionPane.showConfirmDialog(ventana, 
                        "Reporte generado exitosamente en: " + rutaReporte + "\n¿Desea abrirlo ahora?", 
                        "Reporte Generado", JOptionPane.YES_NO_OPTION);
                
                if (opcion == JOptionPane.YES_OPTION) {
                    controladorReportes.abrirReporte(rutaReporte);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventana, 
                    "Error al generar el reporte: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void cancelar() {
        ventana.dispose();
        menuPrincipal.getMiVentana().Menus(true);
    }
}
