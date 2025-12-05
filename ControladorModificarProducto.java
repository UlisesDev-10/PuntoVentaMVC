package Controlador;

import Librerias.ListaProducto;
import Modelo.BaseDatos;
import Modelo.Categoria;
import Modelo.Proveedor;
import Vista.VistaElementoProducto;
import Vista.VistaMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class ControladorModificarProducto implements ActionListener {

    private VistaElementoProducto ventanaProducto;
    private VistaMenu padre;
    private String[] datosProducto;
    private JTable tabla;
    private ListaProducto lista;
    
    /**
     * Constructor para modificar un producto existente
     * @param padre El menú principal
     * @param titulo Título de la ventana
     * @param lista Lista de productos
     * @param tabla Tabla donde se muestran los productos
     * @param datosProducto Datos del producto a modificar
     */
    public ControladorModificarProducto(VistaMenu padre, String titulo, ListaProducto lista, JTable tabla, String[] datosProducto) {
        this.tabla = tabla;
        this.lista = lista;
        this.datosProducto = datosProducto;
        
        // Crear la ventana para modificar productos
        ventanaProducto = new VistaElementoProducto(padre.getFrame(), titulo, true);
        
        // Cargar los datos del producto seleccionado en los campos
        cargarDatosProducto();
        
        // Deshabilitar el campo ID para que no sea modificable
        ventanaProducto.getTidProducto().setEditable(false);
        ventanaProducto.getTstock().setEditable(false);
        
        // Configurar listeners
        ventanaProducto.getBcancel().addActionListener(this);
        ventanaProducto.getBsave().addActionListener(this);
        
        // Cargar el combobox de categorías
        cargarCategorias();
        
            // Cargar el combobox de proveedores
            cargarProveedores();
            padre.Menus(true);
            
        // Mostrar la ventana
        SwingUtilities.invokeLater(() -> ventanaProducto.setVisible(true));
    }
    
    /**
     * Carga los datos del producto en los campos del formulario
     * ORDEN CORRECTO DE COLUMNAS:
     * [0]=idProducto, [1]=sku, [2]=producto, [3]=idcategoria, [4]=idProveedor,
     * [5]=precio_venta, [6]=marca, [7]=stock, [8]=descripcion
     */
    private void cargarDatosProducto() {
        if (datosProducto != null && datosProducto.length >= 9) {
            ventanaProducto.getTidProducto().setText(datosProducto[0]);  // ID Producto
            ventanaProducto.getTsku().setText(datosProducto[1]);         // SKU
            ventanaProducto.getTproducto().setText(datosProducto[2]);    // Nombre
            // datosProducto[3] = idCategoria - se seleccionará en cargarCategorias()
            // datosProducto[4] = idProveedor - se seleccionará en cargarProveedores()
            ventanaProducto.getTprecioventa().setText(datosProducto[5]); // ✅ CORREGIDO: Precio Venta (índice 5)
            ventanaProducto.getTmarca().setText(datosProducto[6]);       // ✅ CORREGIDO: Marca (índice 6)
            ventanaProducto.getTstock().setText(datosProducto[7]);       // ✅ CORREGIDO: Stock (índice 7)
            ventanaProducto.getTdescripcion().setText(datosProducto[8]); // ✅ CORREGIDO: Descripción (índice 8)
        }
    }
    /**
     * Carga el combobox de categorías y selecciona la categoría actual del producto
     */
    private void cargarCategorias() {
        try {
            // Establecemos la conexión a la BD
            BaseDatos bd = new BaseDatos();
            ArrayList<String[]> listaCategorias = bd.consultar("Categorias", null,"activo=1");
            
            // Agregar cada categoría al combobox
            for (String[] categoria : listaCategorias) {
                Categoria cat = new Categoria(categoria[0], categoria[1]);
                ventanaProducto.getCcategoria().addItem(cat);
                
                // Si es la categoría del producto, seleccionarla
                if (categoria[0].equals(datosProducto[3])) {
                    ventanaProducto.getCcategoria().setSelectedItem(cat);
                }
            }
            
            bd.cerrarConexion();
        } catch (Exception e) {
            System.err.println("Error al cargar categorías: " + e.getMessage());
            e.printStackTrace();
        }
    }
        
        /**
         * Carga el combobox de proveedores y selecciona el proveedor actual del producto
         */
        private void cargarProveedores() {
            try {
                // Establecemos la conexión a la BD
                BaseDatos bd = new BaseDatos();
                ArrayList<String[]> listaProveedores = bd.consultar("Proveedores", null, "activo=1");
                
                // Agregar cada proveedor al combobox
                for (String[] proveedor : listaProveedores) {
                    Proveedor prov = new Proveedor(proveedor[0], proveedor[1]);
                    ventanaProducto.getCproveedor().addItem(prov);
                    
                    // Si es el proveedor del producto, seleccionarlo
                    if (proveedor[0].equals(datosProducto[4])) {
                        ventanaProducto.getCproveedor().setSelectedItem(prov);
                    }
                }
                
                bd.cerrarConexion();
            } catch (Exception e) {
                System.err.println("Error al cargar proveedores: " + e.getMessage());
                e.printStackTrace();
            }
        }
    
    /**
     * Valida que todos los campos obligatorios estén completos
     * @return true si falta algún campo, false si están todos completos
     */
    public boolean vacio() {
        boolean resultado = false;
        StringBuilder mensaje = new StringBuilder("Falta información: \n");
        
        // Verificar todos los campos obligatorios
        if (ventanaProducto.getTsku().getText().trim().isEmpty()) {
            mensaje.append("- SKU\n");
            resultado = true;
        }
        
        if (ventanaProducto.getTproducto().getText().trim().isEmpty()) {
            mensaje.append("- Nombre del Producto\n");
            resultado = true;
        }
        
        if (ventanaProducto.getCcategoria().getSelectedIndex() < 0) {
            mensaje.append("- Categoría\n");
            resultado = true;
        }
            
            if (ventanaProducto.getCproveedor().getSelectedIndex() < 0) {
                mensaje.append("- Proveedor\n");
                resultado = true;
            }
        
        String precioTexto = ventanaProducto.getTprecioventa().getText().trim();
        if (precioTexto.isEmpty()) {
            mensaje.append("- Precio de Venta\n");
            resultado = true;
        } else {
            // Verificar que el precio sea numérico
            try {
                // Reemplazar comas por puntos para manejar diferentes formatos numéricos
                precioTexto = precioTexto.replace(',', '.');
                Double.parseDouble(precioTexto);
            } catch (NumberFormatException e) {
                mensaje.append("- El Precio de Venta debe ser un número válido\n");
                resultado = true;
            }
        }
        
        if (ventanaProducto.getTmarca().getText().trim().isEmpty()) {
            mensaje.append("- Marca\n");
            resultado = true;
        }
        
        if (ventanaProducto.getTstock().getText().trim().isEmpty()) {
            mensaje.append("- Stock\n");
            resultado = true;
        }
        
        // Si falta algún campo, mostrar mensaje de error
        if (resultado) {
            JOptionPane.showMessageDialog(ventanaProducto, mensaje.toString(), 
                "Información incompleta", JOptionPane.WARNING_MESSAGE);
        }
        
        return resultado;
    }
    
    /**
     * Recolecta los valores de los campos del formulario
     * @return Array con los valores para actualizar en la base de datos
     * ORDEN: [0]=id, [1]=sku, [2]=nombre, [3]=idCategoria, [4]=idProveedor,
     *        [5]=precioVenta, [6]=marca, [7]=stock, [8]=descripcion
     */
    private String[] obtenerValoresFormulario() {
        String id = ventanaProducto.getTidProducto().getText().trim();
        String sku = ventanaProducto.getTsku().getText().trim();
        String nombre = ventanaProducto.getTproducto().getText().trim();
        
        Categoria cat = (Categoria) ventanaProducto.getCcategoria().getSelectedItem();
        String idCategoria = cat.getId();
        
        Proveedor prov = (Proveedor) ventanaProducto.getCproveedor().getSelectedItem();
        String idProveedor = prov.getIdProveedor();
        
        String precioVenta = normalizarPrecio(ventanaProducto.getTprecioventa().getText().trim(), sku, nombre);
        String marca = ventanaProducto.getTmarca().getText().trim();
        String stock = ventanaProducto.getTstock().getText().trim();
        String descripcion = ventanaProducto.getTdescripcion().getText().trim();
        
        // ✅ ORDEN CORRECTO: id, sku, nombre, idCategoria, idProveedor, precioVenta, marca, stock, descripcion
        return new String[] {id, sku, nombre, idCategoria, idProveedor, precioVenta, marca, stock, descripcion};
    }
    
    /**
     * Normaliza un precio en formato de texto
     * @param precioTexto El precio en formato de texto
     * @param sku El SKU del producto (para mensaje de error)
     * @param nombre El nombre del producto (para mensaje de error)
     * @return El precio normalizado como String
     */
    private String normalizarPrecio(String precioTexto, String sku, String nombre) {
        try {
            // Si es nulo o vacío, retornar "0.0"
            if (precioTexto == null || precioTexto.trim().isEmpty()) {
                return "0.0";
            }
            
            // Limpiar el texto: eliminar caracteres no numéricos excepto punto y coma
            StringBuilder precioLimpio = new StringBuilder();
            boolean puntoComaEncontrado = false;
            
            for (char c : precioTexto.toCharArray()) {
                if (Character.isDigit(c)) {
                    precioLimpio.append(c);
                } else if ((c == '.' || c == ',') && !puntoComaEncontrado) {
                    // Siempre usar punto como separador decimal
                    precioLimpio.append('.');
                    puntoComaEncontrado = true;
                }
            }
            
            String resultado = precioLimpio.toString();
            
            // Si quedó vacío después de la limpieza
            if (resultado.isEmpty() || resultado.equals(".")) {
                return "0.0";
            }
            
            // Validar que sea un número válido
            Double.parseDouble(resultado);
            return resultado;
            
        } catch (NumberFormatException e) {
            return "0.0";
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // Acción del botón Cancelar
        if (e.getSource() == ventanaProducto.getBcancel()) {
            ventanaProducto.dispose();
        } 
        // Acción del botón Guardar
        else if (e.getSource() == ventanaProducto.getBsave()) {
            try {
                // Validar que todos los campos obligatorios estén completos
                if (vacio()) {
                    return;
                }
                
                // Obtener los valores del formulario
                String[] valores = obtenerValoresFormulario();
                String idProducto = valores[0];
                
                BaseDatos bd = new BaseDatos();
                
                // Verificar que no exista otro producto con el mismo SKU (excepto el actual)
                String sku = valores[1];
                String consultaSku = "sku = '" + sku + "' AND idProducto <> '" + idProducto + "'";
                ArrayList<String[]> resultadosSku = bd.consultar("Productos", "idProducto", consultaSku);
                
                if (resultadosSku != null && resultadosSku.size() > 0) {
                    JOptionPane.showMessageDialog(ventanaProducto, 
                        "Ya existe otro producto con el SKU: " + sku, 
                        "SKU duplicado", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Actualizar cada campo del producto en la base de datos
                // valores[] = [0]=id, [1]=sku, [2]=nombre, [3]=idCategoria, [4]=idProveedor,
                //             [5]=precioVenta, [6]=marca, [7]=stock, [8]=descripcion
                boolean resultadoSku = bd.modificar("Productos", "sku", valores[1], "idProducto = '" + idProducto + "'");
                boolean resultadoNombre = bd.modificar("Productos", "producto", valores[2], "idProducto = '" + idProducto + "'");
                boolean resultadoCategoria = bd.modificar("Productos", "idcategoria", valores[3], "idProducto = '" + idProducto + "'");
                boolean resultadoProveedor = bd.modificar("Productos", "idproveedor", valores[4], "idProducto = '" + idProducto + "'");  // ✅ CORREGIDO: índice 4
                boolean resultadoPrecio = bd.modificar("Productos", "precio_venta", valores[5], "idProducto = '" + idProducto + "'");    // ✅ CORREGIDO: índice 5
                boolean resultadoMarca = bd.modificar("Productos", "marca", valores[6], "idProducto = '" + idProducto + "'");            // ✅ CORREGIDO: índice 6
                boolean resultadoStock = bd.modificar("Productos", "stock", valores[7], "idProducto = '" + idProducto + "'");            // ✅ CORREGIDO: índice 7
                boolean resultadoDesc = bd.modificar("Productos", "descripcion", valores[8], "idProducto = '" + idProducto + "'");       // ✅ CORREGIDO: índice 8
                                
                                // Verificar que todas las actualizaciones fueron exitosas
                                if (resultadoSku && resultadoNombre && resultadoCategoria && resultadoProveedor && 
                                    resultadoPrecio && resultadoMarca && resultadoStock && resultadoDesc) {
                    
                    // Consultar todos los productos para actualizar la tabla
                    ArrayList<String[]> listaProductos = bd.consultar("Productos", null, null);
                    
                    // Actualizar el modelo de la tabla con los productos
                    if (listaProductos != null) {
                        DefaultTableModel modelo = lista.getTablaModelo(listaProductos);
                        tabla.setModel(modelo);
                    }
                    
                    // Cerrar la ventana
                    ventanaProducto.dispose();
                    
                    // Mostrar mensaje de éxito
                    JOptionPane.showMessageDialog(null, "Producto modificado correctamente", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Error al modificar el producto en la base de datos", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
                
                bd.cerrarConexion();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), 
                    "Error en la operación", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
