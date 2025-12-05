package Vista;

import javax.swing.*;
import java.awt.event.*;
import Modelo.Usuario;

public class ModificarUsuarioForm extends JFrame {
    private JTextField txtUsuario, txtContraseña, txtRol;
    private JButton btnGuardar;

    private Usuario usuario;

    public ModificarUsuarioForm(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Modificar Usuario");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setBounds(20, 20, 100, 25);
        add(lblUsuario);

        txtUsuario = new JTextField(usuario.getUsuario());
        txtUsuario.setBounds(120, 20, 140, 25);
        add(txtUsuario);

        JLabel lblContraseña = new JLabel("Contraseña:");
        lblContraseña.setBounds(20, 60, 100, 25);
        add(lblContraseña);

        txtContraseña = new JTextField(usuario.getContraseña());
        txtContraseña.setBounds(120, 60, 140, 25);
        add(txtContraseña);

        JLabel lblRol = new JLabel("Rol:");
        lblRol.setBounds(20, 100, 100, 25);
        add(lblRol);

        txtRol = new JTextField(usuario.getRol());
        txtRol.setBounds(120, 100, 140, 25);
        add(txtRol);

        btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(90, 140, 100, 25);
        add(btnGuardar);

        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardarCambios();
            }
        });

        setVisible(true);
    }

    private void guardarCambios() {
        usuario.setContraseña(txtContraseña.getText());
        usuario.setUsuario(txtUsuario.getText());
        usuario.setRol(txtRol.getText());

        JOptionPane.showMessageDialog(this, "Usuario modificado correctamente");
        dispose();
    }
}