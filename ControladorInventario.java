package Controlador;

import Modelo.BaseDatos;
import Modelo.Producto;
import Vista.VentanaProducto;
import Vista.VentanaProducto2;
import Vista.VistaAjusteInventario;
import Vista.VistaMenu;
import Vista.VistaMovimientoInventario;
import Vista.VistaReporteInventario;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class ControladorInventario implements ActionListener {

    private VentanaProducto ventanaVista;
    private VistaMenu padre;
    private VentanaProducto2 ventana;
    private List<Producto> listaProductos;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private DecimalFormat formatoMoneda = new DecimalFormat("#,##0.00");

    public ControladorInventario(VistaMenu padre) {
        this.padre = padre;
        this.listaProductos = new ArrayList<>();
        this.ventana = new VentanaProducto2();
        ventana.setTitle("Gestión de Inventarios");
        
        
        // Configurar tabla
        configurarTablaInventario();
        
        // Cargar productos
        cargarProductos();
        
        // Configurar listeners
        configurarEventos();
        
      
            ventana.setVisible(true);
          padre.Menus(true);
			
            
    }
         
     
    private void configurarTablaInventario() {
        // Configurar el modelo de tabla con columnas específicas para inventario
        String[] columnas = {"ID", "SKU", "Producto", "Categoría", "Stock", "Stock Mínimo", "Precio Costo", "Precio Venta"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Hacer la tabla no editable
            }
        };
        
        ventana.getTdatos().setModel(modeloTabla);
        
        // Configurar ordenamiento
        sorter = new TableRowSorter<>(modeloTabla);
        ventana.getTdatos().setRowSorter(sorter);
    }

    private void configurarEventos() {
        // Botones existentes
        ventana.getBsalida().addActionListener(this);
      
        
        // Renombrar botones para funcionalidades de inventario
        ventana.getBagregar().setText("Entrada Mercancía");
        ventana.getBagregar().addActionListener(this);
        
        ventana.getBmodificar().setText("Salida Mercancía");
        ventana.getBmodificar().addActionListener(this);
        
        ventana.getBeliminar().setText("Ajuste Manual");
        ventana.getBeliminar().addActionListener(this);
        
        // Agregar evento para mostrar detalles al hacer clic en un producto
        ventana.getTdatos().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mostrarDetalleProducto();
            }
        });
    }

    private void cargarProductos() {
        BaseDatos bd = null;
        try {
            bd = new BaseDatos();
            // ⚠️ CRÍTICO: Especificar columnas para evitar confusión con idProveedor
            String columnas = "idProducto, sku, producto, idcategoria, precio_venta, marca, stock, descripcion";
            ArrayList<String[]> lista = bd.consultar("Productos", columnas, "activo = 1");

            // Limpiar listas y modelo
            listaProductos.clear();
            modeloTabla.setRowCount(0);

            for (String[] datos : lista) {
                if (datos.length >= 8) {
                    Producto producto = new Producto();
                    producto.setIdProducto(datos[0]);
                    producto.setSku(datos[1]);
                    producto.setProducto(datos[2]);
                    producto.setIdCategoria(datos[3]);
                    
                    // Validar precio venta
                    try {
                        producto.setPrecioVenta(Double.parseDouble(datos[4]));
                    } catch (NumberFormatException e) {
                        producto.setPrecioVenta(0.0);
                    }
                    
                    producto.setMarca(datos[5]);
                    
                    // Validar stock
                    try {
                        producto.setStock(Integer.parseInt(datos[6]));
                    } catch (NumberFormatException e) {
                        producto.setStock(0);
                    }
                    
                    producto.setDescripcion(datos[7]);
                    
                    // Agregar a la lista de productos
                    listaProductos.add(producto);
                    
                    // Agregar a la tabla de inventario
                    String estadoStock = determinarEstadoStock(producto);
                    
                    // Stock mínimo (podría estar en la base de datos, aquí usamos un valor fijo para demostración)
                    int stockMinimo = 5;
                    
                    // Precio de costo (podría estar en otra tabla, usamos un valor de ejemplo)
                    double precioCosto = producto.getPrecioVenta() * 0.7; // 70% del precio de venta como ejemplo
                    
                    // Proveedor (idealmente desde la base de datos)
                    String proveedor = "Proveedor por defecto";
                    
                    modeloTabla.addRow(new Object[]{
                        producto.getIdProducto(),
                        producto.getSku(),
                        producto.getProducto(),
                        producto.getIdCategoria(),
                        producto.getStock(),
                        stockMinimo,
                        formatoMoneda.format(precioCosto),
                        formatoMoneda.format(producto.getPrecioVenta()),
                        proveedor,
                        estadoStock
                    });
                }
            }
            
            System.out.println("✅ Inventario cargado: " + listaProductos.size() + " productos");
            
            // Actualizar anchura de columnas para mejor visualización
            ajustarAnchoColumnas();
            
        } catch (Exception e) {
            System.out.println("❌ Error al cargar inventario: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(ventana,
                    "Error al cargar datos de inventario: " + e.getMessage(),
                    "Error de Conexión",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            if (bd != null) {
                bd.cerrarConexion();
            }
        }
    }
    
    private String determinarEstadoStock(Producto producto) {
        int stock = producto.getStock();
        int stockMinimo = 5; // Valor fijo para ejemplo, podría obtenerse de la base de datos
        
        if (stock <= 0) {
            return "Sin Stock";
        } else if (stock < stockMinimo) {
            return "Stock Bajo";
        } else {
            return "Disponible";
        }
    }
    
    private void ajustarAnchoColumnas() {
        JTable tabla = ventana.getTdatos();
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        tabla.getColumnModel().getColumn(1).setPreferredWidth(80);  // SKU
        tabla.getColumnModel().getColumn(2).setPreferredWidth(200); // Producto
        tabla.getColumnModel().getColumn(3).setPreferredWidth(80);  // Categoría
        tabla.getColumnModel().getColumn(4).setPreferredWidth(60);  // Stock
        tabla.getColumnModel().getColumn(5).setPreferredWidth(80);  // Stock Mínimo
        tabla.getColumnModel().getColumn(6).setPreferredWidth(100); // Precio Costo
        tabla.getColumnModel().getColumn(7).setPreferredWidth(100); // Precio Venta
       
    }
    
    private void mostrarDetalleProducto() {
        int filaSeleccionada = ventana.getTdatos().getSelectedRow();
        if (filaSeleccionada >= 0) {
            // Convertir índice de vista a modelo (importante cuando hay filtros aplicados)
            int filaModelo = ventana.getTdatos().convertRowIndexToModel(filaSeleccionada);
            
            // Obtener datos del producto seleccionado
            String idProducto = modeloTabla.getValueAt(filaModelo, 0).toString();
            
            // Buscar el producto en la lista de productos
            Producto productoSeleccionado = null;
            for (Producto p : listaProductos) {
                if (p.getIdProducto().equals(idProducto)) {
                    productoSeleccionado = p;
                    break;
                }
            }
            
            if (productoSeleccionado != null) {
                // Mostrar información detallada en el panel de descripción
                StringBuilder htmlContent = new StringBuilder();
                htmlContent.append("<html><body>");
                htmlContent.append("<h3>").append(productoSeleccionado.getProducto()).append("</h3>");
                htmlContent.append("<p><b>SKU:</b> ").append(productoSeleccionado.getSku()).append("</p>");
                htmlContent.append("<p><b>ID:</b> ").append(productoSeleccionado.getIdProducto()).append("</p>");
                htmlContent.append("<p><b>Categoría:</b> ").append(productoSeleccionado.getIdCategoria()).append("</p>");
                htmlContent.append("<p><b>Marca:</b> ").append(productoSeleccionado.getMarca()).append("</p>");
                htmlContent.append("<p><b>Stock:</b> ").append(productoSeleccionado.getStock()).append("</p>");
                htmlContent.append("<p><b>Precio:</b> $").append(formatoMoneda.format(productoSeleccionado.getPrecioVenta())).append("</p>");
                htmlContent.append("<p><b>Descripción:</b><br>").append(productoSeleccionado.getDescripcion()).append("</p>");
                htmlContent.append("</body></html>");
                
                ventana.getTdescripcion().setText(htmlContent.toString());
                
                // Aquí se podría cargar una imagen del producto si existe
                // ventanaVista.getImagen().setIcon(new ImageIcon("ruta/a/imagen/" + productoSeleccionado.getIdProducto() + ".jpg"));
            }
        }
    }
    
   
        
        // Crear filtro según el tipo de búsqueda
    
    private void mostrarEntradaMercancia() {
        VistaMovimientoInventario vista = new VistaMovimientoInventario(SwingUtilities.getWindowAncestor(ventana), "Entrada de Mercancía", listaProductos);
        
        vista.setConfirmarListener(e -> {
            try {
                // Obtener datos del formulario
                String idProducto = vista.getIdProductoSeleccionado();
                int cantidad = Integer.parseInt(vista.getCantidad());
                String referencia = vista.getReferencia();
                
                if (idProducto == null || idProducto.isEmpty()) {
                    JOptionPane.showMessageDialog(vista, "Seleccione un producto", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (cantidad <= 0) {
                    JOptionPane.showMessageDialog(vista, "La cantidad debe ser mayor a cero", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Buscar el producto
                Producto producto = null;
                for (Producto p : listaProductos) {
                    if (p.getIdProducto().equals(idProducto)) {
                        producto = p;
                        break;
                    }
                }
                
                if (producto != null) {
                        // No actualizamos el stock aquí ya que registrarMovimientoInventario ya lo hace
                        int stockActual = producto.getStock();
                        
                        // Registrar movimiento (esto actualizará el stock automáticamente)
                        registrarMovimientoInventario(idProducto, "ENTRADA", cantidad, referencia);
                        
                        // Actualizar el stock en memoria después de registrar el movimiento
                        int nuevoStock = producto.getStock();
                    
                    JOptionPane.showMessageDialog(vista, 
                            "Entrada registrada correctamente.\nStock actualizado: " + stockActual + " → " + nuevoStock, 
                            "Operación Exitosa", 
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    // Cerrar diálogo y recargar tabla
                    vista.dispose();
                    cargarProductos();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(vista, "Ingrese una cantidad válida", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, 
                        "Error al registrar entrada: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        
        vista.setCancelarListener(e -> vista.dispose());
        
        vista.setVisible(true);
    }
    
    private void mostrarSalidaMercancia() {
        VistaMovimientoInventario vista = new VistaMovimientoInventario(SwingUtilities.getWindowAncestor(ventana), "Salida de Mercancía", listaProductos);
        
        vista.setConfirmarListener(e -> {
            try {
                // Obtener datos del formulario
                String idProducto = vista.getIdProductoSeleccionado();
                int cantidad = Integer.parseInt(vista.getCantidad());
                String referencia = vista.getReferencia();
                
                if (idProducto == null || idProducto.isEmpty()) {
                    JOptionPane.showMessageDialog(vista, "Seleccione un producto", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (cantidad <= 0) {
                    JOptionPane.showMessageDialog(vista, "La cantidad debe ser mayor a cero", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Buscar el producto
                Producto producto = null;
                for (Producto p : listaProductos) {
                    if (p.getIdProducto().equals(idProducto)) {
                        producto = p;
                        break;
                    }
                }
                
                if (producto != null) {
                    // Verificar stock disponible
                    int stockActual = producto.getStock();
                    if (stockActual < cantidad) {
                        JOptionPane.showMessageDialog(vista, 
                                "Stock insuficiente. Disponible: " + stockActual, 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                                                    // Registrar movimiento (esto actualizará el stock automáticamente)
                                                    registrarMovimientoInventario(idProducto, "SALIDA", cantidad, referencia);
                                                    
                                                    // Obtener el nuevo stock después de registrar el movimiento
                                                    int nuevoStock = producto.getStock();
                    
                    JOptionPane.showMessageDialog(vista, 
                            "Salida registrada correctamente.\nStock actualizado: " + stockActual + " → " + nuevoStock, 
                            "Operación Exitosa", 
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    // Cerrar diálogo y recargar tabla
                    vista.dispose();
                    cargarProductos();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(vista, "Ingrese una cantidad válida", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, 
                        "Error al registrar salida: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        
        vista.setCancelarListener(e -> vista.dispose());
        
        vista.setVisible(true);
    }
    
    private void mostrarAjusteManual() {
        VistaAjusteInventario vista = new VistaAjusteInventario(SwingUtilities.getWindowAncestor(ventana), listaProductos);
        
        vista.setConfirmarListener(e -> {
            try {
                // Obtener datos del formulario
                String idProducto = vista.getIdProductoSeleccionado();
                int nuevoStock = Integer.parseInt(vista.getNuevoStock());
                String motivo = vista.getMotivo();
                
                if (idProducto == null || idProducto.isEmpty()) {
                    JOptionPane.showMessageDialog(vista, "Seleccione un producto", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (nuevoStock < 0) {
                    JOptionPane.showMessageDialog(vista, "El stock no puede ser negativo", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (motivo == null || motivo.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(vista, "Ingrese un motivo para el ajuste", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Buscar el producto
                Producto producto = null;
                for (Producto p : listaProductos) {
                    if (p.getIdProducto().equals(idProducto)) {
                        producto = p;
                        break;
                    }
                }
                
                if (producto != null) {
                    // Obtener stock actual para calcular la diferencia
                    int stockActual = producto.getStock();
                    int diferencia = nuevoStock - stockActual;
                    
                    // Actualizar stock en memoria
                    producto.setStock(nuevoStock);
                    
                    // Actualizar en base de datos
                    BaseDatos bd = new BaseDatos();
                    bd.modificar("Productos", "stock", String.valueOf(nuevoStock), "idProducto = '" + idProducto + "'");
                    bd.cerrarConexion();
                    
                    // Registrar movimiento
                    String tipoMovimiento = diferencia > 0 ? "AJUSTE_INCREMENTO" : "AJUSTE_DECREMENTO";
                    registrarMovimientoInventario(idProducto, tipoMovimiento, Math.abs(diferencia), "Ajuste manual: " + motivo);
                    
                    JOptionPane.showMessageDialog(vista, 
                            "Ajuste realizado correctamente.\nStock actualizado: " + stockActual + " → " + nuevoStock, 
                            "Operación Exitosa", 
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    // Cerrar diálogo y recargar tabla
                    vista.dispose();
                    cargarProductos();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(vista, "Ingrese un valor numérico válido", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, 
                        "Error al realizar ajuste: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        
        vista.setCancelarListener(e -> vista.dispose());
        
        vista.setVisible(true);
    }
    
    /**
     * Registra un movimiento en el inventario
     * @param idProducto ID del producto afectado
     * @param tipo Tipo de movimiento ("ENTRADA", "SALIDA", "AJUSTE")
     * @param cantidad Cantidad de productos (positiva para entradas, negativa para salidas)
     * @param referencia Información adicional sobre el movimiento
     */
    private void registrarMovimientoInventario(String idProducto, String tipo, int cantidad, String referencia) {
        // Registrar información del movimiento en consola
        System.out.println("MOVIMIENTO INVENTARIO: " + tipo + 
                           " | ID Producto: " + idProducto + 
                           " | Cantidad: " + cantidad + 
                           " | Ref: " + referencia + 
                           " | Fecha: " + new java.util.Date());
                           
        // Registrar en la base de datos
        try {
            BaseDatos bd = new BaseDatos();
            
            // Generar un ID único para el movimiento (formato: MOV-[timestamp]-[random])
            String idMovimiento = "MOV-" + System.currentTimeMillis() + "-" + 
                                 String.format("%04d", new java.util.Random().nextInt(10000));
            
            // Fecha actual en formato adecuado para SQL Server
            java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
            String fecha = timestamp.toString();
            
            String[] valores = {
                idMovimiento,
                idProducto,
                tipo,
                String.valueOf(cantidad),
                fecha,
                referencia
            };
            
            boolean exito = bd.insertar("MovimientosInventario", 
                                       "id, idProducto, tipo, cantidad, fecha, referencia", 
                                       valores);
            
            if (exito) {
                System.out.println("✅ Movimiento de inventario registrado correctamente con ID: " + idMovimiento);
                
                // Actualizar el stock del producto
                actualizarStockProducto(idProducto, cantidad, tipo);
            } else {
                System.err.println("❌ No se pudo registrar el movimiento de inventario");
            }
            
            bd.cerrarConexion();
        } catch (Exception e) {
            System.err.println("❌ Error al registrar movimiento: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                "Error al registrar movimiento de inventario: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarReporteInventario() {
        VistaReporteInventario vista = new VistaReporteInventario(SwingUtilities.getWindowAncestor(ventanaVista), listaProductos);
        vista.setVisible(true);
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ventana.getBsalida()) {
            padre.Menus(true);
            ventana.dispose();
       
        } else if (e.getSource() == ventana.getBagregar()) {
            mostrarEntradaMercancia();
        } else if (e.getSource() == ventana.getBmodificar()) {
            mostrarSalidaMercancia();
        } else if (e.getSource() == ventana.getBeliminar()) {
            mostrarAjusteManual();
        }
       
        }
    
    
    public JInternalFrame getVentana() {
        if (ventana != null && !ventana.isVisible()) {
            // Si la ventana existe pero no es visible, hacerla visible
            ventana.setVisible(true);
        }
        return ventana;
    }

    
    /**
     * Consulta los movimientos de inventario de un producto
     * @param idProducto ID del producto o null para consultar todos
     * @param fechaInicio Fecha de inicio en formato YYYY-MM-DD o null para no filtrar
     * @param fechaFin Fecha de fin en formato YYYY-MM-DD o null para no filtrar
     * @return Lista con los movimientos encontrados
     */
    public ArrayList<String[]> consultarMovimientosInventario(String idProducto, String fechaInicio, String fechaFin) {
    try {
        BaseDatos bd = new BaseDatos();
        
        // Construir condición según los parámetros
        StringBuilder condicion = new StringBuilder();
        
        if (idProducto != null && !idProducto.isEmpty()) {
            condicion.append("idProducto = '").append(idProducto).append("'");
        } 
        
        if (fechaInicio != null && !fechaInicio.isEmpty()) {
            if (condicion.length() > 0) condicion.append(" AND ");
            condicion.append("DATE(fecha) >= '").append(fechaInicio).append("'");
        }
        
        if (fechaFin != null && !fechaFin.isEmpty()) {
            if (condicion.length() > 0) condicion.append(" AND ");
            condicion.append("DATE(fecha) <= '").append(fechaFin).append("'");
        }
        
        // Campos a consultar
        String campos = "id, idProducto, tipo, cantidad, fecha, referencia";
        
        // Ejecutar consulta
        ArrayList<String[]> movimientos = bd.consultar(
            "MovimientosInventario", 
            campos, 
            condicion.length() > 0 ? condicion.toString() : null
        );
        
        bd.cerrarConexion();
        return movimientos;
    } catch (Exception e) {
        System.err.println("❌ Error al consultar movimientos de inventario: " + e.getMessage());
        return new ArrayList<>();
    }
    }
    
    /**
     * Actualiza el stock de un producto después de un movimiento de inventario
     * @param idProducto ID del producto a actualizar
     * @param cantidad Cantidad del movimiento
     * @param tipoMovimiento Tipo de movimiento (ENTRADA, SALIDA, AJUSTE)
     */
    /**
     * Consulta los datos de un producto por su ID
     * @param idProducto ID del producto a consultar
     * @return Objeto Producto con los datos o null si no se encuentra
     */
    private Producto consultarProducto(String idProducto) {
        try {
            BaseDatos bd = new BaseDatos();
            String consulta = "idProducto = '" + idProducto + "' AND (activo = 1 OR activo IS NULL)";
            // Orden consistente: idProducto, sku, producto, idcategoria, precio_venta, marca, stock, descripcion
            ArrayList<String[]> resultados = bd.consultar("Productos", 
                "idProducto, sku, producto, idcategoria, precio_venta, marca, stock, descripcion, activo", consulta);
            
            bd.cerrarConexion();
            
            if (resultados == null || resultados.isEmpty()) {
                System.err.println("No se encontró el producto con ID: " + idProducto);
                return null;
            }
            
            String[] datos = resultados.get(0);
            Producto producto = new Producto();
            // Orden: idProducto, sku, producto, idcategoria, precio_venta, marca, stock, descripcion, activo
            producto.setIdProducto(datos[0]);  // idProducto
            producto.setSku(datos[1]);         // sku
            producto.setProducto(datos[2]);    // producto
            producto.setIdCategoria(datos[3]); // idcategoria
            
            try {
                if (datos[4] != null && !datos[4].isEmpty()) {
                    producto.setPrecioVenta(Double.parseDouble(datos[4]));  // precio_venta
                }
            } catch (NumberFormatException e) {
                System.err.println("Error al convertir precio: " + e.getMessage());
            }
            
            if (datos.length > 5 && datos[5] != null) {
                producto.setMarca(datos[5]);  // marca
            }
            
            try {
                if (datos[6] != null && !datos[6].isEmpty()) {
                    producto.setStock(Integer.parseInt(datos[6]));  // stock
                }
            } catch (NumberFormatException e) {
                System.err.println("Error al convertir stock: " + e.getMessage());
            }
            
            if (datos.length > 7 && datos[7] != null) {
                producto.setDescripcion(datos[7]);  // descripcion
            }
            
            // Establecer el estado activo del producto
            if (datos.length > 8 && datos[8] != null) {
                producto.setActivo("1".equals(datos[8]) || "true".equalsIgnoreCase(datos[8]));  // activo
            } else {
                producto.setActivo(true); // Por defecto está activo
            }
            
            return producto;
        } catch (Exception e) {
            System.err.println("Error al consultar el producto: " + e.getMessage());
            return null;
        }
    }
    
    private void actualizarStockProducto(String idProducto, int cantidad, String tipoMovimiento) {
    try {
        // Consultar stock actual
        Producto producto = consultarProducto(idProducto);
        if (producto == null) {
            System.err.println("❌ No se puede actualizar el stock: Producto no encontrado");
            return;
        }
        
        int stockActual = producto.getStock();
        int nuevoStock = stockActual;
        
        // Calcular nuevo stock según el tipo de movimiento
        if (tipoMovimiento.equals("ENTRADA")) {
            nuevoStock = stockActual + cantidad;
        } else if (tipoMovimiento.equals("SALIDA")) {
            nuevoStock = stockActual - cantidad;
        } else if (tipoMovimiento.equals("AJUSTE")) {
            // Para ajustes, la cantidad representa el valor final
            nuevoStock = cantidad;
        }
        
        // Evitar stock negativo
        if (nuevoStock < 0) {
            System.err.println("⚠️ Advertencia: El stock resultaría negativo. Se establece a 0.");
            nuevoStock = 0;
        }
        
        // Actualizar en la base de datos
        BaseDatos bd = new BaseDatos();
        boolean exito = bd.modificar("Productos", "stock", String.valueOf(nuevoStock), 
                                    "idProducto = '" + idProducto + "'");
        
        if (exito) {
            System.out.println("✅ Stock actualizado: " + stockActual + " → " + nuevoStock);
                
                // Actualizar el objeto en memoria
                // Buscar el producto en la lista y actualizar su stock
                for (Producto p : listaProductos) {
                    if (p.getIdProducto().equals(idProducto)) {
                        p.setStock(nuevoStock);
                        break;
                    }
                }
        } else {
            System.err.println("❌ No se pudo actualizar el stock del producto");
        }
        
        bd.cerrarConexion();
    } catch (Exception e) {
        System.err.println("❌ Error al actualizar stock: " + e.getMessage());
    }
    }
    
   
            

}
