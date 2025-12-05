

package Vista;
import Vista.VistaMenu;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import Modelo.BaseDatos;

public class VistaCancelacionVenta extends JInternalFrame {
    private VistaMenu padre;
    private JTextField tIdVenta;
    private JLabel tFecha, tTotal, tMotivo;
    private JLabel lblTipoPagoTexto; // Declaración de la variable para el texto descriptivo del tipo de pago
    private JButton bBuscar, bCancelarVenta, bSalir;
    private JTable tablaDetalles;
    private DefaultTableModel modeloTabla;
    
    public VistaCancelacionVenta() {
        super("Cancelación de Ventas", true, true, true, true);
        this.setSize(800, 600);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        iniciarComponentes();
        cargarVentasEnTabla(null); // Cargar todas las ventas al iniciar
       // Cargar todas las ventas al iniciar
    }
    
    private void iniciarComponentes() {
        // Inicializar el campo tIdVenta
        tIdVenta = new JTextField(10); // Permitir espacio para ingresar ID
       
        // Inicializar los botones
        bBuscar = new JButton("Buscar");
        setLayout(new BorderLayout(10, 10));
        
        // Panel superior con título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(231, 76, 60));
        panelTitulo.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel lblTitulo = new JLabel("CANCELACIÓN DE VENTAS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelTitulo.add(lblTitulo);
        
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel de búsqueda por ID
        JPanel panelBusquedaId = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusquedaId.setBorder(new EmptyBorder(10, 20, 0, 20));
        panelBusquedaId.setBackground(Color.WHITE);
        JLabel lblIdVenta = new JLabel("ID Venta a Buscar:");
        lblIdVenta.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelBusquedaId.add(lblIdVenta);
        panelBusquedaId.add(tIdVenta);
        panelBusquedaId.add(bBuscar); // Mover el botón buscar aquí

        // Panel central
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBorder(new EmptyBorder(0, 20, 15, 20)); // Ajustar borde superior
        panelCentral.setBackground(Color.WHITE);

        panelCentral.add(panelBusquedaId); // Añadir panel de búsqueda por ID
        
        // Espacio
        panelCentral.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Tabla de detalles
        String[] columnas = {"ID Venta", "Fecha", "Total", "Tipo de Venta", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaDetalles = new JTable(modeloTabla);
        tablaDetalles.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaDetalles.setRowHeight(25);
        tablaDetalles.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaDetalles.getTableHeader().setBackground(new Color(231, 76, 60));
        tablaDetalles.getTableHeader().setForeground(Color.WHITE);
        
        // Configurar listener para selección en tabla (después de inicializar la tabla)
        tablaDetalles.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarDatosVentaSeleccionada();
                
                // Habilitar/deshabilitar botón cancelar según estado de la venta
                int filaSeleccionada = tablaDetalles.getSelectedRow();
                if (filaSeleccionada >= 0) {
                    String estado = (String) modeloTabla.getValueAt(filaSeleccionada, 4);
                    bCancelarVenta.setEnabled(!estado.equals("Cancelada"));
                } else {
                    bCancelarVenta.setEnabled(false);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaDetalles);
        scrollPane.setBorder(new CompoundBorder(
                new TitledBorder("Listado de Ventas"),
                new EmptyBorder(10, 10, 10, 10)
        ));
        panelCentral.add(scrollPane);
        
        // Espacio
        panelCentral.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Panel información de venta
        JPanel panelInfo = new JPanel(new GridLayout(2, 4, 15, 10));
        panelInfo.setBackground(Color.WHITE);
        panelInfo.setBorder(new CompoundBorder(
                new TitledBorder("Información de Venta Seleccionada"),
                new EmptyBorder(10, 10, 10, 10)
        ));
        panelInfo.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        
        // Fecha
        JLabel lblFecha = new JLabel("Total");
        lblFecha.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelInfo.add(lblFecha);
        
        tFecha = new JLabel("--/--/----");
        tFecha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelInfo.add(tFecha);
        
        // Total
        JLabel lblTotal = new JLabel("Tipo de Pago:");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelInfo.add(lblTotal);
        
        tTotal = new JLabel("--");
        tTotal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelInfo.add(tTotal);
        
        // Tipo de Pago
        JLabel lblTipoPago = new JLabel("Fecha:"); // Cambiado de tMotivo a lblTipoPago
        lblTipoPago.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelInfo.add(lblTipoPago);
        
        tMotivo = new JLabel("----"); // Este JLabel mostrará el tipo de pago o estado.
        tMotivo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelInfo.add(tMotivo);
        
        // Inicializar lblTipoPagoTexto (aunque no sea visible en este panel)
        lblTipoPagoTexto = new JLabel(""); 
        
        panelCentral.add(panelInfo);
        
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setBorder(new EmptyBorder(15, 0, 15, 0));
        panelBotones.setBackground(new Color(240, 240, 240));
        
        // Configurar botón de búsqueda
        bBuscar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bBuscar.setBackground(new Color(52, 152, 219)); // Color azul
        bBuscar.setForeground(Color.WHITE);
        bBuscar.setPreferredSize(new Dimension(150, 40));
        bBuscar.setFocusPainted(false);
        // La acción de buscar se definirá en el controlador o aquí mismo si es simple
        bBuscar.addActionListener(e -> {
            String idVentaBusqueda = tIdVenta.getText().trim();
            limpiarCamposInfoVenta(); // Limpiar detalles de venta anterior
            if (!idVentaBusqueda.isEmpty()) {
                cargarVentasEnTabla(idVentaBusqueda);
            } else {
                cargarVentasEnTabla(null); // Cargar todas si no hay ID
            }
        });
        
        bCancelarVenta = new JButton("Cancelar Venta");
        bCancelarVenta.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bCancelarVenta.setBackground(new Color(231, 76, 60));
        bCancelarVenta.setForeground(Color.WHITE);
        bCancelarVenta.setPreferredSize(new Dimension(150, 40));
        bCancelarVenta.setFocusPainted(false);
        bCancelarVenta.setEnabled(false); 
        bCancelarVenta.addActionListener(e -> {
            procesarCancelacionVenta();
        });
        
        bSalir = new JButton("Salir");
        bSalir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bSalir.setBackground(new Color(52, 73, 94));
        bSalir.setForeground(Color.WHITE);
        bSalir.setPreferredSize(new Dimension(150, 40));
        bSalir.setFocusPainted(false);
        bSalir.addActionListener(e -> {
            dispose(); // Cerrar la ventana
        });
        
        // No añadir bBuscar aquí ya que está en panelBusquedaId
        panelBotones.add(bCancelarVenta);
        panelBotones.add(bSalir);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    // Getters
    public JTextField getTxtIdVenta() {
        return tIdVenta;
    }
    
    public JLabel getLblFecha() {
        return tFecha;
    }
    
    public JLabel getLblTotal() {
        return tTotal;
    }
    
    public JLabel getLblTipoPago() { //Getter para el label de Tipo de Pago
        return tMotivo; // tMotivo ahora muestra el tipo de pago o estado
    }
    
    public JLabel getTMotivo() { // Conservado por si se usa en otro lado, pero es el mismo que getLblTipoPago
        return tMotivo;
    }
    
    public JButton getBBuscar() {
        return bBuscar;
    }
    
    // Mantener el método getBtnBuscar para compatibilidad con código existente
    public JButton getBtnBuscar() {
        return bBuscar;
    }
    
    public JButton getBtnCancelar() {
        return bCancelarVenta;
    }
    
    public JButton getBtnSalir() {
        return bSalir;
    }
    
    public JTable getTblDetalles() {
        return tablaDetalles;
    }
    
    public DefaultTableModel getModeloTabla() {
        return modeloTabla;
    }
    
    // Métodos para actualizar la interfaz
    
    // Método para limpiar la tabla de detalles
    public void limpiarTabla() {
        modeloTabla.setRowCount(0);
    }

    // Método para limpiar solo los campos de información de la venta seleccionada
    private void limpiarCamposInfoVenta() {
        tFecha.setText("--/--/----");
        tTotal.setText("---");
        tMotivo.setText("----");
        bCancelarVenta.setEnabled(false);
    }
    
    // Método para limpiar los campos de información y el ID de búsqueda
    public void limpiarCampos() {
        tIdVenta.setText(""); // Limpiar también el campo de ID de búsqueda
        limpiarCamposInfoVenta();
        // No es necesario limpiar la tabla aquí si la búsqueda la recarga
    }
    
    public javax.swing.JLabel getLblTipoPagoTexto() {
        return lblTipoPagoTexto;
    }
    
    // Método para cargar los datos de una venta seleccionada en la tabla
    public void cargarDatosVentaSeleccionada() {
        int filaSeleccionada = tablaDetalles.getSelectedRow();
        if (filaSeleccionada >= 0) {
            // Tomar el ID de la venta de la tabla para asegurar consistencia
            String idVentaSeleccionada = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
            // Opcionalmente, podrías poner este ID en tIdVenta si es útil para otro flujo
            // tIdVenta.setText(idVentaSeleccionada); 

            String fecha = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
            String totalStr = (String) modeloTabla.getValueAt(filaSeleccionada, 2);
            String tipoVenta = (String) modeloTabla.getValueAt(filaSeleccionada, 3);
            String estado = (String) modeloTabla.getValueAt(filaSeleccionada, 4);
            
            tFecha.setText(fecha);
            tTotal.setText(totalStr);
            tMotivo.setText(tipoVenta); // Mostrar tipo de venta
            
            // Habilitar/deshabilitar botón según estado
            bCancelarVenta.setEnabled(!estado.equals("Cancelada"));
        } else {
            limpiarCamposInfoVenta(); // Si no hay selección, limpiar info
        }
    }
    
    // Método para mostrar mensajes
    public void mostrarMensaje(String titulo, String mensaje, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }
    
    // Método para confirmar la cancelación
    public boolean confirmarCancelacion() {
        int filaSeleccionada = tablaDetalles.getSelectedRow();
        if (filaSeleccionada < 0) {
            mostrarMensaje("Advertencia", "Debe seleccionar una venta de la lista para cancelar.", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        String idVenta = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        int opcion = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de cancelar la venta ID: " + idVenta + "?\nEsta acción devolverá todos los productos al inventario.", 
                "Confirmar Cancelación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return opcion == JOptionPane.YES_OPTION;
    }
    
    // Método para procesar la cancelación de una venta seleccionada
    public void procesarCancelacionVenta() {
        int filaSeleccionada = tablaDetalles.getSelectedRow();
        if (filaSeleccionada < 0) {
            mostrarMensaje("Advertencia", "Debe seleccionar una venta para cancelar.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String idVenta = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        String estado = (String) modeloTabla.getValueAt(filaSeleccionada, 4);
        
        if ("Cancelada".equals(estado)) {
            mostrarMensaje("Información", "Esta venta ya ha sido cancelada.", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (confirmarCancelacion()) {
            try {
                BaseDatos bd = new BaseDatos();
                
                // Obtener los detalles de la venta para devolver el stock
                ArrayList<String[]> detalles = bd.consultar(
                        "DetalleVentas", "idProducto, cantidad", "idVenta=" + idVenta);
                
                if (detalles != null && !detalles.isEmpty()) {
                    // Por cada producto, devolver el stock
                    for (String[] detalle : detalles) {
                        String idProducto = detalle[0];
                        int cantidad = Integer.parseInt(detalle[1]);
                        
                        // Actualizar el stock del producto
                        ArrayList<String[]> stockActual = bd.consultar("Productos", "stock", "idProducto=" + idProducto);
                        if (stockActual != null && !stockActual.isEmpty()) {
                            int nuevoStock = Integer.parseInt(stockActual.get(0)[0]) + cantidad;
                            String valorStock = String.valueOf(nuevoStock);
                            bd.modificar("Productos", "stock", valorStock, "idProducto=" + idProducto);
                        }
                    }
                }
                
                // Marcar la venta como cancelada
                String valorCancelada = "1";
                boolean resultado = bd.modificar("Ventas", "cancelada", valorCancelada, "idVenta=" + idVenta);
                
                bd.cerrarConexion();
                
                if (resultado) {
                    mostrarMensaje("Éxito", "Venta cancelada correctamente. Los productos han sido devueltos al inventario.", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Actualizar la tabla
                    modeloTabla.setValueAt("Cancelada", filaSeleccionada, 4);
                    bCancelarVenta.setEnabled(false);
                } else {
                    mostrarMensaje("Error", "No se pudo cancelar la venta.", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                mostrarMensaje("Error", "Error al cancelar la venta: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Método para cargar las ventas en la tabla, con filtro opcional por ID
    private void cargarVentasEnTabla(String idVentaFiltrar) {
        try {
            BaseDatos bd = new BaseDatos();
            String condicion = "";
            if (idVentaFiltrar != null && !idVentaFiltrar.isEmpty()) {
                // Asegurarse de que el idVentaFiltrar es numérico o manejar la excepción
                try {
                    Integer.parseInt(idVentaFiltrar); // Validar que sea un número
                    condicion = "idVenta = " + idVentaFiltrar;
                } catch (NumberFormatException e) {
                    mostrarMensaje("Error de Búsqueda", "El ID de Venta debe ser un número.", JOptionPane.ERROR_MESSAGE);
                    bd.cerrarConexion();
                    return;
                }
            }
            
            ArrayList<String[]> ventas = bd.consultar("Ventas", "idVenta, fecha, total, idTipoVenta, cancelada", condicion);
            
            limpiarTabla(); // Limpiar la tabla antes de cargar nuevos datos
            
            if (ventas != null && !ventas.isEmpty()) {
                for (String[] venta : ventas) {
                    String idVenta = venta[0];
                    String fecha = venta[1];
                    double total = Double.parseDouble(venta[2]);
                    String tipoVenta = obtenerNombreTipoVenta(venta[3]);
                    String estado = venta[4] != null && venta[4].equals("1") ? "Cancelada" : "Activa";
                    
                    Object[] fila = {
                        idVenta,
                        fecha,
                        "$" + new DecimalFormat("#,##0.00").format(total),
                        tipoVenta,
                        estado
                    };
                    modeloTabla.addRow(fila);
                }
                if (idVentaFiltrar != null && !idVentaFiltrar.isEmpty() && ventas.size() == 1) {
                    // Si se buscó por ID y se encontró una, seleccionarla automáticamente
                    tablaDetalles.setRowSelectionInterval(0, 0);
                    cargarDatosVentaSeleccionada(); // Cargar los datos de la venta seleccionada
                } else if (idVentaFiltrar != null && !idVentaFiltrar.isEmpty() && ventas.isEmpty()) {
                     mostrarMensaje("Información", "No se encontró ninguna venta con el ID: " + idVentaFiltrar, JOptionPane.INFORMATION_MESSAGE);
                }
            } else if (idVentaFiltrar != null && !idVentaFiltrar.isEmpty()) {
                mostrarMensaje("Información", "No se encontró ninguna venta con el ID: " + idVentaFiltrar, JOptionPane.INFORMATION_MESSAGE);
            }
            
            bd.cerrarConexion();
        } catch (Exception e) {
            mostrarMensaje("Error", "Error al cargar las ventas: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        } finally {
            // Asegurarse de que los campos de info se limpien si la tabla está vacía o no hay selección
            if (tablaDetalles.getSelectedRow() == -1) {
                limpiarCamposInfoVenta();
            }
        }
    }
    
    // Método para obtener el nombre del tipo de venta
    private String obtenerNombreTipoVenta(String idTipoVenta) {
        if (idTipoVenta == null) return "Desconocido";
        
        switch (idTipoVenta) {
            case "1": return "Efectivo";
            case "2": return "Tarjeta";
            case "3": return "Transferencia";
            default: return "Otro";
        }
    }
}
