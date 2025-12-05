package Vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.toedter.calendar.JDateChooser;

public class VistaReportes extends JInternalFrame {
    
    private JComboBox<String> comboTipoReporte;
    private JPanel panelConfiguracion;
    private JPanel panelProductos;
    private JPanel panelVentas;
    private JPanel panelCaja;
    private JPanel panelProductosMasVendidos;
    
    // Componentes para reporte de productos
    private JRadioButton rbTodosProductos;
    private JRadioButton rbSoloConStock;
    
    // Componentes para reporte de ventas
    private JDateChooser fechaInicioVentas;
    private JDateChooser fechaFinVentas;
    private JRadioButton rbTodasVentas;
    private JRadioButton rbSoloNoCanceladas;
    
    // Componentes para reporte de caja
    private JDateChooser fechaCaja;
    
    // Componentes para reporte de productos más vendidos
    private JDateChooser fechaInicioProductosMasVendidos;
    private JDateChooser fechaFinProductosMasVendidos;
    private JTextField txtLimiteProductos;
    
    // Botones de acción
    private JButton btnGenerar;
    private JButton btnCancelar;
    
    public VistaReportes() {
        super("Generación de Reportes", true, true, true, true);
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        
        // Panel superior para selección de tipo de reporte
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Tipo de Reporte"));
        
        JLabel lblTipoReporte = new JLabel("Seleccione el tipo de reporte:");
        comboTipoReporte = new JComboBox<>(new String[] {
            "Listado de Productos", 
            "Reporte de Ventas", 
            "Reporte de Caja",
            "Productos Más Vendidos"
        });
        
        panelSuperior.add(lblTipoReporte);
        panelSuperior.add(comboTipoReporte);
        
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central para configuraciones específicas de cada reporte
        panelConfiguracion = new JPanel(new BorderLayout());
        panelConfiguracion.setBorder(BorderFactory.createTitledBorder("Configuración del Reporte"));
        
        // Inicializar los paneles para cada tipo de reporte
        inicializarPanelProductos();
        inicializarPanelVentas();
        inicializarPanelCaja();
        inicializarPanelProductosMasVendidos();
        
        // Por defecto, mostrar el panel de productos
        panelConfiguracion.add(panelProductos, BorderLayout.CENTER);
        
        add(panelConfiguracion, BorderLayout.CENTER);
        
        // Panel inferior para botones de acción
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnGenerar = new JButton("Generar Reporte");
        btnCancelar = new JButton("Cancelar");
        
        panelInferior.add(btnGenerar);
        panelInferior.add(btnCancelar);
        
        add(panelInferior, BorderLayout.SOUTH);
        
        // Configurar el tamaño del frame
        setSize(500, 350);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setVisible(true);
            
            // Asegurar que todos los componentes se actualicen y muestren correctamente
            revalidate();
            repaint();
    }
    
    private void inicializarPanelProductos() {
        panelProductos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Opciones para filtrar productos
        JLabel lblFiltro = new JLabel("Filtrar productos:");
        rbTodosProductos = new JRadioButton("Todos los productos");
        rbSoloConStock = new JRadioButton("Solo productos con stock");
        
        // Agrupar los radio buttons
        ButtonGroup grupoFiltro = new ButtonGroup();
        grupoFiltro.add(rbTodosProductos);
        grupoFiltro.add(rbSoloConStock);
        
        // Seleccionar por defecto "Todos los productos"
        rbTodosProductos.setSelected(true);
        
        // Añadir componentes al panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelProductos.add(lblFiltro, gbc);
        
        gbc.gridy = 1;
        panelProductos.add(rbTodosProductos, gbc);
        
        gbc.gridy = 2;
        panelProductos.add(rbSoloConStock, gbc);
    }
    
