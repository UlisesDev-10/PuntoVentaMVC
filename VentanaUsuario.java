package Vista;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class VentanaUsuario extends JInternalFrame {
    private JButton Bsalida;
    private JButton Bagregar;
    private JButton Bmodificar;
    private JButton Beliminar;

    public VentanaUsuario() {
        super("Administración de Usuarios", true, true, true, true); // Set title and properties
        setSize(400, 300); // Set default size
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Cambiamos para controlar el cierre
        
        // Agregamos un listener personalizado para manejar el cierre
        this.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
                // Ocultamos la ventana sin destruirla
                setVisible(false);
                
                // Desbloqueamos la ventana principal
                desbloquearVentanaPrincipal();
            }
        });

        JPanel panel = new JPanel();
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[]{47, 53, 71, 77, 73, 0};
        gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 23, 0};
        gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        panel.setLayout(gbl_panel);
        Bagregar = new JButton("Agregar");
        Bagregar.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	}
        });
        GridBagConstraints gbc_bagregar = new GridBagConstraints();
        gbc_bagregar.anchor = GridBagConstraints.NORTHWEST;
        gbc_bagregar.insets = new Insets(0, 0, 0, 5);
        gbc_bagregar.gridx = 1;
        gbc_bagregar.gridy = 8;
        panel.add(Bagregar, gbc_bagregar);
        Bmodificar = new JButton("Modificar");
        GridBagConstraints gbc_bmodificar = new GridBagConstraints();
        gbc_bmodificar.anchor = GridBagConstraints.NORTHWEST;
        gbc_bmodificar.insets = new Insets(0, 0, 0, 5);
        gbc_bmodificar.gridx = 2;
        gbc_bmodificar.gridy = 8;
        panel.add(Bmodificar, gbc_bmodificar);

        getContentPane().add(panel); // Add panel to the frame
        Beliminar = new JButton("Eliminar");
        GridBagConstraints gbc_beliminar = new GridBagConstraints();
        gbc_beliminar.insets = new Insets(0, 0, 0, 5);
        gbc_beliminar.anchor = GridBagConstraints.NORTHWEST;
        gbc_beliminar.gridx = 3;
        gbc_beliminar.gridy = 8;
        panel.add(Beliminar, gbc_beliminar);
        Bsalida = new JButton("Salir");
        
                GridBagConstraints gbc_bsalida = new GridBagConstraints();
                gbc_bsalida.anchor = GridBagConstraints.NORTHWEST;
                gbc_bsalida.gridx = 4;
                gbc_bsalida.gridy = 8;
                panel.add(Bsalida, gbc_bsalida);
    }

    public JButton getBsalida() {
        return Bsalida;
    }

    public JButton getBagregar() {
        return Bagregar;
    }

    public JButton getBmodificar() {
        return Bmodificar;
    }

    public JButton getBeliminar() {
        return Beliminar;
    }
    
    /**
     * Método para desbloquear la ventana principal (VistaMenu)
     * cuando se cierra esta ventana
     */
    private void desbloquearVentanaPrincipal() {
        try {
            // Intentar obtener el escritorio (JDesktopPane)
            Container parent = this.getParent();
            if (parent instanceof JDesktopPane) {
                JDesktopPane desktop = (JDesktopPane) parent;
                
                // Intentar obtener el frame principal que contiene el escritorio
                Container topParent = desktop.getParent();
                while (topParent != null && !(topParent instanceof JFrame)) {
                    topParent = topParent.getParent();
                }
                
                if (topParent instanceof JFrame) {
                    JFrame mainFrame = (JFrame) topParent;
                    
                    // Habilitar el frame principal
                    mainFrame.setEnabled(true);
                    
                    // Si podemos encontrar la instancia de VistaMenu, mejor
                    for (java.awt.Component comp : mainFrame.getContentPane().getComponents()) {
                        if (comp instanceof JMenuBar) {
                            JMenuBar menuBar = (JMenuBar) comp;
                            menuBar.setEnabled(true);
                            
                            // Habilitar todos los menús
                            for (int i = 0; i < menuBar.getMenuCount(); i++) {
                                JMenu menu = menuBar.getMenu(i);
                                menu.setEnabled(true);
                            }
                        }
                    }
                    
                    // Forzar repintado
                    mainFrame.repaint();
                }
            }
        } catch (Exception ex) {
            System.err.println("Error al desbloquear ventana principal: " + ex.getMessage());
        }
        
        System.out.println("Ventana Usuario cerrada. Interfaz principal desbloqueada.");
    }
}