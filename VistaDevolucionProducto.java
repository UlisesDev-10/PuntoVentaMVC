package Vista;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

public class VistaDevolucionProducto extends JInternalFrame {
    
    private JTextField tIdVenta, tFecha, tTotal, tMotivo;
    private JButton bBuscar, bDevolver, bSalir, bAgregar, bEliminar;
    private JTable tablaDetalles;
    private DefaultTableModel modeloTabla;
    private JCheckBox[] seleccionProductos;
    private JSpinner[] cantidadDevolucion;
    private JComboBox<String> comboProductos;
    private JTextField txtCantidad;
    
    public VistaDevolucionProducto() {
        super("Devolución de Productos", true, true, true, true);
        this.setSize(800, 600);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            
            // Inicializar el modelo de tabla primero para evitar NullPointerException
            String[] columnas = {"Seleccionar", "ID Producto", "Código", "Descripción", "Precio", "Cantidad Original", "Cantidad a Devolver", "Subtotal"};
            modeloTabla = new DefaultTableModel(columnas, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 0 || column == 6; // Solo editables las columnas de selección y cantidad
                }
                
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 0) {
                        return Boolean.class;
                    }
                    return super.getColumnClass(columnIndex);
                }
            };
            
            iniciarComponentes();
            System.out.println("VistaDevolucionProducto inicializada. Modelo de tabla creado.");
    }
    
    private void iniciarComponentes() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel superior con título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(41, 128, 185));
        panelTitulo.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel lblTitulo = new JLabel("DEVOLUCIÓN DE PRODUCTOS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelTitulo.add(lblTitulo);
        
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBorder(new EmptyBorder(15, 20, 15, 20));
        panelCentral.setBackground(Color.WHITE);
        
        // Panel búsqueda
        JPanel panelBusqueda = new JPanel();
        panelBusqueda.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.setBackground(Color.WHITE);
        panelBusqueda.setBorder(new CompoundBorder(
                new TitledBorder("Buscar Venta"),
                new EmptyBorder(10, 10, 10, 10)
        ));
        panelBusqueda.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        
        JLabel lblIdVenta = new JLabel("ID de Venta:");
        lblIdVenta.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelBusqueda.add(lblIdVenta);
        
        tIdVenta = new JTextField();
        tIdVenta.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tIdVenta.setPreferredSize(new Dimension(150, 35));
        panelBusqueda.add(tIdVenta);
        
        bBuscar = new JButton("Buscar");
        bBuscar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bBuscar.setBackground(new Color(52, 152, 219));
        bBuscar.setForeground(Color.WHITE);
        bBuscar.setPreferredSize(new Dimension(100, 35));
        bBuscar.setFocusPainted(false);
        panelBusqueda.add(bBuscar);
        
        panelCentral.add(panelBusqueda);
        
        // Espacio
        panelCentral.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Panel información de venta
        JPanel panelInfo = new JPanel(new GridLayout(2, 4, 15, 10));
        panelInfo.setBackground(Color.WHITE);
        panelInfo.setBorder(new CompoundBorder(
                new TitledBorder("Información de Venta"),
                new EmptyBorder(10, 10, 10, 10)
        ));
        panelInfo.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        
        // Fecha
        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelInfo.add(lblFecha);
        
        tFecha = new JTextField();
        tFecha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tFecha.setEditable(false);
        panelInfo.add(tFecha);
        
        // Total
        JLabel lblTotal = new JLabel("Total:");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelInfo.add(lblTotal);
        
        tTotal = new JTextField();
        tTotal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tTotal.setEditable(false);
        panelInfo.add(tTotal);
        
        // Motivo de devolución
        JLabel lblMotivo = new JLabel("Motivo de Devolución:");
        lblMotivo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelInfo.add(lblMotivo);
        
        tMotivo = new JTextField();
        tMotivo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tMotivo.setPreferredSize(new Dimension(300, 35));
        panelInfo.add(tMotivo);
        
        panelCentral.add(panelInfo);
        
        // Espacio
        panelCentral.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Tabla de detalles con checkboxes para seleccionar productos
        // El modelo ya fue creado en el constructor, no necesitamos crear otro
        
        tablaDetalles = new JTable(modeloTabla);
        tablaDetalles.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaDetalles.setRowHeight(30);
        tablaDetalles.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaDetalles.getTableHeader().setBackground(new Color(41, 128, 185));
        tablaDetalles.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(tablaDetalles);
        scrollPane.setBorder(new CompoundBorder(
                new TitledBorder("Seleccione los Productos a Devolver"),
                new EmptyBorder(10, 10, 10, 10)
        ));
        panelCentral.add(scrollPane);
        
        // Instrucciones para el usuario
        JPanel panelInstrucciones = new JPanel();
        panelInstrucciones.setBackground(Color.WHITE);
        panelInstrucciones.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JLabel lblInstrucciones = new JLabel("Seleccione los productos y especifique la cantidad a devolver para cada uno.");
        lblInstrucciones.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblInstrucciones.setForeground(new Color(52, 73, 94));
        panelInstrucciones.add(lblInstrucciones);
        
        panelCentral.add(panelInstrucciones);
        
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setBorder(new EmptyBorder(15, 0, 15, 0));
        panelBotones.setBackground(new Color(240, 240, 240));
        
        bDevolver = new JButton("Procesar Devolución");
        bDevolver.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bDevolver.setBackground(new Color(46, 204, 113));
        bDevolver.setForeground(Color.WHITE);
        bDevolver.setPreferredSize(new Dimension(200, 40));
        bDevolver.setFocusPainted(false);
        bDevolver.setEnabled(false); // Inicialmente deshabilitado hasta buscar una venta
        
        bSalir = new JButton("Salir");
        bSalir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bSalir.setBackground(new Color(52, 73, 94));
        bSalir.setForeground(Color.WHITE);
        bSalir.setPreferredSize(new Dimension(150, 40));
        bSalir.setFocusPainted(false);
        
        panelBotones.add(bDevolver);
        panelBotones.add(bSalir);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    // Getters
    public JTextField getTxtNumeroVenta() {
        return tIdVenta;
    }
    
    public JTextField getTxtFecha() {
        return tFecha;
    }
    
    public JTextField getTxtTotal() {
        return tTotal;
    }
    
    public JTextField getTxtMotivo() {
        return tMotivo;
    }
    
    public JButton getBtnBuscar() {
        return bBuscar;
    }
    
    public JButton getBtnProcesar() {
        return bDevolver;
    }
    
    public JButton getBtnCancelar() {
        return bSalir;
    }
    
    public JTable getTablaProductos() {
        return tablaDetalles;
    }
    
    public DefaultTableModel getModelTabla() {
        return modeloTabla;
    }
    
    // Métodos para actualizar la interfaz
    
    // Método para limpiar la tabla de detalles
    public void limpiarTabla() {
            // Guardar el modelo y volver a asignarlo después de limpiarlo
            if (modeloTabla != null) {
                System.out.println("Limpiando tabla. Filas antes: " + modeloTabla.getRowCount());
                modeloTabla.setRowCount(0);
                
                // Asegurar que las columnas estén definidas correctamente
                if (modeloTabla.getColumnCount() == 0) {
                    String[] columnas = {"Seleccionar", "ID Producto", "Código", "Descripción", "Precio", "Cantidad Original", "Cantidad a Devolver", "Subtotal"};
                    for (String columna : columnas) {
                        modeloTabla.addColumn(columna);
                    }
                    System.out.println("Se han restablecido las columnas de la tabla");
                }
                
                tablaDetalles.setModel(modeloTabla);
                tablaDetalles.repaint();
                System.out.println("Tabla limpiada. Filas después: " + modeloTabla.getRowCount());
            } else {
                System.out.println("ADVERTENCIA: modeloTabla es null cuando se intenta limpiar");
                // Intentar recrear el modelo
                String[] columnas = {"Seleccionar", "ID Producto", "Código", "Descripción", "Precio", "Cantidad Original", "Cantidad a Devolver", "Subtotal"};
                modeloTabla = new DefaultTableModel(columnas, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return column == 0 || column == 6; // Solo editables las columnas de selección y cantidad
                    }
                    
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        if (columnIndex == 0) {
                            return Boolean.class;
                        }
                        return super.getColumnClass(columnIndex);
                    }
                };
                tablaDetalles.setModel(modeloTabla);
                System.out.println("Se ha creado un nuevo modelo para la tabla");
            }
    }
    
    // Método para limpiar los campos de información
    public void limpiarCampos() {
        tIdVenta.setText("");
        tFecha.setText("");
        tTotal.setText("");
        tMotivo.setText("");
        bDevolver.setEnabled(false);
        limpiarTabla();
    }
    
    // Método para cargar los datos de una venta
    public void cargarDatosVenta(String fecha, double total) {
        tFecha.setText(fecha);
        DecimalFormat df = new DecimalFormat("#,##0.00");
        tTotal.setText(df.format(total));
        bDevolver.setEnabled(true);
    }
    
    // Método para agregar un producto a la tabla de detalles
    public void agregarProductoTabla(String idProducto, String codigo, String descripcion, 
                                  double precio, int cantidad, double subtotal) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        
        try {
            if (modeloTabla == null) {
                System.out.println("ERROR: modeloTabla es null");
                return;
            }
            
            Object[] fila = {
                false, // Checkbox para seleccionar (inicialmente no seleccionado)
                idProducto,
                codigo,
                descripcion,
                df.format(precio),
                cantidad,
                0, // Inicialmente 0 para la cantidad a devolver
                df.format(subtotal)
            };
            modeloTabla.addRow(fila);
            
            // Agregar renderer y editor de spinner para la columna de cantidad
            int ultimaFila = modeloTabla.getRowCount() - 1;
            tablaDetalles.setValueAt(0, ultimaFila, 6); // Inicialmente 0
                
            // Forzar la actualización de la tabla
            tablaDetalles.repaint();
            System.out.println("Producto agregado a la tabla: " + descripcion + " (ID: " + idProducto + ")");
            System.out.println("Filas en la tabla después de agregar: " + modeloTabla.getRowCount());
        } catch (Exception e) {
            System.out.println("Error al agregar producto a la tabla: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Método simplificado para agregar producto que puede ser útil en algunos contextos
        public void agregarProducto(String idProducto, String codigo, String descripcion, double precio, int cantidad) {
            double subtotal = precio * cantidad;
            agregarProductoTabla(idProducto, codigo, descripcion, precio, cantidad, subtotal);
        }
        
        // Método para configurar los renders y editores de la tabla después de agregar todos los productos
    public void configurarTabla() {
        // Configurar la columna de cantidad a devolver con spinners
        tablaDetalles.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                // Renderizar como texto normal
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });
        
        tablaDetalles.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(new JTextField()) {
            private JSpinner spinner;
            
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                int cantidadMax = 0;
                try {
                    cantidadMax = Integer.parseInt(table.getValueAt(row, 5).toString());
                } catch (NumberFormatException e) {
                    cantidadMax = 1;
                }
                
                int valorActual = 0;
                try {
                    valorActual = Integer.parseInt(value.toString());
                } catch (NumberFormatException e) {
                    valorActual = 0;
                }
                
                spinner = new JSpinner(new SpinnerNumberModel(valorActual, 0, cantidadMax, 1));
                return spinner;
            }
            
            @Override
            public Object getCellEditorValue() {
                return spinner.getValue();
            }
        });
    }
    
    // Método para obtener el combo de productos
    public JComboBox getComboProductos() {
        // Como no existe un campo real de comboProductos en esta clase,
        // crearemos uno temporal para que no falle el controlador
        return new JComboBox();
    }
    
    // Método para obtener la cantidad de productos
    public JTextField getTxtCantidad() {
        // Campo temporal para evitar fallos en el controlador
        return new JTextField();
    }
    
    // Método para obtener los productos seleccionados para devolución
    public Object[][] getProductosSeleccionados() {
        int filas = modeloTabla.getRowCount();
        int contadorSeleccionados = 0;
        
        // Contar cuántos productos están seleccionados
        for (int i = 0; i < filas; i++) {
            boolean seleccionado = (boolean) modeloTabla.getValueAt(i, 0);
            Object cantObj = modeloTabla.getValueAt(i, 6);
            int cantidadDevolver = 0;
            
            try {
                if (cantObj instanceof Integer) {
                    cantidadDevolver = (Integer) cantObj;
                } else if (cantObj instanceof String) {
                    cantidadDevolver = Integer.parseInt(cantObj.toString());
                } else if (cantObj != null) {
                    cantidadDevolver = Integer.parseInt(cantObj.toString());
                }
            } catch (NumberFormatException e) {
                cantidadDevolver = 0;
            }
            
            if (seleccionado && cantidadDevolver > 0) {
                contadorSeleccionados++;
            }
        }
        
        // Crear y llenar el array con los productos seleccionados
        Object[][] seleccionados = new Object[contadorSeleccionados][5]; // ID, código, descripción, precio, cantidad
        int indice = 0;
        
        for (int i = 0; i < filas; i++) {
            boolean seleccionado = (boolean) modeloTabla.getValueAt(i, 0);
            Object cantObj = modeloTabla.getValueAt(i, 6);
            int cantidadDevolver = 0;
            
            try {
                if (cantObj instanceof Integer) {
                    cantidadDevolver = (Integer) cantObj;
                } else if (cantObj instanceof String) {
                    cantidadDevolver = Integer.parseInt(cantObj.toString());
                } else if (cantObj != null) {
                    cantidadDevolver = Integer.parseInt(cantObj.toString());
                }
            } catch (NumberFormatException e) {
                cantidadDevolver = 0;
            }
            
            if (seleccionado && cantidadDevolver > 0) {
                seleccionados[indice][0] = modeloTabla.getValueAt(i, 1); // ID Producto
                seleccionados[indice][1] = modeloTabla.getValueAt(i, 2); // Código
                seleccionados[indice][2] = modeloTabla.getValueAt(i, 3); // Descripción
                    
                    // Manejar correctamente el precio (columna 4)
                    Object precioObj = modeloTabla.getValueAt(i, 4);
                    double precio = 0.0;
                    try {
                        if (precioObj instanceof Double) {
                            precio = (Double) precioObj;
                        } else if (precioObj instanceof String) {
                            precio = Double.parseDouble(precioObj.toString());
                        } else if (precioObj != null) {
                            precio = Double.parseDouble(precioObj.toString());
                        }
                    } catch (NumberFormatException e) {
                        precio = 0.0;
                    }
                    
                    seleccionados[indice][3] = precio; // Precio (ya convertido a Double)
                seleccionados[indice][4] = cantidadDevolver; // Cantidad a devolver
                indice++;
            }
        }
        
        return seleccionados;
    }
    
    // Método para obtener el botón agregar
    public JButton getBtnAgregar() {
        // Campo temporal para evitar fallos en el controlador
        return new JButton("Agregar");
    }
    
    // Método para obtener el botón eliminar
    public JButton getBtnEliminar() {
        // Campo temporal para evitar fallos en el controlador
        return new JButton("Eliminar");
    }
    
    // Método para depurar el estado de la tabla
    public void mostrarEstadoTabla() {
        if (modeloTabla == null) {
            System.out.println("ERROR: modeloTabla es null");
            return;
        }
        
        int filas = modeloTabla.getRowCount();
        System.out.println("Estado actual de la tabla:");
        System.out.println("Número de filas: " + filas);
        
        for (int i = 0; i < filas; i++) {
            StringBuilder fila = new StringBuilder("Fila " + i + ": ");
            for (int j = 0; j < modeloTabla.getColumnCount(); j++) {
                fila.append("[").append(j).append("]=").append(modeloTabla.getValueAt(i, j)).append(" | ");
            }
            System.out.println(fila.toString());
        }
    }
    
    // Método para calcular el total de la devolución
    public double calcularTotalDevolucion() {
        int filas = modeloTabla.getRowCount();
        double totalDevolucion = 0.0;
        
        for (int i = 0; i < filas; i++) {
            boolean seleccionado = (boolean) modeloTabla.getValueAt(i, 0);
            
            // Obtener la cantidad a devolver de forma segura
            Object cantObj = modeloTabla.getValueAt(i, 6);
            int cantidadDevolver = 0;
            
            try {
                if (cantObj instanceof Integer) {
                    cantidadDevolver = (Integer) cantObj;
                } else if (cantObj instanceof String) {
                    cantidadDevolver = Integer.parseInt(cantObj.toString());
                } else if (cantObj != null) {
                    cantidadDevolver = Integer.parseInt(cantObj.toString());
                }
            } catch (NumberFormatException e) {
                cantidadDevolver = 0;
            }
            
            if (seleccionado && cantidadDevolver > 0) {
                // Obtener el precio de forma segura
                String precioStr = modeloTabla.getValueAt(i, 4).toString();
                double precio = 0.0;
                
                try {
                    precio = Double.parseDouble(precioStr.replace(",", ""));
                } catch (NumberFormatException e) {
                    try {
                        // Intentar quitar el formato de moneda
                        precio = Double.parseDouble(precioStr.replace("$", "").replace(",", "").trim());
                    } catch (NumberFormatException ex) {
                        precio = 0.0;
                    }
                }
                
                totalDevolucion += precio * cantidadDevolver;
            }
        }
        
        return totalDevolucion;
    }
    
    // Método para mostrar mensajes
    public void mostrarMensaje(String titulo, String mensaje, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }
    
    // Método para confirmar la devolución
    public boolean confirmarDevolucion() {
        int opcion = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de procesar esta devolución?\nEsta acción devolverá los productos seleccionados al inventario.", 
                "Confirmar Devolución", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return opcion == JOptionPane.YES_OPTION;
    }
}
