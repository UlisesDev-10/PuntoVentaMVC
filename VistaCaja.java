package Vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableModel;

public class VistaCaja extends JInternalFrame {
    
    private JButton bSalir, bAgregar, bFinalizar, bBuscar, bEliminar;
    private JTextField tCodigo, tCantidad, tSubtotal;
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private VistaCajaInicial cajaInicial; // Referencia a VistaCajaInicial
    private DecimalFormat formatoMoneda = new DecimalFormat("#,##0.00");
    private final int STOCK_MINIMO = 5; // Stock mínimo antes de mostrar alerta
    
    public VistaCaja(VistaCajaInicial cajaInicial) {
        super("Sistema de Caja", true, true, true, true);
        try {
            this.cajaInicial = cajaInicial; // Guardar referencia a VistaCajaInicial
            setTitle("Caja de Ventas");
            setResizable(true);
            setMaximizable(true);
            setIconifiable(true);
            setClosable(true);
            
            // Establecer dimensiones
            setSize(800, 600);
            setBounds(0, 0, 800, 500);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            
            // Inicializar componentes
            
            // Configurar comportamiento al cerrar para liberar recursos
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            
            // Agregar un listener para interceptar el cierre de la ventana
            this.addInternalFrameListener(new InternalFrameAdapter() {
                
                }
            );
            
            iniciarComponentes();
            
            // Inicializar totales en cero
            actualizarTotales(0.0);
            
        } catch (Exception e) {
            System.err.println("Error al inicializar VistaCaja: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void iniciarComponentes() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel superior con el título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(52, 152, 219));
        JLabel lblTitulo = new JLabel("PUNTO DE VENTA - CAJA");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central con entrada de productos y tabla
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de entrada de productos
        JPanel panelEntrada = new JPanel(new GridLayout(3, 2, 5, 5));
        panelEntrada.setBorder(BorderFactory.createTitledBorder("Agregar Producto"));
        
        JPanel panelCodigo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCodigo.add(new JLabel("Código: "));
        tCodigo = new JTextField(15);
        panelCodigo.add(tCodigo);
        bBuscar = new JButton("Buscar");
        panelCodigo.add(bBuscar);
        panelEntrada.add(panelCodigo);
        
        bEliminar = new JButton("Eliminar Producto");
        bEliminar.setBackground(new Color(255, 127, 80)); // Color naranja/coral
        bEliminar.setForeground(Color.WHITE);
        

        
        JPanel panelCantidad = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCantidad.add(new JLabel("Cantidad: "));
        tCantidad = new JTextField(5);
        tCantidad.setText("1");
        
        // Añadir documento listener para validar entrada numérica
        tCantidad.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                validarCantidad();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                validarCantidad();
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                validarCantidad();
            }
            
            private void validarCantidad() {
                try {
                    String textoActual = tCantidad.getText().trim();
                    // Solo validar si hay texto
                    if (!textoActual.isEmpty()) {
                        // Intentar convertir a entero para validar
                        Integer.parseInt(textoActual);
                    }
                } catch (NumberFormatException ex) {
                    // Si no es un número, limpiar el campo o restaurar valor anterior
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        tCantidad.setText("1");
                    });
                }
            }
        });
        
        panelCantidad.add(tCantidad);
        panelEntrada.add(panelCantidad);
        
        JPanel panelBotonAgregar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bAgregar = new JButton("Agregar a la venta");
        bAgregar.setBackground(new Color(46, 204, 113));
        bAgregar.setForeground(Color.WHITE);
        panelBotonAgregar.add(bAgregar);
        panelEntrada.add(panelBotonAgregar);
        
        panelCentral.add(panelEntrada, BorderLayout.NORTH);
        
        // Tabla de productos
        String[] columnas = {"Código", "Producto", "Precio", "Cantidad", "Subtotal"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaProductos = new JTable(modeloTabla);
        
        // Agregar un listener a la tabla para actualizar totales automáticamente
        modeloTabla.addTableModelListener(e -> {
            // Solo actualizar cuando se modifique la tabla, no durante la inicialización
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE || 
                e.getType() == javax.swing.event.TableModelEvent.INSERT ||
                e.getType() == javax.swing.event.TableModelEvent.DELETE) {
                // Calcular el subtotal desde la tabla
                calcularSubtotalDesdeTabla();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        scrollPane.setPreferredSize(new Dimension(750, 300));
        panelCentral.add(scrollPane, BorderLayout.CENTER);
        
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel inferior con total y botones
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel panelTotal = new JPanel(new GridLayout(1, 2, 5, 5));
        panelTotal.setBorder(BorderFactory.createTitledBorder("Resumen de Venta"));
        panelTotal.setPreferredSize(new Dimension(300, 80));
        panelTotal.setOpaque(true);
        panelTotal.setBackground(new Color(240, 240, 240));  // Fondo gris claro
        
        // Total (era el subtotal antes)
        JLabel lblSubtotal = new JLabel("TOTAL: $", SwingConstants.RIGHT);
        lblSubtotal.setFont(new Font("Arial", Font.BOLD, 16));
        tSubtotal = new JTextField("0.00");
        tSubtotal.setEditable(false);
        tSubtotal.setFont(new Font("Arial", Font.BOLD, 16));
        tSubtotal.setHorizontalAlignment(SwingConstants.RIGHT);
        tSubtotal.setPreferredSize(new Dimension(100, 25));
        tSubtotal.setOpaque(true);
        tSubtotal.setBackground(Color.WHITE);
        
        // Agregar componentes al panel
        panelTotal.add(lblSubtotal);
        panelTotal.add(tSubtotal);
        
        // Asegurar que el campo sea visible
        tSubtotal.setVisible(true);
        
        panelInferior.add(panelTotal, BorderLayout.NORTH);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        panelBotones.add(bEliminar);


        bFinalizar = new JButton("Finalizar Venta");
        bFinalizar.setBackground(new Color(52, 152, 219));
        bFinalizar.setForeground(Color.WHITE);
        bSalir = new JButton("Salir");
        bSalir.setBackground(new Color(231, 76, 60));
        bSalir.setForeground(Color.WHITE);
        
        panelBotones.add(bFinalizar);
        panelBotones.add(bSalir);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);
        
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    // Getters para los componentes
    public JButton getBSalir() {
        return bSalir;
    }
    
    public JButton getBAgregar() {
        return bAgregar;
    }
    
    public JButton getBFinalizar() {
        return bFinalizar;
    }
    
    public JButton getBBuscar() {
        return bBuscar;
    }
    
    public JButton getBEliminar() {
        return bEliminar;
    }
    
    
    
    public JTextField getTCodigo() {
        return tCodigo;
    }
    
    public JTextField getTCantidad() {
        return tCantidad;
    }
    
    public JTable getTablaProductos() {
        return tablaProductos;
    }
    
    public DefaultTableModel getModeloTabla() {
        return modeloTabla;
    }
    
    /**
     * Constructor alternativo sin parámetros para mantener compatibilidad
     */
    public VistaCaja() {
        this(null);
        System.out.println("ADVERTENCIA: Se ha inicializado VistaCaja sin referencia a VistaCajaInicial");
    }
    
    /**
     * Establece la referencia a VistaCajaInicial
     * @param cajaInicial Instancia de VistaCajaInicial
     */
    public void setCajaInicial(VistaCajaInicial cajaInicial) {
        this.cajaInicial = cajaInicial;
    }
    
    public VistaCajaInicial getCajaInicial() {
    		return cajaInicial;
    	}
    
    /**
     * Obtiene el campo de texto para el total
     * @return Campo de texto del total
     */
    public JTextField getTSubtotal() {
        return tSubtotal;
    }
    
    // El método getIvaTasa ha sido eliminado ya que no es necesario
    
	/**
     * Obtiene el fondo actual de la caja
     * @return Fondo actual en la caja o 0.0 si no está disponible
     */
    public double getFondoCaja() {
	        if (cajaInicial != null) {
	            try {
	                if (cajaInicial.isCajaAbierta()) {
	                    double fondo = cajaInicial.getFondoInicial();
	                    System.out.println("VistaCaja: Fondo obtenido correctamente: " + fondo);
	                    return fondo;
	                } else {
	                    System.out.println("VistaCaja: Caja no está abierta");
	                }
	            } catch (Exception e) {
	                System.err.println("VistaCaja: Error al obtener fondo: " + e.getMessage());
	                e.printStackTrace();
	            }
	        } else {
	            System.out.println("VistaCaja: cajaInicial es null");
	        }
	        
	        // Si llegamos aquí, usar un valor por defecto
	        double valorPredeterminado = 1000.0;
	        System.out.println("VistaCaja: Usando valor predeterminado de fondo: " + valorPredeterminado);
	        return valorPredeterminado; // Valor predeterminado seguro
    }
    
    /**
     * Actualiza el total en la interfaz
     * @param total El total de la venta
     */
    public void actualizarTotales(double total) {
        try {
            System.out.println("==== ACTUALIZANDO TOTAL ====");
            System.out.println("Total recibido: " + total);
            
            // Validar que el total no sea negativo
            if (total < 0) {
                System.out.println("Total negativo corregido a 0");
                total = 0;
            }
            
            // Redondear a 2 decimales (misma lógica que VistaTicket)
            total = Math.round(total * 100.0) / 100.0;
            
            System.out.println("Valor calculado: Total=" + total);
            
            // Verificar que el campo exista
            if (tSubtotal == null) {
                System.err.println("ERROR: El campo de total es NULL");
                return;
            }
            
            // Actualizar campo con formato de dos decimales (usando formatoMoneda)
            String totalStr = formatoMoneda.format(total);
            
            // Actualizar en el EDT para asegurar que se ejecute en el hilo de UI
            javax.swing.SwingUtilities.invokeLater(() -> {
                tSubtotal.setText(totalStr);
                
                // Hacer visible el campo si no lo está
                tSubtotal.setVisible(true);
                
                // Forzar repintado del campo
                tSubtotal.repaint();
                
                // Forzar actualización del contenedor padre
                if (tSubtotal.getParent() != null) {
                    tSubtotal.getParent().revalidate();
                    tSubtotal.getParent().repaint();
                }
            });
            
            System.out.println("Texto establecido: Total=" + totalStr);
            System.out.println("==== TOTAL ACTUALIZADO ====");
        } catch (Exception e) {
            System.err.println("Error al actualizar total: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Los métodos de cálculo de IVA han sido eliminados ya que no son necesarios
    
    /**
     * Método para pruebas - actualiza la interfaz con valores de prueba
     * Se puede llamar desde el controlador para verificar la visualización
     */
    public void actualizarTotalesPrueba() {
        System.out.println("Ejecutando prueba de actualización de total...");
        
        // Valor de prueba
        double totalPrueba = 1000.0;
        
        // Actualizar campo directamente usando formatoMoneda
        tSubtotal.setText(formatoMoneda.format(totalPrueba));
        
        // Forzar visibilidad
        tSubtotal.setVisible(true);
        
        // Forzar repintado
        tSubtotal.repaint();
        
        System.out.println("Prueba completada - Revisa si el valor aparece en la interfaz");
    }
    
    /**
     * Calcula el total desde la tabla de productos y actualiza el valor
     * Este método se puede llamar cada vez que se modifique la tabla para mantener
     * el total actualizado en tiempo real
     */
    public void calcularSubtotalDesdeTabla() {
        double total = 0.0;
        
        try {
            System.out.println("==== CALCULANDO TOTAL DESDE TABLA ====");
            
            // Verificar si la tabla está inicializada correctamente
            if (modeloTabla == null) {
                System.err.println("ERROR: modeloTabla es NULL");
                return;
            }
            
            // Recorrer todas las filas de la tabla para sumar subtotales
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                try {
                    // La columna del subtotal es la 4 (índice)
                    Object valorSubtotal = modeloTabla.getValueAt(i, 4);
                    
                    if (valorSubtotal != null) {
                        // Convertir el valor a Double de manera segura
                        String valorStr = valorSubtotal.toString()
                            .replace(",", "")
                            .replace("$", "")
                            .trim();
                        
                        if (!valorStr.isEmpty()) {
                            try {
                                double valor = Double.parseDouble(valorStr);
                                total += valor;
                                System.out.println("Fila " + i + ": Subtotal = " + valor);
                            } catch (NumberFormatException e) {
                                System.err.println("Error al convertir subtotal de fila " + i + ": " + valorStr);
                                // Intentar calcular el subtotal manualmente utilizando precio y cantidad
                                try {
                                    Object valorPrecio = modeloTabla.getValueAt(i, 2);
                                    Object valorCantidad = modeloTabla.getValueAt(i, 3);
                                    
                                    if (valorPrecio != null && valorCantidad != null) {
                                        String precioStr = valorPrecio.toString().replace(",", "").replace("$", "").trim();
                                        String cantidadStr = valorCantidad.toString().trim();
                                        
                                        double precio = Double.parseDouble(precioStr);
                                        int cantidad = Integer.parseInt(cantidadStr);
                                        
                                        double subtotalFila = precio * cantidad;
                                        total += subtotalFila;
                                        
                                        System.out.println("Fila " + i + ": Subtotal calculado manualmente = " + subtotalFila);
                                    }
                                } catch (Exception ex) {
                                    System.err.println("Error al calcular subtotal manualmente para fila " + i + ": " + ex.getMessage());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error procesando fila " + i + ": " + e.getMessage());
                }
            }
            
            System.out.println("Total calculado: " + total);
            
            // Actualizar el total con el valor calculado
            actualizarTotales(total);
            
        } catch (Exception e) {
            System.err.println("Error al calcular total desde tabla: " + e.getMessage());
            e.printStackTrace();
            
            // En caso de error, asegurar que el total muestre algo razonable
            actualizarTotales(0.0);
        }
    }
    public boolean eliminarProductoSeleccionado() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        
        if (filaSeleccionada >= 0) {
            // Eliminar la fila seleccionada
            modeloTabla.removeRow(filaSeleccionada);
            
            // Recalcular el subtotal
            calcularSubtotalDesdeTabla();
            
            return true;
        }
        
        return false;
    }

}