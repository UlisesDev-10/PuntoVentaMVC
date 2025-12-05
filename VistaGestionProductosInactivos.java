package Vista;

import Modelo.BaseDatos;
import Modelo.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;



import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import Modelo.BaseDatos;

/**
 * Vista para gestionar productos inactivos (desactivados)
 */
public class VistaGestionProductosInactivos extends JFrame {
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JButton btnReactivar;
    private JButton btnCerrar;
    private JButton btnActualizar;
    private JLabel lblTitulo;
    private BaseDatos baseDatos;
    
    public VistaGestionProductosInactivos() {
        // Inicializar la base de datos
        baseDatos = new BaseDatos();
        
        // Configurar la ventana
        setTitle("Gestión de Productos Inactivos");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Crear el panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Título
        lblTitulo = new JLabel("Productos Desactivados", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(70, 70, 70));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        
        // Crear tabla
        String[] columnas = {"ID", "Producto", "Descripcion", "Precio", "Stock", "Categoria", "Marca", "SKU"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No permitir edición en la tabla
            }
        };
        
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.getTableHeader().setReorderingAllowed(false);
        
        // Mejorar apariencia de la tabla
        tablaProductos.setRowHeight(25);
        tablaProductos.setIntercellSpacing(new Dimension(10, 5));
        tablaProductos.setGridColor(new Color(230, 230, 230));
        
        // Agregar la tabla a un scroll pane
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        btnReactivar = new JButton("Reactivar Producto");
        btnReactivar.setBackground(new Color(60, 179, 113));
        btnReactivar.setForeground(Color.WHITE);
        btnReactivar.setFocusPainted(false);
        
        btnActualizar = new JButton("Actualizar Lista");
        btnActualizar.setBackground(new Color(70, 130, 180));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        
        btnCerrar = new JButton("Cerrar");
        btnCerrar.setBackground(new Color(170, 170, 170));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        
        panelBotones.add(btnReactivar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnCerrar);
        
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        
        // Configurar eventos de botones
        configurarEventos();
        
        // Agregar panel principal a la ventana
        setContentPane(panelPrincipal);
        
        // Cargar datos iniciales
        cargarProductosInactivos();
    }
    
    /**
     * Configura los eventos de los botones
     */
    private void configurarEventos() {
        // Botón Reactivar
        btnReactivar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reactivarProductoSeleccionado();
            }
        });
        
        // Botón Actualizar
        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarProductosInactivos();
            }
        });
        
        // Botón Cerrar
        btnCerrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    /**
     * Carga la lista de productos inactivos en la tabla
     */
    public void cargarProductosInactivos() {
        // Limpiar tabla
        modeloTabla.setRowCount(0);
        
        // Obtener productos inactivos
        ArrayList<String[]> productos = baseDatos.consultarProductosInactivos();
        
        if (productos != null && !productos.isEmpty()) {
            for (String[] producto : productos) {
                modeloTabla.addRow(producto);
            }
            lblTitulo.setText("Productos Desactivados (" + productos.size() + ")");
        } else {
            lblTitulo.setText("Productos Desactivados (0)");
            JOptionPane.showMessageDialog(this, 
                                        "No hay productos desactivados", 
                                        "Información", 
                                        JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Reactivar el producto seleccionado en la tabla
     */
    private void reactivarProductoSeleccionado() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, 
                                        "Por favor, seleccione un producto para reactivar", 
                                        "Selección requerida", 
                                        JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Obtener ID del producto seleccionado
        String idProducto = (String) tablaProductos.getValueAt(filaSeleccionada, 0);
        
        // Confirmar reactivación
        int confirmacion = JOptionPane.showConfirmDialog(this, 
                                                      "¿Está seguro de que desea reactivar el producto '" + 
                                                      tablaProductos.getValueAt(filaSeleccionada, 1) + "'?", 
                                                      "Confirmar reactivación", 
                                                      JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            // Reactivar producto
            if (baseDatos.reactivarProducto(idProducto)) {
                JOptionPane.showMessageDialog(this, 
                                           "Producto reactivado correctamente", 
                                           "Éxito", 
                                           JOptionPane.INFORMATION_MESSAGE);
                
                // Actualizar tabla
                cargarProductosInactivos();
            } else {
                JOptionPane.showMessageDialog(this, 
                                           "Error al reactivar el producto", 
                                           "Error", 
                                           JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Método principal para probar la vista
     */
    
}
