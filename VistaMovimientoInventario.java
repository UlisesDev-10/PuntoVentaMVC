package Vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import Modelo.Producto;

/**
 * Ventana para registrar movimientos de inventario (entradas y salidas)
 */
public class VistaMovimientoInventario extends JDialog {
    
    private JLabel lblTitulo;
    private JLabel lblProducto;
    private JLabel lblCantidad;
    private JLabel lblReferencia;
    
    private JComboBox<String> cmbProductos;
    private JTextField txtCantidad;
    private JTextField txtReferencia;
    
    private JButton btnConfirmar;
    private JButton btnCancelar;
    
    private List<Producto> listaProductos;
    private ActionListener confirmarListener;
    private ActionListener cancelarListener;
    
    /**
     * Constructor de la vista
     * @param parent Ventana padre
     * @param titulo Título de la ventana (Entrada/Salida)
     * @param productos Lista de productos disponibles
     */
    public VistaMovimientoInventario(Window parent, String titulo, List<Producto> productos) {
        super(parent, titulo, ModalityType.APPLICATION_MODAL);
        this.listaProductos = productos;
        
        // Configurar ventana
        setSize(500, 350);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        // Inicializar componentes
        inicializarComponentes(titulo);
        
        // Cargar productos en el combo
        cargarProductos();
    }
    
    /**
     * Inicializa los componentes de la interfaz
     */
    private void inicializarComponentes(String titulo) {
        // Panel principal con padding
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Título
        lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblTitulo, BorderLayout.NORTH);
        
        // Panel de contenido
        JPanel panelContenido = new JPanel(new GridLayout(3, 2, 10, 15));
        panelContenido.setBorder(new TitledBorder("Información del Movimiento"));
        
        // Producto
        lblProducto = new JLabel("Seleccione Producto:");
        cmbProductos = new JComboBox<>();
        panelContenido.add(lblProducto);
        panelContenido.add(cmbProductos);
        
        // Cantidad
        lblCantidad = new JLabel("Cantidad:");
        txtCantidad = new JTextField("1");
        panelContenido.add(lblCantidad);
        panelContenido.add(txtCantidad);
        
        // Referencia
        lblReferencia = new JLabel("Referencia/Motivo:");
        txtReferencia = new JTextField();
        panelContenido.add(lblReferencia);
        panelContenido.add(txtReferencia);
        
        panel.add(panelContenido, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        btnConfirmar = new JButton("Confirmar");
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
     * Obtiene la cantidad ingresada
     */
    public String getCantidad() {
        return txtCantidad.getText().trim();
    }
    
    /**
     * Obtiene la referencia o motivo
     */
    public String getReferencia() {
        return txtReferencia.getText().trim();
    }
}
