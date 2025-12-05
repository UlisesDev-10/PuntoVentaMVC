package Controlador;

import Vista.VistaElemento;
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

import Librerias.ListaCategoria;
import Modelo.BaseDatos;
import Modelo.Categoria;
import Modelo.Conexion;


public class ControladorVistaElemento   {

VistaElemento VentanaVista;
VistaMenu padre;

String datos[];
JTable tabla;
public ControladorVistaElemento(VistaMenu padre,String titulo,String[] etiquetas,ListaCategoria lista,JTable Tabla) {
	tabla=Tabla;
	 padre.Menus(true);
	VentanaVista = new VistaElemento(padre.getFrame(),titulo,true);
	SwingUtilities.invokeLater(() -> VentanaVista.setVisible(true));
	
	VentanaVista.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	VentanaVista.setEtiqueta1(etiquetas[0]);
	VentanaVista.setEtiqueta2(etiquetas[1]);
	System.out.println(VentanaVista.getBcancel().isFocusable());
	VentanaVista.getBcancel().addActionListener(new ActionListener()
			{
			@Override
		public void actionPerformed(ActionEvent e) {
				System.out.println("cancelar");
				JOptionPane.showMessageDialog(VentanaVista, "probando");
						VentanaVista.dispose();
					
		}		
			}
			);
	this.VentanaVista.getBsave().addActionListener(new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!vacio())
			{	String id = VentanaVista.getT1().getText().trim();
				String etiqueta = VentanaVista.getT2().getText().trim();
				String[] valores = {id,etiqueta};
			 	if(VentanaVista.getT1().isEnabled())
			 	{
			 		if (lista.insertar(new Categoria(id,etiqueta)))
			 		{
			 			BaseDatos bd = new BaseDatos();

						bd.insertar("Categorias", "id,etiqueta", valores);

						bd.cerrarConexion();
			 			Tabla.setModel(lista.getTablaModelo(bd.consultar("Categorias", null, null)));	
			 		}
			 		else
			 		JOptionPane.showMessageDialog(Tabla, "id ya existente");
			 	}
			 	else {
			 		lista.modificar(new Categoria(id,etiqueta));
			 		BaseDatos bd = new BaseDatos(Conexion.obtenerConexion());
			 				 			boolean modificado = bd.modificar("Categorias","etiqueta",valores[1], "id='"+valores[0]+"'");
			 				 			
			 				 			if (modificado) {
			 				 			    // Obtenemos las categorías actualizadas sin cerrar la conexión
			 				 			    ArrayList<String[]> categorias = bd.consultar("Categorias", null, null);
			 				 			    if (categorias != null) {
			 				 			        Tabla.setModel(lista.getTablaModelo(categorias));
			 				 			        JOptionPane.showMessageDialog(null, "Categoría modificada correctamente");
			 				 			    }
			 				 			} else {
			 				 			    JOptionPane.showMessageDialog(null, "No se pudo modificar la categoría", 
			 				 			                                  "Error de modificación", JOptionPane.WARNING_MESSAGE);
			 				 			}
			 	}
			 	JOptionPane.showMessageDialog(null, lista.size()+"  "+Tabla.getRowCount());
				
			 	}
			
			VentanaVista.dispose();
		}		
	}
	);
}


public boolean vacio()
{
	boolean resultado =true;
	String t1,t2 ;
	t1= this.VentanaVista.getT1().getText().trim();
	t2= this.VentanaVista.getT2().getText().trim();
	if(!t1.isEmpty() && !t2.isEmpty())
		resultado=false;
return resultado;	
}

}



	


