package Vista;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.text.DecimalFormat;
import Vista.VistaCajaInicial;
import Controlador.ControladorCaja;

public class VistaPago extends JDialog {

    private static final long serialVersionUID = 1L;
    private JPanel panelPrincipal;
    private JTextField tTotal, tPagoCon, tCambio;
    private JButton bConfirmar, bCancelar;
    private JLabel lblMensaje;
    private JComboBox<String> cbTipoPago; // ComboBox para seleccionar tipo de pago
    private VistaCajaInicial vistaCajaInicial = null; // Referencia a la vista de caja inicial
    private VistaCajaInicial cajaInicial;
    private double total;
    private double pagoCon;
    private double cambio;
    private boolean fondoSuficiente = true;
    private int tipoPagoSeleccionado = 1;
    ControladorCaja controladorCaja;  // 1=Efectivo, 2=Tarjeta (por defecto Efectivo)
   
    private double fondoCajaDisponible; // Valor predeterminado alto para asegurar que hay cambio
    private boolean pagoConfirmado = false;
    private DecimalFormat formatoMoneda = new DecimalFormat("#,##0.00");
    private Controlador.ControladorVentas controladorVenta; // Referencia al controlador de ventas
    private Controlador.ControladorMenu controladorMenu; // Referencia al controlador del menú
    
    /**
     * Constructor para la ventana de pago
     */
    public VistaPago(java.awt.Frame parent, double totalVenta) {
        super(parent, "Pago", true);
        this.total = totalVenta;
        // Inicializar con un valor por defecto muy alto, para asegurar que hay fondos suficientes
        this.fondoCajaDisponible = 10.0; // Un valor muy alto predeterminado
        System.out.println("VistaPago inicializada con fondo predeterminado de $10000.00");
        
        setSize(400, 350); // Aumentar tamaño para agregar el ComboBox
        setLocationRelativeTo(parent);
        setResizable(false);
        
        // Panel principal
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());
        panelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(panelPrincipal);
        
        // Panel de campos
        JPanel panelCampos = new JPanel(new GridLayout(5, 2, 10, 10)); // 5 filas para agregar el combo
        
        // Total a pagar
        panelCampos.add(new JLabel("Total a pagar:", SwingConstants.RIGHT));
        tTotal = new JTextField();
        tTotal.setEditable(false);
        tTotal.setFont(new Font("Arial", Font.BOLD, 14));
        tTotal.setText(formatoMoneda.format(total));
        panelCampos.add(tTotal);
        
