
package Vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.BoxLayout;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import java.awt.Color;

import Modelo.Proveedor;

public class VistaElementoProducto extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField TidProducto, Tsku, Tproducto;
	private JComboBox Ccategoria;
	private JComboBox<Proveedor> Cproveedor;
	private JButton Bcancel, Bsave;
	private JPanel buttonPane;

	private JLabel Etiqueta1, Etiqueta3, Etiqueta2, Etiqueta4, Etiqueta5, EtiquetaId;
	private JPanel panel_1;
	private JTextField textField;
	private JPanel panel_2;
	private JLabel etiqueta2_1;
	private JTextField Tprecioventa;
	private JPanel panel_3;
	private JLabel etiqueta2_2;
	private JTextField Tmarca;
	private JPanel panel_4;
	private JLabel etiqueta2_3;
	private JTextField Tstock;
	private JPanel panel_5;
	private JLabel etiqueta2_4;
	private JScrollPane panel_6;
	private JTextArea Tdescripcion;
	private JPanel panelId;

	/**
	 * Create the dialog.
	 */
	public VistaElementoProducto(Frame padre, String titulo, boolean esmodal) {
		super(padre, titulo, esmodal);
		setBounds(100, 100, 622, 502);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Datos B\u00E1sicos", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		contentPanel.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		panelId = new JPanel();
		panel.add(panelId);
		panelId.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		EtiquetaId = new JLabel("ID Producto:");
		panelId.add(EtiquetaId);
		
		TidProducto = new JTextField();
		panelId.add(TidProducto);
		TidProducto.setColumns(10);
		
		JPanel panelSku = new JPanel();
		panel.add(panelSku);
		panelSku.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		Etiqueta1 = new JLabel("SKU:");
		panelSku.add(Etiqueta1);
		
		Tsku = new JTextField();
		panelSku.add(Tsku);
		Tsku.setColumns(20);
		
		JPanel panelNombre = new JPanel();
		panel.add(panelNombre);
		panelNombre.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		Etiqueta2 = new JLabel("Producto:");
		panelNombre.add(Etiqueta2);
		
		Tproducto = new JTextField();
		panelNombre.add(Tproducto);
		Tproducto.setColumns(30);
		
		JPanel panelCategoria = new JPanel();
		panel.add(panelCategoria);
		panelCategoria.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		Etiqueta3 = new JLabel("Categoría:");
		panelCategoria.add(Etiqueta3);
		
		Ccategoria = new JComboBox();
		panelCategoria.add(Ccategoria);
		
				JPanel panelProveedor = new JPanel();
				panel.add(panelProveedor);
				panelProveedor.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
				
				JLabel etiquetaProveedor = new JLabel("Proveedor:");
				panelProveedor.add(etiquetaProveedor);
				
				Cproveedor = new JComboBox<Proveedor>();
				panelProveedor.add(Cproveedor);
				
		panel_2 = new JPanel();
		panel.add(panel_2);
		panel_2.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		etiqueta2_1 = new JLabel("Precio Venta:");
		panel_2.add(etiqueta2_1);
		
		Tprecioventa = new JTextField();
		panel_2.add(Tprecioventa);
		Tprecioventa.setColumns(10);
		
		panel_3 = new JPanel();
		panel.add(panel_3);
		panel_3.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		etiqueta2_2 = new JLabel("Marca:");
		panel_3.add(etiqueta2_2);
		
		Tmarca = new JTextField();
		panel_3.add(Tmarca);
		Tmarca.setColumns(20);
		
		panel_4 = new JPanel();
		panel.add(panel_4);
		panel_4.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		etiqueta2_3 = new JLabel("Stock:");
		panel_4.add(etiqueta2_3);
		
		Tstock = new JTextField();
		panel_4.add(Tstock);
		Tstock.setColumns(10);
		
		panel_5 = new JPanel();
		panel_5.setBorder(new TitledBorder(null, "Descripci\u00F3n", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPanel.add(panel_5);
		panel_5.setLayout(new BorderLayout(0, 0));
		
		panel_6 = new JScrollPane();
		panel_5.add(panel_6, BorderLayout.CENTER);
		
		Tdescripcion = new JTextArea();
		Tdescripcion.setRows(5);
		panel_6.setViewportView(Tdescripcion);
		
		buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		Bsave = new JButton("Guardar");
		buttonPane.add(Bsave);
		getRootPane().setDefaultButton(Bsave);
		
		Bcancel = new JButton("Cancelar");
		buttonPane.add(Bcancel);
	}

	public JTextField getTsku() {
		return Tsku;
	}

	public JTextField getTproducto() {
		return Tproducto;
	}

	public JComboBox getCcategoria() {
		return Ccategoria;
	}
		
	public JComboBox<Proveedor> getCproveedor() {
		return Cproveedor;
	}

	public JTextField getTprecioventa() {
		return Tprecioventa;
	}

	public JTextField getTmarca() {
		return Tmarca;
	}

	public JTextField getTstock() {
		return Tstock;
	}

	public JTextArea getTdescripcion() {
		return Tdescripcion;
	}

	public void setEtiqueta1(String etiqueta1) {
		Etiqueta1.setText(etiqueta1);
	}

	public void setEtiqueta2(String etiqueta2) {
		Etiqueta2.setText(etiqueta2);
	}

	public JTextField getT1() {
		return Tsku;
	}

	public JTextField getT2() {
		return Tproducto;
	}

	public JButton getBcancel() {
		return this.Bcancel;
	}

	public JButton getBsave() {
		return this.Bsave;
	}
	
	public JTextField getTidProducto() {
		return TidProducto;
	}
	
	public void setEtiquetaId(String etiquetaId) {
		EtiquetaId.setText(etiquetaId);
	}
}
