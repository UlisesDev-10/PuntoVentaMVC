package Controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


import Modelo.BaseDatos;
import Vista.VistaDevolucionProducto;
import Vista.VistaMenu;

public class ControladorDevolucionProducto implements ActionListener {
    
    private VistaDevolucionProducto ventana;
    private ControladorMenu menuPrincipal;
    private VistaMenu padre;
    
    public ControladorDevolucionProducto(ControladorMenu menu) {
        this.menuPrincipal = menu;
        ventana = new VistaDevolucionProducto();
        ventana.setTitle("Devolución de Productos");
        ventana.setSize(800, 600);
        
        // Inicializar la tabla de productos
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Código");
        modelo.addColumn("Descripción");
        modelo.addColumn("Cantidad");
        modelo.addColumn("Precio Unitario");
        modelo.addColumn("Subtotal");
        
        ventana.getTablaProductos().setModel(modelo);
        
        // Agregar listeners a los botones
        ventana.getBtnBuscar().addActionListener(this);
        ventana.getBtnAgregar().addActionListener(this);
        ventana.getBtnEliminar().addActionListener(this);
        ventana.getBtnProcesar().addActionListener(this);
        ventana.getBtnCancelar().addActionListener(this);
        
        ventana.setVisible(true);
        
    }
    
