package Modelo;

import java.util.Objects;

import javax.swing.table.DefaultTableModel;

public class TipoVenta {
	private String id;
    private String etiqueta;

	public TipoVenta(String id, String etiqueta) {
		super();
		this.id = id;
		this.etiqueta = etiqueta;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	
	
	@Override
	public String toString() {
		return this.getEtiqueta();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(etiqueta, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof TipoVenta))
			return false;
		TipoVenta other = (TipoVenta) obj;
		return Objects.equals(id, other.id);
	}
	

	
	
}
