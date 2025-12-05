package Vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.*;
import Modelo.Usuario;
import java.util.List;

public class ModificarUsuarioBuscar extends JFrame {
    private JTextField txtUsuario;
    private JButton btnBuscar;
    private JPanel contentPane;

    private List<Usuario> listaUsuarios;

    public ModificarUsuarioBuscar(List<Usuario> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
        setTitle("Buscar Proveedor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 150);
        setLocationRelativeTo(null);
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));
        
        JPanel panelBusqueda = new JPanel();
        contentPane.add(panelBusqueda, BorderLayout.CENTER);
        
        JLabel nombre = new JLabel("Nombre usuario:");
        panelBusqueda.add(nombre);
        
        txtUsuario = new JTextField();
        panelBusqueda.add(txtUsuario);
        txtUsuario.setColumns(15);
        
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.RIGHT));
        contentPane.add(panelBotones, BorderLayout.SOUTH);
        
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buscarUsuario();
            }
        });
        panelBotones.add(btnBuscar);
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        panelBotones.add(btnCancelar);
    }
    
    private void buscarUsuario() {
        String nombre = txtUsuario.getText().trim();

        for (Usuario u : listaUsuarios) {
            if (u.getUsuario().equalsIgnoreCase(nombre)) {
                new ModificarUsuarioForm(u); // ← otra ventanita para modificar
                dispose();
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Usuario no encontrado");
    }
}