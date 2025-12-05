package Controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import Modelo.BaseDatos;
import Vista.VistaCancelacionVenta;
import Vista.VistaMenu;

/**
 * Controlador para la funcionalidad de cancelación de ventas
 */
public class ControladorCancelacionVenta implements ActionListener {

    private VistaCancelacionVenta vistaCancelacionVenta;
    
    private ControladorMenu controladorMenu;
    private VistaMenu padre;
    private DecimalFormat formatoMoneda = new DecimalFormat("#,##0.00");
    private int ventaId = 0;
    
    /**
     * Constructor que inicializa la vista de cancelación de venta
     * @param controladorMenu Referencia al controlador del menú principal
     */
    public ControladorCancelacionVenta(ControladorMenu controladorMenu) {
        this.controladorMenu = controladorMenu;
        this.vistaCancelacionVenta = new VistaCancelacionVenta();
        this.padre = controladorMenu.getVentana();
        
        // Agregar ActionListeners a los botones
        this.vistaCancelacionVenta.getBtnBuscar().addActionListener(this);
        this.vistaCancelacionVenta.getBtnCancelar().addActionListener(this);
        this.vistaCancelacionVenta.getBtnSalir().addActionListener(this);
        
        // Deshabilitar el botón de cancelar venta hasta que se encuentre una venta
        this.vistaCancelacionVenta.getBtnCancelar().setEnabled(false);
        
       
        padre.desbloquearInterfaz();
      
    }
    
    /**
     * Obtiene la referencia a la ventana de cancelación de venta
     * @return Ventana de cancelación de venta
     */
    public VistaCancelacionVenta getVentana() {
        return this.vistaCancelacionVenta;
    }
    
    /**
     * Devuelve la ventana de cancelación de venta
     * @return JInternalFrame que contiene la vista
     */
    public javax.swing.JInternalFrame getVentanaInterna() {
        return this.vistaCancelacionVenta;
    }
   
