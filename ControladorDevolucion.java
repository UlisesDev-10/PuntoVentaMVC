package Controlador;

import Modelo.BaseDatos;
import Vista.VistaDevolucionProducto;
import Vista.VistaMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JOptionPane;

public class ControladorDevolucion implements ActionListener {
    
    private VistaDevolucionProducto vista;
    private VistaMenu padre;
    private DecimalFormat formatoMoneda = new DecimalFormat("#,##0.00");
    private SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
    
    public ControladorDevolucion(VistaMenu padre) {
        this.padre = padre;
        this.vista = new VistaDevolucionProducto();
        
        // Configurar la vista
        vista.setTitle("Devolución de Productos");
        vista.setClosable(true);
        vista.setMaximizable(true);
        vista.setIconifiable(true);
        vista.setResizable(true);
        
        // Agregar ActionListeners
        vista.getBtnBuscar().addActionListener(this);
        vista.getBtnProcesar().addActionListener(this);
        vista.getBtnCancelar().addActionListener(this);
        
        // Mostrar la ventana en el escritorio del menú principal
        try {
            padre.getEscritorio().add(vista);
            try {
                vista.setMaximum(true);
            } catch (java.beans.PropertyVetoException pve) {
                pve.printStackTrace();
            }
            vista.setVisible(true);
        } catch (Exception e) {
            System.out.println("Error al mostrar ventana de devolución: " + e.getMessage());
            e.printStackTrace();
        }
        padre.Menus(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnBuscar()) {
            buscarVenta();
        } else if (e.getSource() == vista.getBtnProcesar()) {
            procesarDevolucion();
        } else if (e.getSource() == vista.getBtnCancelar()) {
            vista.dispose();
        }
    }
    
