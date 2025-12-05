package Modelo;

import java.util.Objects;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

public class Categoria {
    private String id;
    private String etiqueta;
    private boolean activo = true; // Por defecto, todas las categorías están activas

    public Categoria() {
        super();
    }
    
    public Categoria(String id, String etiqueta) {
        super();
        this.id = id;
        this.etiqueta = etiqueta;
        this.activo = true;
    }
    
    public Categoria(String id, String etiqueta, boolean activo) {
        super();
        this.id = id;
        this.etiqueta = etiqueta;
        this.activo = activo;
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
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
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
        if (!(obj instanceof Categoria))
            return false;
        Categoria other = (Categoria) obj;
        return Objects.equals(id, other.id);
    }
    
    // Método para guardar la categoría en la base de datos
    public boolean guardarEnBD() {
        try {
            BaseDatos bd = new BaseDatos();
            // Cambia "categoria" por el nombre correcto de columna
            String campos = "id,etiqueta,activo";  // O el nombre exacto de columnas en tu base de datos
            String[] valores = {
                    this.id,
                    this.etiqueta,
                    this.activo ? "1" : "0"
            };



    // Primero verificamos si la categoría ya existe
            ArrayList<String[]> existente = bd.consultar("Categorias", "id", "id = '" + this.id + "'");
            
            if (existente != null && existente.size() > 0) {
                // Si existe, actualizamos el registro
                bd.modificar("Categorias", "etiqueta", this.etiqueta, "id = '" + this.id + "'");
                System.out.println("✅ Categoría actualizada correctamente.");
            } else {
                // Si no existe, la insertamos
                bd.insertar("Categorias", campos, valores);
                System.out.println("✅ Categoría insertada correctamente.");
            }
            
            bd.cerrarConexion();
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error al guardar la categoría: " + e.getMessage());
            return false;
        }
    }
    
    // Método estático para obtener todas las categorías activas
    public static ArrayList<Categoria> obtenerTodas() {
        ArrayList<Categoria> categorias = new ArrayList<>();
        BaseDatos bd = new BaseDatos();
        
        ArrayList<String[]> resultados = bd.consultar("Categorias", "id, etiqueta, activo", "activo = 1 OR activo IS NULL");
        if (resultados != null) {
            for (String[] fila : resultados) {
                // Orden de columnas: id, etiqueta, activo
                boolean estaActivo = true; // Por defecto está activo
                if (fila.length > 2 && fila[2] != null) {
                    estaActivo = "1".equals(fila[2]) || "true".equalsIgnoreCase(fila[2]);
                }
                Categoria c = new Categoria(fila[0], fila[1], estaActivo);
                categorias.add(c);
            }
        }
        
        bd.cerrarConexion();
        return categorias;
    }
                    
                    // Método estático para obtener todas las categorías con su estado
                    public static ArrayList<Categoria> obtenerTodasConEstado() {
                        ArrayList<Categoria> categorias = new ArrayList<>();
                        BaseDatos bd = new BaseDatos();
                        
                        ArrayList<String[]> resultados = bd.consultar("Categorias", "id, etiqueta, activo", null);
                        if (resultados != null) {
                            for (String[] fila : resultados) {
                boolean estaActivo = true; // Por defecto está activo
                if (fila.length > 2 && fila[2] != null) {
                    estaActivo = "1".equals(fila[2]) || "true".equalsIgnoreCase(fila[2]);
                }
                Categoria c = new Categoria(fila[0], fila[1], estaActivo);
                categorias.add(c);
                            }
                        }
                        
                        bd.cerrarConexion();
                        return categorias;
                    }
}