    public JInternalFrame getVentana() {
        return ventana;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ventana.getBtnBuscar()) {
            buscarVenta();
        } else if (e.getSource() == ventana.getBtnAgregar()) {
            agregarProducto();
        } else if (e.getSource() == ventana.getBtnEliminar()) {
            eliminarProducto();
        } else if (e.getSource() == ventana.getBtnProcesar()) {
            procesarDevolucion();
        } else if (e.getSource() == ventana.getBtnCancelar()) {
            cancelar();
        }
    }
    
    private void buscarVenta() {
        String numeroVenta = ventana.getTxtNumeroVenta().getText().trim();
        if (numeroVenta.isEmpty()) {
            JOptionPane.showMessageDialog(ventana, 
                    "Debe ingresar un número de venta", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        BaseDatos bd = null;
        try {
            bd = new BaseDatos();
            
            // Buscar la venta en la base de datos
            ArrayList<String[]> datosVenta = bd.consultar("Ventas", "*", "idVenta = '" + numeroVenta + "'");
            
            if (datosVenta.isEmpty()) {
                JOptionPane.showMessageDialog(ventana, 
                        "No se encontró la venta con el número: " + numeroVenta, 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Obtener los datos principales de la venta
            String[] infoVenta = datosVenta.get(0);
            String fecha = infoVenta[1]; // Asumiendo que la fecha está en el índice 1
            double total = 0.0;
            
            // Intentar convertir el total si existe en la posición esperada
            try {
                if (infoVenta.length > 2) {
                    total = Double.parseDouble(infoVenta[2]); // Asumiendo que el total está en el índice 2
                }
            } catch (NumberFormatException ex) {
                System.out.println("Error al convertir el total de la venta: " + ex.getMessage());
            }
            
            // Cargar los datos de la venta en la ventana
            ventana.cargarDatosVenta(fecha, total);
            
            // Buscar los detalles de la venta (productos vendidos)
            ArrayList<String[]> detalleVenta = bd.consultar("DetalleVentas", "*", "idVenta = '" + numeroVenta + "'");
            System.out.println("Número de productos encontrados: " + (detalleVenta != null ? detalleVenta.size() : 0));
            
            // Limpiar la tabla antes de cargar los nuevos productos
            ventana.limpiarTabla();
            
            // Para cada producto en el detalle, buscar su información y agregarlo a la tabla
            for (String[] detalleProducto : detalleVenta) {
                String idProducto = detalleProducto[1]; // Asumiendo que el ID del producto está en el índice 1
                int cantidad = 0;
                double precioUnitario = 0.0;
                double subtotal = 0.0;
                
                try {
                    cantidad = Integer.parseInt(detalleProducto[2]); // Asumiendo que la cantidad está en el índice 2
                    precioUnitario = Double.parseDouble(detalleProducto[3]); // Asumiendo que el precio unitario está en el índice 3
                    subtotal = cantidad * precioUnitario;
                } catch (NumberFormatException ex) {
                    System.out.println("Error al convertir datos numéricos del detalle: " + ex.getMessage());
                }
                
                // Intentamos buscar información del producto en la base de datos
                ArrayList<String[]> datosProducto = bd.consultar("Productos", "*", "idProducto = '" + idProducto + "'");
                
                if (datosProducto != null && !datosProducto.isEmpty()) {
                    // Producto encontrado, usar datos reales del producto
                    String[] infoProducto = datosProducto.get(0);
                    
                    // Extraer información del producto
                    String codigoSKU = infoProducto.length > 1 ? infoProducto[1] : "SKU-" + idProducto;
                    String nombreProducto = infoProducto.length > 2 ? infoProducto[2] : "Producto #" + idProducto;
                    
                    // Agregar producto con datos reales
                    ventana.agregarProductoTabla(idProducto, 
                                              codigoSKU, 
                                              nombreProducto, 
                                              precioUnitario, 
                                              cantidad, 
                                              subtotal);
                    
                    System.out.println("Producto agregado a la tabla con ID: " + idProducto + ", Nombre: " + nombreProducto);
                } else {
                    // Producto no encontrado, usar datos genéricos
                    ventana.agregarProductoTabla(idProducto, 
                                              "SKU-" + idProducto, 
                                              "Producto de venta #" + numeroVenta, 
                                              precioUnitario, 
                                              cantidad, 
                                              subtotal);
                    
                    System.out.println("Producto agregado a la tabla con ID: " + idProducto + " (información genérica)");
                }
            }
                
            // Configurar la tabla después de agregar todos los productos
            ventana.configurarTabla();
                
            // Mostrar estado de la tabla después de cargar los productos
            ventana.mostrarEstadoTabla();
            
            // Habilitar el botón para procesar la devolución
            ventana.getBtnProcesar().setEnabled(true);
            
            JOptionPane.showMessageDialog(ventana, 
                    "Venta #" + numeroVenta + " cargada correctamente.\nSeleccione los productos a devolver y especifique la cantidad.", 
                    "Venta Encontrada", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventana, 
                    "Error al buscar la venta: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            if (bd != null) {
                bd.cerrarConexion();
            }
        }
    }
    
    private void agregarProducto() {
        // Validar que se haya seleccionado un producto
        if (ventana.getComboProductos().getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(ventana, 
                    "Debe seleccionar un producto", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validar cantidad
        String cantidadTexto = ventana.getTxtCantidad().getText().trim();
        if (cantidadTexto.isEmpty()) {
            JOptionPane.showMessageDialog(ventana, 
                    "Debe ingresar una cantidad", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int cantidad = Integer.parseInt(cantidadTexto);
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(ventana, 
                        "La cantidad debe ser mayor a cero", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Aquí iría la lógica para agregar el producto a la devolución
            // Por ahora, mostramos un mensaje de demostración
            JOptionPane.showMessageDialog(ventana, 
                    "Producto agregado a la devolución", 
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(ventana, 
                    "La cantidad debe ser un número entero", 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarProducto() {
        int filaSeleccionada = ventana.getTablaProductos().getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(ventana, 
                    "Debe seleccionar un producto de la tabla", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        DefaultTableModel modelo = (DefaultTableModel) ventana.getTablaProductos().getModel();
        modelo.removeRow(filaSeleccionada);
        
        JOptionPane.showMessageDialog(ventana, 
                "Producto eliminado de la devolución", 
                "Información", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void procesarDevolucion() {
        // Primero vamos a verificar la estructura de la tabla productos para debuggear
        BaseDatos dbCheck = new BaseDatos();
        try {
            // Consultar las tablas existentes
            ArrayList<String[]> tablas = dbCheck.consultar("INFORMATION_SCHEMA.TABLES", "TABLE_NAME", 
                    "TABLE_TYPE = 'BASE TABLE'");
                    
            System.out.println("--- TABLAS EXISTENTES EN LA BASE DE DATOS ---");
            if (tablas != null && !tablas.isEmpty()) {
                for (String[] tabla : tablas) {
                    if (tabla[0].toLowerCase().contains("producto")) {
                        System.out.println("TABLA ENCONTRADA: " + tabla[0]);
                        
                        // Consultar la estructura de esta tabla
                        ArrayList<String[]> columnas = dbCheck.consultar("INFORMATION_SCHEMA.COLUMNS", 
                                "COLUMN_NAME, DATA_TYPE", 
                                "TABLE_NAME = '" + tabla[0] + "'");
                                
                        System.out.println("--- ESTRUCTURA DE LA TABLA " + tabla[0] + " ---");
                        if (columnas != null && !columnas.isEmpty()) {
                            for (String[] columna : columnas) {
                                System.out.println("Columna: " + columna[0] + " - Tipo: " + columna[1]);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al consultar estructura de la base de datos: " + e.getMessage());
        } finally {
            dbCheck.cerrarConexion();
        }
        
        // Verificar si hay productos seleccionados para devolución
        Object[][] productosSeleccionados = ventana.getProductosSeleccionados();
        
        if (productosSeleccionados.length == 0) {
            JOptionPane.showMessageDialog(ventana, 
                    "No ha seleccionado productos para devolver", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Obtener el motivo de la devolución
        String motivo = ventana.getTxtMotivo().getText().trim();
        if (motivo.isEmpty()) {
            JOptionPane.showMessageDialog(ventana, 
                    "Debe ingresar un motivo para la devolución", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Confirmar la devolución
        if (!ventana.confirmarDevolucion()) {
            return;
        }
        
        String idVenta = ventana.getTxtNumeroVenta().getText().trim();
        double totalDevolucion = ventana.calcularTotalDevolucion();
        DecimalFormat df = new DecimalFormat("#,##0.00");
        
        BaseDatos bd = null;
        try {
            bd = new BaseDatos();
            
            // Generar un ID único para la devolución
            String idDevolucion = generarIdDevolucion();
            
            // Registrar la devolución en la tabla devoluciones
            String[] valoresDev = {
                "'" + idDevolucion + "'",
                "'" + idVenta + "'",
                "'" + motivo + "'",
                "'" + String.valueOf(totalDevolucion) + "'",
                "GETDATE()" // Fecha actual en SQL Server
            };
            
            boolean resultadoDev = bd.insertar("Devoluciones", 
                    "idDevolucion, idVenta, motivo, totalDevuelto, fecha", 
                    valoresDev);
            
            if (!resultadoDev) {
                JOptionPane.showMessageDialog(ventana, 
                        "Error al registrar la devolución", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Ya tenemos el ID de devolución generado anteriormente
            // No necesitamos buscarlo de nuevo en la base de datos
            
            // Registrar cada producto devuelto
            for (Object[] producto : productosSeleccionados) {
                // Convertimos de manera segura el ID del producto a String
                String idProducto = producto[0] != null ? producto[0].toString() : "";
                
                // Manejamos la cantidad de forma segura
                int cantidad = 0;
                if (producto[4] instanceof Integer) {
                    cantidad = (Integer) producto[4];
                } else {
                    try {
                        cantidad = Integer.parseInt(producto[4].toString());
                    } catch (NumberFormatException e) {
                        System.out.println("Error al convertir cantidad: " + e.getMessage());
                        cantidad = 0;
                    }
                }
                
                // Manejamos el precio unitario de forma segura
                double precioUnitario = 0.0;
                if (producto[3] instanceof Double) {
                    precioUnitario = (Double) producto[3];
                } else {
                    try {
                        precioUnitario = Double.parseDouble(producto[3].toString());
                    } catch (NumberFormatException e) {
                        System.out.println("Error al convertir precio: " + e.getMessage());
                        precioUnitario = 0.0;
                    }
                }
                
                // Verificar que el producto existe en la base de datos - importante usar el nombre correcto
                // Consultar primero qué nombre exacto tiene la tabla en la base de datos
                ArrayList<String[]> tableName = bd.consultar("INFORMATION_SCHEMA.TABLES", "TABLE_NAME", 
                        "TABLE_NAME LIKE '%producto%' OR TABLE_NAME LIKE '%Producto%'");
                
                String nombreTablaProductos = "productos"; // Nombre por defecto
                
                if (tableName != null && !tableName.isEmpty()) {
                    nombreTablaProductos = tableName.get(0)[0]; // Usar el nombre exacto de la tabla
                    System.out.println("Nombre real de la tabla de productos: " + nombreTablaProductos);
                }
                
                ArrayList<String[]> existeProducto = bd.consultar(nombreTablaProductos, "idProducto, stock", 
                        "idProducto = '" + idProducto + "'");
                
                if (existeProducto == null || existeProducto.isEmpty()) {
                    System.out.println("Advertencia: El producto con ID " + idProducto + " no existe en la tabla " + nombreTablaProductos);
                    JOptionPane.showMessageDialog(ventana, 
                            "Advertencia: El producto con ID " + idProducto + " no existe en la base de datos.\n" +
                            "No se actualizará su stock.", 
                            "Advertencia", JOptionPane.WARNING_MESSAGE);
                    continue; // Saltar este producto
                }
                
                // Registrar detalle de devolución
                // Imprimir los valores a insertar para depuración
                System.out.println("Insertando en detalledevolucion:");
                System.out.println("- idDevolucion: " + idDevolucion);
                System.out.println("- idProducto: " + idProducto);
                System.out.println("- cantidad: " + cantidad);
                
                // Generar un ID único para el detalle de devolución
                String idDetalleDevolucion = "DDEV" + System.currentTimeMillis() + "_" + idProducto;
                
                boolean resultadoDetalle = bd.insertar("detalledevolucion", 
                        "idDetalleDevolucion, idDevolucion, idProducto, cantidad", 
                        new String[] {"'" + idDetalleDevolucion + "'", "'" + idDevolucion + "'", "'" + idProducto + "'", "'" + cantidad + "'"});
                
                if (!resultadoDetalle) {
                    System.err.println("❌ Error al insertar registro en detalledevolucion: " + 
                                     "Valores: idDevolucion=" + idDevolucion + 
                                     ", idProducto=" + idProducto + 
                                     ", cantidad=" + cantidad);
                    
                    JOptionPane.showMessageDialog(ventana, 
                            "Error al registrar el detalle de la devolución para el producto " + idProducto, 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                
                // Actualizar el stock del producto
                try {
                    int stockPrevio = Integer.parseInt(existeProducto.get(0)[1]); // El stock está en el índice 1 del resultado
                    int nuevoStock = stockPrevio + cantidad;
                    
                    System.out.println("Producto ID: " + idProducto + ", Stock Anterior: " + stockPrevio + 
                                       ", Cantidad Devuelta: " + cantidad + ", Nuevo Stock: " + nuevoStock);
                    
                    boolean resultadoStock = bd.modificar("Productos", "stock", 
                            String.valueOf(nuevoStock), 
                            "idProducto = '" + idProducto + "'");
                    
                    if (!resultadoStock) {
                        JOptionPane.showMessageDialog(ventana, 
                                "Error al actualizar el stock del producto " + idProducto, 
                                "Advertencia", JOptionPane.WARNING_MESSAGE);
                    } else {
                        System.out.println("Stock actualizado correctamente para el producto " + idProducto);
                    }
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    System.out.println("Error al procesar stock del producto " + idProducto + ": " + e.getMessage());
                    JOptionPane.showMessageDialog(ventana, 
                            "Error al actualizar el stock del producto " + idProducto + ": " + e.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            
            // Mostrar mensaje de éxito
            JOptionPane.showMessageDialog(ventana, 
                    "Devolución procesada exitosamente\n" +
                    "Monto total devuelto: $" + df.format(totalDevolucion) +
                    "\nNúmero de devolución: " + idDevolucion, 
                    "Devolución Exitosa", JOptionPane.INFORMATION_MESSAGE);
            
            // Limpiar la tabla y campos
            limpiarFormulario();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventana, 
                    "Error al procesar la devolución: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            if (bd != null) {
                bd.cerrarConexion();
            }
        }
    }
    
    /**
     * Retorna la ventana de devolución de productos
     * @return Ventana como JInternalFrame
     */
   
    
    private void cancelar() {
        int opcion = JOptionPane.showConfirmDialog(ventana, 
                "¿Está seguro de cancelar la operación? Se perderán los datos no guardados.", 
                "Confirmar Cancelación", JOptionPane.YES_NO_OPTION);
        
        if (opcion == JOptionPane.YES_OPTION) {
            limpiarFormulario();
            ventana.dispose();
            menuPrincipal.getMiVentana().Menus(true);
        }
    }
    
    private void limpiarFormulario() {
        ventana.limpiarCampos();
    }
        
    // Método para generar un ID único para la devolución
    private String generarIdDevolucion() {
        try {
            BaseDatos bd = new BaseDatos();
            // Obtener el último ID de devolución
            ArrayList<String[]> resultado = bd.consultar("Devoluciones", "MAX(idDevolucion) as maxId", null);
            bd.cerrarConexion();
            
            if (resultado != null && !resultado.isEmpty() && resultado.get(0)[0] != null) {
                // Si hay devoluciones previas, incrementar el último ID
                String ultimoId = resultado.get(0)[0];
                int numero = Integer.parseInt(ultimoId.replaceAll("[^0-9]", "")) + 1;
                return "DEV" + String.format("%07d", numero);
            } else {
                // Si no hay devoluciones previas, empezar con DEV0000001
                return "DEV0000001";
            }
        } catch (Exception e) {
            e.printStackTrace();
            // En caso de error, generar un ID basado en la fecha y hora actual
            return "DEV" + System.currentTimeMillis();
        }
    }
}