    private void buscarVenta() {
        String idVenta = vista.getTxtNumeroVenta().getText().trim();
        
        if (idVenta.isEmpty()) {
            vista.mostrarMensaje("Error", "Ingrese un ID de venta para buscar", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            BaseDatos bd = new BaseDatos();
            
            // Buscar la venta
            ArrayList<String[]> ventaResult = bd.consultar("Ventas", "*", "idVenta = '" + idVenta + "'");
            
            if (ventaResult == null || ventaResult.isEmpty()) {
                vista.mostrarMensaje("No encontrado", "No se encontró ninguna venta con el ID: " + idVenta, JOptionPane.WARNING_MESSAGE);
                bd.cerrarConexion();
                return;
            }
            
            // Obtener datos de la venta
            String[] datoVenta = ventaResult.get(0);
            String fecha = datoVenta[4]; // Ajustar según la posición de la fecha en la tabla Ventas
            double total = Double.parseDouble(datoVenta[3]); // Ajustar según la posición del total
            
            // Cargar los datos de la venta en la vista
            vista.cargarDatosVenta(fecha, total);
            
            // Limpiar la tabla antes de cargar los nuevos productos
            vista.limpiarTabla();
            
            // Buscar los detalles de la venta
            ArrayList<String[]> detallesResult = bd.consultar("DetalleVentas", "*", "idVenta = '" + idVenta + "'");
            
            if (detallesResult == null || detallesResult.isEmpty()) {
                vista.mostrarMensaje("Sin productos", "Esta venta no tiene productos registrados", JOptionPane.WARNING_MESSAGE);
                bd.cerrarConexion();
                return;
            }
            
            // Cargar cada producto en la tabla
            for (String[] detalle : detallesResult) {
                String idProducto = detalle[2]; // Ajustar según posición del idProducto en la tabla DetalleVenta
                int cantidad = Integer.parseInt(detalle[3]); // Ajustar según posición de la cantidad
                
                // Obtener datos del producto
                ArrayList<String[]> productoResult = bd.consultar("Productos", "*", "idProducto = '" + idProducto + "'");
                
                if (productoResult != null && !productoResult.isEmpty()) {
                    String[] producto = productoResult.get(0);
                    String codigo = producto[1]; // Ajustar según posición del SKU
                    String descripcion = producto[2]; // Ajustar según posición del nombre
                    double precio = Double.parseDouble(producto[4]); // Ajustar según posición del precio
                    double subtotal = precio * cantidad;
                    
                    // Agregar el producto a la tabla
                    vista.agregarProductoTabla(idProducto, codigo, descripcion, precio, cantidad, subtotal);
                }
            }
            
            // Configurar la tabla después de agregar todos los productos
            vista.configurarTabla();
            
            bd.cerrarConexion();
            
        } catch (Exception e) {
            vista.mostrarMensaje("Error", "Error al buscar la venta: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void procesarDevolucion() {
        if (vista.getTxtNumeroVenta().getText().trim().isEmpty()) {
            vista.mostrarMensaje("Error", "Primero debe buscar una venta", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String motivo = vista.getTxtMotivo().getText().trim();
        if (motivo.isEmpty()) {
            vista.mostrarMensaje("Error", "Debe ingresar un motivo para la devolución", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Obtener productos seleccionados
        Object[][] productosSeleccionados = vista.getProductosSeleccionados();
        
        if (productosSeleccionados.length == 0) {
            vista.mostrarMensaje("Error", "Debe seleccionar al menos un producto para devolver", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Calcular el total de la devolución
        double totalDevolucion = vista.calcularTotalDevolucion();
        
        // Confirmar devolución
        if (!vista.confirmarDevolucion()) {
            return;
        }
        
        try {
            BaseDatos bd = new BaseDatos();
            
            // Crear la devolución en la base de datos
            String idVenta = vista.getTxtNumeroVenta().getText().trim();
            String fechaActual = formatoFecha.format(new Date());
            
            // Insertar en la tabla Devoluciones
            String campos = "idVenta, motivo, totalDevuelto, fecha";
            String[] valores = {idVenta, motivo, String.valueOf(totalDevolucion), fechaActual};
            boolean resultadoDevolucion = bd.insertar("Devoluciones", campos, valores);
            
            if (!resultadoDevolucion) {
                throw new Exception("Error al registrar la devolución");
            }
            
            // Obtener el ID de la devolución recién insertada
            ArrayList<String[]> ultimaDevolucionResult = bd.consultar("Devoluciones", "MAX(idDevolucion) as idDevolucion", null);
            int idDevolucion = Integer.parseInt(ultimaDevolucionResult.get(0)[0]);
            
            // Insertar cada producto en DetalleDevolucion
            for (Object[] producto : productosSeleccionados) {
                String idProducto = producto[0].toString();
                int cantidad = Integer.parseInt(producto[4].toString());
                
                String camposDetalle = "idDetalleDevolucion, idDevolucion, idProducto, cantidad";
                String[] valoresDetalle = {"DEFAULT", String.valueOf(idDevolucion), idProducto, String.valueOf(cantidad)};
                
                boolean resultadoDetalle = bd.insertar("detalledevolucion", camposDetalle, valoresDetalle);
                
                if (!resultadoDetalle) {
                    throw new Exception("Error al registrar detalle de devolución");
                }
                
                // Actualizar el stock del producto
                // Primero obtener el stock actual
                ArrayList<String[]> stockResult = bd.consultar("Productos", "stock", "idProducto = '" + idProducto + "'");
                if (stockResult != null && !stockResult.isEmpty()) {
                    int stockActual = Integer.parseInt(stockResult.get(0)[0]);
                    int nuevoStock = stockActual + cantidad;
                    
                    // Actualizar el stock - usando el método correcto
                    bd.modificar("Productos", "stock", String.valueOf(nuevoStock), "idProducto = '" + idProducto + "'");
                }
            }
            
            bd.cerrarConexion();
            
            vista.mostrarMensaje("Éxito", "Devolución procesada correctamente", JOptionPane.INFORMATION_MESSAGE);
            vista.limpiarCampos();
            
        } catch (Exception e) {
            vista.mostrarMensaje("Error", "Error al procesar la devolución: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    public VistaDevolucionProducto getVista() {
        return vista;
    }
}
