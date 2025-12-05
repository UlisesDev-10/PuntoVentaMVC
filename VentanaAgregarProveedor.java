package Vista;
import javax.swing.JInternalFrame;
import javax.swing.*;
import java.awt.*;

public class VentanaAgregarProveedor extends JDialog {
    private JTextField txtId;
    private JTextField txtNombre;
    private JButton btnGuardar;
    private JButton btnCancelar;

    public VentanaAgregarProveedor(JFrame parent, String titulo) {
        super(parent, titulo, true);
        setSize(300, 200);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(4, 2, 5, 5));

        add(new JLabel("ID Proveedor:"));
        txtId = new JTextField();
        add(txtId);

        add(new JLabel("Nombre del proveedor:"));
        txtNombre = new JTextField();
        add(txtNombre);

        btnGuardar = new JButton("Guardar");
        add(btnGuardar);

        btnCancelar = new JButton("Cancelar");
        add(btnCancelar);

        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        // Agregamos listener para manejar el cierre
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                // Solo ocultamos la ventana sin destruirla
                setVisible(false);
            }
        });
    }

    public JTextField getTxtId() { return txtId; }
    public JTextField getTxtNombre() { return txtNombre; }
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnCancelar() { return btnCancelar; }
}