package Controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import Vista.VentanaVista2;
import Modelo.BaseDatos;
import Modelo.Usuario;
import Vista.VentanaAgregarUsuario;
import Vista.VentanaVista;
import Vista.VistaMenu;

public class ControladorUsuarios implements ActionListener {

    private VentanaVista2 VentanaVista;
    private VistaMenu padre;
    private DefaultTableModel modeloTabla;
    
    public ControladorUsuarios(VistaMenu padre) {
        this.padre = padre;
            // Crear la ventana de visualización
            VentanaVista = new VentanaVista2();
            // Indicar que es vista de usuarios
            VentanaVista.setTitle("Gestión de Usuarios");
        VentanaVista.setVisible(true);
        
        // Configurar los botones
        VentanaVista.getBsalid().addActionListener(this);
        VentanaVista.getBagregar().addActionListener(this);
        VentanaVista.getBeliminar().addActionListener(this);
        VentanaVista.getBmodificar().addActionListener(this);
        
        // Configurar la tabla
        String[] columnas = {"Usuario", "Rol"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        VentanaVista.getTdatos().setModel(modeloTabla);
        
        // Cargar datos iniciales
        RefrescarTabla();
        padre.Menus(true);
    }
    
    public JInternalFrame getVentana() {
        return VentanaVista;
    }
    
    public void RefrescarTabla() {
        // Limpiar tabla
        modeloTabla.setRowCount(0);
        
        // Obtener datos de la base de datos
        ArrayList<Usuario> usuarios = Usuario.obtenerTodos();
        
        // Llenar la tabla con los datos
        for (Usuario usuario : usuarios) {
            Object[] fila = {usuario.getUsuario(), usuario.getRol()};
            modeloTabla.addRow(fila);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == VentanaVista.getBsalid()) {
            padre.Menus(true);
            VentanaVista.dispose();
        }
        else if (e.getSource() == VentanaVista.getBagregar()) {
            // Usar la clase VentanaAgregarUsuario
            VentanaAgregarUsuario ventanaAgregar = new VentanaAgregarUsuario(padre.getFrame(), "Agregar Usuario");
            
            ventanaAgregar.getBtnGuardar().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String usuario = ventanaAgregar.getTxtUsuario().getText();
                    String contraseña = new String(ventanaAgregar.getTxtContraseña().getPassword());
                    String rol = ventanaAgregar.getCbRol().getSelectedItem().toString();
                    
                    if (!usuario.isEmpty() && !contraseña.isEmpty()) {
                        // Crear nuevo usuario
                        Usuario nuevoUsuario = new Usuario(usuario, contraseña, rol);
                        
                        // Guardar en la base de datos
                        if (nuevoUsuario.guardarEnBD()) {
                            JOptionPane.showMessageDialog(ventanaAgregar, "Usuario guardado con éxito");
                            RefrescarTabla();
                            ventanaAgregar.dispose();
                        } else {
                            JOptionPane.showMessageDialog(ventanaAgregar, "Error al guardar el usuario", 
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(ventanaAgregar, "Todos los campos son obligatorios", 
                                "Error", JOptionPane.WARNING_MESSAGE);
                    }
                }
            });
            
            ventanaAgregar.getBtnCancelar().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ventanaAgregar.dispose();
                }
            });
            
            ventanaAgregar.setVisible(true);
        }
        else if (e.getSource() == VentanaVista.getBmodificar()) {
            int filaSeleccionada = VentanaVista.getTdatos().getSelectedRow();
            
            if (filaSeleccionada >= 0) {
                String usuarioSeleccionado = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
                
                // Buscar el usuario en la base de datos
                BaseDatos bd = new BaseDatos();
                ArrayList<String[]> resultados = bd.consultar("Usuarios", "*", "usuario = '" + usuarioSeleccionado + "'");
                
                if (resultados != null && resultados.size() > 0) {
                    String[] datosUsuario = resultados.get(0);
                    
                    // Crear ventana para modificar usuario
                    VentanaAgregarUsuario ventanaModificar = new VentanaAgregarUsuario(padre.getFrame(), "Modificar Usuario");
                    
                    // Establecer valores actuales
                    ventanaModificar.getTxtUsuario().setText(datosUsuario[0]);
                    ventanaModificar.getTxtUsuario().setEditable(false); // No permitir cambiar el nombre de usuario
                    ventanaModificar.getTxtContraseña().setText(datosUsuario[1]);
                    
                    // Seleccionar el rol en el combobox
                    for (int i = 0; i < ventanaModificar.getCbRol().getItemCount(); i++) {
                        if (ventanaModificar.getCbRol().getItemAt(i).toString().equals(datosUsuario[2])) {
                            ventanaModificar.getCbRol().setSelectedIndex(i);
                            break;
                        }
                    }
                    
                    ventanaModificar.getBtnGuardar().addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String contraseña = new String(ventanaModificar.getTxtContraseña().getPassword());
                            String rol = ventanaModificar.getCbRol().getSelectedItem().toString();
                            
                            if (!contraseña.isEmpty()) {
                                // Actualizar usuario
                                Usuario usuarioModificado = new Usuario(usuarioSeleccionado, contraseña, rol);
                                
                                if (usuarioModificado.guardarEnBD()) {
                                    JOptionPane.showMessageDialog(ventanaModificar, "Usuario actualizado con éxito");
                                    RefrescarTabla();
                                    ventanaModificar.dispose();
                                } else {
                                    JOptionPane.showMessageDialog(ventanaModificar, "Error al actualizar el usuario", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(ventanaModificar, "La contraseña no puede estar vacía", 
                                        "Error", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    });
                    
                    ventanaModificar.getBtnCancelar().addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ventanaModificar.dispose();
                        }
                    });
                    
                    ventanaModificar.setVisible(true);
                }
                
                bd.cerrarConexion();
            } else {
                JOptionPane.showMessageDialog(VentanaVista, "Seleccione un usuario para modificar", 
                        "Selección requerida", JOptionPane.WARNING_MESSAGE);
            }
        }
        else if (e.getSource() == VentanaVista.getBeliminar()) {
            int filaSeleccionada = VentanaVista.getTdatos().getSelectedRow();
            
            if (filaSeleccionada >= 0) {
                String usuarioSeleccionado = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
                
                int confirmacion = JOptionPane.showConfirmDialog(VentanaVista, 
                        "¿Está seguro de eliminar el usuario " + usuarioSeleccionado + "?", 
                        "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                
                if (confirmacion == JOptionPane.YES_OPTION) {
                    BaseDatos bd = new BaseDatos();
                    bd.eliminar("Usuarios", "usuario", usuarioSeleccionado);
                    bd.cerrarConexion();
                    
                    JOptionPane.showMessageDialog(VentanaVista, "Usuario eliminado con éxito");
                    RefrescarTabla();
                }
            } else {
                JOptionPane.showMessageDialog(VentanaVista, "Seleccione un usuario para eliminar", 
                        "Selección requerida", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}

