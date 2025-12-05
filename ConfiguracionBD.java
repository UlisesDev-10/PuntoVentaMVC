package Modelo;

import java.util.ArrayList;

public class ConfiguracionBD {
    private BaseDatos bd;
    
    public ConfiguracionBD() {
        this.bd = new BaseDatos();
    }
    
    public boolean verificarEstructuraBD() {
        // Verificar y crear las estructuras necesarias
        boolean resultado = true;
        
        // Verificar y agregar columna 'cancelada' a la tabla Ventas si no existe
        resultado &= verificarColumnaVentas();
        
        // Verificar y crear tabla CajaCierres si no existe
   
        
        return resultado;
    }
    
    private boolean verificarColumnaVentas() {
        try {
            // Consultar si existe la columna 'cancelada' en la tabla Ventas
            ArrayList<String[]> resultado = bd.consultar("INFORMATION_SCHEMA.COLUMNS", 
                    "COLUMN_NAME", 
                    "TABLE_SCHEMA = 'Proyecto' AND TABLE_NAME = 'Ventas' AND COLUMN_NAME = 'cancelada'");
            
            if (resultado.isEmpty()) {
                // La columna no existe, agregarla
                String sql = "ALTER TABLE Ventas ADD cancelada TINYINT(1) DEFAULT 0 NOT NULL";
                return bd.ejecutarSQL(sql);
            }
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error al verificar columna 'cancelada': " + e.getMessage());
            return false;
        }
    }
    

}
