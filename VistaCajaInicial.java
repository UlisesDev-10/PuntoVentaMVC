package Vista;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

public class VistaCajaInicial extends JInternalFrame {
    
    private JTextField tFondoInicial;
    private JButton bAceptar, bCancelar;
    private JLabel lFecha, lHora, lCajero;
    private double fondoInicial;
    private boolean cajaAbierta;
    
    // Eliminamos la instancia innecesaria de VistaPago
    
    public VistaCajaInicial() {
        super("Apertura de Caja", true, true, true, true);
        this.setSize(500, 350);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        // Agregamos un listener para el evento de cierre
        this.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                // Al cerrar la ventana, solo la ocultamos sin destruirla
                setVisible(false);
                
                // Intentar obtener la ventana principal
                JDesktopPane desktop = (JDesktopPane) getParent();
                if (desktop != null && desktop.getParent() instanceof JFrame) {
                    JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(desktop);
                    if (mainFrame != null) {
                        // Habilitar el frame principal
                        mainFrame.setEnabled(true);
                        mainFrame.requestFocus();
                        
                        // Si es el VistaMenu, usar el método especializado
                        if (mainFrame.getContentPane().getComponent(0) instanceof JDesktopPane) {
                            // Buscar la instancia de VistaMenu
                            for (Frame frame : Frame.getFrames()) {
                                if (frame instanceof JFrame && frame.isVisible()) {
                                    // Asumimos que este es nuestro frame principal
                                    frame.setEnabled(true);
                                }
                            }
                        }
                    }
                }
                
                System.out.println("VistaCajaInicial cerrada con X");
            }
        });
        
        iniciarComponentes();
        this.cajaAbierta = false;
        this.fondoInicial = 0.0;
    }
    
    private void iniciarComponentes() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel superior con el título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(41, 128, 185));
        panelTitulo.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelTitulo.setLayout(new BorderLayout());
        
        JLabel lblTitulo = new JLabel("APERTURA DE CAJA");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelTitulo.add(lblTitulo, BorderLayout.CENTER);
        
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central con información y entrada
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBorder(new EmptyBorder(20, 30, 20, 30));
        panelCentral.setBackground(Color.WHITE);
        
        // Información de apertura
        JPanel panelInfo = new JPanel(new GridLayout(3, 2, 10, 10));
        panelInfo.setBackground(Color.WHITE);
        panelInfo.setBorder(new CompoundBorder(
                new TitledBorder("Información"),
                new EmptyBorder(10, 10, 10, 10)
        ));
        
        // Fecha
        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelInfo.add(lblFecha);
        
        lFecha = new JLabel(java.time.LocalDate.now().toString());
        lFecha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelInfo.add(lFecha);
        
        // Hora
        JLabel lblHora = new JLabel("Hora:");
        lblHora.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelInfo.add(lblHora);
        
        lHora = new JLabel(java.time.LocalTime.now().toString().substring(0, 8));
        lHora.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelInfo.add(lHora);
        
        // Cajero
        JLabel lblCajero = new JLabel("Cajero:");
        lblCajero.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelInfo.add(lblCajero);
        
        lCajero = new JLabel("Usuario Actual");
        lCajero.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelInfo.add(lCajero);
        
        panelCentral.add(panelInfo);
        
        // Espacio
        panelCentral.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Panel para fondo inicial
        JPanel panelFondo = new JPanel();
        panelFondo.setLayout(new FlowLayout(FlowLayout.CENTER));
        panelFondo.setBackground(Color.WHITE);
        
        JLabel lblFondo = new JLabel("Fondo Inicial ($):");
        lblFondo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panelFondo.add(lblFondo);
        
        // Crear un formato para permitir solo números y punto decimal
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(0.0);
        // Establecer un valor máximo muy alto para permitir fondos grandes
        formatter.setMaximum(Double.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        
        // Usar JFormattedTextField en lugar de JTextField para garantizar entrada numérica
        tFondoInicial = new JFormattedTextField(formatter);
        tFondoInicial.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tFondoInicial.setPreferredSize(new Dimension(150, 35));
        tFondoInicial.setHorizontalAlignment(SwingConstants.RIGHT);
        tFondoInicial.setText("0.00"); // Valor predeterminado
        panelFondo.add(tFondoInicial);
        
        panelCentral.add(panelFondo);
        
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setBorder(new EmptyBorder(0, 0, 20, 0));
        panelBotones.setBackground(new Color(240, 240, 240));
        
        bAceptar = new JButton("Abrir Caja");
        bAceptar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bAceptar.setBackground(new Color(46, 204, 113));
        bAceptar.setForeground(Color.WHITE);
        bAceptar.setPreferredSize(new Dimension(150, 40));
        bAceptar.setFocusPainted(false);
        
        bCancelar = new JButton("Cancelar");
        bCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bCancelar.setBackground(new Color(231, 76, 60));
        bCancelar.setForeground(Color.WHITE);
        bCancelar.setPreferredSize(new Dimension(150, 40));
        bCancelar.setFocusPainted(false);
        
        panelBotones.add(bAceptar);
        panelBotones.add(bCancelar);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    // Getters y Setters
    public JTextField getTFondoInicial() {
        return tFondoInicial;
    }
    
    public JButton getBtnAceptar() {
        return bAceptar;
    }
    
    public JButton getBtnCancelar() {
        return bCancelar;
    }
    
    public JLabel getLFecha() {
        return lFecha;
    }
    
    public JLabel getLHora() {
        return lHora;
    }
    
    public JLabel getLCajero() {
        return lCajero;
    }
    
    public void setCajero(String nombre) {
        this.lCajero.setText(nombre);
    }
    
    public double getFondoInicial() {
        try {
                // Verificar si el campo de texto existe
                if (tFondoInicial != null) {
                    // Intentar obtener el valor numérico del campo
                    String textoFondo = tFondoInicial.getText();
                    if (textoFondo != null && !textoFondo.isEmpty()) {
                        // Eliminar comas y otros caracteres de formato para poder parsear el número
                        textoFondo = textoFondo.replace(",", "");
                        return Double.parseDouble(textoFondo);
                    }
                }
                
                // Si el campo es nulo o vacío, usar el valor almacenado en la variable
                if (fondoInicial > 0) {
                    System.out.println("VistaCajaInicial: Usando valor almacenado de fondoInicial: " + fondoInicial);
                    return fondoInicial;
                } else {
                    // Usar un valor predeterminado seguro
                    double valorPredeterminado = 1000.0;
                    System.out.println("VistaCajaInicial: Usando valor predeterminado de fondoInicial: " + valorPredeterminado);
                    return valorPredeterminado;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error al convertir fondo inicial: " + e.getMessage());
                return fondoInicial > 0 ? fondoInicial : 1000.0; // Devolver el valor almacenado o un valor predeterminado
            } catch (Exception e) {
                System.out.println("Error inesperado en getFondoInicial: " + e.getMessage());
                return 100.0; // Valor predeterminado seguro
        }
    }
    
    public void setFondoInicial(double fondo) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        this.tFondoInicial.setText(df.format(fondo));
        this.fondoInicial = fondo;
        System.out.println("VistaCajaInicial: Fondo establecido a $" + df.format(fondo));
    }
    
    /**
     * Método estático para ayudar a establecer el fondo inicial en un objeto VistaCajaInicial
     * @param fondo Monto a establecer como fondo inicial
     * @param vista Objeto VistaCajaInicial donde establecer el fondo
     */
    public static void setFondoInicial(double fondo, VistaCajaInicial vista) {
        if (vista != null) {
            vista.setFondoInicial(fondo);
        } else {
            System.out.println("Error: No se puede establecer fondo en una vista nula");
        }
    }
    
    public boolean isCajaAbierta() {
        return cajaAbierta;
    }
    
    public void setCajaAbierta(boolean cajaAbierta) {
        this.cajaAbierta = cajaAbierta;
    }
    
    /**
     * Establece el listener para el botón de abrir caja
     * @param listener ActionListener que manejará el evento
     */
    public void setAbrirCajaListener(ActionListener listener) {
        bAceptar.setActionCommand("abrirCaja");
        bAceptar.addActionListener(listener);
    }
    
    /**
     * Establece el listener para el botón de cancelar
     * @param listener ActionListener que manejará el evento
     */
    public void setCancelarListener(ActionListener listener) {
        bCancelar.addActionListener(listener);
    }
    
}