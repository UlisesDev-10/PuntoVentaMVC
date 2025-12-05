package Controlador;

import Vista.VentanaVista;
import java.awt.event.ActionListener;
import javax.swing.JInternalFrame;

public class ControladorVentanaVista {
    
    private JInternalFrame vista;
    
    /**
     * Obtiene la vista actual
     * @return La ventana de vista
     */
    public JInternalFrame getVista() {
        return vista;
    }
    
    /**
     * Establece la vista
     * @param vista La ventana de vista
     */
    public void setVista(JInternalFrame vista) {
        this.vista = vista;
    }
    public VentanaVista VentanaVista;
    
    public ControladorVentanaVista(String titulo) {
        VentanaVista = new VentanaVista(titulo);
        VentanaVista.setVisible(true);
    }
    
    public JInternalFrame getVentana() {
        return this.VentanaVista;
    }
    
    public VentanaVista getVentanaVista() {
        return this.VentanaVista;
    }
}