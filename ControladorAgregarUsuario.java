package Controlador;

import Modelo.Usuario;
import Vista.VentanaAgregarUsuario;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorAgregarUsuario {

    private VentanaAgregarUsuario vista;

    public ControladorAgregarUsuario(JFrame padre) {
        vista = new VentanaAgregarUsuario(padre, "Agregar Usuario");

        vista.getBtnGuardar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = vista.getTxtUsuario().getText();
                String contraseña = new String(vista.getTxtContraseña().getPassword());
                String rol = vista.getCbRol().getSelectedItem().toString();

                if (!usuario.isEmpty() && !contraseña.isEmpty()) {
                    Usuario nuevo = new Usuario(usuario, contraseña, rol);
                    JOptionPane.showMessageDialog(vista, "Usuario agregado:\n" + usuario);
                    vista.dispose(); // cerrar después de guardar
                } else {
                    JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios");
                }
            }
        });

        vista.getBtnCancelar().addActionListener(e -> vista.dispose());

        vista.setVisible(true);
    }

    public void mostrarVentana() {
        vista.setVisible(true);
    }
}