package Vista;
import Modelo.Proveedor;
import Modelo.Usuario;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame; // Mantenido para compatibilidad del getter
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class VistaMenu {

	private JFrame frame;
	private JPanel panelNavegacion; // Panel lateral derecho
	private JButton btnVentas, btnCaja, btnCancelarVenta;
	private JButton btnProveedores, btnProductos, btnInventario;
	private JButton btnUsuarios, btnCategorias;
	private JButton btnReportes, btnVerDocumentos, btnSalir;
	private JDesktopPane Escritorio;
	
	// Referencias para compatibilidad con código existente
	private JMenuItem Moventas, MCancelav, Mdevolu, Moinventarios;
	private JMenuItem Mocategorias, Moproductos, Moprovedores;
	private JMenuItem MoTipoVenta, Musuarios, MoSalida, Mcaja;
	private JMenuItem menuReportes;
	private JMenu Mgestion; // Referencia legacy
	private List<Usuario> listaUsuarios = new ArrayList<>();
	private List<Proveedor> listaProveedores = new ArrayList<>();
	private Modelo.CajaEstado estadoCaja;

	// Colores para la interfaz - Tema Azul/Gris Profesional
	private final Color COLOR_FONDO = new Color(44, 62, 80); // Azul frío oscuro
	private final Color COLOR_PRIMARIO = new Color(236, 240, 241); // Gris claro para barra de menú
	private final Color COLOR_SECUNDARIO = new Color(52, 152, 219); // Azul brillante
	private final Color COLOR_TEXTO = new Color(44, 62, 80); // Gris oscuro para texto
	private final Color COLOR_BRANDING = new Color(46, 204, 113); // Verde esmeralda para branding

	public VistaMenu() {
		initialize();
		// Cargar la lista de proveedores desde la base de datos
		listaProveedores = Proveedor.obtenerTodos();
			// Inicializar el estado de caja
			this.estadoCaja = new Modelo.CajaEstado();
	}

	private void initialize() {
		// Configurar el estilo visual global
		
		// Configuración del JFrame principal
		frame = new JFrame("Sistema de Gestión Comercial");
		frame.setBounds(100, 100, 1024, 768);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBackground(COLOR_FONDO);

		// Crear panel de navegación lateral derecho
		crearPanelNavegacion();
		
		// Cambiar a BorderLayout para permitir la barra de estado
		frame.getContentPane().setLayout(new java.awt.BorderLayout());

		// Configuración del escritorio con fondo degradado azul/gris
		Escritorio = new JDesktopPane() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				// Degradado de azul oscuro a gris medio
				GradientPaint gp = new GradientPaint(0, 0, COLOR_FONDO, getWidth(), getHeight(), new Color(52, 73, 94));
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		frame.getContentPane().add(Escritorio, java.awt.BorderLayout.CENTER);
		
		// Agregar panel de navegación a la derecha
		frame.getContentPane().add(panelNavegacion, java.awt.BorderLayout.EAST);
		
		// Agregar barra de estado en la parte inferior
		agregarBarraEstado();
	}
	
	/**
	 * Crea el panel de navegación lateral derecho con botones verticales
	 */
	private void crearPanelNavegacion() {
		panelNavegacion = new JPanel();
		panelNavegacion.setLayout(new BoxLayout(panelNavegacion, BoxLayout.Y_AXIS));
		panelNavegacion.setBackground(COLOR_PRIMARIO);
		panelNavegacion.setPreferredSize(new Dimension(200, 0));
		
		// Aplicar borde separador visual
		panelNavegacion.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(127, 140, 141), 2),
			BorderFactory.createEmptyBorder(15, 10, 15, 10)
		));
		
		// Añadir espaciado inicial
		panelNavegacion.add(Box.createRigidArea(new Dimension(0, 10)));
		
		// SECCIÓN: VENTAS
		panelNavegacion.add(crearEtiquetaSeccion("VENTAS"));
		
		btnCaja = crearBotonNavegacion("💵 Caja", "Administrar caja");
		panelNavegacion.add(btnCaja);
		panelNavegacion.add(Box.createRigidArea(new Dimension(0, 5)));
		
		btnVentas = crearBotonNavegacion("🛒 Ventas", "Registrar ventas");
		panelNavegacion.add(btnVentas);
		panelNavegacion.add(Box.createRigidArea(new Dimension(0, 5)));
		
		btnCancelarVenta = crearBotonNavegacion("❌ Cancelar Venta", "Cancelar ventas");
		panelNavegacion.add(btnCancelarVenta);
		panelNavegacion.add(Box.createRigidArea(new Dimension(0, 15)));
		
		// SECCIÓN: PRODUCTOS
		panelNavegacion.add(crearEtiquetaSeccion("PRODUCTOS"));
		
		btnProveedores = crearBotonNavegacion("📦 Proveedores", "Gestionar proveedores");
		panelNavegacion.add(btnProveedores);
		panelNavegacion.add(Box.createRigidArea(new Dimension(0, 5)));
		
		btnProductos = crearBotonNavegacion("🏪 Productos", "Gestionar productos");
		panelNavegacion.add(btnProductos);
		panelNavegacion.add(Box.createRigidArea(new Dimension(0, 5)));
		
		btnInventario = crearBotonNavegacion("📋 Inventario", "Control de inventario");
		panelNavegacion.add(btnInventario);
		panelNavegacion.add(Box.createRigidArea(new Dimension(0, 15)));
		
		// SECCIÓN: CONFIGURACIÓN
		panelNavegacion.add(crearEtiquetaSeccion("CONFIGURACIÓN"));
		
		btnUsuarios = crearBotonNavegacion("👤 Usuarios", "Administrar usuarios");
		panelNavegacion.add(btnUsuarios);
		panelNavegacion.add(Box.createRigidArea(new Dimension(0, 5)));
		
		btnCategorias = crearBotonNavegacion("📏 Categorías", "Administrar categorías");
		panelNavegacion.add(btnCategorias);
		panelNavegacion.add(Box.createRigidArea(new Dimension(0, 15)));
		
		// SECCIÓN: REPORTES
		panelNavegacion.add(crearEtiquetaSeccion("REPORTES"));
		
		btnReportes = crearBotonNavegacion("📊 Generar Reportes", "Crear informes");
		panelNavegacion.add(btnReportes);
		panelNavegacion.add(Box.createRigidArea(new Dimension(0, 5)));
		
		btnVerDocumentos = crearBotonNavegacion("📄 Ver Documentos", "Ver tickets guardados");
		btnVerDocumentos.addActionListener(e -> {
		    try {
		        File carpetaReportes = new File("reportes");
		        if (!carpetaReportes.exists()) {
		            carpetaReportes.mkdir();
		            JOptionPane.showMessageDialog(frame, 
		                "No hay tickets guardados. La carpeta de reportes ha sido creada.", 
		                "Información", JOptionPane.INFORMATION_MESSAGE);
		        } else if (Desktop.isDesktopSupported()) {
		            Desktop.getDesktop().open(carpetaReportes);
		        } else {
		            JOptionPane.showMessageDialog(frame, 
		                "No se puede abrir la carpeta automáticamente.", 
		                "Información", JOptionPane.INFORMATION_MESSAGE);
		        }
		    } catch (Exception ex) {
		        JOptionPane.showMessageDialog(frame, 
		            "Error al abrir la carpeta de reportes.", 
		            "Error", JOptionPane.ERROR_MESSAGE);
		    }
		});
		panelNavegacion.add(btnVerDocumentos);
		panelNavegacion.add(Box.createRigidArea(new Dimension(0, 15)));
		
		// Botón de salida al final
		panelNavegacion.add(Box.createVerticalGlue());
		btnSalir = crearBotonNavegacion("⛔ Salir", "Cerrar aplicación");
		btnSalir.setBackground(new Color(231, 76, 60));
		btnSalir.setForeground(Color.WHITE);
		panelNavegacion.add(btnSalir);
		panelNavegacion.add(Box.createRigidArea(new Dimension(0, 10)));
		
		// Crear referencias de compatibilidad (wrappers)
		crearReferenciaCompatibilidad();
	}
	
	/**
	 * Crea un botón de navegación con estilo consistente
	 */
	private JButton crearBotonNavegacion(String texto, String tooltip) {
		JButton boton = new JButton(texto);
		boton.setFont(new Font("Arial", Font.PLAIN, 13));
		boton.setForeground(COLOR_TEXTO);
		boton.setBackground(Color.WHITE);
		boton.setFocusPainted(false);
		boton.setBorderPainted(false);
		boton.setAlignmentX(Component.CENTER_ALIGNMENT);
		boton.setMaximumSize(new Dimension(180, 35));
		boton.setToolTipText(tooltip);
		boton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		
		// Efecto hover
		boton.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				boton.setBackground(COLOR_SECUNDARIO);
				boton.setForeground(Color.WHITE);
			}
			public void mouseExited(java.awt.event.MouseEvent evt) {
				boton.setBackground(Color.WHITE);
				boton.setForeground(COLOR_TEXTO);
			}
		});
		
		return boton;
	}
	
	/**
	 * Crea una etiqueta de sección para organizar los botones
	 */
	private JLabel crearEtiquetaSeccion(String texto) {
		JLabel etiqueta = new JLabel(texto);
		etiqueta.setFont(new Font("Arial", Font.BOLD, 11));
		etiqueta.setForeground(new Color(127, 140, 141));
		etiqueta.setAlignmentX(Component.CENTER_ALIGNMENT);
		return etiqueta;
	}
	
	/**
	 * Crea referencias de compatibilidad con el código existente
	 * Simula JMenuItems para que el controlador funcione sin cambios
	 */
	private void crearReferenciaCompatibilidad() {
		// Validar que todos los botones estén creados antes de envolver
		if (btnCaja == null || btnVentas == null || btnCancelarVenta == null ||
		    btnProveedores == null || btnProductos == null || btnInventario == null ||
		    btnUsuarios == null || btnCategorias == null || btnReportes == null || btnSalir == null) {
		    throw new IllegalStateException("Todos los botones deben estar inicializados antes de crear wrappers");
		}
		
		Mcaja = new ButtonWrapper(btnCaja);
		Moventas = new ButtonWrapper(btnVentas);
		MCancelav = new ButtonWrapper(btnCancelarVenta);
		Moprovedores = new ButtonWrapper(btnProveedores);
		Moproductos = new ButtonWrapper(btnProductos);
		Moinventarios = new ButtonWrapper(btnInventario);
		Musuarios = new ButtonWrapper(btnUsuarios);
		Mocategorias = new ButtonWrapper(btnCategorias);
		menuReportes = new ButtonWrapper(btnReportes);
		MoSalida = new ButtonWrapper(btnSalir);
		
		// Inicializar referencias que no tienen botón correspondiente como stubs
		// Para evitar NullPointerException si el controlador las llama
		Mdevolu = new JMenuItem(); // Stub - funcionalidad no implementada en panel lateral
		Mdevolu.setEnabled(false);
		
		MoTipoVenta = new JMenuItem(); // Stub - funcionalidad no implementada en panel lateral
		MoTipoVenta.setEnabled(false);
		
		Mgestion = new JMenu(); // Stub - menú convertido a botones individuales
		Mgestion.setEnabled(false);
	}
	
	/**
	 * Clase auxiliar para mantener compatibilidad con JMenuItem
	 */
	private class ButtonWrapper extends JMenuItem {
		private final JButton boton;
		
		public ButtonWrapper(JButton boton) {
			if (boton == null) {
				throw new IllegalArgumentException("El botón no puede ser null en ButtonWrapper");
			}
			this.boton = boton;
		}
		
		@Override
		public void addActionListener(java.awt.event.ActionListener l) {
			if (boton != null && l != null) {
				boton.addActionListener(l);
			}
		}
		
		@Override
		public void setEnabled(boolean b) {
			if (boton != null) {
				boton.setEnabled(b);
			}
		}
		
		@Override
		public boolean isEnabled() {
			return boton != null && boton.isEnabled();
		}
	}
	
	/**
	 * Agrega una barra de estado en la parte inferior de la ventana
	 * con el branding personalizado del sistema
	 */
	private void agregarBarraEstado() {
		JPanel barraEstado = new JPanel();
		barraEstado.setLayout(new java.awt.BorderLayout());
		barraEstado.setBackground(new Color(33, 47, 61)); // Azul oscuro
		barraEstado.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
		
		// Etiqueta de branding con fuente en negrita y color verde esmeralda
		JLabel lblBranding = new JLabel("Sistema ULISE | Ingeniería en Sistemas");
		lblBranding.setFont(new Font("Arial", Font.BOLD, 13));
		lblBranding.setForeground(COLOR_BRANDING);
		
		barraEstado.add(lblBranding, java.awt.BorderLayout.WEST);
		
		// Agregar la barra al sur del frame
		frame.getContentPane().add(barraEstado, java.awt.BorderLayout.SOUTH);
	}
	
	

	// Getters para los botones reales (necesarios para comparación en actionPerformed)
	public JButton getBtnCaja() {
		return btnCaja;
	}
	
	public JButton getBtnVentas() {
		return btnVentas;
	}
	
	public JButton getBtnCancelarVenta() {
		return btnCancelarVenta;
	}
	
	public JButton getBtnProveedores() {
		return btnProveedores;
	}
	
	public JButton getBtnProductos() {
		return btnProductos;
	}
	
	public JButton getBtnInventario() {
		return btnInventario;
	}
	
	public JButton getBtnUsuarios() {
		return btnUsuarios;
	}
	
	public JButton getBtnCategorias() {
		return btnCategorias;
	}
	
	public JButton getBtnReportes() {
		return btnReportes;
	}
	
	public JButton getBtnVerDocumentos() {
		return btnVerDocumentos;
	}
	
	public JButton getBtnSalir() {
		return btnSalir;
	}
	
	public List<Usuario> getListaUsuarios() {
		return listaUsuarios;
	}

	public List<Proveedor> getListaProveedores() {
		return listaProveedores;
	}

	public JMenuItem getMoinventarios() {
		return Moinventarios;
	}

	public JMenuItem getMoventas() {
		return Moventas;
	}

	public JMenuItem getMusuarios() {
		return Musuarios;
	}

	public JMenuItem getMoproductos() {
		return Moproductos;
	}

	public JMenuItem getMcaja() {
		return Mcaja;
	}

	public JMenuItem getMenuReportes() {
	    return menuReportes;
	}

	public void setMcaja(JMenuItem mcaja) {
		Mcaja = mcaja;
	}

	public JFrame getFrame()
	{ return this.frame;
	}
	
	/**
	 * Obtiene referencia al estado de caja actual
	 */
	public Modelo.CajaEstado getEstadoCaja() {
	    return this.estadoCaja;
	}

	/**
	 * Establece el estado de caja
	 * @param estadoCaja El nuevo estado de caja
	 */
	public void setEstadoCaja(Modelo.CajaEstado estadoCaja) {
	    this.estadoCaja = estadoCaja;
	}
	
	public JMenuItem getSalida()
	{
		return this.MoSalida;
	}
	public void setTitulo(String titulo)
	{
		this.frame.setTitle(titulo);
	}

	public  JDesktopPane getEscritorio()
	{
		return this.Escritorio;
	}
	
	

	public JMenu getMgestion() {
		return Mgestion; // Retorna null o referencia legacy
	}

	public void setMgestion(JMenu mgestion) {
		Mgestion = mgestion;
	}

	public JMenuItem getMCancelav() {
		return MCancelav;
	}

	public void setMCancelav(JMenuItem mCancelav) {
		MCancelav = mCancelav;
	}

	public JMenuItem getMdevolu() {
		return Mdevolu;
	}

	public void setMdevolu(JMenuItem mdevolu) {
		Mdevolu = mdevolu;
	}

	public JMenuItem getMoprovedores() {
		return this.Moprovedores;
	}

	public JMenuItem getMocategorias() {
		return this.Mocategorias;
	}

	public JMenuItem getMoTipoVenta() {
		return MoTipoVenta;
	}

	public JMenuBar getBarraMenu() {
		return null; // La barra de menú ya no existe, se usó panel lateral
	}

	public void Menus(Boolean accion)
	{
		// Habilitar/deshabilitar todos los botones de navegación
		btnCaja.setEnabled(accion);
		btnVentas.setEnabled(accion);
		btnCancelarVenta.setEnabled(accion);
		btnProveedores.setEnabled(accion);
		btnProductos.setEnabled(accion);
		btnInventario.setEnabled(accion);
		btnUsuarios.setEnabled(accion);
		btnCategorias.setEnabled(accion);
		btnReportes.setEnabled(accion);
		btnVerDocumentos.setEnabled(accion);
		btnSalir.setEnabled(accion);
	}
		
		/**
		 * Desbloquea completamente la interfaz del menú principal
		 * Asegura que todos los componentes de navegación estén habilitados
		 */
		public void desbloquearInterfaz() {
			// Habilitar el frame principal
			this.frame.setEnabled(true);
			
			// Habilitar el panel de navegación
			if (this.panelNavegacion != null) this.panelNavegacion.setEnabled(true);
			
			// Habilitar todos los botones
			Menus(true);
			
			// Refrescar la interfaz
			this.frame.repaint();
			if (this.panelNavegacion != null) this.panelNavegacion.repaint();
		}
	
	/**
	 * Configura una ventana interna para que no se cierre completamente al hacer clic en la X
	 * @param ventana Ventana interna a configurar
	 */
	
	/**
	 * Muestra una ventana interna en el escritorio
	 * @param ventana Ventana interna a mostrar
	 */
		public void mostrarVentana(JInternalFrame ventana) {
		    if (!ventana.isVisible()) {
		        if (ventana.getParent() == null) {
		            this.Escritorio.add(ventana);
		        }
		        ventana.setVisible(true);
		    }
		    try {
		        ventana.setIcon(false);    // Por si está minimizada
		        ventana.setMaximum(true);  // Maximizar automáticamente
		        ventana.setSelected(true); // Traerla al frente
		        ventana.toFront();         // Otra forma de asegurar visibilidad
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
		
}