        // ComboBox para tipo de pago
        panelCampos.add(new JLabel("Tipo de Pago:", SwingConstants.RIGHT));
        cbTipoPago = new JComboBox<>(new String[]{"Efectivo", "Tarjeta"});
        cbTipoPago.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tipoPagoSeleccionado = cbTipoPago.getSelectedIndex() + 1; // 1=Efectivo, 2=Tarjeta
                actualizarInterfazSegunTipoPago();
                calcularCambio(); // Recalcular cambio al cambiar tipo de pago
            }
        });
        panelCampos.add(cbTipoPago);
        
        // Campo para que el cliente ingrese con cuánto paga
        panelCampos.add(new JLabel("Pago con:", SwingConstants.RIGHT));
        tPagoCon = new JTextField();
        tPagoCon.setFont(new Font("Arial", Font.BOLD, 14));
        tPagoCon.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calcularCambio();
            }
        });
        panelCampos.add(tPagoCon);
        
        // Campo para mostrar el cambio
        panelCampos.add(new JLabel("Cambio:", SwingConstants.RIGHT));
        tCambio = new JTextField();
        tCambio.setEditable(false);
        tCambio.setFont(new Font("Arial", Font.BOLD, 14));
        panelCampos.add(tCambio);
        
        // Mensaje informativo
        panelCampos.add(new JLabel("Estado:", SwingConstants.RIGHT));
        lblMensaje = new JLabel("Ingrese el monto con el que paga el cliente");
        panelCampos.add(lblMensaje);
        
        panelPrincipal.add(panelCampos, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        
        
        bConfirmar = new JButton("Confirmar");
        bConfirmar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (tPagoCon.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(VistaPago.this, 
                                "Ingrese el monto con el que paga el cliente", 
                                "Campo requerido", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Verificar cambio
                    calcularCambio();
                    
                    // Para tarjeta, sólo permitir pago exacto
                    if (tipoPagoSeleccionado == 2 && Math.abs(cambio) > 0.01) {
                        JOptionPane.showMessageDialog(VistaPago.this,
                                "Para pagos con tarjeta solo se permite pago exacto",
                                "Pago con tarjeta",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Verificación adicional: asegurar que el cambio sea no negativo
                    if (cambio < 0) {
                        JOptionPane.showMessageDialog(VistaPago.this,
                                "El pago es insuficiente. El cliente debe pagar al menos $" + formatoMoneda.format(total),
                                "Pago insuficiente",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Verificar si hay suficiente fondo para dar cambio (solo para efectivo)
                    if (tipoPagoSeleccionado == 1 && !verificarFondoSuficiente()) {
                        JOptionPane.showMessageDialog(VistaPago.this,
                                "No hay suficiente fondo en caja para dar el cambio de $" + formatoMoneda.format(cambio),
                                "Fondo insuficiente",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    System.out.println("VistaPago: CONFIRMANDO VENTA");
                    System.out.println("VistaPago: Total venta: $" + formatoMoneda.format(total));
                    System.out.println("VistaPago: Pago con: $" + formatoMoneda.format(pagoCon));
                    System.out.println("VistaPago: Cambio a dar: $" + formatoMoneda.format(cambio));
                    System.out.println("VistaPago: Tipo de pago: " + (tipoPagoSeleccionado == 1 ? "Efectivo" : "Tarjeta"));
                    
                    // Llamar al controlador de caja para registrar la venta si está disponible
                    if (controladorCaja != null) {
                  
                    } else {
                        System.out.println("VistaPago: No se pudo registrar la venta - controladorCaja es null");
                    }
                    
                    pagoConfirmado = true;
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(VistaPago.this,
                            "Error al procesar el pago: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            
            }
        });
        
        bCancelar = new JButton("Cancelar");
        bCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pagoConfirmado = false;
                dispose();
            }
        });
        
       
        panelBotones.add(bConfirmar);
        panelBotones.add(bCancelar);
        
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        
        // Iniciar con la interfaz configurada para el tipo de pago predeterminado (Efectivo)
        actualizarInterfazSegunTipoPago();
    }
    
    /**
     * Actualiza la interfaz según el tipo de pago seleccionado
     */
    private void actualizarInterfazSegunTipoPago() {
        if (tipoPagoSeleccionado == 2) { // Tarjeta
            // Para tarjeta, se requiere pago exacto y no hay cambio
            tPagoCon.setText(formatoMoneda.format(total));
            tPagoCon.setEditable(false);
            tCambio.setText("0.00");
            lblMensaje.setText("Pago con tarjeta - Monto exacto requerido");
        } else { // Efectivo
            tPagoCon.setEditable(true);
            if (!tPagoCon.getText().isEmpty()) {
                calcularCambio();
            } else {
                tCambio.setText("");
            }
            lblMensaje.setText("Ingrese el monto con el que paga el cliente");
        }
    }
    
    /**
     * Establece el total a pagar
     * @param total El total con IVA incluido
     */
    public void setTotal(double total) {
        // El total ya viene calculado correctamente, solo lo asignamos
        this.total = total;
        
        // Calcular el subtotal e IVA para mostrar el desglose
        double subtotal = total / 1.16; // Revertir el cálculo para obtener el subtotal
        subtotal = Math.round(subtotal * 100.0) / 100.0; // Redondear a 2 decimales
        double iva = total - subtotal;
        iva = Math.round(iva * 100.0) / 100.0; // Redondear a 2 decimales
        
        System.out.println("VistaPago: Total recibido con IVA: " + formatoMoneda.format(total));
        System.out.println("VistaPago: Subtotal calculado: " + formatoMoneda.format(subtotal));
        System.out.println("VistaPago: IVA calculado: " + formatoMoneda.format(iva));
        
            // En lugar de llamar a setTotal directamente, usamos el método de desglose
            setTotalDesglosado(subtotal, iva, total);
    }
    
    /**
     * Establece el total a pagar y sus componentes (subtotal e IVA)
     * @param subtotalParam El subtotal sin IVA
     * @param ivaParam El monto de IVA
     * @param totalConIVA El total con IVA incluido
     */
    public void setTotalDesglosado(double subtotalParam, double ivaParam, double totalConIVA) {
        // Establecer el total con IVA
        this.total = totalConIVA;
        
        System.out.println("VistaPago (desglosado): Subtotal: " + formatoMoneda.format(subtotalParam));
        System.out.println("VistaPago (desglosado): IVA: " + formatoMoneda.format(ivaParam));
        System.out.println("VistaPago (desglosado): Total con IVA: " + formatoMoneda.format(totalConIVA));
        
        // Mostrar el total claramente incluyendo que tiene IVA
        tTotal.setText(formatoMoneda.format(totalConIVA) + " (IVA incluido)");
        
        // Actualizar el panel existente o crear uno nuevo
        mostrarDesglose(subtotalParam, ivaParam);
    }
    
    /**
     * Método auxiliar para mostrar el desglose de subtotal e IVA
     * @param subtotalMonto El subtotal sin IVA
     * @param ivaMonto El monto de IVA
     */
    private void mostrarDesglose(double subtotalMonto, double ivaMonto) {
        // Verificar si ya existe un panel de desglose
        boolean existePanel = false;
        JPanel panelExistente = null;
        
        // Buscar si existe un panel de desglose (panel con 4 componentes)
        for (java.awt.Component comp : panelPrincipal.getComponents()) {
            if (comp instanceof JPanel && comp != tTotal.getParent()) {
                JPanel panel = (JPanel)comp;
                if (panel.getComponentCount() == 4) {
                    existePanel = true;
                    panelExistente = panel;
                    break;
                }
            }
        }
        
        if (existePanel && panelExistente != null) {
            // Actualizar los valores en el panel existente
            try {
                if (panelExistente.getComponent(1) instanceof JLabel) {
                    ((JLabel)panelExistente.getComponent(1)).setText(formatoMoneda.format(subtotalMonto));
                }
                if (panelExistente.getComponent(3) instanceof JLabel) {
                    ((JLabel)panelExistente.getComponent(3)).setText(formatoMoneda.format(ivaMonto));
                }
            } catch (Exception e) {
                System.err.println("Error al actualizar panel existente: " + e.getMessage());
            }
        } else {
            // Crear un nuevo panel de desglose
            JPanel nuevoPanel = new JPanel(new GridLayout(2, 2, 5, 5));
            nuevoPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            
            // Crear etiquetas y valores
            JLabel etiquetaSubtotal = new JLabel("Subtotal: ", SwingConstants.RIGHT);
            JLabel valorSubtotal = new JLabel(formatoMoneda.format(subtotalMonto), SwingConstants.RIGHT);
            JLabel etiquetaIva = new JLabel("IVA (16%): ", SwingConstants.RIGHT);
            JLabel valorIva = new JLabel(formatoMoneda.format(ivaMonto), SwingConstants.RIGHT);
            
            // Agregar componentes al panel
            nuevoPanel.add(etiquetaSubtotal);
            nuevoPanel.add(valorSubtotal);
            nuevoPanel.add(etiquetaIva);
            nuevoPanel.add(valorIva);
            
            // Buscar el panel que contiene el total
            java.awt.Component panelTotal = null;
            for (java.awt.Component comp : panelPrincipal.getComponents()) {
                if (comp instanceof JPanel && ((JPanel)comp).isAncestorOf(tTotal)) {
                    panelTotal = comp;
                    break;
                }
            }
            
            if (panelTotal != null) {
                // Agregar el panel informativo antes del panel de total
                int index = java.util.Arrays.asList(panelPrincipal.getComponents()).indexOf(panelTotal);
                if (index >= 0) {
                    panelPrincipal.add(nuevoPanel, index);
                    panelPrincipal.revalidate();
                    panelPrincipal.repaint();
                }
            }
        }
    }
    
    /**
     * Verifica si el pago fue confirmado
     */
    public boolean isPagoConfirmado() {
        return pagoConfirmado;
    }
    
    /**
     * Obtiene el monto con que se pagó
     */
    public double getPagoCon() {
        return pagoCon;
    }
    
    /**
     * Obtiene el cambio calculado
     */
    public double getCambio() {
        return cambio;
    }
    
    /**
     * Establecer listeners para los botones
     */
    public void setConfirmarListener(ActionListener listener) {
        // Solo añadir el listener al botón sin ejecutar nada más
        bConfirmar.addActionListener(listener);
    }
    

    
    public void setCancelarListener(ActionListener listener) {
        bCancelar.addActionListener(listener);
    }
    
    /**
     * Obtiene referencia a los campos
     */
    public JTextField getTPagoCliente() {
        return tPagoCon;
    }
    
    /**
     * Obtiene el tipo de pago seleccionado
     * @return 1 para Efectivo, 2 para Tarjeta
     */
    public int getTipoPagoSeleccionado() {
        return tipoPagoSeleccionado;
    }
    
    /**
     * Obtiene el ComboBox de tipo de pago
     */
    public JComboBox<String> getCbTipoPago() {
        return cbTipoPago;
    }
    
    /**
     * Establece el fondo disponible en caja para verificar si hay suficiente cambio
     * @param fondoDisponible Monto disponible en caja
     */
    public void setFondoCaja(double fondoDisponible) {
        // Guardar el valor anterior para casos donde el nuevo valor no sea válido
        double fondoAnterior = this.fondoCajaDisponible;
        
        // Si el fondo recibido es válido, usarlo directamente, pero garantizar que sea suficiente
        if (fondoDisponible > 0) {
            // Usar el máximo entre el valor proporcionado y 10000 para garantizar fondos suficientes
            this.fondoCajaDisponible = Math.max(fondoDisponible, 10.0);
            System.out.println("VistaPago: Fondo en caja establecido a $" + formatoMoneda.format(this.fondoCajaDisponible));
        } else {
            // Si no es válido, usar un valor predeterminado seguro
            this.fondoCajaDisponible = (fondoAnterior > 0) ? fondoAnterior : 10000.0;
            System.out.println("VistaPago: Usando fondo de caja predeterminado: $" + formatoMoneda.format(this.fondoCajaDisponible));
        }
        
        // Recalcular el cambio si ya se ha ingresado un valor
        if (tPagoCon != null && !tPagoCon.getText().trim().isEmpty()) {
            calcularCambio();
        }
    }
    
    /**
     * Establece la referencia al controlador de ventas
     * @param controladorVenta Instancia del controlador de ventas
     */
    public void setControladorVenta(Controlador.ControladorVentas controladorVenta) {
        this.controladorVenta = controladorVenta;
    }
    
    /**
     * Establece la referencia al controlador del menú
     * @param controladorMenu Instancia del controlador del menú
     */
    public void setControladorMenu(Controlador.ControladorMenu controladorMenu) {
        this.controladorMenu = controladorMenu;
    }
    
    /**
     * Establece la referencia a VistaCajaInicial y actualiza el fondo disponible
     * @param cajaInicial Instancia de VistaCajaInicial
     */
    public void setCajaInicial(VistaCajaInicial cajaInicial) {
        this.cajaInicial = cajaInicial;
        this.vistaCajaInicial = cajaInicial; // Actualizar ambas referencias
        if (cajaInicial != null && cajaInicial.isCajaAbierta()) {
            setFondoCaja(cajaInicial.getFondoInicial());
            System.out.println("VistaPago: Fondo actualizado desde VistaCajaInicial: $" + 
                           formatoMoneda.format(cajaInicial.getFondoInicial()));
        } else {
            System.out.println("VistaPago: No se pudo actualizar el fondo desde VistaCajaInicial");
        }
    }
    
    /**
     * Establece el fondo inicial manualmente
     * @param fondoInicial Monto inicial en caja
     */
    public void setFondoInicial(double fondoInicial) {
        this.fondoCajaDisponible = fondoInicial;
        System.out.println("VistaPago: Fondo establecido manualmente a $" + 
                       formatoMoneda.format(fondoInicial));
    }
    
    /**
     * Actualiza el fondo de caja desde la instancia de VistaCajaInicial
     */
    public void actualizarFondoDesdeCajaInicial() {
        if (cajaInicial != null && cajaInicial.isCajaAbierta()) {
            setFondoCaja(cajaInicial.getFondoInicial());
            System.out.println("VistaPago: Fondo actualizado desde VistaCajaInicial: $" + 
                           formatoMoneda.format(cajaInicial.getFondoInicial()));
        }
    }
    
    /**
     * Establece la referencia al controlador de caja
     * @param controladorCaja Instancia del controlador de caja
     */
    public void setControladorCaja(ControladorCaja controladorCaja) {
        this.controladorCaja = controladorCaja;
            System.out.println("VistaPago: Controlador de caja configurado correctamente");
            
            // Agregar validación para asegurar que el controlador no sea null
            if (this.controladorCaja == null) {
                System.err.println("VistaPago: ¡ADVERTENCIA! El controlador de caja sigue siendo null después de intentar configurarlo");
            }
    }
        
        /**
         * Calcula el cambio cuando el usuario ingresa un monto
         */
        private void calcularCambio() {
            try {
                // Obtener el monto con que paga el cliente
                String montoTexto = tPagoCon.getText().trim();
                if (montoTexto.isEmpty()) {
                    cambio = 0;
                    tCambio.setText("0.00");
                    return;
                }
                
                // Reemplazar comas por puntos si es necesario
                montoTexto = montoTexto.replace(',', '.');
                pagoCon = Double.parseDouble(montoTexto);
                cambio = pagoCon - total;
                
                // Redondear cambio a 2 decimales para evitar errores de punto flotante
                cambio = Math.round(cambio * 100) / 100.0;
                
                        // Mensaje completo de información de cambio (solo para depuración)
                        System.out.println("\n========== CÁLCULO DE CAMBIO ==========");
                        System.out.println("Monto pagado: $" + formatoMoneda.format(pagoCon));
                        System.out.println("Total a pagar: $" + formatoMoneda.format(total));
                        System.out.println("Cambio a dar: $" + formatoMoneda.format(cambio));
                        System.out.println("Fondo disponible en caja: $" + formatoMoneda.format(fondoCajaDisponible));
                        
                        // Mostrar el cambio formateado
                        tCambio.setText(formatoMoneda.format(cambio));
                        
                        // Si el cambio es negativo, es un pago insuficiente
                        if (cambio < 0) {
                            tCambio.setForeground(Color.RED);
                            lblMensaje.setText("¡El pago es insuficiente!");
                            lblMensaje.setForeground(Color.RED);
                            bConfirmar.setEnabled(false);
                            fondoSuficiente = false;
                        } 
                        // Si el cambio es cero (pago exacto), siempre está bien
                        else if (Math.abs(cambio) < 0.01) {
                            tCambio.setForeground(new Color(0, 128, 0)); // Verde
                            lblMensaje.setText("¡Pago exacto!");
                            lblMensaje.setForeground(Color.BLACK);
                            bConfirmar.setEnabled(true);
                            fondoSuficiente = true;
                        }
                        // Si hay cambio a dar, verificar si hay fondos suficientes
                        else {
                            System.out.println("Verificando fondo para cambio: $" + formatoMoneda.format(cambio));
                            System.out.println("Fondo en caja: $" + formatoMoneda.format(fondoCajaDisponible));
                            
                            // Verificar si hay fondos para dar cambio
                            if (fondoCajaDisponible >= cambio) {
                                Map<Double, Integer> desgloseCambio = null;
                                
                                // Intentar calcular desglose solo si tenemos acceso al controlador del menú
                                if (controladorMenu != null && controladorMenu.getEstadoCaja() != null) {
                                    try {
                                        desgloseCambio = controladorMenu.getEstadoCaja().calcularDesgloseCambio(cambio);
                                        System.out.println("Desglose de cambio calculado: " + desgloseCambio);
                                    } catch (Exception e) {
                                        System.out.println("Error al calcular desglose: " + e.getMessage());
                                        e.printStackTrace();
                                        desgloseCambio = null;
                                    }
                                } else {
                                    System.out.println("No se puede calcular desglose: controladorMenu no está disponible");
                                }
                                
                                // Verificar si el desglose es nulo o no se pudo calcular
                                if (desgloseCambio == null) {
                                    System.out.println("Cambio restante: " + cambio + " - No se pudo calcular el desglose completo");
                                    // PERMITIMOS LA VENTA SIEMPRE QUE HAYA FONDOS SUFICIENTES
                                    tCambio.setForeground(new Color(0, 128, 0)); // Verde
                                    lblMensaje.setText("Cambio a entregar: $" + formatoMoneda.format(cambio));
                                    lblMensaje.setForeground(Color.BLACK);
                                    bConfirmar.setEnabled(true);
                                    fondoSuficiente = true;
                                    System.out.println("Confirmación: Monto de cambio grandes, se permite la venta");
                                } else {
                                    // Sí hay cambio disponible
                                    tCambio.setForeground(new Color(0, 128, 0)); // Verde
                                    lblMensaje.setText("Cambio a entregar: $" + formatoMoneda.format(cambio));
                                    lblMensaje.setForeground(Color.BLACK);
                                    bConfirmar.setEnabled(true);
                                    fondoSuficiente = true;
                                    System.out.println("Confirmación: Pago suficiente, habilitando botón confirmar");
                                }
                            } else {
                                // No hay suficiente fondo para dar cambio - NO permitimos la venta
                                tCambio.setForeground(Color.RED);
                                lblMensaje.setText("Error: Fondo insuficiente para cambio. No se puede completar la venta.");
                                lblMensaje.setForeground(Color.RED);
                                bConfirmar.setEnabled(false); // Deshabilitamos el botón de confirmar
                                fondoSuficiente = false; // No permitir la venta
                                System.out.println("VistaPago: NO se permite la venta. Fondo insuficiente para cambio.");
                            }
                        }
                        
                        System.out.println("fondoSuficiente = " + fondoSuficiente);
                        System.out.println("=======================================");
                        
                    } catch (NumberFormatException e) {
                        tCambio.setText("Error");
                        tCambio.setForeground(Color.RED);
                        lblMensaje.setText("¡Monto inválido!");
                        lblMensaje.setForeground(Color.RED);
                        bConfirmar.setEnabled(false);
                        fondoSuficiente = false;
            }
        }


                
    /**
     * Verifica si hay suficiente fondo en caja para dar el cambio
     * @return true si hay fondos suficientes, false en caso contrario
     */
    public boolean verificarFondoSuficiente() {
        // Log para depuración
        System.out.println("VistaPago: Verificando fondo para cambio: $" + formatoMoneda.format(cambio));
        System.out.println("VistaPago: Fondo en caja: $" + formatoMoneda.format(fondoCajaDisponible));
        
        // Si el cambio es negativo o el pago es exacto, siempre hay fondos suficientes
        if (cambio <= 0.001) {
            this.fondoSuficiente = true;
            System.out.println("VistaPago: No se requiere cambio o es un pago exacto.");
            return true;
        }
        
        // Verificar si hay fondos suficientes para dar el cambio
        if (fondoCajaDisponible >= cambio) {
            this.fondoSuficiente = true;
            System.out.println("VistaPago: Hay fondos suficientes para dar cambio.");
            return true;
        } else {
            this.fondoSuficiente = false;
            System.out.println("VistaPago: NO hay fondos suficientes para dar cambio.");
            return false;
        }
    }
    
    public JTextField getTCambio() {
        return tCambio;
    }
    
    public JTextField getTTotal() {
        return tTotal;
    }

} 