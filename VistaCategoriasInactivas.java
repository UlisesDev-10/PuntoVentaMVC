package Vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Ventana para visualizar las categorías inactivas
 */
public class VistaCategoriasInactivas extends JDialog {
    
    private JTable tabla;
    private DefaultTableModel modelo;
    private JButton btnCerrar;
    private JButton btnReactivar;
    
    /**
     * Constructor de la ventana
     * @param parent Ventana padre
     */
    public VistaCategoriasInactivas(JFrame parent) {
        super(parent, "Categorías Inactivas", true);
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Configurar la tabla
        String[] columnas = {"ID", "Categoría"};
        modelo = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(25);
        tabla.setFont(new Font("Arial", Font.PLAIN, 14));
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        btnReactivar = new JButton("Reactivar");
        btnReactivar.setBackground(new Color(40, 167, 69));
        btnReactivar.setForeground(Color.WHITE);
        btnReactivar.setFont(new Font("Arial", Font.BOLD, 14));
        btnReactivar.setFocusPainted(false);
        
        btnCerrar = new JButton("Cerrar");
        btnCerrar.setBackground(new Color(108, 117, 125));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCerrar.setFocusPainted(false);
        
        btnCerrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        panelBotones.add(btnReactivar);
        panelBotones.add(btnCerrar);
        
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        
        // Título superior
        JLabel lblTitulo = new JLabel("Listado de Categorías Inactivas");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        
        setContentPane(panelPrincipal);
    }
    
    /**
     * Carga los datos de categorías inactivas en la tabla
     * @param datos Lista de datos a cargar
     */
    public void cargarDatos(ArrayList<String[]> datos) {
        modelo.setRowCount(0);
        
        if (datos != null) {
            for (String[] fila : datos) {
                modelo.addRow(fila);
            }
        }
    }
    
    /**
     * Obtiene el botón de reactivar
     * @return Botón de reactivar
     */
    public JButton getBtnReactivar() {
        return btnReactivar;
    }
    
    /**
     * Obtiene la tabla de datos
     * @return La tabla de datos
     */
    public JTable getTabla() {
        return tabla;
    }
}
