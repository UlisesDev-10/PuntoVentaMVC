package Vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import Modelo.Producto;

/**
 * Ventana para realizar ajustes manuales de inventario
 */
public class VistaAjusteInventario extends JDialog {
    
    private JLabel lblTitulo;
    private JLabel lblProducto;
    private JLabel lblStockActual;
    private JLabel lblNuevoStock;
    private JLabel lblMotivo;
    
    private JComboBox<String> cmbProductos;
    private JTextField txtStockActual;
    private JTextField txtNuevoStock;
    private JTextArea txtMotivo;
    
    private JButton btnConfirmar;
    private JButton btnCancelar;
    
    private List<Producto> listaProductos;
    private ActionListener confirmarListener;
    private ActionListener cancelarListener;
    
    /**
     * Constructor de la vista
     * @param parent Ventana padre
     * @param productos Lista de productos disponibles
     */
    public VistaAjusteInventario(Window parent, List<Producto> productos) {
        super(parent, "Ajuste Manual de Inventario", ModalityType.APPLICATION_MODAL);
        this.listaProductos = productos;
        
        // Configurar ventana
        setSize(550, 450);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        // Inicializar componentes
        inicializarComponentes();
        
        // Cargar productos en el combo
        cargarProductos();
        
        // Añadir listener al combo para mostrar stock actual
        cmbProductos.addActionListener(e -> actualizarStockActual());
    }
    
    /**
     * Inicializa los componentes de la interfaz
     */
    private void inicializarComponentes() {
        // Panel principal con padding
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Título
        lblTitulo = new JLabel("Ajuste Manual de Inventario");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblTitulo, BorderLayout.NORTH);
        
        // Panel de contenido
        JPanel panelContenido = new JPanel(new GridLayout(4, 2, 10, 15));
        panelContenido.setBorder(new TitledBorder("Información del Ajuste"));
        
        // Producto
        lblProducto = new JLabel("Seleccione Producto:");
        cmbProductos = new JComboBox<>();
        panelContenido.add(lblProducto);
        panelContenido.add(cmbProductos);
        
        // Stock Actual
        lblStockActual = new JLabel("Stock Actual:");
        txtStockActual = new JTextField();
        txtStockActual.setEditable(false);
        txtStockActual.setBackground(new Color(240, 240, 240));
        panelContenido.add(lblStockActual);
        panelContenido.add(txtStockActual);
        
        // Nuevo Stock
        lblNuevoStock = new JLabel("Nuevo Stock:");
        txtNuevoStock = new JTextField();
        panelContenido.add(lblNuevoStock);
        panelContenido.add(txtNuevoStock);
        
        // Motivo
        lblMotivo = new JLabel("Motivo del Ajuste:");
        txtMotivo = new JTextArea();
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);
        JScrollPane scrollMotivo = new JScrollPane(txtMotivo);
        scrollMotivo.setPreferredSize(new Dimension(200, 80));
        
        panelContenido.add(lblMotivo);
        panelContenido.add(scrollMotivo);
        
        panel.add(panelContenido, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        btnConfirmar = new JButton("Confirmar Ajuste");
        btnCancelar = new JButton("Cancelar");
        
        // Agregar acciones predeterminadas
        btnConfirmar.addActionListener(e -> {
            if (confirmarListener != null) {
                confirmarListener.actionPerformed(e);
            }
        });
        
        btnCancelar.addActionListener(e -> {
            if (cancelarListener != null) {
                cancelarListener.actionPerformed(e);
            } else {
                dispose();
            }
        });
        
        panelBotones.add(btnConfirmar);
        panelBotones.add(btnCancelar);
        
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        // Agregar panel a la ventana
        setContentPane(panel);
    }
    
    /**
     * Carga la lista de productos en el combo
     */
    private void cargarProductos() {
        cmbProductos.removeAllItems();
        
        // Agregar un elemento inicial
        cmbProductos.addItem("- Seleccione un producto -");
        
        // Agregar cada producto
        if (listaProductos != null) {
            for (Producto p : listaProductos) {
                String itemTexto = p.getIdProducto() + " - " + p.getProducto() + 
                                 " (Stock: " + p.getStock() + ")";
                cmbProductos.addItem(itemTexto);
            }
        }
    }
    
    /**
     * Actualiza el campo de stock actual según el producto seleccionado
     */
    private void actualizarStockActual() {
        int selectedIndex = cmbProductos.getSelectedIndex();
        if (selectedIndex <= 0 || selectedIndex > listaProductos.size()) {
            txtStockActual.setText("");
            txtNuevoStock.setText("");
            return;
        }
        
        // Restamos 1 porque el primer item es "Seleccione un producto"
        Producto producto = listaProductos.get(selectedIndex - 1);
        String stockActual = String.valueOf(producto.getStock());
        
        txtStockActual.setText(stockActual);
        txtNuevoStock.setText(stockActual); // Sugerimos el mismo valor inicial
    }
    
    /**
     * Establece el listener para el botón confirmar
     */
    public void setConfirmarListener(ActionListener listener) {
        this.confirmarListener = listener;
    }
    
    /**
     * Establece el listener para el botón cancelar
     */
    public void setCancelarListener(ActionListener listener) {
        this.cancelarListener = listener;
    }
    
    /**
     * Obtiene el ID del producto seleccionado
     */
    public String getIdProductoSeleccionado() {
        int selectedIndex = cmbProductos.getSelectedIndex();
        if (selectedIndex <= 0 || selectedIndex > listaProductos.size()) {
            return null;
        }
        
        // Restamos 1 porque el primer item es "Seleccione un producto"
        return listaProductos.get(selectedIndex - 1).getIdProducto();
    }
    
    /**
     * Obtiene el nuevo stock ingresado
     */
    public String getNuevoStock() {
        return txtNuevoStock.getText().trim();
    }
    
    /**
     * Obtiene el motivo del ajuste
     */
    public String getMotivo() {
        return txtMotivo.getText().trim();
    }
}
