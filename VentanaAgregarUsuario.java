
package Vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class VentanaAgregarUsuario extends JDialog {
    
    private JTextField txtUsuario;
    private JPasswordField txtContraseña;
    private JComboBox<String> cbRol;
    private JButton btnGuardar;
    private JButton btnCancelar;
    
    public VentanaAgregarUsuario(JFrame parent, String title) {
        super(parent, title, true);
        initComponents();
    }
    
    private void initComponents() {
        // Configurar ventana
        setSize(400, 250);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        
        // Panel de formulario
        JPanel panelForm = new JPanel();
        panelForm.setLayout(new GridLayout(3, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblUsuario = new JLabel("Usuario:");
        txtUsuario = new JTextField(20);
        
        JLabel lblContraseña = new JLabel("Contraseña:");
        txtContraseña = new JPasswordField(20);
        
        JLabel lblRol = new JLabel("Rol:");
        cbRol = new JComboBox<>(new String[] {"administrador", "vendedor","Supervisor", "inventarista"});
        
        panelForm.add(lblUsuario);
        panelForm.add(txtUsuario);
        panelForm.add(lblContraseña);
        panelForm.add(txtContraseña);
        panelForm.add(lblRol);
        panelForm.add(cbRol);
        
        // Panel de botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        
        // Agregar paneles a la ventana
        add(panelForm, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    // Getters para acceder a los componentes desde el controlador
    public JTextField getTxtUsuario() {
        return txtUsuario;
    }
    
    public JPasswordField getTxtContraseña() {
        return txtContraseña;
    }
    
    public JComboBox<String> getCbRol() {
        return cbRol;
    }
    
    public JButton getBtnGuardar() {
        return btnGuardar;
    }
    
    public JButton getBtnCancelar() {
        return btnCancelar;
    }
}