    private void inicializarPanelVentas() {
        panelVentas = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Campos para seleccionar el rango de fechas
        JLabel lblPeriodo = new JLabel("Seleccione el período:");
        JLabel lblFechaInicio = new JLabel("Fecha Inicio:");
        JLabel lblFechaFin = new JLabel("Fecha Fin:");
        
        fechaInicioVentas = new JDateChooser();
        fechaInicioVentas.setDate(new Date()); // Fecha actual por defecto
        
        fechaFinVentas = new JDateChooser();
        fechaFinVentas.setDate(new Date()); // Fecha actual por defecto
        
        // Opciones para filtrar ventas
        JLabel lblFiltroVentas = new JLabel("Tipo de ventas:");
        rbTodasVentas = new JRadioButton("Todas las ventas");
        rbSoloNoCanceladas = new JRadioButton("Solo ventas no canceladas");
        
        // Agrupar los radio buttons
        ButtonGroup grupoFiltroVentas = new ButtonGroup();
        grupoFiltroVentas.add(rbTodasVentas);
        grupoFiltroVentas.add(rbSoloNoCanceladas);
        
        // Seleccionar por defecto "Todas las ventas"
        rbTodasVentas.setSelected(true);
        
        // Añadir componentes al panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panelVentas.add(lblPeriodo, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panelVentas.add(lblFechaInicio, gbc);
        
        gbc.gridx = 1;
        panelVentas.add(fechaInicioVentas, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelVentas.add(lblFechaFin, gbc);
        
        gbc.gridx = 1;
        panelVentas.add(fechaFinVentas, gbc);
        
        // Añadir filtro de ventas
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panelVentas.add(lblFiltroVentas, gbc);
        
        gbc.gridwidth = 2;
        gbc.gridy = 4;
        panelVentas.add(rbTodasVentas, gbc);
        
        gbc.gridy = 5;
        panelVentas.add(rbSoloNoCanceladas, gbc);
    }
    
    private void inicializarPanelProductosMasVendidos() {
        panelProductosMasVendidos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Campos para seleccionar el rango de fechas
        JLabel lblPeriodo = new JLabel("Seleccione el período:");
        JLabel lblFechaInicio = new JLabel("Fecha Inicio:");
        JLabel lblFechaFin = new JLabel("Fecha Fin:");
        
        fechaInicioProductosMasVendidos = new JDateChooser();
        fechaInicioProductosMasVendidos.setDate(new Date()); // Fecha actual por defecto
        
        fechaFinProductosMasVendidos = new JDateChooser();
        fechaFinProductosMasVendidos.setDate(new Date()); // Fecha actual por defecto
        
        // Campo para límite de productos a mostrar
        JLabel lblLimiteProductos = new JLabel("Mostrar top productos:");
        txtLimiteProductos = new JTextField("10", 5); // Por defecto 10 productos
        
        // Añadir componentes al panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panelProductosMasVendidos.add(lblPeriodo, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panelProductosMasVendidos.add(lblFechaInicio, gbc);
        
        gbc.gridx = 1;
        panelProductosMasVendidos.add(fechaInicioProductosMasVendidos, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelProductosMasVendidos.add(lblFechaFin, gbc);
        
        gbc.gridx = 1;
        panelProductosMasVendidos.add(fechaFinProductosMasVendidos, gbc);
        
        // Añadir límite de productos
        gbc.gridx = 0;
        gbc.gridy = 3;
        panelProductosMasVendidos.add(lblLimiteProductos, gbc);
        
        gbc.gridx = 1;
        panelProductosMasVendidos.add(txtLimiteProductos, gbc);
    }
    
    private void inicializarPanelCaja() {
        panelCaja = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Campo para seleccionar la fecha
        JLabel lblFecha = new JLabel("Seleccione la fecha:");
        
        fechaCaja = new JDateChooser();
        fechaCaja.setDate(new Date()); // Fecha actual por defecto
        
        // Añadir componentes al panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelCaja.add(lblFecha, gbc);
        
        gbc.gridy = 1;
        panelCaja.add(fechaCaja, gbc);
    }
    
    // Método para cambiar el panel de configuración según el tipo de reporte seleccionado
    public void mostrarPanelConfiguracion(String tipoReporte) {
        panelConfiguracion.removeAll();
        
        switch (tipoReporte) {
            case "Listado de Productos":
                panelConfiguracion.add(panelProductos, BorderLayout.CENTER);
                break;
            case "Reporte de Ventas":
                panelConfiguracion.add(panelVentas, BorderLayout.CENTER);
                break;
            case "Reporte de Caja":
                panelConfiguracion.add(panelCaja, BorderLayout.CENTER);
                break;
            case "Productos Más Vendidos":
                panelConfiguracion.add(panelProductosMasVendidos, BorderLayout.CENTER);
                break;
        }
        
        panelConfiguracion.revalidate();
        panelConfiguracion.repaint();
    }
    
    // Getters para los componentes
    
    public JComboBox<String> getComboTipoReporte() {
        return comboTipoReporte;
    }
    
    public JRadioButton getRbTodosProductos() {
        return rbTodosProductos;
    }
    
    public JRadioButton getRbSoloConStock() {
        return rbSoloConStock;
    }
    
    public JDateChooser getFechaInicioVentas() {
        return fechaInicioVentas;
    }
    
    public JDateChooser getFechaFinVentas() {
        return fechaFinVentas;
    }
    
    public JDateChooser getFechaCaja() {
        return fechaCaja;
    }
    
    public JButton getBtnGenerar() {
        return btnGenerar;
    }
    
    public JButton getBtnCancelar() {
        return btnCancelar;
    }
    
    public JRadioButton getRbTodasVentas() {
        return rbTodasVentas;
    }
    
    public JRadioButton getRbSoloNoCanceladas() {
        return rbSoloNoCanceladas;
    }
    
    public JDateChooser getFechaInicioProductosMasVendidos() {
        return fechaInicioProductosMasVendidos;
    }
    
    public JDateChooser getFechaFinProductosMasVendidos() {
        return fechaFinProductosMasVendidos;
    }
    
    public JTextField getTxtLimiteProductos() {
        return txtLimiteProductos;
    }
}
