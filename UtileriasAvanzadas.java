// Contenido de UtileriasAvanzadas.java
package Modelo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtileriasAvanzadas {

    /**
     * Demuestra la ejecución de componentes avanzados de BD (SP y Función).
     */
    public static void demostrarComponentesAvanzados() {
        // Demostración del Procedimiento Almacenado (SP)
        ejecutarActualizacionLote("PROV-01", 1.30);
        
        // Demostración de la Función
        consultarMargenProducto("P001", 10.00);
        
        // Demostración del Mantenimiento de Auditoría
        ejecutarMantenimientoAuditoria(90);
    }

    // Método para llamar al SP: sp_ActualizarPrecioLote
    private static void ejecutarActualizacionLote(String idProveedor, double factor) {
        Connection conn = null;
        try {
            conn = Conexion.obtenerConexion();
            // Llama al procedimiento con la sintaxis {call nombre_procedimiento(?, ?)}
            String call = "{CALL sp_ActualizarPrecioLote(?, ?)}"; 
            CallableStatement cstmt = conn.prepareCall(call);

            // Establecer parámetros
            cstmt.setString(1, idProveedor);
            cstmt.setDouble(2, factor);

            cstmt.execute();
            System.out.println("✅ Ejecución de SP 'sp_ActualizarPrecioLote' completada para proveedor: " + idProveedor);

        } catch (SQLException e) {
            System.err.println("❌ Error al ejecutar SP: " + e.getMessage());
        } finally {
            // Manejo del cierre de conexión si es necesario
        }
    }

    // Método para llamar a la Función: fn_CalcularMargenGanancia
    public static void consultarMargenProducto(String idProducto, double costo) {
        Connection conn = null;
        try {
            conn = Conexion.obtenerConexion();
            // Llama a la función usando una consulta SELECT
            String sql = "SELECT fn_CalcularMargenGanancia(?, ?) AS Margen";
            
            // Usamos CallableStatement para mayor compatibilidad, aunque PreparedStatement serviría
            CallableStatement cstmt = conn.prepareCall(sql);
            cstmt.setString(1, idProducto);
            cstmt.setDouble(2, costo);

            ResultSet rs = cstmt.executeQuery();
            if (rs.next()) {
                System.out.println("✅ Margen de ganancia para " + idProducto + ": " + rs.getDouble("Margen") + "%");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al consultar función: " + e.getMessage());
        } finally {
            // Manejo del cierre de conexión
        }
    }

    /**
     * Ejecuta el procedimiento de mantenimiento de auditoría.
     * @param dias Número de días a mantener en el log de auditoría
     */
    public static void ejecutarMantenimientoAuditoria(int dias) {
        Connection conn = null;
        try {
            conn = Conexion.obtenerConexion();
            // Llama al procedimiento de mantenimiento
            String call = "{CALL sp_Mantenimiento_Logs(?)}";
            CallableStatement cstmt = conn.prepareCall(call);

            // Establecer parámetro: número de días a conservar
            cstmt.setInt(1, dias);

            cstmt.execute();
            System.out.println("✅ Ejecución de SP 'sp_Mantenimiento_Logs' completada. Logs de más de " + dias + " días limpiados.");

        } catch (SQLException e) {
            System.err.println("❌ Error al ejecutar SP de Mantenimiento: " + e.getMessage());
        } finally {
            // Manejo del cierre de conexión si es necesario
        }
    }
}
