package Controlador;

import Modelo.CajaEstado;
import Modelo.Usuario;
import Vista.LoginView;
import Vista.VistaMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ControladorMenu implements ActionListener {

    private VistaMenu miVentana;
    private Usuario usuarioActual;
    private CajaEstado estadoCaja;
    
    private ControladorCaja controladorCaja;

    private ControladorCancelacionVenta controladorCancelacionVenta;
 

    public ControladorMenu(Usuario usuario) {
        this.usuarioActual = usuario;
        miVentana = new VistaMenu();
        miVentana.setTitulo("Sistema de Punto de Venta - Usuario: " + usuarioActual.getUsuario() + " (" + usuarioActual.getRol() + ")");
        miVentana.getFrame().setLocationRelativeTo(null);
        estadoCaja = new CajaEstado();
        
        miVentana.getFrame().setVisible(true);
        agregarListeners();

        // Configurar permisos según el rol del usuario
        configurarPermisosSegunRol();
    }
    
    private void agregarListeners() {
        // Asignar listeners a todos los elementos del menú
        miVentana.getMoproductos().addActionListener(this);
        miVentana.getMoventas().addActionListener(this);
        miVentana.getMocategorias().addActionListener(this);
        miVentana.getMoprovedores().addActionListener(this);
        miVentana.getSalida().addActionListener(this);
        miVentana.getMcaja().addActionListener(this);
        miVentana.getMusuarios().addActionListener(this);
        miVentana.getMenuReportes().addActionListener(this);
        miVentana.getMoinventarios().addActionListener(this);
     
        miVentana.getMCancelav().addActionListener(this);
        miVentana.getMgestion().addActionListener(this);

        // Asegurarse de agregar listeners a todos los elementos posibles
        if (miVentana.getMoTipoVenta() != null) {
            miVentana.getMoTipoVenta().addActionListener(this);
        }

        // Agregar listener para el menú de reportes
        if (miVentana.getMenuReportes() != null) {
            miVentana.getMenuReportes().addActionListener(this);
        }
    
        // Comprobar si se asignaron correctamente
        System.out.println("✅ Listeners asignados correctamente");
        System.out.println("🔵 Rol del usuario: " + usuarioActual.getRol());
    }

    private void configurarPermisosSegunRol() {
        String rol = usuarioActual.getRol().toLowerCase();

        // Por defecto, mostrar todo para administradores
        if (rol.equals("administrador")) {
            // El administrador tiene acceso a todo
            return;
        }

        // Para vendedores, limitar acceso a ciertas funciones
        if (rol.equals("vendedor")) {
            // Desactivar opciones que no debería ver un vendedor
         
            
    
            // Aquí podrías agregar más restricciones según necesites
        }
        else if (rol.equals("supervisor")) {
            // Desactivar opciones que no debería ver un supervisor

        }
        // Podrías agregar más roles y sus respectivos permisos aquí
    }
    

    public VistaMenu getMiVentana() {
        return miVentana;
    }
    
    public VistaMenu getVentana() {
        return miVentana;
    }
    
    /**
     * Verifica si el usuario actual tiene permiso para acceder a una funcionalidad específica
     * @param funcionalidad El nombre de la funcionalidad a verificar
     * @return true si tiene permiso, false en caso contrario
     */
    private boolean tienePermiso(String funcionalidad) {
        String rol = usuarioActual.getRol().toLowerCase();
        
        // El administrador siempre tiene acceso a todo
        if (rol.equals("administrador")) {
            return true;
        }
        
        // Definir permisos por rol
        switch (funcionalidad) {
            case "productos":
            	return rol.equals("administrador") || rol.equals("inventarista")|| rol.equals("supervisor"); // Todos pueden ver productos
            case "ventas":
            	return rol.equals("administrador") || rol.equals("supervisor") || rol.equals("vendedor"); // Todos pueden ver ventas
            case "categorias":
                return rol.equals("administrador") || rol.equals("supervisor");
            case "proveedores":
                return rol.equals("administrador") || rol.equals("supervisor") || rol.equals("inventarista");
            case "usuarios":
                return rol.equals("administrador");
            case "inventarios":
                return rol.equals("administrador") || rol.equals("inventarista")|| rol.equals("supervisor");
            case "reportes":
                return rol.equals("administrador") || rol.equals("supervisor");
            case "cancelar venta":
                return rol.equals("administrador") || rol.equals("supervisor") || rol.equals("vendedor");
            default:
                return false;
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == miVentana.getBtnProductos()) {
            try {
                // Verificar permisos antes de abrir la ventana
                if (!tienePermiso("productos")) {
                    JOptionPane.showMessageDialog(miVentana.getFrame(),
                            "No tiene permisos para acceder a esta funcionalidad.",
                            "Acceso denegado", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // El controlador se encarga de agregar la ventana al escritorio
                new ControladorProductos(miVentana);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(miVentana.getFrame(), 
                        "Error al abrir ventana de productos: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
        else if (e.getSource() == miVentana.getBtnVentas()) {
            // Crear y mostrar controlador de ventas
            try {
                if (!tienePermiso("ventas")) {
                    JOptionPane.showMessageDialog(miVentana.getFrame(),
                            "No tiene permisos para acceder a esta funcionalidad.",
                            "Acceso denegado", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                miVentana.Menus(true);
                ControladorVentas controladorVentas = new ControladorVentas(this);
                // No es necesario agregar la ventana aquí porque el ControladorVentas ya lo hace
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(miVentana.getFrame(), 
                        "Error al abrir ventana de ventas: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
        else if (e.getSource() == miVentana.getBtnCaja()) {
            if (!tienePermiso("ventas")) {
                JOptionPane.showMessageDialog(miVentana.getFrame(),
                        "No tiene permisos para acceder a esta funcionalidad.",
                        "Acceso denegado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!estadoCaja.estaAbierta()) {
                JOptionPane.showMessageDialog(miVentana.getFrame(), 
                        "La caja está cerrada. Debe abrir la caja primero.",
                        "Caja Cerrada", JOptionPane.WARNING_MESSAGE);
                return;
            }
            miVentana.Menus(true);
            // Crear controlador de caja pasando la referencia correcta
            controladorCaja = new ControladorCaja(this);
            // Agregar manualmente al escritorio
           
            controladorCaja.getVentana().setVisible(true);
        }
        else if (e.getSource() == miVentana.getBtnCategorias()) {
            if (!tienePermiso("categorias")) {
                JOptionPane.showMessageDialog(miVentana.getFrame(),
                        "No tiene permisos para acceder a esta funcionalidad.",
                        "Acceso denegado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            miVentana.Menus(false);
            ControladorCategoria control = new ControladorCategoria(miVentana);
            miVentana.getEscritorio().add(control.getVentana());
            try {
                control.getVentana().setMaximum(true);
            } catch (java.beans.PropertyVetoException ex) {
                ex.printStackTrace();
            }
        }
        else if (e.getSource() == miVentana.getBtnUsuarios()) {
            if (!tienePermiso("usuarios")) {
                JOptionPane.showMessageDialog(miVentana.getFrame(),
                        "No tiene permisos para acceder a esta funcionalidad.",
                        "Acceso denegado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            miVentana.Menus(false);
            ControladorUsuarios control = new ControladorUsuarios(miVentana);
            miVentana.getEscritorio().add(control.getVentana());
            try {
                control.getVentana().setMaximum(true);
            } catch (java.beans.PropertyVetoException ex) {
                ex.printStackTrace();
            }
        }
        else if (e.getSource() == miVentana.getMoTipoVenta()) {
            if (!tienePermiso("ventas")) {
                JOptionPane.showMessageDialog(miVentana.getFrame(),
                        "No tiene permisos para acceder a esta funcionalidad.",
                        "Acceso denegado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            miVentana.Menus(false);
            ControladorTipoVenta control = new ControladorTipoVenta(miVentana);
            miVentana.getEscritorio().add(control.getVentana());
            try {
                control.getVentana().setMaximum(true);
            } catch (java.beans.PropertyVetoException ex) {
                ex.printStackTrace();
            }
        }
        else if (e.getSource() == miVentana.getBtnInventario()) {
            if (!tienePermiso("inventarios")) {
                JOptionPane.showMessageDialog(miVentana.getFrame(),
                        "No tiene permisos para acceder a esta funcionalidad.",
                        "Acceso denegado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            miVentana.Menus(false);
            ControladorInventario control = new ControladorInventario(miVentana);
            miVentana.getEscritorio().add(control.getVentana());
            try {
                control.getVentana().setMaximum(true);
            } catch (java.beans.PropertyVetoException ex) {
                ex.printStackTrace();
            }
        }
        else if (e.getSource() == miVentana.getBtnProveedores()) {
            if (!tienePermiso("proveedores")) {
                JOptionPane.showMessageDialog(miVentana.getFrame(),
                        "No tiene permisos para acceder a esta funcionalidad.",
                        "Acceso denegado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            miVentana.Menus(false);
            ControladorProveedores control = new ControladorProveedores(miVentana);
            miVentana.getEscritorio().add(control.getVentana());
            try {
                control.getVentana().setMaximum(true);
            } catch (java.beans.PropertyVetoException ex) {
                ex.printStackTrace();
            }
        }
        else if (e.getSource() == miVentana.getBtnReportes()) {
            if (!tienePermiso("reportes")) {
                JOptionPane.showMessageDialog(miVentana.getFrame(),
                        "No tiene permisos para acceder a esta funcionalidad.",
                        "Acceso denegado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Abrir módulo de reportes
            abrirReportes();
        }
        else if (e.getSource() == miVentana.getBtnCancelarVenta()) {
            try {
                if (!tienePermiso("cancelar venta")) {
                    JOptionPane.showMessageDialog(miVentana.getFrame(),
                            "No tiene permisos para acceder a esta funcionalidad.",
                            "Acceso denegado", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                miVentana.Menus(true);
                if (controladorCancelacionVenta == null) {
                    controladorCancelacionVenta = new ControladorCancelacionVenta(this);
                }
                miVentana.mostrarVentana(controladorCancelacionVenta.getVentana());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(miVentana.getFrame(),
                    "Error al abrir la ventana de devolución de ventas: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
        else if (e.getSource() == miVentana.getBtnSalir()) {
            cerrarSesion();
        }
        // Nota: Se eliminaron las condiciones que utilizaban métodos no implementados
        // Si deseas implementar estas funcionalidades, deberás agregar los botones
        // correspondientes en la clase VistaMenu y sus respectivos getters
    }
    
    
    
            private void abrirReportes() {
        // Crear una nueva instancia del controlador de reportes
        ControladorMenuReportes controladorMenuReportes = new ControladorMenuReportes(this);
        
        // Obtener la ventana y traerla al frente
        JInternalFrame ventanaReportes = controladorMenuReportes.getVentana();
        if (ventanaReportes != null) {
            ventanaReportes.toFront();
        }
            }
            
    private void cerrarSesion() {
        int respuesta = JOptionPane.showConfirmDialog(miVentana.getFrame(), 
                "¿Está seguro que desea cerrar sesión?", 
                "Cerrar Sesión", JOptionPane.YES_NO_OPTION);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            // Verificar si la caja está abierta
            if (estadoCaja.estaAbierta()) {
                JOptionPane.showMessageDialog(miVentana.getFrame(), 
                        "Debe realizar el cierre de caja antes de salir.", 
                        "Caja Abierta", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Cerrar la ventana actual
            miVentana.getFrame().dispose();
            
            // Crear y mostrar la ventana de login
            SwingUtilities.invokeLater(() -> {
                LoginView loginView = new LoginView();
                loginView.setVisible(true);
                // Inicializa el controlador de login
            });
        }
    }
        
        /**
         * Desbloquea la interfaz principal garantizando que todos los menús estén habilitados
         */
        public void desbloquearInterfaz() {
            if (miVentana != null) {
                miVentana.desbloquearInterfaz();
                System.out.println("ControladorMenu: Interfaz desbloqueada");
            }
        }
    
    
    
    public void actualizarEstadoCaja(boolean abierta) {
        estadoCaja.setAbierta(abierta);
    }
    
    public boolean estaCajaAbierta() {
        return estadoCaja.estaAbierta();
    }
    
    /**
     * Devuelve el objeto que representa el estado de la caja
     * @return Estado de la caja
     */
    public CajaEstado getEstadoCaja() {
        return this.estadoCaja;
    }
    
    /**
     * Devuelve el nombre de usuario del usuario actual
     * @return Nombre de usuario
     */
    public String getNombreUsuarioActual() {
        if (usuarioActual != null) {
            return usuarioActual.getUsuario();
        }
        return "";
    }
     
} 
    
    
