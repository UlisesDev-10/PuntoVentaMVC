

package Vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import Controlador.ControladorMenu;
import Modelo.BaseDatos;
import Modelo.Usuario;

import java.util.ArrayList;

public class LoginView extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JLabel lblError;

    public LoginView() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        setLocationRelativeTo(null);
        setTitle("LOGIN");
        getContentPane().setLayout(null);
        
        JPanel panel = new JPanel();
        panel.setBackground(new Color(112, 128, 144));
        panel.setBounds(0, 0, 436, 51);
        getContentPane().add(panel);
        
        JLabel lblNewLabel = new JLabel("PUNTO DE VENTA");
        lblNewLabel.setForeground(new Color(255, 255, 255));
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        panel.add(lblNewLabel);
        
        JLabel lblNewLabel_1 = new JLabel("Usuario:");
        lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblNewLabel_1.setBounds(85, 105, 75, 14);
        getContentPane().add(lblNewLabel_1);
        
        txtUsuario = new JTextField();
        txtUsuario.setBounds(161, 104, 121, 20);
        getContentPane().add(txtUsuario);
        txtUsuario.setColumns(10);
        
        JLabel lblNewLabel_2 = new JLabel("Contraseña:");
        lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblNewLabel_2.setBounds(76, 143, 84, 14);
        getContentPane().add(lblNewLabel_2);
        
        txtPassword = new JPasswordField();
        txtPassword.setBounds(161, 142, 121, 20);
        getContentPane().add(txtPassword);
        
        lblError = new JLabel("");
        lblError.setForeground(Color.RED);
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        lblError.setBounds(50, 175, 350, 20);
        getContentPane().add(lblError);
        
        JButton btnIngresar = new JButton("INGRESAR");
        btnIngresar.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnIngresar.setBounds(95, 208, 109, 23);
        getContentPane().add(btnIngresar);
        
        JButton btnSalir = new JButton("SALIR");
        btnSalir.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnSalir.setBounds(231, 208, 89, 23);
        getContentPane().add(btnSalir);
        
        // Agregar acción al botón Ingresar
        btnIngresar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                validarLogin();
            }
        });
        
        // Agregar acción al botón Salir
        btnSalir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        // Permitir presionar Enter para enviar el formulario
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    validarLogin();
                }
            }
        });
    }
    
    private void validarLogin() {
        String usuario = txtUsuario.getText();
        String password = new String(txtPassword.getPassword());
        
        if (usuario.isEmpty() || password.isEmpty()) {
            lblError.setText("Por favor ingrese usuario y contraseña");
            return;
        }
        
        try {
            // Validar usuario contra la base de datos
            BaseDatos bd = new BaseDatos();
            ArrayList<String[]> resultados = bd.consultar("Usuarios", "*", 
                    "usuario = '" + usuario + "' AND contraseña = '" + password + "'");
            
            if (resultados != null && resultados.size() > 0) {
                // Usuario autenticado
                String[] datosUsuario = resultados.get(0);
                Usuario usuarioAutenticado = new Usuario(datosUsuario[0], datosUsuario[1], datosUsuario[2]);
                
                // Imprimir información para debug
                System.out.println("-------------------------------------");
                System.out.println("✅ Login exitoso:");
                System.out.println("🧑 Usuario: " + usuarioAutenticado.getUsuario());
                System.out.println("🔑 Rol: " + usuarioAutenticado.getRol());
                System.out.println("-------------------------------------");
                
                // Ocultar ventana de login
                this.dispose();
                
                // Mostrar ventana principal con el usuario autenticado
                ControladorMenu menu = new ControladorMenu(usuarioAutenticado);
            } else {
                lblError.setText("Usuario o contraseña incorrectos");
                txtPassword.setText("");
                System.out.println("❌ Login fallido para usuario: " + usuario);
            }
            
            bd.cerrarConexion();
        } catch (Exception e) {
            lblError.setText("Error al conectar con la base de datos");
            System.err.println("❌ Error en validarLogin: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
