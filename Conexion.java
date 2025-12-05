package Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
	// Configuración mejorada con autoReconnect y timeouts aumentados
	private static final String URL = "jdbc:mysql://localhost:3306/Proyecto?" +
			"serverTimezone=UTC&" +
			"autoReconnect=true&" +
			"useSSL=false&" +
			"allowPublicKeyRetrieval=true&" +
			"connectTimeout=30000&" +       // 30 segundos para conectar
			"socketTimeout=60000";           // 60 segundos para operaciones
	private static final String USER = "root";
	private static final String PASSWORD = "";

    public static Connection conexion = null;

    public static Connection obtenerConexion() {
        try {
            // Si la conexión es nula, está cerrada o no es válida, crear una nueva
            if (conexion == null || conexion.isClosed() || !conexion.isValid(2)) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Conexión exitosa a MySQL.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Error al cargar el controlador JDBC de MySQL: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar con la base de datos: " + e.getMessage());
            // Intentar reconectar una vez más
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Reconexión exitosa a MySQL.");
            } catch (Exception ex) {
                System.err.println("❌ Falló la reconexión: " + ex.getMessage());
            }
        }
        return conexion;
    }
    
    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("🔒 Conexión cerrada correctamente.");
            } catch (SQLException e) {
                System.err.println("❌ Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    // Agrega este método solo para probar la conexión directamente desde esta clase
    public static void main(String[] args) {
        obtenerConexion();
        cerrarConexion();
    }
}