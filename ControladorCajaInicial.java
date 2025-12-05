package Controlador;

import Modelo.BaseDatos;
import Vista.VistaCajaInicial;
import Vista.VistaMenu;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

/**
 * Controlador para la vista de apertura inicial de caja
 */
public class ControladorCajaInicial implements ActionListener {

    private VistaCajaInicial vistaCajaInicial;
    private VistaMenu padre;
    private ControladorMenu controladorMenu;
    private DecimalFormat formatoMoneda = new DecimalFormat("#,##0.00");
    
    /**
     * Constructor que inicializa la vista de apertura de caja
     * @param controladorMenu Referencia al controlador del menú principal
     */
    public ControladorCajaInicial(ControladorMenu controladorMenu) {
        this.controladorMenu = controladorMenu;
        this.vistaCajaInicial = new VistaCajaInicial();
        this.vistaCajaInicial.getBtnAceptar().addActionListener(this);
        this.vistaCajaInicial.getBtnCancelar().addActionListener(this);
        
        // Mostrar la vista de apertura de caja
        controladorMenu.getVentana().getEscritorio().add(this.vistaCajaInicial);
        try {
            this.vistaCajaInicial.setMaximum(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
        this.vistaCajaInicial.setVisible(true);
    }
    
    /**
     * Devuelve la ventana de apertura de caja
     * @return JInternalFrame que contiene la vista
     */
    public JInternalFrame getVentana() {
        return this.vistaCajaInicial;
    }
    
    /**
     * Procesa la apertura de caja y registra el monto inicial
     */
    private void abrirCaja() {
        try {
            // Obtener el monto ingresado por el usuario
            String montoTexto = vistaCajaInicial.getTFondoInicial().getText().trim();
            
            // Validar que se haya ingresado un monto válido
            if (montoTexto.isEmpty()) {
                JOptionPane.showMessageDialog(vistaCajaInicial, 
                        "Por favor ingrese el monto inicial de la caja.", 
                        "Monto Requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Convertir el monto a número, manejando posibles formatos con comas
            montoTexto = montoTexto.replace(",", "");
            double montoInicial = Double.parseDouble(montoTexto);
            
            // Validar que el monto sea positivo
            if (montoInicial <= 0) {
                JOptionPane.showMessageDialog(vistaCajaInicial, 
                        "El monto inicial debe ser mayor a cero.", 
                        "Monto Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Obtener fecha y hora actual
            Date fechaActual = new Date();
            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
            String fecha = formatoFecha.format(fechaActual);
            String hora = formatoHora.format(fechaActual);
            
            // NO generar ID manualmente - MySQL lo auto-incrementa
            // El campo 'id' es AUTO_INCREMENT en MySQL
            
            // Registrar apertura de caja en la base de datos
            BaseDatos bd = new BaseDatos();
            // Omitir el campo 'id' para que MySQL lo genere automáticamente
            // Usar null para campos DECIMAL vacíos - MySQL no acepta cadenas vacías "" en columnas numéricas
            String[] valores = {
                fecha, 
                hora, 
                String.valueOf(montoInicial), 
                "1",   // estado = 1 (Abierta)
                null,  // monto_final (NULL en lugar de "")
                null   // hora_cierre (NULL en lugar de "")
            };
            boolean resultado = bd.insertar("CajaAperturas", "fecha,hora,monto_inicial,estado,monto_final,hora_cierre", valores);
            bd.cerrarConexion();
            
            if (resultado) {
                // Actualizar el estado de la caja en el controlador de menú
                controladorMenu.actualizarEstadoCaja(true);
                
                controladorMenu.getEstadoCaja().setFondoActual(montoInicial);
                
                // Mostrar mensaje de éxito
                JOptionPane.showMessageDialog(vistaCajaInicial, 
                        "Caja abierta con éxito con un fondo inicial de $" + 
                        formatoMoneda.format(montoInicial), 
                        "Apertura Exitosa", JOptionPane.INFORMATION_MESSAGE);
                
                try {
                    // Cerrar la ventana de apertura antes de abrir la nueva
                    vistaCajaInicial.dispose();
                    
                    // Crear la ventana de caja usando el constructor que recibe un ControladorMenu
                    ControladorCaja controladorCaja = new ControladorCaja(controladorMenu);
                    
                    // Obtener la ventana (JInternalFrame) antes de intentar mostrarla
                    JInternalFrame ventanaCaja = controladorCaja.getVentana();
                    
                    // Primera verificación: asegurarse que la ventana no esté ya en un contenedor
                    if (ventanaCaja.getParent() != null) {
                        System.out.println("¡Atención! La ventana ya tiene un padre. Removiéndola primero.");
                        Container contenedorPadre = ventanaCaja.getParent();
                        contenedorPadre.remove(ventanaCaja);
                    }
                    
                    // Tenemos que asegurarnos que la ventana esté en un estado adecuado
                    try {
                        if (ventanaCaja.isClosed()) {
                            // Si está cerrada, no se puede volver a usar
                            JOptionPane.showMessageDialog(controladorMenu.getVentana().getFrame(),
                                "La ventana de caja se cerró y no se puede reutilizar. Creando nueva instancia.",
                                "Aviso", JOptionPane.WARNING_MESSAGE);
                            
                            // Crear una nueva instancia
                            controladorCaja = new ControladorCaja(controladorMenu);
                            ventanaCaja = controladorCaja.getVentana();
                        }
                    } catch (Exception ex) {
                        // Ignorar errores al verificar el estado
                    }
                    
                    // Ahora intentamos realizar la operación usando un enfoque directo
                    // sin usar el método mostrarVentana que podría estar causando problemas
                    JDesktopPane escritorio = controladorMenu.getVentana().getEscritorio();
                    
                    // Añadir la ventana al escritorio
                    try {
                        escritorio.add(ventanaCaja);
                        
                        // Maximizar la ventana automáticamente
                        try {
                            ventanaCaja.setMaximum(true);
                        } catch (java.beans.PropertyVetoException pve) {
                            pve.printStackTrace();
                        }
                        
                        // Configurar la posición y hacerla visible
                        ventanaCaja.setVisible(true);
                        ventanaCaja.toFront();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(controladorMenu.getVentana().getFrame(),
                            "Error al añadir la ventana de caja al escritorio: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(controladorMenu.getVentana().getFrame(), 
                            "Error al abrir la ventana de caja: " + e.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                    
                    // Si ocurre un error, permitir regresar al menú
                    controladorMenu.getVentana().getFrame().setEnabled(true);
                }
            } else {
                JOptionPane.showMessageDialog(vistaCajaInicial, 
                        "Error al registrar la apertura de caja. Intente nuevamente.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vistaCajaInicial, 
                    "Por favor ingrese un monto válido (sólo números y punto decimal).", 
                    "Formato Inválido", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vistaCajaInicial, 
                    "Error: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaCajaInicial.getBtnAceptar()) {
            abrirCaja();
        } else if (e.getSource() == vistaCajaInicial.getBtnCancelar()) {
            try {
                // Solo ocultamos la ventana sin destruirla completamente
                vistaCajaInicial.setVisible(false);
                // Asegurar que el menú principal quede habilitado
                if (controladorMenu != null && controladorMenu.getVentana() != null) {
                    // Usar el nuevo método completo para desbloquear la interfaz
                    controladorMenu.getVentana().desbloquearInterfaz();
                    
                    // Asegurar que el frame principal tenga el foco
                    controladorMenu.getVentana().getFrame().requestFocus();
                    
                    System.out.println("Menú desbloqueado por cierre con botón Cancelar");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(vistaCajaInicial, 
                    "Error al cerrar la ventana: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
