package Controlador;

import Vista.VistaElemento;
import Vista.VistaElementoProducto;
import Vista.VistaMenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import Librerias.ListaCategoria;
import Librerias.ListaProducto;
import Modelo.BaseDatos;
import Modelo.Categoria;
import Modelo.Conexion;
import Modelo.Proveedor;


public class ControladorVistaElementoProducto implements ActionListener {

VistaElementoProducto VentanaVista;
VistaMenu padre;
String datos[];
JTable tabla;
ListaProducto lista;

public void CargarCombobox()
{
	 if (padre != null) {
	     padre.Menus(true);
	 } else {
	     System.out.println("Error: padre es nulo en CargarCombobox");
	     // Podríamos también lanzar una excepción o manejar de otra forma
	 }
		// establecemos la conexion bd
		BaseDatos bd = new BaseDatos();
		
		// Cargar categorías
		ArrayList<String[]> lista = bd.consultar("Categorias", null,"activo=1");
		if (lista != null) {
			for(String[] nodo:lista)
			{
				Categoria nodoc = new Categoria(nodo[0],nodo[1]);
				this.VentanaVista.getCcategoria().addItem(nodoc);
			}
		}
		
		// Cargar proveedores
		ArrayList<String[]> listaProveedores = bd.consultar("Proveedores", null, "activo=1");
		if (listaProveedores != null) {
			for(String[] prov:listaProveedores)
			{
				Proveedor proveedor = new Proveedor(prov[0], prov[1]);
				this.VentanaVista.getCproveedor().addItem(proveedor);
			}
		}
		
		bd.cerrarConexion();		
}


	

public String InfoVacio() {
    try {
        // Cadena que contendrá los valores separados por comas
        StringBuilder cadena = new StringBuilder();
        
        // Total de campos a verificar
        int totalCampos = 9; // Ahora son 9 campos con el proveedor
        int camposCompletos = 0;
        
        // 1. Validar ID Producto
        String idProducto = this.VentanaVista.getTidProducto().getText().trim();
        if (!idProducto.isEmpty()) {
            cadena.append(idProducto).append(",");
            camposCompletos++;
        } else {
            System.out.println("Campo ID Producto vacío");
        }
        
        // 2. Validar SKU
        String sku = this.VentanaVista.getTsku().getText().trim();
        if (!sku.isEmpty()) {
            cadena.append(sku).append(",");
            camposCompletos++;
        } else {
            System.out.println("Campo SKU vacío");
        }
        
        // 3. Validar Nombre del Producto
        String producto = this.VentanaVista.getTproducto().getText().trim();
        if (!producto.isEmpty()) {
            cadena.append(producto).append(",");
            camposCompletos++;
        } else {
            System.out.println("Campo Producto vacío");
        }
        
        // 4. Validar Categoría
        int pos = this.VentanaVista.getCcategoria().getSelectedIndex();
        if (pos >= 0) {
            Categoria cat = (Categoria)this.VentanaVista.getCcategoria().getSelectedItem();
            cadena.append(cat.getId()).append(",");
            camposCompletos++;
        } else {
            System.out.println("No se seleccionó categoría");
        }
        
        // 5. Validar Proveedor
        int posProveedor = this.VentanaVista.getCproveedor().getSelectedIndex();
        if (posProveedor >= 0) {
            Proveedor prov = (Proveedor)this.VentanaVista.getCproveedor().getSelectedItem();
            cadena.append(prov.getIdProveedor()).append(",");
            camposCompletos++;
        } else {
            System.out.println("No se seleccionó proveedor");
        }
        
        // 6. Validar Precio de Venta
        String precioVenta = this.VentanaVista.getTprecioventa().getText().trim();
        if (!precioVenta.isEmpty()) {
            cadena.append(precioVenta).append(",");
            camposCompletos++;
        } else {
            System.out.println("Campo Precio vacío");
        }
        
        // 7. Validar Marca
        String marca = this.VentanaVista.getTmarca().getText().trim();
        if (!marca.isEmpty()) {
            cadena.append(marca).append(",");
            camposCompletos++;
        } else {
            System.out.println("Campo Marca vacío");
        }
        
        // 8. Validar Stock
        String stock = this.VentanaVista.getTstock().getText().trim();
        if (!stock.isEmpty()) {
            cadena.append(stock).append(",");
            camposCompletos++;
        } else {
            System.out.println("Campo Stock vacío");
        }
        
        // 9. Validar Descripción
        String descripcion = this.VentanaVista.getTdescripcion().getText().trim();
        if (!descripcion.isEmpty()) {
            cadena.append(descripcion);
            camposCompletos++;
        } else {
            System.out.println("Campo Descripción vacío");
        }
        
        // Si faltan campos, retornamos cadena vacía
        if (camposCompletos < totalCampos) {
            System.out.println("Faltan campos: " + camposCompletos + " de " + totalCampos);
            return "";
        }
        
        return cadena.toString();
    } catch (Exception e) {
        System.out.println("Error en InfoVacio: " + e.getMessage());
        e.printStackTrace();
        return "";
    }
}



public ControladorVistaElementoProducto(VistaMenu padre, String titulo, ListaProducto Lista, JTable Tabla) {
    this.padre = padre; // Inicializar la variable padre
    tabla = Tabla;
    lista = Lista;
    VentanaVista = new VistaElementoProducto(padre.getFrame(), titulo, true);
    SwingUtilities.invokeLater(() -> VentanaVista.setVisible(true));
    CargarCombobox();
    VentanaVista.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    
    // Usar this como ActionListener ya que la clase implementa ActionListener
    VentanaVista.getBcancel().addActionListener(this);
    VentanaVista.getBsave().addActionListener(this);
}


public boolean vacio() {
    boolean resultado = true;
    
    // Obtenemos los campos principales
    String id = this.VentanaVista.getTidProducto().getText().trim();
    String sku = this.VentanaVista.getT1().getText().trim();
    String producto = this.VentanaVista.getT2().getText().trim();
    
    // Mensaje para campos faltantes
    StringBuilder mensaje = new StringBuilder("Falta información: \n");
    boolean faltaInfo = false;
    
    // Verificamos ID del Producto
    if (id.isEmpty()) {
        mensaje.append("- ID del Producto\n");
        faltaInfo = true;
    }
    
    // Verificamos SKU
    if (sku.isEmpty()) {
        mensaje.append("- SKU\n");
        faltaInfo = true;
    }
    
    // Verificamos Nombre del Producto
    if (producto.isEmpty()) {
        mensaje.append("- Nombre del Producto\n");
        faltaInfo = true;
    }
    
    // Verificamos Categoría
    if (this.VentanaVista.getCcategoria().getSelectedIndex() < 0) {
        mensaje.append("- Categoría\n");
        faltaInfo = true;
    }
    
    // Verificamos Proveedor
    if (this.VentanaVista.getCproveedor().getSelectedIndex() < 0) {
        mensaje.append("- Proveedor\n");
        faltaInfo = true;
    }
    
    // Verificamos Proveedor
    if (this.VentanaVista.getCproveedor().getSelectedIndex() < 0) {
        mensaje.append("- Proveedor\n");
        faltaInfo = true;
    }
    
    // Verificamos Precio
    if (this.VentanaVista.getTprecioventa().getText().trim().isEmpty()) {
        mensaje.append("- Precio de Venta\n");
        faltaInfo = true;
    }
    
    // Verificamos Marca
    if (this.VentanaVista.getTmarca().getText().trim().isEmpty()) {
        mensaje.append("- Marca\n");
        faltaInfo = true;
    }
    
    // Verificamos Stock
    if (this.VentanaVista.getTstock().getText().trim().isEmpty()) {
        mensaje.append("- Stock\n");
        faltaInfo = true;
    }
    
    // Verificamos Descripción
    if (this.VentanaVista.getTdescripcion().getText().trim().isEmpty()) {
        mensaje.append("- Descripción\n");
        faltaInfo = true;
    }
    
    // Si todos los campos están completos, el resultado es falso (no está vacío)
    if (!faltaInfo) {
        resultado = false;
    } else {
        // Mostramos mensaje de error con los campos faltantes
        JOptionPane.showMessageDialog(null, mensaje.toString(), "Información incompleta", JOptionPane.WARNING_MESSAGE);
    }
    
    return resultado;
}

@Override
public void actionPerformed(ActionEvent e) {
    // Acción del botón Cancelar
    if (e.getSource() == VentanaVista.getBcancel()) {
        VentanaVista.dispose();
    } 
    // Acción del botón Guardar
    else if (e.getSource() == VentanaVista.getBsave()) {
        try {
            // Primero verificamos que no falten campos obligatorios
            if (vacio()) {
                return; // El método vacio() ya muestra mensaje de error
            }
            
            // Obtenemos los datos de todos los campos
            String cadenavacia = InfoVacio();
            if (cadenavacia.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Faltan datos por completar", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Separamos los valores
            String[] valores = cadenavacia.split(",");
            BaseDatos bd = new BaseDatos();
            
            // Verificar que no exista el ID del producto
            String idProducto = valores[0];
            String cadenacomparacionId = "idProducto='" + idProducto + "'";
            ArrayList<String[]> resultadosId = bd.consultar("Productos", "idProducto", cadenacomparacionId);
            if (resultadosId != null && resultadosId.size() > 0) {
                JOptionPane.showMessageDialog(tabla, "Ya existe un producto con el ID: " + idProducto, 
                    "ID duplicado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Verificar que no exista el SKU
            String sku = valores[1]; // SKU es el segundo valor ahora
            String cadenacomparacionSku = "sku='" + sku + "'";
            ArrayList<String[]> resultadosSku = bd.consultar("Productos", "sku", cadenacomparacionSku);
            if (resultadosSku != null && resultadosSku.size() > 0) {
                JOptionPane.showMessageDialog(tabla, "Ya existe un producto con el SKU: " + sku, 
                    "SKU duplicado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Si llegamos aquí, podemos insertar el nuevo producto
            String campos = "idProducto,sku,producto,idcategoria,idProveedor,precio_venta,marca,stock,descripcion";
            boolean resultado = bd.insertar("productos", campos, valores);
            
            if (resultado) {
                // Consultamos todos los productos para actualizar la tabla
                ArrayList<String[]> listaProductos = bd.consultar("Productos", null, null);
                bd.cerrarConexion();
                
                // Actualizar el modelo de la tabla con los productos
                DefaultTableModel x = lista.getTablaModelo(listaProductos);
                tabla.setModel(x);
                
                // Cerramos la ventana
                VentanaVista.dispose();
                
                // Mostrar mensaje de éxito
                JOptionPane.showMessageDialog(null, "Producto agregado correctamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error al insertar el producto en la base de datos", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), 
                "Error en la operación", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}

}



	


