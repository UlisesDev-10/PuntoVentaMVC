package Controlador;

import Vista.VistaVentas;
import Vista.VistaCajaInicial;
import Vista.VistaCierreCaja;
import Vista.VistaMenu;
import Vista.VistaPago;
import Vista.VistaTicket;
import Modelo.ConfiguracionBD;
import java.awt.event.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.JOptionPane;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class ControladorVentas {
    
    private VistaVentas vistaVentas;
    private VistaMenu padre;
    private ControladorMenu controladorMenu;
    private boolean cajaAbierta;
    private DecimalFormat formatoMoneda = new DecimalFormat("#,##0.00");
    private VistaPago vistaPago;
    private DefaultTableModel modelo;
    // Constante para el porcentaje de IVA (16%)
    private static final double PORCENTAJE_IVA = 0.16;
    
    public ControladorVentas(ControladorMenu controladorMenu) {
        this.modelo = new DefaultTableModel();
        this.controladorMenu = controladorMenu;
        this.vistaVentas = new VistaVentas();
        this.cajaAbierta = this.controladorMenu.getEstadoCaja().estaAbierta();
        
        
        // Inicializar el fondo actual si la caja está abierta
        if (this.cajaAbierta) {
            this.controladorMenu.getEstadoCaja().inicializarFondoActual();
        }
        
        // Verificar y configurar la estructura de la base de datos
        ConfiguracionBD configBD = new ConfiguracionBD();
        if (!configBD.verificarEstructuraBD()) {
            JOptionPane.showMessageDialog(null, 
                "Se detectaron problemas con la estructura de la base de datos.\n" +
                "Se intentó corregir automáticamente, pero podría requerir revisión manual.",
                "Advertencia - Estructura BD", 
                JOptionPane.WARNING_MESSAGE);
        }
        
        // Inicializar estado visual de la caja
        vistaVentas.actualizarEstadoCaja(cajaAbierta);
        
        // Asignar eventos
        this.vistaVentas.getBtnAbrirCaja().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirCaja();
            }
        });
        
        this.vistaVentas.getBtnCerrarCaja().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrarCaja();
            }
        });
        
        // Mostrar la vista en el escritorio
        controladorMenu.getVentana().getEscritorio().add(vistaVentas);
        try {
            vistaVentas.setMaximum(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
        vistaVentas.setVisible(true);
    }
    
    // Método para abrir la caja
    private void abrirCaja() {
        if (!cajaAbierta) {
            ControladorCajaInicial controladorCajaInicial = new ControladorCajaInicial(controladorMenu);
            // Actualizar vista después de abrir caja
            controladorCajaInicial.getVentana().addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    cajaAbierta = controladorMenu.getEstadoCaja().estaAbierta();
                    vistaVentas.actualizarEstadoCaja(cajaAbierta);
                }
            });
        }
    }
    
    // Método para cerrar la caja
    private void cerrarCaja() {
        if (cajaAbierta) {
            ControladorCierreCaja controladorCierreCaja = new ControladorCierreCaja(controladorMenu);
            // Actualizar vista después de cerrar caja
            controladorCierreCaja.getVistaCierreCaja().addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    cajaAbierta = controladorMenu.getEstadoCaja().estaAbierta();
                    vistaVentas.actualizarEstadoCaja(cajaAbierta);
                }
            });
        }
    }
    
    // Método para realizar una venta
    private void realizarVenta() {
        if (!cajaAbierta) {
            JOptionPane.showMessageDialog(vistaVentas, 
                    "La caja debe estar abierta para realizar ventas", 
                    "Caja cerrada", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Asumimos un total de venta solo para fines de demostración
        // En un sistema real, este valor vendría del proceso de selección de productos
        double subtotal = 100.0; // Ejemplo de subtotal (sin IVA)
        double iva = calcularIVA(subtotal);
        // Asegurarse de que el IVA esté calculado correctamente
       
        double totalVenta = subtotal + iva; // Total con IVA incluido
        
        System.out.println("ControladorVentas: Subtotal: " + formatoMoneda.format(subtotal));
        System.out.println("ControladorVentas: IVA: " + formatoMoneda.format(iva));
        System.out.println("ControladorVentas: Total con IVA: " + formatoMoneda.format(totalVenta));
        
        // Crear y configurar la vista de pago
        vistaPago = new VistaPago(null, totalVenta);
        vistaPago.setModal(true);
        
        // También podemos usar el método setTotalDesglosado para mayor claridad
        vistaPago.setTotalDesglosado(subtotal, iva, totalVenta);
        
        // Configurar el fondo disponible para el cambio
        configurarFondoDisponiblePago(vistaPago);
        
        // Configurar listener para confirmar el pago
        vistaPago.setConfirmarListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (vistaPago.isPagoConfirmado()) {
                    // Obtener los valores del pago
                    double montoPagado = vistaPago.getPagoCon();
                    double cambio = vistaPago.getCambio();
                    
                    // Actualizar el fondo de caja
                    actualizarFondoCaja(totalVenta, montoPagado);
                    
                    // Cerrar la ventana de pago
                    vistaPago.dispose();
                    
                    // Abrir la ventana de ticket después de confirmar el pago
                    abrirVentanaTicket(subtotal, iva, totalVenta, montoPagado, cambio);
                }
            }
        });
        
        // Mostrar la ventana de pago
        vistaPago.setVisible(true);
    }
    
    /**
     * Método para abrir la ventana de ticket
     * @param subtotal Subtotal de la venta (sin IVA)
     * @param iva Monto del IVA
     * @param totalVenta Total de la venta (con IVA)
     * @param montoPagado Monto pagado por el cliente
     * @param cambio Cambio a entregar
     */
    private void abrirVentanaTicket(double subtotal, double iva, double totalVenta, double montoPagado, double cambio) {
        try {
            // Crear una lista de productos para demostración
            // En un sistema real, esta información vendría del proceso de venta
            List<Object[]> productos = new ArrayList<>();
            
            // Ejemplo de productos para demostración - Ahora con precios sin IVA
            double precioUnitario1 = 43.10; // Precio sin IVA
            double precioUnitario2 = 43.10; // Precio sin IVA
            double ivaProducto1 = calcularIVA(precioUnitario1);
            double ivaProducto2 = calcularIVA(precioUnitario2);
            
            // Formato: Nombre, Cantidad, Precio Unitario (sin IVA), IVA unitario, Total unitario (con IVA)
            productos.add(new Object[]{"Producto 1", 1, precioUnitario1, ivaProducto1, precioUnitario1 + ivaProducto1});
            productos.add(new Object[]{"Producto 2", 1, precioUnitario2, ivaProducto2, precioUnitario2 + ivaProducto2});
            
            // Crear y configurar la vista ticket con el nuevo constructor que acepta subtotal e IVA
            VistaTicket vistaTicket = new VistaTicket(null, productos, subtotal, iva, totalVenta, montoPagado, cambio);
            vistaTicket.setModal(true);
            
            // Configurar listener para el botón cerrar
            vistaTicket.setCerrarListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    vistaTicket.dispose();
                }
            });
            
            // El botón de imprimir ya tiene la funcionalidad de generar PDF
            vistaTicket.setImprimirListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(vistaTicket, 
                            "Imprimiendo ticket...", 
                            "Impresión", 
                            JOptionPane.INFORMATION_MESSAGE);
                }
            });
            
            // Mostrar la ventana de ticket
            vistaTicket.setVisible(true);
            
        } catch (Exception e) {
            System.err.println("Error al abrir ventana de ticket: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(vistaVentas,
                    "Error al generar el ticket: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Método para obtener la vista
    public VistaVentas getVistaVentas() {
        return vistaVentas;
    }
    
    /**
     * Verifica si hay suficiente cambio disponible en la caja
     * @param totalVenta Total de la venta
     * @param montoPagado Monto pagado por el cliente
     * @return true si hay suficiente cambio, false si no
     */
    public boolean verificarCambioDisponible(double totalVenta, double montoPagado) {
        // Calcular cambio a devolver
        double cambio = montoPagado - totalVenta;
        
        // Si el cambio es positivo, verificar si hay suficiente en caja
        if (cambio > 0) {
            boolean suficienteCambio = controladorMenu.getEstadoCaja().haySuficienteCambio(cambio);
            
            // Si no hay suficiente cambio, mostrar advertencia
            if (!suficienteCambio) {
                JOptionPane.showMessageDialog(vistaVentas,
                    "¡Atención! No hay suficiente dinero en caja para dar cambio.\n" +
                    "Cambio requerido: $" + formatoMoneda.format(cambio) + "\n" +
                    "Fondo disponible: $" + formatoMoneda.format(controladorMenu.getEstadoCaja().getFondoActual()),
                    "Fondo insuficiente para cambio",
                    JOptionPane.WARNING_MESSAGE);
            }
            
            return suficienteCambio;
        }
        
        // Si no hay que dar cambio, siempre retornar true
        return true;
    }
    
    /**
     * Configura el fondo disponible en caja para la ventana de pagos
     * @param vistaPago Ventana de pagos que necesita conocer el fondo disponible
     */
    public void configurarFondoDisponiblePago(VistaPago vistaPago) {
        if (vistaPago != null && controladorMenu != null && controladorMenu.getEstadoCaja() != null) {
            // Intentar obtener la referencia a VistaCajaInicial si existe
            try {
                VistaCajaInicial cajaInicial = new VistaCajaInicial();
                double fondoEstado = controladorMenu.getEstadoCaja().getFondoActual();
                
                // Asegurar que VistaCajaInicial tenga el mismo fondo que el estado
                cajaInicial.setFondoInicial(fondoEstado);
                
                // Establecer esta instancia en VistaPago
                vistaPago.setCajaInicial(cajaInicial);
                System.out.println("ControladorVentas: Se creó y configuró una instancia de VistaCajaInicial");
            } catch (Exception e) {
                System.err.println("Error al crear VistaCajaInicial: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Forzar la inicialización del fondo actual si es cero
            double fondoActual = controladorMenu.getEstadoCaja().getFondoActual();
            
            if (fondoActual <= 0) {
                System.out.println("¡AVISO! Fondo actual en CajaEstado es cero o negativo, intentando inicializar...");
                controladorMenu.getEstadoCaja().inicializarFondoActual();
                
                // Verificar si se inicializó correctamente
                fondoActual = controladorMenu.getEstadoCaja().getFondoActual();
                
                // Si sigue siendo cero o negativo, intentar usar el fondo inicial
                if (fondoActual <= 0) {
                    double fondoInicial = controladorMenu.getEstadoCaja().getFondoInicial();
                    if (fondoInicial > 0) {
                        System.out.println("Usando fondoInicial como respaldo: " + formatoMoneda.format(fondoInicial));
                        fondoActual = fondoInicial;
                    }
                }
            }
            
            System.out.println("DEPURACIÓN: Fondo inicial en CajaEstado: " + formatoMoneda.format(controladorMenu.getEstadoCaja().getFondoInicial()));
            System.out.println("DEPURACIÓN: Fondo actual en CajaEstado: " + formatoMoneda.format(fondoActual));
            System.out.println("Obteniendo fondo de controladorMenu: $" + fondoActual);
            
            // Establecer la referencia al controlador del menú en VistaPago
            vistaPago.setControladorMenu(controladorMenu);
            
            // Establecer la referencia al controlador primero para asegurar que esté disponible
            vistaPago.setControladorVenta(this);
            
            // Luego actualizar el fondo - Asegurar que estamos pasando un valor válido
            // Si fondoActual sigue siendo cero, intentar usar el fondo inicial
            if (fondoActual <= 0) {
                System.out.println("¡ADVERTENCIA! No se pudo obtener un fondo válido, usando valor de fondo inicial");
                double fondoInicial = controladorMenu.getEstadoCaja().getFondoInicial();
                if (fondoInicial > 0) {
                    fondoActual = fondoInicial;
                }
                System.out.println("Estableciendo fondo de caja para pago: $" + fondoActual);
            }
            
            // Pasar el fondo real a VistaPago
            System.out.println("Fondo pasado a VistaPago: $" + fondoActual);
            vistaPago.setFondoCaja(fondoActual);
            this.vistaPago = vistaPago; // Guardar referencia para actualizaciones futuras
            
            System.out.println("Fondo disponible en caja configurado: $" + formatoMoneda.format(fondoActual));
        } else {
            System.out.println("No se pudo configurar el fondo disponible - referencias nulas");
        }
    }
    
    /**
     * Actualiza el fondo de caja después de una venta en efectivo
     * @param totalVenta Total de la venta
     * @param montoPagado Monto pagado por el cliente
     */
    public void actualizarFondoCaja(double totalVenta, double montoPagado) {
        // Solo actualizar si el pago es en efectivo (tipo de venta 1)
        // Aquí deberías verificar el tipo de venta seleccionado
            if (controladorMenu != null && controladorMenu.getEstadoCaja() != null) {
                    // Verificar estado inicial
                    double fondoInicial = controladorMenu.getEstadoCaja().getFondoActual();
                    System.out.println("Fondo antes de actualizar: $" + new DecimalFormat("#,##0.00").format(fondoInicial));
                    
                    // Actualizar fondo en el modelo
                    controladorMenu.getEstadoCaja().actualizarFondoDespuesDeVenta(totalVenta, montoPagado);
                    
                    // Verificar que se haya actualizado correctamente
                    double nuevoFondo = controladorMenu.getEstadoCaja().getFondoActual();
                    System.out.println("Fondo después de actualizar: $" + new DecimalFormat("#,##0.00").format(nuevoFondo));
                    
                    // Actualizar el fondo disponible en la vista de pago si está activa
                    if (vistaPago != null && vistaPago.isVisible()) {
                        vistaPago.setFondoCaja(nuevoFondo);
                        System.out.println("Fondo de caja actualizado en vista de pago: $" + 
                                new DecimalFormat("#,##0.00").format(nuevoFondo));
                    }
                } else {
                    System.out.println("ERROR: No se pudo actualizar el fondo de caja - controladorMenu o estadoCaja es null");
            }
    }
    
    // Método alternativo para obtener la vista (mantener consistencia con otros controladores)
    public VistaVentas getVentana() {
        return vistaVentas;
    }
    
    /**
     * Retorna la referencia al controlador de menú
     * @return Controlador del menú principal
     */
    public ControladorMenu getControladorMenu() {
        return controladorMenu;
    }
    
    /**
     * Calcula el IVA de un monto dado
     * @param monto Monto sin IVA
     * @return El valor del IVA
     */
    public double calcularIVA(double monto) {
        return monto * PORCENTAJE_IVA;
    }
    
    /**
     * Calcula el precio con IVA incluido
     * @param precioSinIVA Precio sin IVA
     * @return Precio con IVA incluido
     */
    public double calcularPrecioConIVA(double precioSinIVA) {
        return precioSinIVA * (1 + PORCENTAJE_IVA);
    }
    
    /**
     * Calcula el precio sin IVA
     * @param precioConIVA Precio con IVA
     * @return Precio sin IVA
     */
    public double calcularPrecioSinIVA(double precioConIVA) {
        return precioConIVA / (1 + PORCENTAJE_IVA);
    }
}
