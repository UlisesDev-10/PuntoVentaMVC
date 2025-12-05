package Controlador;


import Librerias.ListaProducto;
import Modelo.BaseDatos;
import Vista.VentanaProducto;
import Vista.VistaGestionProductosInactivos;
import Vista.VistaMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class ControladorProductos implements ActionListener {

	private VentanaProducto ventana;
    private VistaMenu padre;
    private ListaProducto listaProductos;
    private ListSelectionModel listamodelo;
    private DefaultTableModel modeloTabla;
    
    /**
     * Devuelve la ventana del producto
     * @return VentanaProducto que contiene la vista
     */
    public VentanaProducto getVentana() {
        return this.ventana;
    }
    
    public ControladorProductos(VistaMenu padre) {
        this.padre = padre;
        this.ventana = new VentanaProducto();
        this.listaProductos = new ListaProducto();
        
        // Establecer propiedades de la ventana
        ventana.setTitle("Gestión de Productos");
        ventana.setClosable(true);
        ventana.setMaximizable(true);
        ventana.setIconifiable(true);
        ventana.setResizable(true);
        padre.Menus(true);
        // Inicializar la tabla
        cargarProductos();
        
        // Verificar que los botones estén inicializados correctamente
        if (this.ventana.getBagregar() != null) {
            this.ventana.getBagregar().addActionListener(this);
        } else {
            System.out.println("Error: Botón Agregar no está inicializado");
        }
        
        if (this.ventana.getBmodificar() != null) {
            this.ventana.getBmodificar().addActionListener(this);
        } else {
            System.out.println("Error: Botón Modificar no está inicializado");
        }
        
        if (this.ventana.getBeliminar() != null) {
            this.ventana.getBeliminar().addActionListener(this);
        } else {
            System.out.println("Error: Botón Eliminar no está inicializado");
        }
        
        if (this.ventana.getBsalida() != null) {
            this.ventana.getBsalida().addActionListener(this);
        } else {
            System.out.println("Error: Botón Salida no está inicializado");
        }
        
        if (this.ventana.getBbuscarId() != null) {
            this.ventana.getBbuscarId().addActionListener(this);
            this.ventana.getBbuscarId().setToolTipText("Buscar por código o ID");
        } else {
            System.out.println("Error: Botón BuscarId no está inicializado");
        }
        
        if (this.ventana.getBbuscarNombre() != null) {
            this.ventana.getBbuscarNombre().addActionListener(this);
            this.ventana.getBbuscarNombre().setToolTipText("Buscar por nombre de producto");
        } else {
            System.out.println("Error: Botón BuscarNombre no está inicializado");
        }
        
        // Agregar listener para el botón de gestionar productos inactivos
        if (this.ventana.getBgestionarInactivos() != null) {
            this.ventana.getBgestionarInactivos().addActionListener(this);
            this.ventana.getBgestionarInactivos().setToolTipText("Ver y gestionar productos inactivos");
        } else {
            System.out.println("Error: Botón Gestionar Productos Inactivos no está inicializado");
        }
        
        // Agregar acción al campo de búsqueda para buscar al presionar Enter
        if (this.ventana.getTbuscar() != null) {
            this.ventana.getTbuscar().addActionListener(e -> buscarPorNombre());
        } else {
            System.out.println("Error: Campo de búsqueda no está inicializado");
        }
        
        // Agregar listener a la tabla para seleccionar productos
        this.ventana.getTdatos().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = ventana.getTdatos().getSelectedRow();
                if (fila != -1) {
                    // Obtener el ID del producto seleccionado
                    String idProducto = ventana.getTdatos().getValueAt(fila, 0).toString();
                    mostrarDetallesProducto(idProducto);
                }
            }
        });
        
        // Mostrar la ventana
        try {
            // Si la clase VistaMenu tiene un escritorio (JDesktopPane)
            java.lang.reflect.Method getEscritorioMethod = VistaMenu.class.getMethod("getEscritorio");
            javax.swing.JDesktopPane desktop = (javax.swing.JDesktopPane) getEscritorioMethod.invoke(padre);
            desktop.add(ventana);
        } catch (Exception ex) {
            System.out.println("Error al agregar ventana al escritorio: " + ex.getMessage());
            // Alternativa: agregarlo directamente al frame principal
            try {
                padre.getFrame().add(ventana);
            } catch (Exception e) {
                System.out.println("Error al agregar ventana al frame: " + e.getMessage());
            }
        }
        
        // Código para forzar la maximización:
        try {
            this.ventana.setMaximum(true); 
        } catch (java.beans.PropertyVetoException ex) {
            System.out.println("Error al maximizar ventana: " + ex.getMessage());
        }
        
        ventana.setVisible(true);
    }
    
    // Cargar productos activos en la tabla
    private void cargarProductos() {
        try {
            BaseDatos bd = new BaseDatos();
            // Filtrar solo productos activos (donde activo = 1)
            // ⚠️ CRÍTICO: Especificar columnas para evitar confusión con idProveedor
            String columnas = "idProducto, sku, producto, idcategoria, precio_venta, marca, stock, descripcion";
            ArrayList<String[]> datos = bd.consultar("Productos", columnas, "activo = 1");
            bd.cerrarConexion();
            
            if (datos != null) {
                modeloTabla = listaProductos.getTablaModelo(datos);
                ventana.getTdatos().setModel(modeloTabla);
                System.out.println("Cargados " + datos.size() + " productos activos");
            } else {
                // Si no hay datos, crear un modelo vacío
                String[] columnasTabla = {"ID", "SKU", "Producto", "Categoría", "Precio", "Marca", "Stock", "Descripción","Proveedores"};
                modeloTabla = new DefaultTableModel(columnasTabla, 0);
                ventana.getTdatos().setModel(modeloTabla);
                System.out.println("No se encontraron productos activos");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventana, "Error al cargar productos: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Mostrar detalles de un producto seleccionado
    private void mostrarDetallesProducto(String idProducto) {
        try {
            BaseDatos bd = new BaseDatos();
            String columnas = "idProducto, sku, producto, idcategoria, precio_venta, marca, stock, descripcion";
            ArrayList<String[]> resultado = bd.consultar("Productos", columnas, "idProducto = '" + idProducto + "'");
            bd.cerrarConexion();
            
            if (resultado != null && resultado.size() > 0) {
                String[] producto = resultado.get(0);
                
                // Mostrar descripción
                if (producto.length > 7) { // Asumiendo que la descripción está en índice 7
                    ventana.getTdescripcion().setText(producto[7]);
                }
                
                // Aquí podríamos cargar la imagen del producto si existiera
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventana, "Error al cargar detalles del producto: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Buscar productos por código
    private void buscarPorCodigo() {
        String codigo = ventana.getTbuscar().getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(ventana, "Ingrese un código para buscar", 
                "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            BaseDatos bd = new BaseDatos();
            // Buscar por ID o SKU
            ArrayList<String[]> resultado = bd.consultar("Productos", "*", "sku = '" + codigo + "' OR idProducto = '" + codigo + "'");
            bd.cerrarConexion();
            
            if (resultado != null && resultado.size() > 0) {
                modeloTabla = listaProductos.getTablaModelo(resultado);
                ventana.getTdatos().setModel(modeloTabla);
                mostrarDetallesProducto(resultado.get(0)[0]); // Mostrar detalles del primer producto encontrado
                System.out.println("Producto(s) encontrado(s) por código: " + resultado.size());
            } else {
                JOptionPane.showMessageDialog(ventana, "No se encontró ningún producto con el código: " + codigo, 
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
                // Recargar todos los productos si no se encuentra ninguno
                cargarProductos();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventana, "Error al buscar producto: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Buscar productos por nombre
    private void buscarPorNombre() {
        String nombre = ventana.getTbuscar().getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(ventana, "Ingrese un nombre para buscar", 
                "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            BaseDatos bd = new BaseDatos();
            // Corregido: quitar activo=1 de las columnas, va en el WHERE
            String columnas = "idProducto, sku, producto, descripcion, precio_venta, stock, idcategoria, marca";
            ArrayList<String[]> resultado = bd.consultar("Productos", columnas, "producto LIKE '%" + nombre + "%' AND activo=1");
            bd.cerrarConexion();
            
            if (resultado != null && resultado.size() > 0) {
                modeloTabla = listaProductos.getTablaModelo(resultado);
                ventana.getTdatos().setModel(modeloTabla);
                System.out.println("Producto(s) encontrado(s) por nombre: " + resultado.size());
            } else {
                JOptionPane.showMessageDialog(ventana, "No se encontraron productos con el nombre: " + nombre, 
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
                // Recargar todos los productos si no se encuentra ninguno
                cargarProductos();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventana, "Error al buscar productos: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Abrir ventana para agregar nuevo producto
    private void agregarProducto() {
        JTable tabla = ventana.getTdatos();
        ControladorVistaElementoProducto controlador = new ControladorVistaElementoProducto(padre, "Agregar Producto", listaProductos, tabla);
        // Después de agregar, actualizamos la tabla
        cargarProductos();
    }
    
    // Modificar producto seleccionado
    private void modificarProducto() {
        int fila = ventana.getTdatos().getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(ventana, "Seleccione un producto para modificar", 
                "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Obtener el ID del producto seleccionado
        String idProducto = ventana.getTdatos().getValueAt(fila, 0).toString();
        
        try {
            // Buscar los datos actuales del producto
            BaseDatos bd = new BaseDatos();
            ArrayList<String[]> resultado = bd.consultar("Productos", "*", "idProducto = '" + idProducto + "'");
            
            if (resultado != null && resultado.size() > 0) {
                String[] productoActual = resultado.get(0);
                
                // Crear una ventana para modificar el producto
                JTable tabla = ventana.getTdatos();
                
                // Pasar el producto seleccionado para su edición
                ControladorModificarProducto controlador = new ControladorModificarProducto(
                    padre, "Modificar Producto", listaProductos, tabla, productoActual);
                
                // Después de modificar, actualizamos la tabla
                cargarProductos();
            }
            
            bd.cerrarConexion();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(ventana, "Error al preparar la modificación: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    // Eliminar producto seleccionado
    private void eliminarProducto() {
        int fila = ventana.getTdatos().getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(ventana, "Seleccione un producto para eliminar", 
                "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Obtener datos del producto a eliminar
        String idProducto = ventana.getTdatos().getValueAt(fila, 0).toString();
        String nombreProducto = ventana.getTdatos().getValueAt(fila, 2).toString();
        
        int opcion = JOptionPane.showConfirmDialog(ventana, 
            "¿Está seguro que desea eliminar el producto: " + nombreProducto + "?", 
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                BaseDatos bd = new BaseDatos();
                bd.eliminar("Productos", "idProducto", idProducto);
                bd.cerrarConexion();
                
                JOptionPane.showMessageDialog(ventana, "Producto eliminado correctamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    
                // Actualizar la tabla después de eliminar
                cargarProductos();
                
                // Limpiar la descripción
                ventana.getTdescripcion().setText("");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(ventana, "Error al eliminar producto: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

            // Método para abrir la ventana de gestión de productos inactivos
            private void gestionarProductosInactivos() {
        try {
            // Crear y mostrar la ventana de productos inactivos
            VistaGestionProductosInactivos vistaInactivos = new VistaGestionProductosInactivos();
            vistaInactivos.setLocationRelativeTo(ventana);
                    
                    // Agregar listener para actualizar la vista principal cuando se cierre
                    vistaInactivos.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            // Actualizar la vista principal al cerrar la ventana de inactivos
                            cargarProductos();
                            System.out.println("Vista principal actualizada después de gestionar inactivos");
                        }
                    });
                    
                    vistaInactivos.setVisible(true);
            System.out.println("Ventana de gestión de productos inactivos abierta");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(ventana, 
                "Error al abrir la ventana de productos inactivos: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
            }
        
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ventana.getBagregar()) {
            agregarProducto();
        } else if (e.getSource() == ventana.getBmodificar()) {
            modificarProducto();
        } else if (e.getSource() == ventana.getBeliminar()) {
            eliminarProducto();
        } else if (e.getSource() == ventana.getBsalida()) {
            ventana.dispose();
        } else if (e.getSource() == ventana.getBbuscarId()) {
            System.out.println("Ejecutando búsqueda por código/ID...");
            buscarPorCodigo();
        } else if (e.getSource() == ventana.getBbuscarNombre()) {
            System.out.println("Ejecutando búsqueda por nombre...");
            buscarPorNombre();
        } else if (e.getSource() == ventana.getTbuscar()) {
            // Si se presiona Enter en el campo de búsqueda
            buscarPorNombre();
        } else if (e.getSource() == ventana.getBgestionarInactivos()) {
            System.out.println("Abriendo gestión de productos inactivos...");
            gestionarProductosInactivos();
        }
    }
}
