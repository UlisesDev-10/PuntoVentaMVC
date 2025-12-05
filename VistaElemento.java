package Vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class VistaElemento extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField T1,T2;
	private JButton Bcancel,Bsave;
	private JPanel buttonPane;
	

	private JLabel Etiqueta1, Etiqueta2;
	public VistaElemento(Frame padre,String titulo,boolean esmodal) {
		super(padre,titulo,esmodal);
		setBounds(100, 100, 450, 241);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 1.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JPanel panel = new JPanel();
			GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.anchor = GridBagConstraints.WEST;
			gbc_panel.gridwidth = 14;
			gbc_panel.insets = new Insets(0, 0, 5, 0);
			gbc_panel.fill = GridBagConstraints.VERTICAL;
			gbc_panel.gridx = 0;
			gbc_panel.gridy = 1;
			contentPanel.add(panel, gbc_panel);
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			{
				Etiqueta1 = new JLabel("");
				panel.add(Etiqueta1);
			}
			{
				T1 = new JTextField();
				panel.add(T1);
				T1.setColumns(9);
			}
		}
		{
			JPanel panel = new JPanel();
			GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.anchor = GridBagConstraints.WEST;
			gbc_panel.gridwidth = 14;
			gbc_panel.insets = new Insets(0, 0, 5, 5);
			gbc_panel.fill = GridBagConstraints.VERTICAL;
			gbc_panel.gridx = 0;
			gbc_panel.gridy = 3;
			contentPanel.add(panel, gbc_panel);
			{
				Etiqueta2 = new JLabel("");
				panel.add(Etiqueta2);
			}
			{
				T2 = new JTextField();
				panel.add(T2);
				T2.setColumns(10);
			}
		}
		{
			buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				Bsave = new JButton("Guardar");
				buttonPane.add(Bsave);
		
			}
			{
				Bcancel = new JButton("Cancelar");
				buttonPane.add(Bcancel);
			}
		}
	}

	public void setEtiqueta1(String etiqueta1) {
		Etiqueta1.setText(etiqueta1);
	}

	public void setEtiqueta2(String etiqueta2) {
		Etiqueta2.setText(etiqueta2);
	}

	public JTextField getT1() {
		return T1;
	}

	public JTextField getT2() {
		return T2;
	}

	public JButton getBcancel() {
		return this.Bcancel;
	}


	public JButton getBsave() {
		return this.Bsave;
		
	}

	
}