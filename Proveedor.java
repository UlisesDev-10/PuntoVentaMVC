package Modelo;

import java.util.ArrayList;
import java.util.Objects;

public class Proveedor {
    private String idProveedor;
    private String proveedor;
    private boolean activo = true; // Por defecto, todos los proveedores están activos
    
    public Proveedor() {
        super();
    }
    
    public Proveedor(String idProveedor, String proveedor) {
        super();
        this.idProveedor = idProveedor;
        this.proveedor = proveedor;
        this.activo = true;
    }
    
    public Proveedor(String idProveedor, String proveedor, boolean activo) {
        super();
        this.idProveedor = idProveedor;
        this.proveedor = proveedor;
        this.activo = activo;
    }
    
    public String getIdProveedor() {
        return idProveedor;
    }
    
    public void setIdProveedor(String idProveedor) {
        this.idProveedor = idProveedor;
    }
    
    public String getProveedor() {
        return proveedor;
    }
    
    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }
        
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    @Override
    public String toString() {
        return this.getProveedor();
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idProveedor);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Proveedor))
            return false;
        Proveedor other = (Proveedor) obj;
        return Objects.equals(idProveedor, other.idProveedor);
    }
    
    // Método para guardar el proveedor en la base de datos
    public boolean guardarEnBD() {
        try {
            BaseDatos bd = new BaseDatos();
            String campos = "idProveedor,proveedor,activo";
            String[] valores = {
                this.idProveedor,
                this.proveedor,
                this.activo ? "1" : "0"
            };
            
            // Primero verificamos si el proveedor ya existe
            ArrayList<String[]> existente = bd.consultar("Proveedores", "idProveedor", "idProveedor = '" + this.idProveedor + "'");
            
            if (existente != null && existente.size() > 0) {
                // Si existe, actualizamos el registro
                bd.modificar("Proveedores", "proveedor", this.proveedor, "idProveedor = '" + this.idProveedor + "'");
                bd.modificar("Proveedores", "activo", this.activo ? "1" : "0", "idProveedor = '" + this.idProveedor + "'");
                System.out.println("✅ Proveedor actualizado correctamente.");
            } else {
                // Si no existe, lo insertamos
                bd.insertar("Proveedores", campos, valores);
                System.out.println("✅ Proveedor insertado correctamente.");
            }
            
            bd.cerrarConexion();
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error al guardar el proveedor: " + e.getMessage());
            return false;
        }
    }
    
    // Método estático para obtener todos los proveedores activos
    public static ArrayList<Proveedor> obtenerTodos() {
        ArrayList<Proveedor> proveedores = new ArrayList<>();
        BaseDatos bd = new BaseDatos();
        
        ArrayList<String[]> resultados = bd.consultar("Proveedores", null, "activo = 1 OR activo IS NULL");
        if (resultados != null) {
            for (String[] fila : resultados) {
                // Asumiendo que el orden de columnas es: idProveedor, proveedor
                Proveedor p = new Proveedor(fila[0], fila[1], true);
                proveedores.add(p);
            }
        }
        
        bd.cerrarConexion();
        return proveedores;
    }
    
    // Método estático para obtener todos los proveedores (activos e inactivos)
    public static ArrayList<Proveedor> obtenerTodosConEstado() {
        ArrayList<Proveedor> proveedores = new ArrayList<>();
        BaseDatos bd = new BaseDatos();
        
        ArrayList<String[]> resultados = bd.consultar("Proveedores", "idProveedor, proveedor, activo", null);
        if (resultados != null) {
            for (String[] fila : resultados) {
                boolean estaActivo = true; // Por defecto está activo
                if (fila.length > 2 && fila[2] != null) {
                    estaActivo = "1".equals(fila[2]) || "true".equalsIgnoreCase(fila[2]);
                }
                Proveedor p = new Proveedor(fila[0], fila[1], estaActivo);
                proveedores.add(p);
            }
        }
        
        bd.cerrarConexion();
        return proveedores;
    }
}