    /**
     * Busca una venta por su ID
     */
    private void buscarVenta() {
        try {
            String idTexto = vistaCancelacionVenta.getTxtIdVenta().getText().trim();
            
            if (idTexto.isEmpty()) {
                JOptionPane.showMessageDialog(vistaCancelacionVenta, 
                        "Por favor ingrese el ID de la venta a buscar.", 
                        "Campo Requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int id = Integer.parseInt(idTexto);
            
            BaseDatos bd = new BaseDatos();
            // Especificar explícitamente las columnas necesarias en el orden correcto
            ArrayList<String[]> ventaData = bd.consultar("Ventas", "idVenta, fecha, total, idTipoVenta, cancelada", "idVenta=" + id);
            
            if (ventaData != null && !ventaData.isEmpty()) {
                String[] venta = ventaData.get(0);
                
                // Verificar si la venta ya está cancelada - el índice correcto es 4 (corresponde a la columna "cancelada")
                if (venta.length > 4 && venta[4] != null && venta[4].equals("1")) {
                    JOptionPane.showMessageDialog(vistaCancelacionVenta, 
                            "La venta con ID " + id + " ya ha sido cancelada.", 
                            "Venta Cancelada", JOptionPane.WARNING_MESSAGE);
                    limpiarDatos();
                    return;
                }
                
                // Guardar el ID de la venta
                ventaId = id;
                
                // Mostrar los datos de la venta con verificación de índices correctos
                if (venta.length > 1 && venta[1] != null) {
                    vistaCancelacionVenta.getLblFecha().setText(venta[1]);
                } else {
                    vistaCancelacionVenta.getLblFecha().setText("Fecha no disponible");
                }
                
                if (venta.length > 2 && venta[2] != null) {
                    try {
                        double total = Double.parseDouble(venta[2]);
                        vistaCancelacionVenta.getLblTotal().setText("$" + formatoMoneda.format(total));
                    } catch (NumberFormatException e) {
                        vistaCancelacionVenta.getLblTotal().setText("$0.00");
                        System.err.println("Error al parsear el total: " + e.getMessage());
                    }
                } else {
                    vistaCancelacionVenta.getLblTotal().setText("$0.00");
                }
                
                if (venta.length > 3 && venta[3] != null) {
                    // Mostrar el ID del tipo de pago
                    vistaCancelacionVenta.getLblTipoPago().setText(venta[3]);
                    
                    // Convertir ID a texto descriptivo
                    String tipoPagoTexto = "Desconocido";
                    try {
                        int tipoPagoId = Integer.parseInt(venta[3]);
                        switch (tipoPagoId) {
                            case 1:
                                tipoPagoTexto = "Efectivo";
                                break;
                            case 2:
                                tipoPagoTexto = "Tarjeta";
                                break;
                            default:
                                tipoPagoTexto = "Otro (" + tipoPagoId + ")";
                        }
                    } catch (NumberFormatException e) {
                        tipoPagoTexto = "Error: " + venta[3];
                    }
                    
                    vistaCancelacionVenta.getLblTipoPagoTexto().setText(tipoPagoTexto);
                } else {
                    vistaCancelacionVenta.getLblTipoPago().setText("--");
                    vistaCancelacionVenta.getLblTipoPagoTexto().setText("Tipo de pago no disponible");
                }
                
                // Imprimir información de depuración para entender la estructura
                System.out.println("Estructura de venta obtenida para ID " + id + ": " + venta.length + " elementos");
                System.out.println("Columnas consultadas: idVenta, fecha, total, idTipoVenta, cancelada");
                for (int i = 0; i < venta.length; i++) {
                    System.out.println("Índice " + i + ": " + (venta[i] != null ? venta[i] : "null"));
                }
                
                // Cargar los detalles de la venta
                cargarDetallesVenta(id);
                
                // Habilitar el botón de cancelar
                vistaCancelacionVenta.getBtnCancelar().setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(vistaCancelacionVenta, 
                        "No se encontró ninguna venta con el ID: " + id, 
                        "Venta No Encontrada", JOptionPane.WARNING_MESSAGE);
                limpiarDatos();
            }
            
            bd.cerrarConexion();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vistaCancelacionVenta, 
                    "Por favor ingrese un ID válido (sólo números).", 
                    "Formato Inválido", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vistaCancelacionVenta, 
                    "Error al buscar la venta: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Carga los detalles de una venta en la tabla
     */
    private void cargarDetallesVenta(int idVenta) {
        try {
            BaseDatos bd = new BaseDatos();
            
            // Usar la consulta correcta con el nombre de columna adecuado
            ArrayList<String[]> detalles = bd.consultar(
                "DetalleVentas d JOIN Productos p ON d.idProducto = p.idProducto", 
                "d.idProducto, p.producto, d.cantidad, d.precioUnitario, (CONVERT(FLOAT, d.cantidad) * CONVERT(FLOAT, d.precioUnitario)) AS subtotall", 
                "d.idVenta = " + idVenta);
            
            // Crear modelo para la tabla
            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[] {"ID", "Producto", "Cantidad", "Precio Unit.", "Subtotal"}, 0);
            
            if (detalles != null && !detalles.isEmpty()) {
                for (String[] detalle : detalles) {
                    double precioUnitario = Double.parseDouble(detalle[3]);
                    double subtotal = Double.parseDouble(detalle[4]);
                    
                    modelo.addRow(new Object[] {
                            detalle[0],
                            detalle[1],
                            detalle[2],
                            "$" + formatoMoneda.format(precioUnitario),
                            "$" + formatoMoneda.format(subtotal)
                    });
                }
            }
            
            vistaCancelacionVenta.getTblDetalles().setModel(modelo);
            bd.cerrarConexion();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vistaCancelacionVenta, 
                    "Error al cargar los detalles de la venta: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Cancela la venta seleccionada
     */
    private void cancelarVenta() {
        if (ventaId <= 0) {
            JOptionPane.showMessageDialog(vistaCancelacionVenta, 
                    "No hay una venta seleccionada para cancelar.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(vistaCancelacionVenta, 
                "¿Está seguro de que desea cancelar la venta con ID " + ventaId + "?\n" +
                "Esta acción no se puede deshacer.", 
                "Confirmar Cancelación", JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                BaseDatos bd = new BaseDatos();
                
                // Obtener los detalles de la venta para devolver el stock
                ArrayList<String[]> detalles = bd.consultar(
                        "DetalleVentas", "idProducto, cantidad", "idVenta=" + ventaId);
                
                if (detalles != null && !detalles.isEmpty()) {
                    // Por cada producto, devolver el stock
                    for (String[] detalle : detalles) {
                        int idProducto = Integer.parseInt(detalle[0]);
                        int cantidad = Integer.parseInt(detalle[1]);
                        
                        // Actualizar el stock del producto
                        ArrayList<String[]> stockActual = bd.consultar("Productos", "stock", "idProducto=" + idProducto);
                        if (stockActual != null && !stockActual.isEmpty()) {
                            int nuevoStock = Integer.parseInt(stockActual.get(0)[0]) + cantidad;
                            String valorStock = String.valueOf(nuevoStock);
                            bd.modificar("Productos", "stock", valorStock, "idProducto=" + idProducto);
                            
                            // Registrar el movimiento en el inventario
                            Date fechaActual = new Date();
                            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
                            String fecha = formatoFecha.format(fechaActual);
                            String hora = formatoHora.format(fechaActual);
                            
                            String[] valoresMovimiento = {
                                String.valueOf(idProducto),
                                fecha,
                                hora,
                                "Cancelación de venta #" + ventaId,
                                String.valueOf(cantidad),
                                "0",  // Tipo: 0 = Entrada
                                String.valueOf(nuevoStock)
                            };
                            
                            bd.insertar("Inventario", 
                                    "idProducto,fecha,hora,descripcion,cantidad,tipo,stockFinal", 
                                    valoresMovimiento);
                        }
                    }
                }
                
                // Marcar la venta como cancelada
                String valorCancelada = "1";
                boolean resultado = bd.modificar("Ventas", "cancelada", valorCancelada, "idVenta=" + ventaId);
                
                if (resultado) {
                    JOptionPane.showMessageDialog(vistaCancelacionVenta, 
                            "Venta cancelada con éxito. Los productos han sido devueltos al inventario.", 
                            "Cancelación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Limpiar los datos de la venta
                    limpiarDatos();
                } else {
                    JOptionPane.showMessageDialog(vistaCancelacionVenta, 
                            "Error al cancelar la venta. Intente nuevamente.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                
                bd.cerrarConexion();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(vistaCancelacionVenta, 
                        "Error al cancelar la venta: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Limpia los datos de la venta en la vista
     */
    private void limpiarDatos() {
        ventaId = 0;
        vistaCancelacionVenta.getTxtIdVenta().setText("");
        vistaCancelacionVenta.getLblFecha().setText("--/--/----");
        vistaCancelacionVenta.getLblTotal().setText("$0.00");
        vistaCancelacionVenta.getLblTipoPago().setText("----");
        
        // Limpiar la tabla de detalles
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[] {"ID", "Producto", "Cantidad", "Precio Unit.", "Subtotal"}, 0);
        vistaCancelacionVenta.getTblDetalles().setModel(modelo);
        
        // Deshabilitar el botón de cancelar
        vistaCancelacionVenta.getBtnCancelar().setEnabled(false);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaCancelacionVenta.getBtnBuscar()) {
            buscarVenta();
        } else if (e.getSource() == vistaCancelacionVenta.getBtnCancelar()) {
            cancelarVenta();
        } else if (e.getSource() == vistaCancelacionVenta.getBtnSalir()) {
            vistaCancelacionVenta.dispose();
        }
    }
}
