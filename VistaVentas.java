package Vista;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class VistaVentas extends JInternalFrame {
    
    private JButton bAbrirCaja, bCerrarCaja;
    private JButton bPagarTarjeta; // Nuevo botón para pago con tarjeta
    private JLabel lEstadoCaja;
    
    public VistaVentas() {
        super("Gestión de Ventas", true, true, true, true);
        this.setSize(600, 400);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        iniciarComponentes();
    }
    
    private void iniciarComponentes() {
        setLayout(new BorderLayout(10, 10));
        
        // Inicializar componentes para el pago
        bPagarTarjeta = new JButton("Pagar con Tarjeta");
        bPagarTarjeta.setFont(new Font("Segoe UI", Font.BOLD, 16));
        bPagarTarjeta.setBackground(new Color(41, 128, 185));
        bPagarTarjeta.setForeground(Color.WHITE);
        
        // Panel superior con el título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(41, 128, 185));
        panelTitulo.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelTitulo.setLayout(new BorderLayout());
        
        JLabel lblTitulo = new JLabel("GESTIÓN DE VENTAS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelTitulo.add(lblTitulo, BorderLayout.CENTER);
        
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central con información y botones
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBorder(new EmptyBorder(20, 30, 20, 30));
        panelCentral.setBackground(Color.WHITE);
        
        // Estado de caja
        JPanel panelEstado = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelEstado.setBackground(Color.WHITE);
        panelEstado.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel lblEstado = new JLabel("Estado de Caja:");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panelEstado.add(lblEstado);
        
        lEstadoCaja = new JLabel("CERRADA");
        lEstadoCaja.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lEstadoCaja.setForeground(new Color(231, 76, 60));
        panelEstado.add(lEstadoCaja);
        
        panelCentral.add(panelEstado);
        
        // Panel de botones para caja
        JPanel panelBotonesCaja = new JPanel();
        panelBotonesCaja.setLayout(new GridLayout(1, 2, 30, 0));
        panelBotonesCaja.setBackground(Color.WHITE);
        panelBotonesCaja.setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(new Color(52, 152, 219), 1), "Operaciones de Caja", 
                        TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)),
                new EmptyBorder(30, 50, 30, 50)
        ));
        
        // Botón Abrir Caja
        bAbrirCaja = new JButton("Abrir Caja");
        bAbrirCaja.setFont(new Font("Segoe UI", Font.BOLD, 16));
        bAbrirCaja.setBackground(new Color(46, 204, 113));
        bAbrirCaja.setForeground(Color.WHITE);
        bAbrirCaja.setFocusPainted(false);
        bAbrirCaja.setIcon(null); // Puedes agregar un icono si lo deseas
        panelBotonesCaja.add(bAbrirCaja);
        
        // Botón Cerrar Caja
        bCerrarCaja = new JButton("Cerrar Caja");
        bCerrarCaja.setFont(new Font("Segoe UI", Font.BOLD, 16));
        bCerrarCaja.setBackground(new Color(231, 76, 60));
        bCerrarCaja.setForeground(Color.WHITE);
        bCerrarCaja.setFocusPainted(false);
        bCerrarCaja.setIcon(null); // Puedes agregar un icono si lo deseas
        panelBotonesCaja.add(bCerrarCaja);
        
        panelCentral.add(panelBotonesCaja);
        
        // Espacio
        panelCentral.add(Box.createRigidArea(new Dimension(0, 20)));
        
        add(panelCentral, BorderLayout.CENTER);
    }
    
   
    // Getters
    public JButton getBtnAbrirCaja() {
        return bAbrirCaja;
    }
    
    public JButton getBtnCerrarCaja() {
        return bCerrarCaja;
    }
    
    public JLabel getLblEstadoCaja() {
        return lEstadoCaja;
    }
    
    // Método para actualizar el estado visual de la caja
    public void actualizarEstadoCaja(boolean abierta) {
        if (abierta) {
            lEstadoCaja.setText("ABIERTA");
            lEstadoCaja.setForeground(new Color(46, 204, 113));
            bAbrirCaja.setEnabled(false);
            bCerrarCaja.setEnabled(true);
        } else {
            lEstadoCaja.setText("CERRADA");
            lEstadoCaja.setForeground(new Color(231, 76, 60));
            bAbrirCaja.setEnabled(true);
            bCerrarCaja.setEnabled(false);
        }
    }
}
