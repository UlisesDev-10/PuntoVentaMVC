
package Controlador;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.JInternalFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ArrayList;
import java.text.DecimalFormat;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JInternalFrame;
import java.beans.PropertyVetoException;

import Modelo.BaseDatos;
import Modelo.Producto;
import Vista.VistaCaja;
import Vista.VistaCajaInicial;
import Vista.VistaPago;
import Vista.VistaTicket;
import Vista.VistaMenu;

import javax.swing.JOptionPane;
import javax.swing.JInternalFrame;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import Modelo.BaseDatos;
import Modelo.Producto;
import Vista.VistaCaja;
import Vista.VistaMenu;
import Vista.VistaPago;
import Vista.VistaTicket;

public class ControladorCaja implements ActionListener {

    private VistaCaja vistaCaja;
    private VistaMenu padre;
    private List<Producto> listaProductos;
    private double totalVenta = 0.0;
    private DecimalFormat formatoMoneda = new DecimalFormat("#,##0.00");
    private ControladorMenu controladorMenu;
    private VistaPago vistaPago;

    /**
     * Constructor del controlador
     * @param padre Referencia al menú principal que contiene el escritorio
     * @param cajaInicial Referencia a la vista de caja inicial
     */
    public ControladorCaja(VistaMenu padre, VistaCajaInicial cajaInicial) {
        try {
            this.padre = padre;
            this.controladorMenu = null;
            this.vistaCaja = new VistaCaja(cajaInicial); // Pasa la referencia
            this.listaProductos = new ArrayList<>();

            this.vistaCaja.getBSalir().addActionListener(this);
            this.vistaCaja.getBAgregar().addActionListener(this);
            this.vistaCaja.getBFinalizar().addActionListener(this);
            this.vistaCaja.getBBuscar().addActionListener(this);
            this.vistaCaja.getBEliminar().addActionListener(this);

            this.vistaCaja.getTCodigo().addActionListener(e -> {
                String codigo = vistaCaja.getTCodigo().getText().trim();
                if (!codigo.isEmpty()) {
                    buscarProducto(codigo);
                }
            });

            cargarProductos();

            if (padre != null && padre.getEscritorio() != null) {
                padre.getEscritorio().add(vistaCaja);
                try {
                    vistaCaja.setMaximum(true);
                    vistaCaja.setSelected(true);
                } catch (java.beans.PropertyVetoException ex) {
                    System.out.println("Error al maximizar/seleccionar ventana: " + ex.getMessage());
                }
            }

            vistaCaja.setVisible(true);

        } catch (Exception e) {
            System.err.println("Error al inicializar ControladorCaja: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Constructor del controlador
     * @param controladorMenu Referencia al controlador del menú principal
     */
    public ControladorCaja(ControladorMenu controladorMenu) {
        try {
            this.controladorMenu = controladorMenu;
            this.padre = controladorMenu.getVentana();
            
            // Eliminar cualquier instancia previa de VistaCaja del escritorio
            if (padre != null && padre.getEscritorio() != null) {
                JInternalFrame[] frames = padre.getEscritorio().getAllFrames();
                for (JInternalFrame frame : frames) {
                    if (frame instanceof VistaCaja) {
                        System.out.println("Eliminando instancia previa de VistaCaja");
                        frame.setVisible(false);
                        padre.getEscritorio().remove(frame);
                        frame.dispose();
                    }
                }
                
                // Actualizar el escritorio
                padre.getEscritorio().revalidate();
                padre.getEscritorio().repaint();
                
                // Pequeño retraso para asegurar que la eliminación se completó
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            // No necesitamos verificar el tipo de pago aquí, se hará durante la venta
            // System.out.println("Registrando venta con tipo de pago ID: " + idTipoVenta);
            
            // Siempre crear una nueva instancia de VistaCajaInicial
            VistaCajaInicial cajaInicial = new VistaCajaInicial();
            
            // Configurar el fondo de caja si está disponible
            if (controladorMenu != null && controladorMenu.getEstadoCaja() != null) {
                double fondoActual = controladorMenu.getEstadoCaja().getFondoActual();
                cajaInicial.setFondoInicial(fondoActual);
                cajaInicial.setCajaAbierta(true);
                System.out.println("Fondo de caja establecido: " + fondoActual);
            } else {
                System.out.println("No se pudo obtener el estado de caja o es null");
                cajaInicial.setFondoInicial(0.0);
                cajaInicial.setCajaAbierta(false);
            }
            
            // Crear una nueva instancia de VistaCaja con la nueva cajaInicial
            this.vistaCaja = new VistaCaja(cajaInicial);
            this.listaProductos = new ArrayList<>();
            
            // Configurar los listeners
            this.vistaCaja.getBSalir().addActionListener(this);
            this.vistaCaja.getBAgregar().addActionListener(this);
            this.vistaCaja.getBFinalizar().addActionListener(this);
            this.vistaCaja.getBBuscar().addActionListener(this);
            this.vistaCaja.getBEliminar().addActionListener(this);
            // Agregar listener para tecla Enter en el campo de código
            this.vistaCaja.getTCodigo().addActionListener(e -> {
                String codigo = vistaCaja.getTCodigo().getText().trim();
                if (!codigo.isEmpty()) {
                    buscarProducto(codigo);
                }
            });
            
            // Cargar productos disponibles
            cargarProductos();
            
            // Agregar la ventana al escritorio
            if (padre != null && padre.getEscritorio() != null) {
                // Agregar la ventana y después hacerla visible
                padre.getEscritorio().add(vistaCaja);
                
                // Maximizar e intentar seleccionar la ventana (enfocarla)
                try {
                    vistaCaja.setMaximum(true);
                    vistaCaja.setSelected(true);
                } catch (java.beans.PropertyVetoException ex) {
                    System.out.println("Error al maximizar/seleccionar ventana: " + ex.getMessage());
                }
                
                vistaCaja.setVisible(true);
            } else {
                System.out.println("Error: No se pudo acceder al escritorio del menú principal");
            }
            
            System.out.println("Vista Caja inicializada correctamente. Visible: " + vistaCaja.isVisible());
            
        } catch (Exception e) {
            System.err.println("Error al inicializar ControladorCaja: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Devuelve la ventana de caja
     */
    public JInternalFrame getVentana() {
        return this.vistaCaja;
    }

    /**
     * Carga productos desde la base de datos
     */
    private void cargarProductos() {
        BaseDatos bd = null;
        try {
            // Asegurarnos de que la lista esté inicializada
            if (listaProductos == null) {
                listaProductos = new ArrayList<>();
            } else {
                // Limpiar lista actual
                listaProductos.clear();
            }
            
            bd = new BaseDatos();
            // ⚠️ CRÍTICO: Especificar columnas explícitamente para evitar que idProveedor se lea como precio
            String columnas = "idProducto, sku, producto, idcategoria, precio_venta, marca, stock, descripcion";
            ArrayList<String[]> lista = bd.consultar("Productos", columnas, "activo = 1");
    
            if (lista != null) {
                for (String[] datos : lista) {
                    if (datos.length >= 8) {
                        Producto producto = new Producto();
                        producto.setIdProducto(datos[0]); // idProducto
                        producto.setSku(datos[1]);        // sku
                        producto.setProducto(datos[2]);   // producto
                        producto.setIdCategoria(datos[3]); // idCategoria
                        
                        // Validar y convertir precio_venta
                        if (esNumerico(datos[4])) {
                            producto.setPrecioVenta(Double.parseDouble(datos[4]));
                        } else {
                            producto.setPrecioVenta(0.0);
                            System.out.println("Precio no numérico: " + datos[4]);
                        }
    
                        producto.setMarca(datos[5]); // marca
    
                        // Validar y convertir stock
                        if (esNumerico(datos[6])) {
                            producto.setStock(Integer.parseInt(datos[6]));
                        } else {
                            producto.setStock(0);
                            System.out.println("Stock no numérico: " + datos[6]);
                        }
    
                        producto.setDescripcion(datos[7]); // descripcion
    
                        listaProductos.add(producto);
                    } else {
                        System.out.println("❌ Fila de producto incompleta: " + java.util.Arrays.toString(datos));
                    }
                }
                System.out.println("✅ Productos cargados: " + listaProductos.size());
            } else {
                System.out.println("❌ No se encontraron productos en la base de datos");
            }
        } catch (Exception e) {
            System.out.println("❌ Error al cargar productos: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(vistaCaja,
                    "Error al conectar con la base de datos: " + e.getMessage(),
                    "Error de Conexión",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            if (bd != null) {
                bd.cerrarConexion();
            }
        }
    }

    /**
     * Agrega productos de demostración
     */

    /**
     * Método auxiliar para verificar si una cadena es numérica
     * @param str La cadena a verificar
     * @return true si la cadena puede convertirse a un número, false en caso contrario
     */
    private boolean esNumerico(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        
        // Corregir precios no numéricos en la base de datos
        corregirPreciosNoNumericos();
        
        // Intentar convertir a double (para precio)
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
            
    /**
     * Elimina el producto seleccionado de la venta
     */
    private void eliminarProducto() {
    	
    	System.out.println("Método eliminarProducto() llamado");

    	
        if (vistaCaja.eliminarProductoSeleccionado()) {
            JOptionPane.showMessageDialog(vistaCaja, 
                    "Producto eliminado de la venta correctamente",
                    "Producto Eliminado", 
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(vistaCaja, 
                    "Debe seleccionar un producto para eliminar",
                    "Seleccione Producto", 
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Busca un producto por su código
     */
    private Producto buscarProductoPorCodigo(String codigo) {
        // Mejor buscar en la base de datos para garantizar que esté activo
        BaseDatos bd = new BaseDatos();
        ArrayList<String[]> resultados = bd.consultar("Productos", 
                "idProducto, sku, producto, idcategoria, precio_venta, marca, stock, descripcion", 
                "idProducto = '" + codigo + "' AND activo = 1");
        bd.cerrarConexion();
        
        if (resultados != null && !resultados.isEmpty()) {
            return Producto.fromStringArray(resultados.get(0));
        }
        
        return null;
    }

    /**
     * Busca un producto y muestra su información
     */
    private void buscarProducto(String codigo) {
    	BaseDatos bds = new BaseDatos();
       
        bds.cerrarConexion();
        if (codigo == null || codigo.isEmpty()) {
            JOptionPane.showMessageDialog(vistaCaja,
                    "Ingrese un código de producto",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            vistaCaja.getTCodigo().requestFocus();
            return;
        }

        // Intentar buscar primero en la lista local
        Producto producto = buscarProductoPorCodigo(codigo);

        // Si no se encuentra, intentar buscar en la base de datos (por si hay productos nuevos)
        if (producto == null) {
            BaseDatos bd = null;
            try {
                bd = new BaseDatos();
                String columnas = "idProducto, sku, producto, idcategoria, precio_venta, marca, stock, descripcion";
                ArrayList<String[]> resultados = bd.consultar("Productos", 
                        columnas, 
                        "idProducto = '" + codigo + "' AND activo = 1");

                if (!resultados.isEmpty() && resultados.get(0).length >= 8) {
                    String[] datos = resultados.get(0);
                    producto = new Producto();
                    producto.setIdProducto(datos[0]);   // idProducto
                    producto.setSku(datos[1]);          // sku
                    producto.setProducto(datos[2]);     // producto
                    producto.setIdCategoria(datos[3]);  // idcategoria
                    try {
                        // Precio está en índice 4 (NO en 2)
                        if (esNumerico(datos[4])) {
                            producto.setPrecioVenta(Double.parseDouble(datos[4]));
                        } else {
                            producto.setPrecioVenta(0.0);
                            System.out.println("Advertencia: Precio no numérico para producto " + datos[2] + ": " + datos[4] + ". Se asignó 0.0");
                        }
                        
                        producto.setMarca(datos[5]);  // marca
                        
                        // Stock está en índice 6 (NO en 3)
                        if (esNumerico(datos[6])) {
                            producto.setStock(Integer.parseInt(datos[6]));
                        } else {
                            producto.setStock(0);
                            System.out.println("Advertencia: Stock no numérico para producto " + datos[2] + ": " + datos[6] + ". Se asignó 0");
                        }
                        
                        producto.setDescripcion(datos[7]);  // descripcion
                    } catch (NumberFormatException e) {
                        System.out.println("Error al convertir datos numéricos: " + e.getMessage());
                        // Establecer valores predeterminados
                        producto.setPrecioVenta(0.0);
                        producto.setStock(0);
                    }
                    listaProductos.add(producto); // Añadir a la lista local
                }
            } catch (Exception e) {
                System.out.println("❌ Error al buscar producto en la base de datos: " + e.getMessage());
            } finally {
                if (bd != null) {
                    bd.cerrarConexion();
                }
            }
        }

        if (producto != null) {
            // Obtener valores
            String nombre = producto.getProducto();
            double precio = producto.getPrecioVenta();
            int stock = producto.getStock();

            // Mostrar información o agregar directamente según el contexto
            String info = "Producto: " + nombre +
                    "\nPrecio: $" + formatoMoneda.format(precio) +
                    "\nStock disponible: " + stock;

            // Preguntar si se quiere agregar el producto
            int respuesta = JOptionPane.showConfirmDialog(
                    vistaCaja,
                    info + "\n\n¿Desea agregar este producto a la venta?",
                    "Producto Encontrado",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

            if (respuesta == JOptionPane.YES_OPTION) {
                vistaCaja.getTCantidad().requestFocus();
                agregarProductoAVenta();
            }
        } else {
            JOptionPane.showMessageDialog(vistaCaja,
                    "Producto no encontrado",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            vistaCaja.getTCodigo().requestFocus();
            vistaCaja.getTCodigo().selectAll();
        }
    }

    /**
     * Agrega un producto a la venta actual
     */
    private void agregarProductoAVenta() {
        String codigo = vistaCaja.getTCodigo().getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(vistaCaja,
                    "Ingrese un código de producto",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            vistaCaja.getTCodigo().requestFocus();
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(vistaCaja.getTCantidad().getText().trim());
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(vistaCaja,
                        "La cantidad debe ser mayor a cero",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                vistaCaja.getTCantidad().requestFocus();
                vistaCaja.getTCantidad().selectAll();
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vistaCaja,
                    "Ingrese una cantidad válida",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            vistaCaja.getTCantidad().requestFocus();
            vistaCaja.getTCantidad().selectAll();
            return;
        }

        // Intentar buscar primero en la lista local
        Producto producto = buscarProductoPorCodigo(codigo);

        // Si no se encuentra, intentar buscar en la base de datos en tiempo real
        if (producto == null) {
            BaseDatos bd = null;
            try {
                bd = new BaseDatos();
                String columnas = "idProducto, sku, producto, idcategoria, precio_venta, marca, stock, descripcion";
                ArrayList<String[]> resultados = bd.consultar("Productos", 
                        columnas, 
                        "idProducto = '" + codigo + "' AND activo = 1");

                if (!resultados.isEmpty() && resultados.get(0).length >= 8) {
                    String[] datos = resultados.get(0);
                    producto = new Producto();
                    producto.setIdProducto(datos[0]);   // idProducto
                    producto.setSku(datos[1]);          // sku
                    producto.setProducto(datos[2]);     // producto
                    producto.setIdCategoria(datos[3]);  // idcategoria
                    try {
                        // Precio está en índice 4 (NO en 2)
                        if (esNumerico(datos[4])) {
                            producto.setPrecioVenta(Double.parseDouble(datos[4]));
                        } else {
                            producto.setPrecioVenta(0.0);
                            System.out.println("Advertencia: Precio no numérico para producto " + datos[2] + ": " + datos[4] + ". Se asignó 0.0");
                        }
                        
                        producto.setMarca(datos[5]);  // marca
                        
                        // Stock está en índice 6 (NO en 3)
                        if (esNumerico(datos[6])) {
                            producto.setStock(Integer.parseInt(datos[6]));
                        } else {
                            producto.setStock(0);
                            System.out.println("Advertencia: Stock no numérico para producto " + datos[2] + ": " + datos[6] + ". Se asignó 0");
                        }
                        
                        producto.setDescripcion(datos[7]);  // descripcion
                    } catch (NumberFormatException e) {
                        System.out.println("Error al convertir datos numéricos: " + e.getMessage());
                        // Establecer valores predeterminados
                        producto.setPrecioVenta(0.0);
                        producto.setStock(0);
                    }

                    listaProductos.add(producto); // Añadir a la lista local
                }
            } catch (Exception e) {
                System.out.println("❌ Error al buscar producto en la base de datos: " + e.getMessage());
            } finally {
                if (bd != null) {
                    bd.cerrarConexion();
                }
            }
        }

        if (producto == null) {
            JOptionPane.showMessageDialog(vistaCaja,
                    "Producto no encontrado",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            vistaCaja.getTCodigo().requestFocus();
            vistaCaja.getTCodigo().selectAll();
            return;
        }

        // Verificar stock disponible
        int stockDisponible = producto.getStock();

        // Mostrar alerta si el stock es menor a 10
        if (stockDisponible < 10) {
            JOptionPane.showMessageDialog(vistaCaja,
                    "Advertencia: El producto \"" + producto.getProducto() + "\" tiene un stock bajo (" + stockDisponible + " unidades).",
                    "Stock Bajo",
                    JOptionPane.WARNING_MESSAGE);
        }

        if (stockDisponible < cantidad) {
            JOptionPane.showMessageDialog(vistaCaja,
                    "Stock insuficiente. Disponible: " + stockDisponible,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            vistaCaja.getTCantidad().requestFocus();
            vistaCaja.getTCantidad().selectAll();
            return;
        }

        // Calcular subtotal
        double precio = producto.getPrecioVenta();
        double iva = precio * 0.16; // Calcular IVA del 16%
        double precioConIva = precio + iva; // Precio con IVA incluido
        double subtotal = precioConIva * cantidad; // Subtotal con IVA incluido

        // Verificar si ya existe el producto en la tabla
        boolean productoExistente = false;
        DefaultTableModel modelo = vistaCaja.getModeloTabla();

        for (int i = 0; i < modelo.getRowCount(); i++) {
            String idProducto = (String) modelo.getValueAt(i, 0);
            if (idProducto.equals(producto.getIdProducto())) {
                // Actualizar cantidad y subtotal
                int cantidadActual = Integer.parseInt(modelo.getValueAt(i, 3).toString());
                int nuevaCantidad = cantidadActual + cantidad;

                // Verificar si hay suficiente stock para la nueva cantidad
                if (nuevaCantidad > stockDisponible) {
                    JOptionPane.showMessageDialog(vistaCaja,
                            "Stock insuficiente para agregar " + cantidad + " unidades más. Disponible: " +
                                    (stockDisponible - cantidadActual),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double nuevoSubtotal = precioConIva * nuevaCantidad;

                modelo.setValueAt(nuevaCantidad, i, 3);
                modelo.setValueAt(formatoMoneda.format(nuevoSubtotal), i, 4);

                // Actualizar total (restar el subtotal anterior y agregar el nuevo)
                double subtotalAnterior = precioConIva * cantidadActual;
                totalVenta = totalVenta - subtotalAnterior + nuevoSubtotal;

                productoExistente = true;
                break;
            }
        }

        // Si no existe el producto, agregarlo como nuevo
        if (!productoExistente) {
            modelo.addRow(new Object[]{
                    producto.getIdProducto(),
                    producto.getProducto(),
                    formatoMoneda.format(precio), // Mostrar precio con IVA
                    cantidad,
                    formatoMoneda.format(subtotal) // Subtotal con IVA incluido
            });

            // Actualizar total
            totalVenta += subtotal;
        }

        // Actualizar la visualización del total
        vistaCaja.getTSubtotal().setText(formatoMoneda.format(totalVenta));

        // Reducir stock (se implementaría al finalizar la venta en un entorno real)
        // int nuevoStock = stockDisponible - cantidad;
        // producto.setStock(String.valueOf(nuevoStock));

        // Limpiar campos
        vistaCaja.getTCodigo().setText("");
        vistaCaja.getTCantidad().setText("1");
        vistaCaja.getTCodigo().requestFocus();
    }

    /**
     * Finaliza la venta actual
     */
    private void finalizarVenta() {
        if (vistaCaja.getModeloTabla().getRowCount() == 0) {
            JOptionPane.showMessageDialog(vistaCaja,
                    "No hay productos en la venta",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

            // Proceder directamente sin confirmación
            mostrarVentanaPago();
    }

    /**
     * Obtiene el ID del tipo de pago seleccionado en la ventana de pago
     * @return ID del tipo de pago (1=Efectivo, 2=Tarjeta, 3=Transferencia)
     */
    private String obtenerIdTipoPagoSeleccionado() {
        // Por defecto asumimos efectivo (1)
       
    	 String idTipoPago = "1";
        // Si la ventana de pago está visible, obtenemos el tipo seleccionado
        
            String tipoPagoSeleccionado = (String) vistaPago.getCbTipoPago().getSelectedItem();
           // Efectivo por defecto
            // Mapear el texto seleccionado al ID correspondiente
            if (tipoPagoSeleccionado != null) {
            	if (tipoPagoSeleccionado.contains("Efectivo")) {
					idTipoPago = "1";
				} else
                if (tipoPagoSeleccionado.contains("Tarjeta")) {
                    idTipoPago = "2";
                } else if (tipoPagoSeleccionado.contains("Transferencia")) {
                    idTipoPago = "3";
                }
                // Si es Efectivo, ya tenemos "1" como valor predeterminado
            }
            
            System.out.println("Tipo de pago seleccionado: " + tipoPagoSeleccionado + " (ID: " + idTipoPago + ")");
        
        
        return idTipoPago;
    }
    
    /**
     * Muestra la ventana de pago
     */
    private void mostrarVentanaPago() {
            try {
                // Ejecutar diagnóstico de totales para asegurar que están correctos
                diagnosticarTotales();
                
                // Asegurar que el total esté actualizado
                actualizarTotalVenta();
                
                // Verificar que totalVenta sea válido
                if (totalVenta <= 0) {
                    System.err.println("Error: Total de venta inválido: " + totalVenta);
                    JOptionPane.showMessageDialog(vistaCaja,
                            "Error al calcular el total de la venta. Intente nuevamente.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                System.out.println("Mostrando ventana de pago con total: " + totalVenta);
                
                // Crear y configurar la ventana de pago
                // Pasar solo el subtotal, el IVA se calculará dentro de VistaPago
                this.vistaPago = new VistaPago(null, totalVenta);
                vistaPago.setModal(true);
                
                // Configurar el fondo disponible para el cambio
                if (controladorMenu != null) {
                    double fondoDisponible = controladorMenu.getEstadoCaja().getFondoActual();
                    vistaPago.setFondoCaja(fondoDisponible);
                    vistaPago.setControladorMenu(controladorMenu);
                } else if (padre != null) {
                    double fondoDisponible = padre.getEstadoCaja().getFondoActual();
                    vistaPago.setFondoCaja(fondoDisponible);
                }
                
                // Configurar listener para confirmar el pago
                vistaPago.setConfirmarListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            // La confirmación ya se establece dentro de VistaPago
                            // Obtener valores del pago
                            double montoPagado = vistaPago.getPagoCon();
                            double cambio = vistaPago.getCambio();
                            System.out.println("Tipo pago desde método directo: " + vistaPago.getTipoPagoSeleccionado());
                            System.out.println("Pago confirmado: Monto=" + montoPagado + ", Cambio=" + cambio);
                            
                            // Cerrar la ventana de pago
                            vistaPago.dispose();
                            
                            // Procesar la venta con el pago confirmado
                            procesarVentaConfirmada(montoPagado, cambio);
                        } catch (Exception ex) {
                            System.err.println("Error al procesar pago: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                });
                
                // Mostrar la ventana de pago
                vistaPago.setVisible(true);
            } catch (Exception e) {
                System.err.println("Error al mostrar ventana de pago: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(vistaCaja,
                        "Error al procesar el pago: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        
        /**
         * Realiza un diagnóstico de los totales para verificar que estén correctos
         */
        private void diagnosticarTotales() {
            try {
                System.out.println("=== DIAGNÓSTICO DE TOTALES ===");
                double totalCalculado = 0.0;
                DefaultTableModel modelo = vistaCaja.getModeloTabla();
                
                // Verificar que la tabla tenga datos
                System.out.println("Filas en tabla: " + modelo.getRowCount());
                
                // Iterar por cada fila y sumar los subtotales
                for (int i = 0; i < modelo.getRowCount(); i++) {
                    String nombreProducto = modelo.getValueAt(i, 1).toString();
                    String cantidadStr = modelo.getValueAt(i, 3).toString();
                    String subtotalStr = modelo.getValueAt(i, 4).toString().replaceAll("[^\\d.]", "");
                    
                    try {
                        double subtotal = Double.parseDouble(subtotalStr);
                        totalCalculado += subtotal;
                        System.out.println("Producto: " + nombreProducto + ", Cantidad: " + cantidadStr + ", Subtotal: " + subtotalStr);
                    } catch (NumberFormatException e) {
                        System.err.println("Error al procesar fila " + i + ": " + e.getMessage());
                    }
                }
                
                System.out.println("Total calculado: " + totalCalculado);
                System.out.println("Total almacenado: " + totalVenta);
                
                if (Math.abs(totalCalculado - totalVenta) > 0.01) {
                    System.out.println("ADVERTENCIA: Discrepancia en el total. Se corregirá.");
                    totalVenta = totalCalculado;
                    vistaCaja.actualizarTotales(totalVenta);
                } else {
                    System.out.println("Los totales son correctos.");
                }
                
                System.out.println("=== FIN DIAGNÓSTICO ===");
            } catch (Exception e) {
                System.err.println("Error en diagnóstico de totales: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        /**
         * Procesa la venta después de confirmar el pago
         * @param pagoCon Monto con el que pagó el cliente
         * @param cambio Cambio a devolver al cliente
         */
        private void procesarVentaConfirmada(double pagoCon, double cambio) {
            System.out.println("Procesando venta confirmada - Pago: " + pagoCon + ", Cambio: " + cambio);
            
            // Registrar la venta en la base de datos y actualizar inventario
            registrarVenta();
            
            // Actualizar el fondo de caja
            if (controladorMenu != null) {
                controladorMenu.getEstadoCaja().actualizarFondoDespuesDeVenta(totalVenta, pagoCon);
            } else if (padre != null) {
                padre.getEstadoCaja().actualizarFondoDespuesDeVenta(totalVenta, pagoCon);
            }
    
            // Crear lista de productos para el ticket
            List<Object[]> productosVenta = new ArrayList<>();
            DefaultTableModel modelo = vistaCaja.getModeloTabla();
            for (int i = 0; i < modelo.getRowCount(); i++) {
                Object[] fila = new Object[4];
                fila[0] = modelo.getValueAt(i, 0); // código
                fila[1] = modelo.getValueAt(i, 1); // nombre
                fila[2] = modelo.getValueAt(i, 2); // precio
                fila[3] = modelo.getValueAt(i, 3); // cantidad
                productosVenta.add(fila);
            }
    
            // Mostrar ticket
            mostrarTicket(productosVenta, totalVenta, pagoCon, cambio);
    
            // Limpiar la tabla y resetear el total
            vistaCaja.getModeloTabla().setRowCount(0);
            
            // Resetear el total a cero después de completar la venta
            totalVenta = 0.0;
            
            // Actualizar la UI con el total reseteado
            vistaCaja.actualizarTotales(0.0);
            
            // Registrar la actualización para depuración
            System.out.println("ControladorCaja.actualizarTotalVenta: Venta completada, total reseteado a 0.0");
    }

    /**
     * Registra la venta en la base de datos y actualiza inventario
     * Método sobrecargado sin parámetros que obtiene el tipo de pago seleccionado
     */
    private void registrarVenta() {
        // Obtener el tipo de pago seleccionado
        String idTipoVenta = obtenerIdTipoPagoSeleccionado();
        // Llamar al método con el parámetro
        registrarVenta(idTipoVenta);
    }
    
    /**
     * Registra la venta en la base de datos y actualiza inventario
     * @param idTipoVenta ID del tipo de venta (1=Efectivo, 2=Tarjeta, 3=Transferencia)
     */
    private void registrarVenta(String idTipoVenta) {
        BaseDatos bd = null;
        try {
            // Validar que haya productos en la venta y un total válido
            if (vistaCaja.getModeloTabla().getRowCount() == 0) {
                JOptionPane.showMessageDialog(vistaCaja,
                        "No hay productos en la venta",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
                                                // Verificar si hay productos con stock bajo
                                                verificarStockBajo();
                                    
            if (totalVenta <= 0) {
                JOptionPane.showMessageDialog(vistaCaja,
                        "El total de la venta no es válido",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Conectar a la base de datos
            bd = new BaseDatos();
    
            // Registrar la venta en la tabla Ventas
            java.util.Date fechaActual = new java.util.Date();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(fechaActual.getTime());
            String fechaVenta = timestamp.toString();
    
            // Obtener el próximo ID secuencial para la venta (MySQL auto-incrementa INT)
            ArrayList<String[]> resultadoVenta = bd.consultar("Ventas", "MAX(idVenta) AS ultimoID", null);
            int nuevoIdVenta = 1;
            if (!resultadoVenta.isEmpty() && resultadoVenta.get(0)[0] != null) {
                nuevoIdVenta = Integer.parseInt(resultadoVenta.get(0)[0]) + 1;
            }
            String idVenta = String.valueOf(nuevoIdVenta);
            
            System.out.println("Registrando venta con ID: " + idVenta + ", Fecha: " + fechaVenta + ", Total: " + totalVenta);
            
            String[] valoresVenta = {
                idVenta,
                fechaVenta,
                String.valueOf(totalVenta),
                idTipoVenta  // Ahora usamos el tipo de venta seleccionado
            };
            boolean ventaRegistrada = bd.insertar("Ventas", "idVenta, fecha, total, idTipoVenta", valoresVenta);
    
            if (ventaRegistrada) {
                System.out.println("Venta registrada correctamente en tabla Ventas");
                
                // Registrar detalles de la venta y actualizar inventario
                DefaultTableModel modelo = vistaCaja.getModeloTabla();
                
                for (int i = 0; i < modelo.getRowCount(); i++) {
                    try {
                        String codigoProducto = modelo.getValueAt(i, 0).toString();
                        int cantidad = Integer.parseInt(modelo.getValueAt(i, 3).toString());
                        
                        // Eliminar caracteres no numéricos del precio
                        String precioStr = modelo.getValueAt(i, 2).toString().replaceAll("[^\\d.]", "");
                        double precio = Double.parseDouble(precioStr);
                        double subtotal = precio * cantidad;
                        
                        System.out.println("Registrando detalle - Producto: " + codigoProducto + 
                                          ", Cantidad: " + cantidad + 
                                          ", Precio: " + precio + 
                                          ", Subtotal: " + subtotal);
        
                        // Obtener el próximo ID secuencial para detalle de venta (MySQL auto-incrementa INT)
                        ArrayList<String[]> resultadoDetalle = bd.consultar("DetalleVentas", "MAX(idDetalleVenta) AS ultimoID", null);
                        int nuevoIdDetalle = 1;
                        if (!resultadoDetalle.isEmpty() && resultadoDetalle.get(0)[0] != null) {
                            nuevoIdDetalle = Integer.parseInt(resultadoDetalle.get(0)[0]) + 1;
                        }
                        String idDetalle = String.valueOf(nuevoIdDetalle);
                        
                        String[] valoresDetalle = {
                            idDetalle,
                            idVenta,
                            codigoProducto,
                            String.valueOf(cantidad),
                            String.valueOf(precio)
                        };
        
                        boolean detalleRegistrado = bd.insertar("DetalleVentas", "idDetalleVenta, idVenta, idProducto, cantidad, precioUnitario", valoresDetalle);
                        
                        if (detalleRegistrado) {
                            System.out.println("✅ Detalle registrado correctamente con ID: " + idDetalle);
                            
                            // Actualizar stock en la base de datos
                            Producto producto = buscarProductoPorCodigo(codigoProducto);
                            if (producto != null) {
                                int stockActual = producto.getStock();
                                int nuevoStock = stockActual - cantidad;
                                producto.setStock(nuevoStock);
        
                                // Actualizar stock en BD
                                boolean stockActualizado = bd.modificar("Productos", "stock", String.valueOf(nuevoStock), "idProducto = '" + producto.getIdProducto() + "'");
                                
                                if (stockActualizado) {
                                    System.out.println("✅ Stock actualizado para " + producto.getProducto() + 
                                                     ": " + stockActual + " -> " + nuevoStock);
                                } else {
                                    System.out.println("⚠️ No se pudo actualizar el stock para el producto: " + codigoProducto);
                                }
                            } else {
                                System.out.println("⚠️ No se encontró el producto para actualizar stock: " + codigoProducto);
                            }
                        } else {
                            System.out.println("⚠️ No se pudo registrar el detalle de venta para el producto: " + codigoProducto);
                        }
                    } catch (Exception ex) {
                        System.err.println("Error al procesar producto en posición " + i + ": " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
    
                System.out.println("✅ Venta registrada completamente con ID: " + idVenta);
                
                // Recargar la lista de productos para reflejar los cambios de stock
                cargarProductos();
                
                // Mostrar mensaje de éxito al usuario
                JOptionPane.showMessageDialog(vistaCaja,
                        "Venta registrada correctamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                
            } else {
                System.out.println("⚠️ No se pudo registrar la venta en la base de datos");
                JOptionPane.showMessageDialog(vistaCaja,
                        "Error al registrar la venta. Intente nuevamente.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            System.out.println("❌ Error al registrar la venta: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(vistaCaja,
                    "Error al registrar la venta: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            if (bd != null) {
                bd.cerrarConexion();
            }
        }
    }

    /**
     * Muestra el ticket de venta
     */
    private void mostrarTicket(List<Object[]> productos, double total, double pagoCon, double cambio) {
        VistaTicket vistaTicket = new VistaTicket(null, productos, total, pagoCon, cambio);

        // Configurar listeners - esto automáticamente incluye la generación de PDF
        vistaTicket.setImprimirListener(e -> {
            JOptionPane.showMessageDialog(vistaTicket, 
                    "Imprimiendo ticket...", 
                    "Impresión", 
                    JOptionPane.INFORMATION_MESSAGE);
            // Aquí iría el código para imprimir físicamente
        });
        

        vistaTicket.setCerrarListener(e -> {
            vistaTicket.dispose();
            // Recargar productos después de cerrar el ticket para tener datos actualizados
            cargarProductos();
        });

        // Mostrar ticket
        vistaTicket.setVisible(true);
    }

    /**
     * Corrige los precios no numéricos en la base de datos
     * Este método se debe llamar al iniciar la aplicación para garantizar
     * que todos los precios sean válidos
     * 
     * ⚠️ CRÍTICO: Debe especificar columnas explícitas para evitar leer idProveedor como precio
     */
    private void corregirPreciosNoNumericos() {
        try {
            BaseDatos bd = new BaseDatos();
            // ✅ FIX: Especificar columnas explícitas para evitar que idProveedor se lea como precio
            String columnas = "idProducto, sku, producto, idcategoria, precio_venta, marca, stock, descripcion, idProveedor";
            ArrayList<String[]> productos = bd.consultar("Productos", columnas, null);
            
            if (productos != null) {
                for (String[] producto : productos) {
                    String idProducto = producto[0];  // idProducto
                    String sku = producto[1];         // sku
                    String nombre = producto[2];      // producto
                    // idcategoria es producto[3]
                    String precioStr = producto[4];   // precio_venta (AHORA EN ÍNDICE CORRECTO)
                    
                    // Verificar si el precio es numérico
                    try {
                        if (precioStr == null || precioStr.trim().isEmpty()) {
                            System.out.println("⚠️ Precio vacío para producto " + sku + ": " + nombre);
                            // NO sobrescribir - dejar como está o usar un valor por defecto más alto
                            // bd.modificar("Productos", "precio_venta", "0.0", "idProducto = '" + idProducto + "'");
                        } else {
                            // Intentar convertir a número
                            precioStr = precioStr.replace(',', '.');
                            double precio = Double.parseDouble(precioStr);
                            
                            // Solo actualizar si el precio es válido y diferente
                            if (precio > 0 && !producto[4].equals(String.valueOf(precio))) {
                                System.out.println("✅ Precio válido para producto " + sku + ": $" + precio);
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("⚠️ Precio no numérico para producto " + sku + ": " + nombre + " (valor: " + precioStr + ")");
                        // NO sobrescribir con 0 - el precio podría ser válido
                        // bd.modificar("Productos", "precio_venta", "0.0", "idProducto = '" + idProducto + "'");
                    }
                }
            }
            
            bd.cerrarConexion();
        } catch (Exception e) {
            System.err.println("❌ Error al verificar precios: " + e.getMessage());
        }
    }

    /**
     * Refresca la lista de productos desde la base de datos
     * Útil cuando otro usuario podría haber modificado los datos
     */
    private void refrescarProductos() {
        // Limpiar la lista actual y volver a cargar
        cargarProductos();
        JOptionPane.showMessageDialog(vistaCaja,
                "Lista de productos actualizada correctamente",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Verifica si algún producto tiene stock bajo y muestra una alerta
     */
    private void verificarStockBajo() {
        DefaultTableModel modelo = vistaCaja.getModeloTabla();
        StringBuilder mensajeStockBajo = new StringBuilder();
        
        for (int i = 0; i < modelo.getRowCount(); i++) {
            String codigoProducto = modelo.getValueAt(i, 0).toString();
            Producto producto = buscarProductoPorCodigo(codigoProducto);
            
            if (producto != null && producto.getStock() < 5) {
                mensajeStockBajo.append("• ").append(producto.getProducto())
                               .append(": ").append(producto.getStock()).append(" unidades\n");
            }
        }
        
        if (mensajeStockBajo.length() > 0) {
            JOptionPane.showMessageDialog(vistaCaja,
                    "Los siguientes productos tienen stock bajo:\n" + mensajeStockBajo.toString(),
                    "Alerta de Stock",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Acción detectada: " + e.getActionCommand());
        
        // Verificar que la vista existe y está accesible
        if (vistaCaja == null) {
            System.err.println("Error: La vista de caja es nula");
            return;
        }
        
        // Asegurar que los totales estén correctamente calculados antes de mostrar la ventana de pago
        actualizarTotalVenta();
                    
        // Comprobar fuente del evento y ejecutar la acción correspondiente
        if (e.getSource() == vistaCaja.getBSalir()) {
            System.out.println("Botón Salir presionado");
            
            // Activar los menús del padre
            if (padre != null) {
                padre.Menus(true);
            }
            
            // Método simple: ocultar y disponer la ventana
            vistaCaja.setVisible(false);
            
            // Remover del escritorio
            if (padre != null && padre.getEscritorio() != null) {
                padre.getEscritorio().remove(vistaCaja);
                padre.getEscritorio().repaint();
            }
            
            // Liberar recursos
            vistaCaja.dispose();
            
            // No llamar a System.gc() ya que puede afectar el rendimiento
            System.out.println("Ventana de caja cerrada correctamente");
            
        } else if (e.getSource() == vistaCaja.getBAgregar()) {
            System.out.println("Botón Agregar presionado");
            agregarProductoAVenta();
        } else if (e.getSource() == vistaCaja.getBFinalizar()) {
            System.out.println("Botón Finalizar presionado");
            finalizarVenta();
        } else  if (e.getSource() == vistaCaja.getBEliminar()) {
            	System.out.println("Botón Eliminar presionado");
                eliminarProducto();
            }
        else if (e.getSource() == vistaCaja.getBBuscar()) {
            System.out.println("Botón Buscar presionado");
            String codigo = vistaCaja.getTCodigo().getText().trim();
            buscarProducto(codigo);


        } else {
            System.out.println("Fuente de evento desconocida: " + e.getSource());
        }
    }
    
    /**
     * Actualiza el total de la venta calculando la suma de todos los productos
     * en la tabla y actualiza los campos de la interfaz
     */
    private void actualizarTotalVenta() {
        try {
            // Recalcular el total basado en los productos en la tabla
            // Reiniciar el total a cero para evitar acumulación incorrecta
            totalVenta = 0.0;
            
            DefaultTableModel modelo = vistaCaja.getModeloTabla();
            
            if (modelo == null) {
                System.err.println("Error: Modelo de tabla es nulo");
                return;
            }
            
            System.out.println("Calculando total de venta con " + modelo.getRowCount() + " productos");
            
            for (int i = 0; i < modelo.getRowCount(); i++) {
                // Obtener el subtotal de cada producto
                if (modelo.getValueAt(i, 4) == null) {
                    System.err.println("Error: Valor nulo en la fila " + i + ", columna 4");
                    continue;
                }
                
                String subtotalStr = modelo.getValueAt(i, 4).toString();
                System.out.println("Producto " + (i+1) + ": Subtotal String = " + subtotalStr);
                
                // Eliminar caracteres no numéricos (como '$' y ',')
                subtotalStr = subtotalStr.replaceAll("[^\\d.]", "");
                
                try {
                    double subtotal = Double.parseDouble(subtotalStr);
                    totalVenta += subtotal;
                    System.out.println("Producto " + (i+1) + ": Subtotal = " + subtotal + ", Total acumulado = " + totalVenta);
                } catch (NumberFormatException e) {
                    System.err.println("Error al convertir subtotal: " + subtotalStr + " - " + e.getMessage());
                }
            }
            
            // El IVA ya está incluido en los precios individuales,
            // no es necesario calcularlo nuevamente aquí
            
            System.out.println("Total final calculado = " + totalVenta);
            
            // Actualizar la vista con los nuevos totales (sin agregar IVA adicional)
            if (vistaCaja != null) {
                vistaCaja.actualizarTotales(totalVenta);
                System.out.println("Totales actualizados en la interfaz: " + totalVenta);
                
                // Verificar si se está realizando un registro de venta y el total no es cero
                if (totalVenta > 0) {
                    System.out.println("Registrando venta en la base de datos: $" + totalVenta);
                }
            } else {
                System.err.println("Error: Vista Caja es nula");
            }
        } catch (Exception e) {
            System.err.println("Error en actualizarTotalVenta: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
}
    
