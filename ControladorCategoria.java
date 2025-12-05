package Controlador;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import Librerias.ListaCategoria;
import Librerias.ListaTipoVenta;
import Modelo.BaseDatos;
import Modelo.Categoria;
import Modelo.Conexion;
import Vista.VentanaVista;

import Vista.VistaCategoriasInactivas;
import Vista.VistaGestionProveedoresInactivos;
import Vista.VistaMenu;

public class ControladorCategoria implements ActionListener {

ControladorVentanaVista controlador;
VistaMenu padre;
ListaCategoria listadatos;
VentanaVista VentanaVista;

public ControladorCategoria(VistaMenu padre) {
	listadatos = new ListaCategoria();

	this.padre=padre;

	this.controlador = new ControladorVentanaVista(" Seccion de Categorias de Productos");
	this.RefrescarTabla();
	this.controlador.VentanaVista.getBsalid().addActionListener(this);
	this.controlador.VentanaVista.getBagregar().addActionListener(this);
	this.controlador.VentanaVista.getBeliminar().addActionListener(this);
	this.controlador.VentanaVista.getBmodificar().addActionListener(this);
	this.controlador.VentanaVista.getBverInactivos().addActionListener(this);
	
	padre.Menus(true);

}



public JInternalFrame getVentana()
{
	
return this.controlador.VentanaVista;	
}


public void RefrescarTabla()
{
	// establecemos la conexion bd
	try {
		// Usamos una única instancia de BaseDatos para todas las operaciones
		BaseDatos bd = new BaseDatos(Conexion.obtenerConexion());
		ArrayList<String[]> lista = bd.consultar("Categorias", null, "activo = 1");
		
		if (lista != null) {
		    System.out.println("lista "+lista.size());
		    this.controlador.VentanaVista.getTdatos().setModel(listadatos.getTablaModelo(lista));
		} else {
		    System.err.println("❌ La consulta a Categorias devolvió NULL");
		    JOptionPane.showMessageDialog(null, 
		        "No se pudieron cargar las categorías. Compruebe la conexión a la base de datos.", 
		        "Error de consulta", JOptionPane.WARNING_MESSAGE);
		}
		
		// Ya no cerramos la conexión para mantenerla disponible para futuras operaciones
	} catch (Exception e) {
		System.err.println("❌ Error al refrescar tabla de categorías: " + e.getMessage());
		JOptionPane.showMessageDialog(null, "Error al cargar las categorías: " + e.getMessage(), 
		                            "Error de base de datos", JOptionPane.ERROR_MESSAGE);
	}
		
}

/**
 * Muestra la ventana de categorías inactivas
 */


@Override
public void actionPerformed(ActionEvent e) {
	if (e.getSource() == this.controlador.VentanaVista.getBsalid())
	{
		padre.Menus(true);
		this.controlador.VentanaVista.dispose();
		
	}
	else if (e.getSource() == this.controlador.VentanaVista.getBagregar())
	{
		String[] etiquetas ={"Id Categoria ","  Categoria"};
		ControladorVistaElemento  hijo;
		this.controlador.VentanaVista.setEnabled(false);
		hijo = new ControladorVistaElemento(padre,"Agregar Categoria ",etiquetas,this.listadatos,this.controlador.VentanaVista.getTdatos());
	}
	else if (e.getSource() == this.controlador.VentanaVista.getBverInactivos())
	{

		mostrarCategoriasInactivas();
	}
	else if (e.getSource() == this.controlador.VentanaVista.getBmodificar())
	{
		int fila=this.controlador.VentanaVista.getTdatos().getSelectedRow();
		if(fila>=0)
		{
			String[] etiquetas ={"Id Categoria ","  Categoria"};
			String id = (String) this.controlador.VentanaVista.getTdatos().getValueAt(fila, 0);
			String etiqueta=(String) this.controlador.VentanaVista.getTdatos().getValueAt(fila, 1);
			ControladorVistaElemento  hijo;
			hijo = new ControladorVistaElemento(padre,"Agregar Categoria ",etiquetas,this.listadatos,this.controlador.VentanaVista.getTdatos());
					   
			hijo.VentanaVista.getT1().setEnabled(false);
			hijo.VentanaVista.getT1().setText(id);
			hijo.VentanaVista.getT2().setText(etiqueta);
			
		}
			
		{
		}
	}
	else if (e.getSource() == this.controlador.VentanaVista.getBeliminar())
	{
		int fila=this.controlador.VentanaVista.getTdatos().getSelectedRow();
		if(fila>=0)
		{
			String id = (String) this.controlador.VentanaVista.getTdatos().getValueAt(fila, 0);
			String etiqueta=(String) this.controlador.VentanaVista.getTdatos().getValueAt(fila, 1);
			Categoria nodo = new Categoria(id.trim(),etiqueta.trim());
			this.listadatos.eliminar(nodo);
			BaseDatos bd = new BaseDatos(Conexion.obtenerConexion());
			
			try {
				// El método eliminar() ahora devuelve un booleano para indicar éxito
				// Marcar como inactivo en lugar de eliminar físicamente
				boolean eliminado = bd.modificar("Categorias", "activo", "0", "id = '" + id + "'");
				
				if (eliminado) {
					// Si la eliminación fue exitosa, actualizamos la tabla
					this.RefrescarTabla();
					JOptionPane.showMessageDialog(null, "Categoría eliminada con éxito");
				} else {
					JOptionPane.showMessageDialog(null, 
						"No se pudo eliminar la categoría. Puede estar siendo usada por productos.", 
						"Error de eliminación", JOptionPane.WARNING_MESSAGE);
				}
			} catch (Exception ex) {
				// Si hay una excepción, mostramos un mensaje de error
				JOptionPane.showMessageDialog(null, 
					"No se pudo eliminar la categoría: " + ex.getMessage(), 
					"Error de eliminación", JOptionPane.ERROR_MESSAGE);
			}
			// Ya no cerramos la conexión para mantenerla disponible para futuras operaciones
		}
		}
	}
	private void mostrarCategoriasInactivas() {
	    BaseDatos bd = new BaseDatos();
	    // Consultar categorías inactivas
	    ArrayList<String[]> categoriasInactivas = bd.consultarCategoriasInactivas();
	    bd.cerrarConexion();
	    
	    if (categoriasInactivas == null || categoriasInactivas.isEmpty()) {
	        JOptionPane.showMessageDialog(null, "No hay categorías inactivas", 
	                "Información", JOptionPane.INFORMATION_MESSAGE);
	        return;
	    }
	    
	    // Crear y mostrar la ventana de categorías inactivas
	    VistaCategoriasInactivas ventanaInactivas = new VistaCategoriasInactivas(padre.getFrame());
	    ventanaInactivas.cargarDatos(categoriasInactivas);
	    
	    // Configurar el botón de reactivar
	    ventanaInactivas.getBtnReactivar().addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            int filaSeleccionada = ventanaInactivas.getTabla().getSelectedRow();
	            
	            if (filaSeleccionada >= 0) {
	                String idCategoria = (String) ventanaInactivas.getTabla().getValueAt(filaSeleccionada, 0);
	                reactivarCategoria(idCategoria);
	                
	                // Actualizar la tabla de inactivos quitando la fila reactivada
	                DefaultTableModel modelo = (DefaultTableModel) ventanaInactivas.getTabla().getModel();
	                modelo.removeRow(filaSeleccionada);
	                
	                // Si no quedan más categorías inactivas, cerrar la ventana
	                if (modelo.getRowCount() == 0) {
	                    JOptionPane.showMessageDialog(ventanaInactivas, 
	                            "No quedan más categorías inactivas", 
	                            "Información", JOptionPane.INFORMATION_MESSAGE);
	                    ventanaInactivas.dispose();
	                }
	                
	                // Refrescar la tabla de categorías activas
	                RefrescarTabla();
	            } else {
	                JOptionPane.showMessageDialog(ventanaInactivas, 
	                        "Seleccione una categoría para reactivar", 
	                        "Selección requerida", JOptionPane.WARNING_MESSAGE);
	            }
	        }
	    });
	    
	    ventanaInactivas.setVisible(true);
	}

	/**
	 * Reactiva una categoría en la base de datos
	 * @param idCategoria ID de la categoría a reactivar
	 */
	private void reactivarCategoria(String idCategoria) {
	    BaseDatos bd = new BaseDatos();
	    boolean exito = bd.modificar("Categorias", "activo", "1", "id = '" + idCategoria + "'");
	    bd.cerrarConexion();
	    
	    if (exito) {
	        JOptionPane.showMessageDialog(null, "Categoría reactivada exitosamente", 
	                "Éxito", JOptionPane.INFORMATION_MESSAGE);
	    } else {
	        JOptionPane.showMessageDialog(null, "Error al reactivar la categoría", 
	                "Error", JOptionPane.ERROR_MESSAGE);
	    }
	}
}






