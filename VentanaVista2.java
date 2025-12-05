package Vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.awt.*;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Ventana principal para visualizar datos en una tabla
 */
public class VentanaVista2 extends JInternalFrame {
    
    private JTable tdatos;
    private JButton bsalid;
    private JButton bagregar;
    private JButton beliminar;
    private JButton bmodificar;
    private JButton bverInactivos;

    public VentanaVista2() {
        // Configuración del JInternalFrame
        super("Visualización de Datos", true, true, true, true);
        inicializarComponentes();
    }
    
    public VentanaVista2(String titulo) {
        // Configuración del JInternalFrame con título personalizado
        super(titulo, true, true, true, true);
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        setSize(800, 500);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

        // Panel principal con bordes para mejor espaciado
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(panelPrincipal);

        // Panel para la tabla con bordes
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createTitledBorder("Datos"));

        // Configuración de la tabla
        tdatos = new JTable();
        tdatos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tdatos.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tdatos.setRowHeight(25); // Altura de filas para mejor visualización
        tdatos.setFont(new Font("Arial", Font.PLAIN, 14));
        tdatos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        // Scroll pane para la tabla
        JScrollPane scrollPane = new JScrollPane(tdatos);
        scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        panelTabla.add(scrollPane, BorderLayout.CENTER);

        // Panel para los botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        // Crear y configurar botones con estilo
        bagregar = crearBoton("Agregar", new Color(40, 167, 69)); // Verde
        bmodificar = crearBoton("Modificar", new Color(0, 123, 255)); // Azul
        beliminar = crearBoton("Eliminar", new Color(220, 53, 69)); // Rojo
       
        bsalid = crearBoton("Salir", new Color(108, 117, 125)); // Gris

        // Añadir botones al panel
        panelBotones.add(bagregar);
        panelBotones.add(bmodificar);
        panelBotones.add(beliminar);
       
        panelBotones.add(bsalid);

        // Añadir paneles al panel principal
        panelPrincipal.add(panelTabla, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        // Configuración final
        setVisible(true);
    }

    /**
     * Crea un botón con estilo personalizado
     * @param texto Texto del botón
     * @param color Color de fondo del botón
     * @return El botón configurado
     */
    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setPreferredSize(new Dimension(120, 35));
        return boton;
    }

    /**
     * Establece el modelo de la tabla
     * @param modelo Modelo de tabla a establecer
     */
    public void setTablaModelo(DefaultTableModel modelo) {
        tdatos.setModel(modelo);
    }

    /**
     * Obtiene la tabla de datos
     * @return La tabla de datos
     */
    public JTable getTabla() {
        return tdatos;
    }

    /**
     * Obtiene la tabla de datos (método alternativo para compatibilidad)
     * @return La tabla de datos
     */
    public JTable getTdatos() {
        return tdatos;
    }

    /**
     * Obtiene el botón de salida
     * @return Botón de salida
     */
    public JButton getBsalid() {
        return bsalid;
    }

    /**
     * Obtiene el botón para agregar
     * @return Botón para agregar
     */
    public JButton getBagregar() {
        return bagregar;
    }

    /**
     * Obtiene el botón para eliminar
     * @return Botón para eliminar
     */
    public JButton getBeliminar() {
        return beliminar;
    }

    /**
     * Obtiene el botón para modificar
     * @return Botón para modificar
     */
    public JButton getBmodificar() {
        return bmodificar;
    }
    
    /**
     * Obtiene el botón para ver elementos inactivos
     * @return Botón para ver inactivos
     */
   
}
