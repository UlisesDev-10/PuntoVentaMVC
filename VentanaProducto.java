package Vista;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Ventana para gestión de productos
 */
public class VentanaProducto extends JInternalFrame {
    
    private JTable tablaProductos;
    private JButton btnAgregar;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnBuscar;
    private JButton btnBuscarNombre;
    private JButton btnBuscarId;
    private JButton btnSalida;
    private JButton btnGestionarInactivos;
    private JTextField txtBuscar;
    private JLabel lblBuscar; // Etiqueta para campo de búsqueda
    private JTextPane txtDescripcion;
    private DecimalFormat formatoMoneda = new DecimalFormat("#,##0.00");
    
    /**
     * Constructor para la ventana de productos
     */
    public VentanaProducto() {
        super("Gestión de Productos", true, true, true, true);
        setSize(900, 600);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        
        // Panel principal con borde para mejor espaciado
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(panelPrincipal);
        
        // Panel superior para búsqueda y filtrado
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 0));
        panelSuperior.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Panel para el campo de búsqueda
        JPanel panelBusqueda = new JPanel(new BorderLayout(5, 0));
        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Arial", Font.BOLD, 14));
        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Arial", Font.PLAIN, 14));
        btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(new Color(23, 162, 184)); // Cian
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFont(new Font("Arial", Font.BOLD, 14));
        btnBuscar.setFocusPainted(false);
        
        // Botones adicionales para compatibilidad con ControladorInventario
        btnBuscarNombre = new JButton("Buscar por Nombre");
        btnBuscarNombre.setBackground(new Color(23, 162, 184));
        btnBuscarNombre.setForeground(Color.WHITE);
        btnBuscarNombre.setFont(new Font("Arial", Font.BOLD, 14));
        
        btnBuscarId = new JButton("Buscar por ID");
        btnBuscarId.setBackground(new Color(23, 162, 184));
        btnBuscarId.setForeground(Color.WHITE);
        btnBuscarId.setFont(new Font("Arial", Font.BOLD, 14));
        
        btnSalida = new JButton("Salir");
        btnSalida.setBackground(new Color(108, 117, 125));
        btnSalida.setForeground(Color.WHITE);
        btnSalida.setFont(new Font("Arial", Font.BOLD, 14));
        btnSalida.setFocusPainted(false);
        btnSalida.setBorderPainted(false);
        btnSalida.setPreferredSize(new Dimension(120, 35));
        
        panelBusqueda.add(lblBuscar, BorderLayout.WEST);
        panelBusqueda.add(txtBuscar, BorderLayout.CENTER);
        panelBusqueda.add(btnBuscar, BorderLayout.EAST);
        
        // Añadir panel de búsqueda al panel superior
        panelSuperior.add(panelBusqueda, BorderLayout.CENTER);
        
        // Panel central para la tabla
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Listado de Productos", 
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
            javax.swing.border.TitledBorder.DEFAULT_POSITION, 
            new Font("Arial", Font.BOLD, 14)));
        
        // Configuración de la tabla
        tablaProductos = new JTable();
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.setRowHeight(25);
        tablaProductos.setFont(new Font("Arial", Font.PLAIN, 14));
        tablaProductos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        // Panel de descripción para detalles del producto
        JPanel panelDescripcion = new JPanel(new BorderLayout());
        panelDescripcion.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Detalles del Producto", 
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
            javax.swing.border.TitledBorder.DEFAULT_POSITION, 
            new Font("Arial", Font.BOLD, 14)));
            
        txtDescripcion = new JTextPane();
        txtDescripcion.setContentType("text/html");
        txtDescripcion.setEditable(false);
        txtDescripcion.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        scrollDescripcion.setPreferredSize(new Dimension(250, 0));
        panelDescripcion.add(scrollDescripcion, BorderLayout.CENTER);
        
        // Configurar el modelo de la tabla con columnas
        DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "SKU", "Producto", "Categoría", "Precio", "Marca", "Stock", "Descripción","Proveedores"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer todas las celdas no editables
            }
        };
        
        tablaProductos.setModel(modeloTabla);
        
        // Scroll pane para la tabla
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        panelTabla.add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior para los botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        // Crear botones de acción
        btnAgregar = crearBoton("Agregar", new Color(40, 167, 69)); // Verde
        btnEditar = crearBoton("Editar", new Color(0, 123, 255)); // Azul
        btnEliminar = crearBoton("Eliminar", new Color(220, 53, 69)); // Rojo
        
        // Crear botón para gestionar productos inactivos
        btnGestionarInactivos = new JButton("Gestionar Productos Inactivos");
        btnGestionarInactivos.setBackground(new Color(255, 193, 7)); // Color amarillo/naranja
        btnGestionarInactivos.setForeground(Color.BLACK);
        btnGestionarInactivos.setFont(new Font("Arial", Font.BOLD, 14));
        btnGestionarInactivos.setFocusPainted(false);
        btnGestionarInactivos.setBorderPainted(false);
        btnGestionarInactivos.setPreferredSize(new Dimension(220, 35));
        
        // Añadir botones al panel
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnGestionarInactivos);
        
        // Añadir todos los paneles al panel principal
        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        
        // Crear panel para tabla y descripción
        JPanel panelCentral = new JPanel(new BorderLayout(10, 0));
        panelCentral.add(panelTabla, BorderLayout.CENTER);
        panelCentral.add(panelDescripcion, BorderLayout.EAST);
        
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        
        // Añadir btnSalida al panel de botones
        panelBotones.add(btnSalida);
        
        // Configuración final
        setVisible(true);
    }
    
    /**
     * Crea un botón con estilo personalizado
     * @param texto Texto del botón
     * @param color Color de fondo
     * @return Botón configurado
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
     * Obtiene la tabla de productos
     * @return La tabla de productos
     */
    public JTable getTablaProductos() {
        return tablaProductos;
    }
    
    /**
     * Obtiene el botón para agregar productos
     * @return Botón para agregar
     */
    public JButton getBtnAgregar() {
        return btnAgregar;
    }
    
    /**
     * Obtiene el botón para editar productos
     * @return Botón para editar
     */
    public JButton getBtnEditar() {
        return btnEditar;
    }
    
    /**
     * Obtiene el botón para eliminar productos
     * @return Botón para eliminar
     */
    public JButton getBtnEliminar() {
        return btnEliminar;
    }
    
    /**
     * Obtiene el botón para buscar productos
     * @return Botón para buscar
     */
    public JButton getBtnBuscar() {
        return btnBuscar;
    }
    
    /**
     * Obtiene el campo de texto para la búsqueda
     * @return Campo de texto para búsqueda
     */
    public JTextField getTxtBuscar() {
        return txtBuscar;
    }
    
    /**
     * Establece el modelo de la tabla
     * @param modelo Modelo para la tabla
     */
    public void setModeloTabla(DefaultTableModel modelo) {
        tablaProductos.setModel(modelo);
    }
    
    /**
     * Formatea un valor monetario
     * @param valor Valor a formatear
     * @return Cadena formateada como moneda
     */
    public String formatearMoneda(double valor) {
        return formatoMoneda.format(valor);
    }
    
    /**
     * Métodos de compatibilidad con ControladorInventario
     */
    
    /**
     * Obtiene la tabla de datos (método para compatibilidad con ControladorInventario)
     * @return La tabla de productos
     */
    public JTable getTdatos() {
        return tablaProductos;
    }
    
    /**
     * Obtiene el botón de salida (método para compatibilidad con ControladorInventario)
     * @return Botón de salida
     */
    public JButton getBsalida() {
        return btnSalida;
    }
    
    /**
     * Obtiene el campo de búsqueda (método para compatibilidad con ControladorInventario)
     * @return Campo de texto para búsqueda
     */
    public JTextField getTbuscar() {
        return txtBuscar;
    }
    
    /**
     * Obtiene el botón para buscar por nombre (para ControladorInventario)
     * @return Botón de búsqueda por nombre
     */
    public JButton getBbuscarNombre() {
        return btnBuscarNombre;
    }
    
    /**
     * Obtiene el botón para buscar por ID (para ControladorInventario)
     * @return Botón de búsqueda por ID
     */
    public JButton getBbuscarId() {
        return btnBuscarId;
    }
    
    /**
     * Obtiene el panel de descripción (para ControladorInventario)
     * @return Panel de descripción
     */
    public JTextPane getTdescripcion() {
        return txtDescripcion;
    }
    
    /**
     * Métodos alternativos para compatibilidad con la antigua VentanaVista
     */
     
    /**
     * Obtiene el botón para agregar (compatibilidad con nombre en VentanaVista)
     * @return Botón para agregar
     */
    public JButton getBagregar() {
        return btnAgregar;
    }
    
    /**
     * Obtiene el botón para eliminar (compatibilidad con nombre en VentanaVista)
     * @return Botón para eliminar
     */
    public JButton getBeliminar() {
        return btnEliminar;
    }
    
    /**
     * Obtiene el botón para modificar (compatibilidad con nombre en VentanaVista)
     * @return Botón para modificar
     */
    public JButton getBmodificar() {
        return btnEditar;
    }
    
    /**
     * Obtiene el botón para gestionar productos inactivos
     * @return Botón para gestionar productos inactivos
     */
    public JButton getBgestionarInactivos() {
        return btnGestionarInactivos;
    }
}
