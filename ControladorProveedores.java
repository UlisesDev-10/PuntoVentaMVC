
package Controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import Modelo.BaseDatos;
import Modelo.Proveedor;
import Vista.VentanaAgregarProveedor;
import Vista.VentanaVista;

import Vista.VistaGestionProveedoresInactivos;
import Vista.VistaMenu;

public class ControladorProveedores implements ActionListener {

    private VentanaVista VentanaVista;
    private VistaMenu padre;
    private DefaultTableModel modeloTabla;

    public ControladorProveedores(VistaMenu padre) {
        this.padre = padre;
       
        VentanaVista = new VentanaVista();
        VentanaVista.setTitle("Gestión de Proveedores");
        VentanaVista.setVisible(true);
        
        // Configurar los botones
        VentanaVista.getBsalid().addActionListener(this);
        VentanaVista.getBagregar().addActionListener(this);
        VentanaVista.getBeliminar().addActionListener(this);
        VentanaVista.getBmodificar().addActionListener(this);
            VentanaVista.getBverInactivos().addActionListener(this);
        
        // Configurar la tabla
        String[] columnas = {"ID", "Proveedor"};
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
        ArrayList<Proveedor> proveedores = Proveedor.obtenerTodos();
        
        // Llenar la tabla con los datos
        for (Proveedor proveedor : proveedores) {
            Object[] fila = {proveedor.getIdProveedor(), proveedor.getProveedor()};
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
            // Usar la ventana de agregar proveedor
            VentanaAgregarProveedor ventanaAgregar = new VentanaAgregarProveedor(padre.getFrame(), "Agregar Proveedor");
            
            ventanaAgregar.getBtnGuardar().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String id = ventanaAgregar.getTxtId().getText();
                    String nombre = ventanaAgregar.getTxtNombre().getText();
                    
                    if (!id.isEmpty() && !nombre.isEmpty()) {
                        // Crear nuevo proveedor
                        Proveedor nuevoProveedor = new Proveedor(id, nombre);
                        
                        // Guardar en la base de datos
                        if (nuevoProveedor.guardarEnBD()) {
                            JOptionPane.showMessageDialog(ventanaAgregar, "Proveedor guardado con éxito");
                            RefrescarTabla();
                            ventanaAgregar.dispose();
                        } else {
                            JOptionPane.showMessageDialog(ventanaAgregar, "Error al guardar el proveedor", 
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
                String idSeleccionado = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
                String nombreActual = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
                
                // Crear ventana para modificar proveedor
                VentanaAgregarProveedor ventanaModificar = new VentanaAgregarProveedor(
                        padre.getFrame(), "Modificar Proveedor");
                
                // Establecer valores actuales
                ventanaModificar.getTxtId().setText(idSeleccionado);
                ventanaModificar.getTxtId().setEditable(false); // No permitir cambiar el ID
                ventanaModificar.getTxtNombre().setText(nombreActual);
                
                ventanaModificar.getBtnGuardar().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String nuevoNombre = ventanaModificar.getTxtNombre().getText();
                        
                        if (!nuevoNombre.isEmpty()) {
                            // Actualizar proveedor
                            Proveedor proveedorModificado = new Proveedor(idSeleccionado, nuevoNombre);
                            
                            if (proveedorModificado.guardarEnBD()) {
                                JOptionPane.showMessageDialog(ventanaModificar, "Proveedor actualizado con éxito");
                                RefrescarTabla();
                                ventanaModificar.dispose();
                            } else {
                                JOptionPane.showMessageDialog(ventanaModificar, "Error al actualizar el proveedor", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(ventanaModificar, "El nombre no puede estar vacío", 
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
        }
        else if (e.getSource() == VentanaVista.getBverInactivos()) {
            mostrarProveedoresInactivos();
        }
        else if (e.getSource() == VentanaVista.getBeliminar()) {
            int filaSeleccionada = VentanaVista.getTdatos().getSelectedRow();
            
            if (filaSeleccionada >= 0) {
                String idSeleccionado = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
                
                int confirmacion = JOptionPane.showConfirmDialog(VentanaVista, 
                        "¿Está seguro de eliminar el proveedor con ID " + idSeleccionado + "?", 
                        "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                
                if (confirmacion == JOptionPane.YES_OPTION) {
                    BaseDatos bd = new BaseDatos();
                    // Marcar como inactivo en lugar de eliminar físicamente
                    bd.modificar("Proveedores", "activo", "0", "idProveedor = '" + idSeleccionado + "'");
                    bd.cerrarConexion();
                    
                    JOptionPane.showMessageDialog(VentanaVista, "Proveedor desactivado exitosamente. Puede verlo en 'Proveedores inactivos'.");
                    RefrescarTabla();
                }
            } else {
                JOptionPane.showMessageDialog(VentanaVista, "Seleccione un proveedor para eliminar", 
                        "Selección requerida", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Muestra la ventana de proveedores inactivos
     */
    private void mostrarProveedoresInactivos() {
        try {
            // Obtener datos de proveedores inactivos
            BaseDatos bd = new BaseDatos();
            ArrayList<String[]> proveedoresInactivos = bd.consultar("Proveedores", "idProveedor, proveedor", "activo = 0");
            
            if (proveedoresInactivos != null && !proveedoresInactivos.isEmpty()) {
                // Mostrar ventana de proveedores inactivos
                VistaGestionProveedoresInactivos vistaInactivos = new VistaGestionProveedoresInactivos(padre.getFrame());
                vistaInactivos.cargarProveedoresInactivos(proveedoresInactivos);
                
                // Configurar acción para reactivar proveedores
                vistaInactivos.getBtnReactivar().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int filaSeleccionada = vistaInactivos.getTabla().getSelectedRow();
                        if (filaSeleccionada >= 0) {
                            String idProveedor = (String) vistaInactivos.getTabla().getValueAt(filaSeleccionada, 0);
                            reactivarProveedor(idProveedor);
                            
                            // Actualizar la tabla de inactivos
                            BaseDatos bd = new BaseDatos();
                            ArrayList<String[]> nuevaLista = bd.consultar("Proveedores", "idProveedor, proveedor", "activo = 0");
                            vistaInactivos.cargarProveedoresInactivos(nuevaLista);
                            bd.cerrarConexion();
                            
                            // Refrescar la tabla principal
                            RefrescarTabla();
                            
                            JOptionPane.showMessageDialog(vistaInactivos, 
                                    "Proveedor reactivado con éxito", 
                                    "Operación exitosa", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(vistaInactivos, 
                                    "Debe seleccionar un proveedor para reactivar", 
                                    "Selección requerida", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                });
                
                // Mostrar la vista
                vistaInactivos.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(VentanaVista, 
                        "No hay proveedores inactivos para mostrar", 
                        "Información", JOptionPane.INFORMATION_MESSAGE);
            }
            
            bd.cerrarConexion();
        } catch (Exception ex) {
            System.err.println("❌ Error al cargar proveedores inactivos: " + ex.getMessage());
            JOptionPane.showMessageDialog(VentanaVista, 
                "Error al cargar proveedores inactivos: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Reactiva un proveedor en la base de datos
     * @param idProveedor ID del proveedor a reactivar
     */
    private void reactivarProveedor(String idProveedor) {
        try {
            BaseDatos bd = new BaseDatos();
            // Actualizar el campo 'activo' a 1 (activo)
            bd.modificar("Proveedores", "activo", "1", "idProveedor = '" + idProveedor + "'");
            bd.cerrarConexion();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, 
                    "Error al reactivar proveedor: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}