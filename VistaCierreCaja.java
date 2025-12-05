package Vista;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

import Vista.VistaTicket;

public class VistaCierreCaja extends JInternalFrame {
    
    private JTextField tFondoInicial, tVentasTotal, tEfectivo, tTarjeta, tDiferencia, tTotal, tGanancias;
    private JTextField tVentasEfectivo, tVentasTarjeta, tVentasTransferencia, tDineroEfectivo;
    private JButton bCerrarCaja, bCancelar;
    private JTable tablaResumen;
    private JLabel lFecha, lHora, lCajero;
    private VistaTicket vistaTicket;
    
    public VistaCierreCaja() {
        super("Cierre de Caja", true, true, true, true);
        this.setSize(600, 550);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        iniciarComponentes();
    }
    
    private void iniciarComponentes() {
        // Definir fuentes para consistencia
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
        
        // Inicializar los campos para ventas por tipo de pago
        tVentasEfectivo = new JTextField("0.00");
        tVentasTarjeta = new JTextField("0.00");
        tVentasTransferencia = new JTextField("0.00");
        
        setLayout(new BorderLayout(10, 10));
        
        // Panel superior con el título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(142, 68, 173));
        panelTitulo.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelTitulo.setLayout(new BorderLayout());
        
        JLabel lblTitulo = new JLabel("CIERRE DE CAJA");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelTitulo.add(lblTitulo, BorderLayout.CENTER);
        
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central con contenido
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBorder(new EmptyBorder(15, 20, 15, 20));
        panelCentral.setBackground(Color.WHITE);
        
        // Panel de información
        JPanel panelInfo = new JPanel(new GridLayout(3, 2, 10, 5));
        panelInfo.setBackground(Color.WHITE);
        panelInfo.setBorder(new CompoundBorder(
                new TitledBorder("Información"),
                new EmptyBorder(5, 5, 5, 5)
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
        
        // Hacer que el panel de info tenga tamaño fijo
        panelInfo.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        panelCentral.add(panelInfo);
        
        // Espacio
        panelCentral.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Resumen de ventas
        String[] columnas = {"Tipo", "Cantidad", "Total"};
        Object[][] datos = {
            {"Ventas en Efectivo", "0", "$0.00"},
            {"Ventas con Tarjeta", "0", "$0.00"},
            {"Devoluciones", "0", "$0.00"},
            {"Cancelaciones", "0", "$0.00"}
        };
        
        tablaResumen = new JTable(datos, columnas);
        tablaResumen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaResumen.setRowHeight(25);
        tablaResumen.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaResumen.getTableHeader().setBackground(new Color(142, 68, 173));
        tablaResumen.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(tablaResumen);
        scrollPane.setBorder(new CompoundBorder(
                new TitledBorder("Resumen de Ventas"),
                new EmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.setMaximumSize(new Dimension(Short.MAX_VALUE, 150));
        panelCentral.add(scrollPane);
        
        // Espacio
        panelCentral.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Panel de balance
        JPanel panelBalance = new JPanel(new GridLayout(8, 2, 10, 10));
        panelBalance.setBackground(Color.WHITE);
        panelBalance.setBorder(new CompoundBorder(
                new TitledBorder("Balance de Caja"),
                new EmptyBorder(5, 5, 5, 5)
        ));
        
        // Fondo Inicial
        JLabel lblFondoInicial = new JLabel("Fondo Inicial:");
        lblFondoInicial.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelBalance.add(lblFondoInicial);
        
        tFondoInicial = new JTextField("0.00");
        tFondoInicial.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tFondoInicial.setEditable(false);
        tFondoInicial.setHorizontalAlignment(SwingConstants.RIGHT);
        panelBalance.add(tFondoInicial);
        
        // Ventas Totales
        JLabel lblVentasTotal = new JLabel("Ventas Totales:");
        lblVentasTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelBalance.add(lblVentasTotal);
        
        tVentasTotal = new JTextField("0.00");
        tVentasTotal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tVentasTotal.setEditable(false);
        tVentasTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        panelBalance.add(tVentasTotal);
        
        // Dinero en Efectivo
        JLabel lblDineroEfectivo = new JLabel("Dinero en Efectivo ($):");
        lblDineroEfectivo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelBalance.add(lblDineroEfectivo);
        
        tDineroEfectivo = new JTextField("0.00");
        tDineroEfectivo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tDineroEfectivo.setEditable(false);
        tDineroEfectivo.setHorizontalAlignment(SwingConstants.RIGHT);
        panelBalance.add(tDineroEfectivo);
        
        // Ganancias
        JLabel lblGanancias = new JLabel("Ganancias:");
        lblGanancias.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelBalance.add(lblGanancias);
        
        tGanancias = new JTextField("0.00");
        tGanancias.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tGanancias.setEditable(false);
        tGanancias.setHorizontalAlignment(SwingConstants.RIGHT);
        tGanancias.setForeground(new Color(46, 204, 113)); // Verde para las ganancias
        panelBalance.add(tGanancias);
        
        // Efectivo en Caja
        JLabel lblEfectivo = new JLabel("Efectivo en Caja:");
        lblEfectivo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelBalance.add(lblEfectivo);
        
        tEfectivo = new JTextField();
        tEfectivo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tEfectivo.setHorizontalAlignment(SwingConstants.RIGHT);
        panelBalance.add(tEfectivo);
        
        // Ventas con Tarjeta
        JLabel lblTarjeta = new JLabel("Ventas con Tarjeta:");
        lblTarjeta.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelBalance.add(lblTarjeta);
        
        tTarjeta = new JTextField("0.00");
        tTarjeta.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tTarjeta.setEditable(false);
        tTarjeta.setHorizontalAlignment(SwingConstants.RIGHT);
        panelBalance.add(tTarjeta);
        
        // Diferencia
        JLabel lblDiferencia = new JLabel("Diferencia:");
        lblDiferencia.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelBalance.add(lblDiferencia);
        
        tDiferencia = new JTextField("0.00");
        tDiferencia.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tDiferencia.setEditable(false);
        tDiferencia.setHorizontalAlignment(SwingConstants.RIGHT);
        panelBalance.add(tDiferencia);
        
        // Total en Caja
        JLabel lblTotal = new JLabel("TOTAL EN CAJA:");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelBalance.add(lblTotal);
        
        tTotal = new JTextField("0.00");
        tTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tTotal.setEditable(false);
        tTotal.setBackground(new Color(240, 240, 240));
        tTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        panelBalance.add(tTotal);
        
        panelCentral.add(panelBalance);
        
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotones.setBorder(new EmptyBorder(0, 0, 15, 0));
        panelBotones.setBackground(new Color(240, 240, 240));
        
        bCerrarCaja = new JButton("Cerrar Caja");
        bCerrarCaja.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bCerrarCaja.setBackground(new Color(142, 68, 173));
        bCerrarCaja.setForeground(Color.WHITE);
        bCerrarCaja.setPreferredSize(new Dimension(130, 40));
        bCerrarCaja.setFocusPainted(false);
        
        
        bCancelar = new JButton("Cancelar");
        bCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bCancelar.setBackground(new Color(231, 76, 60));
        bCancelar.setForeground(Color.WHITE);
        bCancelar.setPreferredSize(new Dimension(130, 40));
        bCancelar.setFocusPainted(false);
        
        panelBotones.add(bCerrarCaja);
        panelBotones.add(bCancelar);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    // Getters
    public JTextField getTFondoInicial() {
        return tFondoInicial;
    }
    
    public JTextField getTVentasTotal() {
        return tVentasTotal;
    }
        
    public JTextField getTTotalVentas() {
        return tVentasTotal; // Retorna el mismo campo que TVentasTotal
    }
        
    public JTextField getTGanancias() {
            return tGanancias;
        }
    
    public JTextField getTEfectivo() {
        return tEfectivo;
    }
    
    public JTextField getTTarjeta() {
        return tTarjeta;
    }
    
    public JTextField getTDiferencia() {
        return tDiferencia;
    }
    
    public JTextField getTTotal() {
        return tTotal;
    }
    
    public JButton getBtnCerrarCaja() {
        // Personalización del botón de cierre de caja
        if (bCerrarCaja != null) {
            bCerrarCaja.setBackground(new Color(221, 75, 57)); // Rojo
            bCerrarCaja.setForeground(Color.WHITE);
            bCerrarCaja.setFont(new Font("Segoe UI", Font.BOLD, 14));
            bCerrarCaja.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(172, 41, 37), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            bCerrarCaja.setFocusPainted(false);
        }
        return bCerrarCaja;
    }
    
    public JButton getBtnCancelar() {
        // Personalización del botón de cancelación
        if (bCancelar != null) {
            bCancelar.setBackground(new Color(236, 240, 245)); // Gris claro
            bCancelar.setForeground(new Color(60, 60, 60));
            bCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
            bCancelar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 214, 222), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            bCancelar.setFocusPainted(false);
        }
        return bCancelar;
    }
    
    
    public JButton getBtnCalcular() {
        return null; // Este botón no está definido en la vista, agregarlo si es necesario
    }
    
    public JButton getBtnImprimir() {
        return null; // Este botón ha sido eliminado pero se mantiene el método para compatibilidad
    }
    
    public JTable getTablaResumen() {
        return tablaResumen;
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
    
    // Método para actualizar los valores del balance
    public void actualizarBalance(double fondoInicial, double ventasTotal, double tarjeta) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        this.tFondoInicial.setText(df.format(fondoInicial));
        this.tVentasTotal.setText(df.format(ventasTotal));
        this.tTarjeta.setText(df.format(tarjeta));
        
        // Calcula y muestra el total automáticamente
        double total = fondoInicial + ventasTotal;
        this.tTotal.setText(df.format(total));
    }
    
    // Método para calcular diferencia
    public void calcularDiferencia() {
        try {
            double efectivo = Double.parseDouble(tEfectivo.getText().replace(",", ""));
            double fondoInicial = Double.parseDouble(tFondoInicial.getText().replace(",", ""));
            double ventasEfectivo = Double.parseDouble(tVentasTotal.getText().replace(",", "")) - 
                                   Double.parseDouble(tTarjeta.getText().replace(",", ""));
            
            double diferencia = efectivo - (fondoInicial + ventasEfectivo);
            
            DecimalFormat df = new DecimalFormat("#,##0.00");
            tDiferencia.setText(df.format(diferencia));
            
            // Cambia el color según si hay faltante o sobrante
            if (diferencia < 0) {
                tDiferencia.setForeground(new Color(231, 76, 60)); // Rojo para faltante
            } else if (diferencia > 0) {
                tDiferencia.setForeground(new Color(46, 204, 113)); // Verde para sobrante
            } else {
                tDiferencia.setForeground(Color.BLACK); // Negro si no hay diferencia
            }
            
        } catch (NumberFormatException e) {
            tDiferencia.setText("Error");
            tDiferencia.setForeground(Color.RED);
        }
    }
    
    // Getters para los campos de ventas por tipo de pago
    public JTextField getTVentasEfectivo() {
        return tVentasEfectivo;
    }
    
    public JTextField getTVentasTarjeta() {
        return tVentasTarjeta;
    }
    
    public JTextField getTVentasTransferencia() {
        return tVentasTransferencia;
    }
    
    public JTextField getTDineroEfectivo() {
        return tDineroEfectivo;
    }
}
