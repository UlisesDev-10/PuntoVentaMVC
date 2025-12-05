package Vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import Modelo.Producto;

/**
 * Ventana para visualizar reportes de inventario
 */
public class VistaReporteInventario extends JDialog {
    
    private JLabel lblTitulo;
    private JTabbedPane tabbedPane;
    private JTable tablaStockBajo;
    private JTable tablaValoracion;
    private JPanel panelGrafico;
    private JButton btnExportar;
    private JButton btnCerrar;
    
    private List<Producto> listaProductos;
    private DecimalFormat formatoMoneda = new DecimalFormat("#,##0.00");
    
    /**
     * Constructor de la vista
     * @param parent Ventana padre
     * @param productos Lista de productos disponibles
     */
    public VistaReporteInventario(Window parent, List<Producto> productos) {
        super(parent, "Reportes de Inventario", ModalityType.APPLICATION_MODAL);
        this.listaProductos = productos;
        
        // Configurar ventana
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setResizable(true);
        
        // Inicializar componentes
        inicializarComponentes();
        
        // Cargar datos en las tablas
        cargarDatosReportes();
    }
    
    /**
     * Inicializa los componentes de la interfaz
     */
    private void inicializarComponentes() {
        // Panel principal con padding
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Título
        lblTitulo = new JLabel("Reportes de Inventario");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblTitulo, BorderLayout.NORTH);
        
        // Panel de pestañas
        tabbedPane = new JTabbedPane();
        
        // Pestaña de Productos con Stock Bajo
        JPanel panelStockBajo = new JPanel(new BorderLayout(5, 5));
        panelStockBajo.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Tabla de stock bajo
        String[] columnasStockBajo = {"ID", "SKU", "Producto", "Stock Actual", "Stock Mínimo", "Estado"};
        DefaultTableModel modeloStockBajo = new DefaultTableModel(columnasStockBajo, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaStockBajo = new JTable(modeloStockBajo);
        JScrollPane scrollStockBajo = new JScrollPane(tablaStockBajo);
        panelStockBajo.add(scrollStockBajo, BorderLayout.CENTER);
        
        // Pestaña de Valoración de Inventario
        JPanel panelValoracion = new JPanel(new BorderLayout(5, 5));
        panelValoracion.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Tabla de valoración
        String[] columnasValoracion = {"ID", "Producto", "Stock", "Precio Unitario", "Valor Total"};
        DefaultTableModel modeloValoracion = new DefaultTableModel(columnasValoracion, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaValoracion = new JTable(modeloValoracion);
        JScrollPane scrollValoracion = new JScrollPane(tablaValoracion);
        panelValoracion.add(scrollValoracion, BorderLayout.CENTER);
        
        // Pestaña de Gráfico (simulado)
        panelGrafico = new JPanel();
        panelGrafico.setBorder(new TitledBorder("Distribución de Inventario por Categoría"));
        panelGrafico.setLayout(new BorderLayout());
        
        JLabel lblGraficoSimulado = new JLabel("Gráfico de Distribución (simulado)", JLabel.CENTER);
        lblGraficoSimulado.setFont(new Font("Arial", Font.PLAIN, 16));
        lblGraficoSimulado.setPreferredSize(new Dimension(600, 400));
        lblGraficoSimulado.setBackground(new Color(245, 245, 245));
        lblGraficoSimulado.setOpaque(true);
        
        panelGrafico.add(lblGraficoSimulado, BorderLayout.CENTER);
        
        // Agregar pestañas
        tabbedPane.addTab("Productos con Stock Bajo", panelStockBajo);
        tabbedPane.addTab("Valoración de Inventario", panelValoracion);
        tabbedPane.addTab("Gráfico por Categoría", panelGrafico);
        
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        btnExportar = new JButton("Exportar Reporte");
        btnCerrar = new JButton("Cerrar");
        
        // Agregar acciones
        btnExportar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                    "Funcionalidad de exportación no implementada.",
                    "Información", 
                    JOptionPane.INFORMATION_MESSAGE);
        });
        
        btnCerrar.addActionListener(e -> dispose());
        
        panelBotones.add(btnExportar);
        panelBotones.add(btnCerrar);
        
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        // Agregar panel a la ventana
        setContentPane(panel);
    }
    
    /**
     * Carga los datos en las tablas de reportes
     */
    private void cargarDatosReportes() {
        if (listaProductos == null || listaProductos.isEmpty()) {
            return;
        }
        
        // Obtener modelos de las tablas
        DefaultTableModel modeloStockBajo = (DefaultTableModel) tablaStockBajo.getModel();
        DefaultTableModel modeloValoracion = (DefaultTableModel) tablaValoracion.getModel();
        
        // Limpiar tablas
        modeloStockBajo.setRowCount(0);
        modeloValoracion.setRowCount(0);
        
        // Definir stock mínimo (podría venir de la configuración)
        int stockMinimo = 5;
        
        // Variables para valor total del inventario
        double valorTotalInventario = 0;
        
        // Cargar datos en las tablas
        for (Producto p : listaProductos) {
            int stockActual = p.getStock();
            double precioUnitario = p.getPrecioVenta();
            double valorTotal = stockActual * precioUnitario;
            
            // Agregar a la tabla de valoración
            modeloValoracion.addRow(new Object[]{
                p.getIdProducto(),
                p.getProducto(),
                stockActual,
                formatoMoneda.format(precioUnitario),
                formatoMoneda.format(valorTotal)
            });
            
            // Sumar al valor total
            valorTotalInventario += valorTotal;
            
            // Si tiene stock bajo, agregar a esa tabla
            if (stockActual <= stockMinimo) {
                String estado = (stockActual == 0) ? "Sin Stock" : "Stock Bajo";
                
                modeloStockBajo.addRow(new Object[]{
                    p.getIdProducto(),
                    p.getSku(),
                    p.getProducto(),
                    stockActual,
                    stockMinimo,
                    estado
                });
            }
        }
        
        // Agregar fila con el total en la tabla de valoración
        modeloValoracion.addRow(new Object[]{
            "", "VALOR TOTAL DEL INVENTARIO", "", "", formatoMoneda.format(valorTotalInventario)
        });
        
        // Ajustar anchos de columnas
        ajustarAnchoColumnas();
    }
    
    /**
     * Ajusta el ancho de las columnas de las tablas
     */
    private void ajustarAnchoColumnas() {
        // Tabla de stock bajo
        tablaStockBajo.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tablaStockBajo.getColumnModel().getColumn(1).setPreferredWidth(80);  // SKU
        tablaStockBajo.getColumnModel().getColumn(2).setPreferredWidth(250); // Producto
        tablaStockBajo.getColumnModel().getColumn(3).setPreferredWidth(80);  // Stock Actual
        tablaStockBajo.getColumnModel().getColumn(4).setPreferredWidth(80);  // Stock Mínimo
        tablaStockBajo.getColumnModel().getColumn(5).setPreferredWidth(80);  // Estado
        
        // Tabla de valoración
        tablaValoracion.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tablaValoracion.getColumnModel().getColumn(1).setPreferredWidth(250); // Producto
        tablaValoracion.getColumnModel().getColumn(2).setPreferredWidth(80);  // Stock
        tablaValoracion.getColumnModel().getColumn(3).setPreferredWidth(100); // Precio Unitario
        tablaValoracion.getColumnModel().getColumn(4).setPreferredWidth(100); // Valor Total
    }
}
