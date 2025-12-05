package Controlador;

import Modelo.Proveedor;
import Vista.VentanaAgregarProveedor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorAgregarProveedor {

    private VentanaAgregarProveedor vista;

    public ControladorAgregarProveedor(JFrame padre) {
        vista = new VentanaAgregarProveedor(padre, "Agregar Proveedor");

        vista.getBtnGuardar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = vista.getTxtId().getText();
                String nombre = vista.getTxtNombre().getText();

                if (!id.isEmpty() && !nombre.isEmpty()) {
                    Proveedor nuevo = new Proveedor();
  https://marketplace.eclipse.org/marketplace-client-intro?mpc_install=5321178                  nuevo.setIdProveedior(id);
                    nuevo.setProveedor(nombre);
    https://marketplace.eclipse.org/marketplace-client-intro?mpc_install=5321178                JOptionPane.showMessageDialog(vista, "Proveedor agregado:\n" + nombre);
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
