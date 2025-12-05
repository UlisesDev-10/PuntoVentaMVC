package Controlador;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JInternalFrame;

import Librerias.ListaTipoVenta;
import Vista.VistaMenu;

public class ControladorTipoVenta implements ActionListener {

ControladorVentanaVista controlador;
ListaTipoVenta listadatos;
VistaMenu padre;
public ControladorTipoVenta(VistaMenu padre) {
	
	listadatos = new ListaTipoVenta();
	this.padre=padre;
	controlador = new ControladorVentanaVista(" Seccion de Tipo de Venta del Productos");
	this.controlador.VentanaVista.getTdatos().setModel(listadatos.getTablaModelo());
	this.controlador.VentanaVista.getBsalid().addActionListener(this);
}






@Override
public void actionPerformed(ActionEvent e) {
	if ( e.getSource() == this.controlador.VentanaVista.getBsalid())
	{
		this.padre.Menus(true);
		this.controlador.VentanaVista.dispose();

	}
	
}

public JInternalFrame getVentana() {
return this.controlador.getVentana();
}
}
