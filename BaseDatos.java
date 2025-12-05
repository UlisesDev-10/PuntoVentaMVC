package Modelo;

import java.sql.*;
import java.util.ArrayList;

public class BaseDatos {
    private Connection conexion;

    // Constructor que recibe una conexión activa
    public BaseDatos(Connection conexion) {
        this.conexion = conexion;
    }

    // Constructor sin parámetros que utiliza la conexión de la clase Conexion
    public BaseDatos() {
        this.conexion = Conexion.obtenerConexion();
    }

    // Método para consultar registros de una tabla
    public ArrayList<String[]> consultar(String tabla, String campos, String condicion) {
        ArrayList<String[]> resultados = new ArrayList<>();
        String sql = (campos == null) ? "SELECT * FROM " + tabla : "SELECT " + campos + " FROM " + tabla;
        if (condicion != null && !condicion.isEmpty()) {
            sql += " WHERE " + condicion;
        }

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            ResultSetMetaData rsmd = rs.getMetaData();
            int numColumnas = rsmd.getColumnCount();

            while (rs.next()) {
                String[] fila = new String[numColumnas];
                for (int i = 1; i <= numColumnas; i++) {
                    fila[i - 1] = rs.getString(i);
                }
                resultados.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al consultar registros: " + e.getMessage());
                System.err.println("   Consulta SQL: " + sql);
                
                // Si hay un error de sintaxis, mostrar un mensaje más detallado
                if (e.getMessage().contains("syntax")) {
                    System.err.println("   NOTA: Verifica la sintaxis SQL para MySQL.");
                    System.err.println("   Usa LIMIT al final de la consulta en lugar de TOP.");
                }
        }
        return resultados;
    }

    // Método para verificar si un registro existe en la tabla
    public boolean existe(String tabla, String condicion) {
        boolean enc = false;
        String sql = "SELECT 1 FROM " + tabla + " WHERE " + condicion + " LIMIT 1";

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            enc = rs.next();
        } catch (SQLException e) {
            System.err.println("❌ Error al verificar existencia: " + e.getMessage());
        }
        return enc;
    }
    
    /**
     * Este método es un puente para asegurar que la eliminación de productos
     * siempre pase por el sistema de soft delete, sin importar cómo se llame.
     * Para uso del sistema cuando intenta eliminar productos a través del método general.
     */
    public boolean eliminarProductoGenerico(String idProducto) {
        return eliminar("Productos", "idProducto", idProducto);
    }
    
    /**
     * Obtiene un producto por su ID, independientemente de su estado (activo o inactivo)
     * @param idProducto ID del producto a buscar
     * @return Producto encontrado o null si no existe
     */
    public Producto obtenerProductoPorId(String idProducto) {
        Connection conn = Conexion.obtenerConexion();
        String sql = "SELECT * FROM Productos WHERE idProducto = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idProducto);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Producto producto = new Producto(
                    rs.getString("idProducto"),
                    rs.getString("sku"),
                    rs.getString("producto"),
                    rs.getString("idcategoria"),
                    rs.getString("idProveedor"),
                    rs.getDouble("precio_venta"),
                    rs.getString("marca"),
                    rs.getInt("stock"),
                    rs.getString("descripcion")
                );
                
                // Establecer el estado activo/inactivo
                producto.setActivo(rs.getBoolean("activo"));
                
                return producto;
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Error al obtener el producto: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Reactiva un producto previamente desactivado
     * @param idProducto ID del producto a reactivar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean reactivarProducto(String idProducto) {
        Connection conn = Conexion.obtenerConexion();
        String sql = "UPDATE Productos SET activo = 1 WHERE idProducto = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idProducto);
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✅ Producto reactivado correctamente (ID: " + idProducto + ")");
                return true;
            } else {
                System.out.println("❌ No se encontró el producto a reactivar (ID: " + idProducto + ")");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al reactivar el producto: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Método para consultar todos los productos, incluyendo los inactivos
     * @return Lista de productos activos e inactivos
     */
    public ArrayList<String[]> consultarTodosProductos() {
        return consultar("Productos", "idProducto, producto, descripcion, precio_venta, stock, idcategoria, marca, sku, activo", null);
    }
    
    /**
     * Consulta solo los productos inactivos (desactivados)
     * @return Lista de productos inactivos
     */
    public ArrayList<String[]> consultarProductosInactivos() {
        return consultar("Productos", "idProducto, producto, descripcion, precio_venta, stock, idcategoria, marca, sku, activo", 
                         "activo = 0");
    }
    
    /**
     * Consulta solo los productos activos
     * @return Lista de productos activos
     */
    public ArrayList<String[]> consultarProductosActivos() {
        return consultar("Productos", "idProducto, sku, producto, idcategoria, precio_venta, marca, stock, descripcion, activo", 
                         "activo = 1");
    }
    
    /**
     * Consulta productos activos que coincidan con un patrón de búsqueda
     * @param patron Patrón para buscar en el nombre del producto
     * @return Lista de productos activos que coinciden con el patrón
     */
    public ArrayList<String[]> consultarProductosActivosPorPatron(String patron) {
        return consultar("Productos", "idProducto, sku, producto, idcategoria, precio_venta, marca, stock, descripcion, activo", 
                         "activo = 1 AND producto LIKE '%" + patron + "%'");
    }
    
    /**
     * Consulta solo los productos activos
     * @return Lista de productos activos
     */
   
    /**
     * Consulta solo las categorías activas
     * @return Lista de categorías activas
     */
    public ArrayList<String[]> consultarCategoriasActivas() {
        return consultar("Categorias", "*", "activo = 1 OR activo IS NULL");
    }
    
    /**
     * Consulta solo las categorías inactivas
     * @return Lista de categorías inactivas
     */
   
    /**
     * Consulta solo los proveedores activos
     * @return Lista de proveedores activos
     */
    public ArrayList<String[]> consultarProveedoresActivos() {
        return consultar("Proveedores", "*", "activo = 1 OR activo IS NULL");
    }
    
    /**
     * Consulta solo los proveedores inactivos
     * @return Lista de proveedores inactivos
     */
    public ArrayList<String[]> consultarProveedoresInactivos() {
        return consultar("Proveedores", "idProveedor, proveedor, activo", "activo = 0");
    }
    
    /**
     * Consulta solo las categorías inactivas
     * @return Lista de categorías inactivas
     */
    public ArrayList<String[]> consultarCategoriasInactivas() {
        return consultar("Categorias", "id, etiqueta, activo", "activo = 0");
    }
    
    /**
     * Método para realizar una inserción en la base de datos y obtener el ID generado
     * @param tabla Nombre de la tabla donde se insertarán los datos
     * @param campos Lista de campos separados por comas
     * @param valores Array con los valores a insertar
     * @return El ID generado por la inserción, o -1 si hubo un error
     */
    public int insertarRetornarId(String tabla, String campos, String[] valores) {
        int idGenerado = -1;
        
        try {
            // Crear la cadena de valores con marcadores de posición
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < valores.length; i++) {
                placeholders.append("?");
                if (i < valores.length - 1) {
                    placeholders.append(",");
                }
            }
            
            String sql = "INSERT INTO " + tabla + " (" + campos + ") VALUES (" + placeholders.toString() + ")";
            PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            // Establecer los valores en la consulta
            for (int i = 0; i < valores.length; i++) {
                stmt.setString(i + 1, valores[i]);
            }
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                // Obtener el ID generado
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    idGenerado = rs.getInt(1);
                }
                rs.close();
            }
            
            stmt.close();
        } catch (Exception e) {
            System.err.println("❌ Error en método insertarRetornarId: " + e.getMessage());
            e.printStackTrace();
        }
        
        return idGenerado;
    }

    // Método para insertar un nuevo registro en la tabla
    public boolean insertar(String tabla, String campos, String[] valores) {
        String sql = "INSERT INTO " + tabla + " (" + campos + ") VALUES (" + "?,".repeat(valores.length - 1) + "?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            for (int i = 0; i < valores.length; i++) {
                ps.setString(i + 1, valores[i]);
            }
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✅ Registro insertado correctamente en " + tabla);
                return true;
            } else {
                System.out.println("⚠️ No se pudo insertar el registro en " + tabla);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar registro en " + tabla + ": " + e.getMessage());
            return false;
        }
    }

    // Método para eliminar un registro de la tabla
    public boolean eliminar(String tabla, String campo, String valor) {
            // Para la tabla Productos, usar soft delete en lugar de DELETE físico
            if (tabla.equalsIgnoreCase("Productos")) {
                return desactivarProducto(valor);
            }
            
            // Para otras tablas, usar eliminación física normal
        String sql = "DELETE FROM " + tabla + " WHERE " + campo + " = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, valor);
            int resultado = pstmt.executeUpdate();
            System.out.println(resultado > 0 ? "✅ Registros eliminados correctamente." : "⚠️ No se encontraron registros para eliminar.");
            return resultado > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar registros: " + e.getMessage());
            return false;
        }
    }

    // Método para modificar un registro en la tabla
    public boolean modificar(String tabla, String campoActualizar, String nuevoValor, String condicion) {
        String sql = "UPDATE " + tabla + " SET " + campoActualizar + " = ? WHERE " + condicion;
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nuevoValor);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✅ " + filasAfectadas + " registro(s) actualizado(s) correctamente en " + tabla);
                return true;
            } else {
                System.out.println("⚠️ No se encontraron registros para actualizar en " + tabla + " con condición: " + condicion);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar registros en " + tabla + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Ejecuta un script SQL directamente en la base de datos
     * @param sqlScript Script SQL a ejecutar
     * @return true si se ejecutó correctamente, false en caso contrario
     */
   
    
    // Método para modificar múltiples campos de un registro en la tabla
    public boolean modificarMultiple(String tabla, String[] campos, String[] valores, String condicion) {
        if (campos.length != valores.length) {
            System.err.println("❌ Error: La cantidad de campos y valores debe ser igual");
            return false;
        }
        
        StringBuilder setClause = new StringBuilder();
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) setClause.append(", ");
            setClause.append(campos[i]).append(" = ?");
        }
        
        String sql = "UPDATE " + tabla + " SET " + setClause + " WHERE " + condicion;
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            for (int i = 0; i < valores.length; i++) {
                ps.setString(i + 1, valores[i]);
            }
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✅ " + filasAfectadas + " registro(s) actualizado(s) con múltiples campos en " + tabla);
                return true;
            } else {
                System.out.println("⚠️ No se encontraron registros para actualizar en " + tabla + " con condición: " + condicion);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar múltiples campos en " + tabla + ": " + e.getMessage());
            return false;
        }
    }
    
    
    public ArrayList<String[]> consultarProductos() {
            return consultar("Productos", "idProducto, producto, descripcion, precio_venta, stock, idcategoria, marca, sku, activo", 
                                 "activo = 1");
    }
        
        /**
         * Desactiva lógicamente un producto en la base de datos (Soft Delete)
         * @param idProducto ID del producto a desactivar
         * @return true si la operación fue exitosa, false en caso contrario
         */
        public boolean desactivarProducto(String idProducto) {
            if (idProducto == null || idProducto.trim().isEmpty()) {
                System.err.println("❌ Error: ID de producto vacío o nulo");
                return false;
            }
            
            Connection conn = Conexion.obtenerConexion();
            if (conn == null) {
                System.err.println("❌ Error: No se pudo obtener conexión a la base de datos");
                return false;
            }
            
            String sql = "UPDATE Productos SET activo = 0 WHERE idProducto = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, idProducto);
                int filasAfectadas = pstmt.executeUpdate();
                
                // Registramos la desactivación en el log o en la consola
                if (filasAfectadas > 0) {
                    System.out.println("✅ Producto desactivado correctamente (ID: " + idProducto + ")");
                    return true;
                } else {
                    System.out.println("❌ No se encontró el producto a desactivar (ID: " + idProducto + ")");
                    return false;
                }
            } catch (SQLException e) {
                System.err.println("❌ Error al desactivar el producto: " + e.getMessage());
                e.printStackTrace(); // Agregar stack trace para mejor depuración
                return false;
            }
        }
    
    // Método para consultar ventas con nombres de columnas correctos
    public ArrayList<String[]> consultarVentas(String fechaInicio, String fechaFin) {
        return consultar("Ventas", "idVenta, fecha, total, idTipoVenta", 
                         "fecha BETWEEN '" + fechaInicio + "' AND '" + fechaFin + "' ORDER BY fecha");
    }
    
    /**
     * Método para obtener el total de ventas por tipo de pago en una fecha específica
     * @param fecha Fecha en formato yyyy-MM-dd
     * @param idTipoPago ID del tipo de pago (1=Efectivo, 2=Tarjeta, 3=Transferencia)
     * @return Total de ventas en el tipo de pago especificado
     */
    public double obtenerVentasPorTipoPago(String fecha, int idTipoPago) {
        double total = 0.0;
        try {
            ArrayList<String[]> resultado = consultar(
                "Ventas", 
                "SUM(CAST(total AS DECIMAL(10,2))) AS total_ventas", 
                "DATE(fecha) = '" + fecha + "' AND idTipoVenta = '" + idTipoPago + "'"
            );
            
            System.out.println("Consultando ventas por tipo: DATE(fecha) = '" + fecha + "' AND idTipoVenta = '" + idTipoPago + "'");
            
            if (resultado != null && !resultado.isEmpty() && resultado.get(0)[0] != null) {
                total = Double.parseDouble(resultado.get(0)[0]);
                System.out.println("Total encontrado para tipo " + idTipoPago + ": " + total);
            } else {
                System.out.println("No se encontraron ventas para tipo " + idTipoPago);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener ventas por tipo de pago: " + e.getMessage());
            e.printStackTrace();
        }
        return total;
    }
        
        // Método para consultar ventas no canceladas
        public ArrayList<String[]> consultarVentasNoCanceladas(String fechaInicio, String fechaFin) {
            return consultar("Ventas", "idVenta, fecha, total, idTipoVenta", 
                             "fecha BETWEEN '" + fechaInicio + "' AND '" + fechaFin + "' AND (cancelada IS NULL OR cancelada != 1) ORDER BY fecha");
        }
        
        /**
         * Método para desactivar un producto (soft delete)
         * @param idProducto ID del producto a desactivar
         * @return true si se desactivó correctamente, false en caso contrario
         */
       
        
        /**
         * Método para desactivar una categoría (soft delete)
         * @param idCategoria ID de la categoría a desactivar
         * @return true si se desactivó correctamente, false en caso contrario
         */
        public boolean desactivarCategoria(String idCategoria) {
            return modificar("Categorias", "activo", "0", "id = '" + idCategoria + "'");
        }
        
        /**
         * Método para desactivar un proveedor (soft delete)
         * @param idProveedor ID del proveedor a desactivar
         * @return true si se desactivó correctamente, false en caso contrario
         */
        public boolean desactivarProveedor(String idProveedor) {
            return modificar("Proveedores", "activo", "0", "id = '" + idProveedor + "'");
        }
        
        /**
         * Método para desactivar un producto (soft delete)
         * @param idProducto ID del producto a desactivar
         * @return true si se desactivó correctamente, false en caso contrario
         */
       
        // Método para obtener el total de ventas de un día específico
        public double obtenerTotalVentasDia(String fecha) {
            double total = 0;
            String condicion = "fecha = '" + fecha + "' AND (cancelada IS NULL OR cancelada != 1)";
            ArrayList<String[]> resultados = consultar("Ventas", "total", condicion);
            System.out.println("Consulta SQL para ventas del día " + fecha + " encontró " + 
                              (resultados != null ? resultados.size() : 0) + " registros");
            
            if (resultados != null && !resultados.isEmpty()) {
                for (String[] fila : resultados) {
                    try {
                        if (fila[0] != null && !fila[0].isEmpty()) {
                            total += Double.parseDouble(fila[0]);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("❌ Error al convertir valor a número: " + fila[0]);
                    }
                }
            }
            
            System.out.println("Total de ventas calculado para el día " + fecha + ": $" + total);
            return total;
        }
        
        /**
         * Método para obtener el total de ventas por tipo de pago para un día específico
         * @param fecha Fecha en formato adecuado para la consulta SQL (YYYY-MM-DD)
         * @param idTipoPago ID del tipo de pago (1: Efectivo, 2: Tarjeta, 3: Transferencia, etc.)
         * @return Total de ventas para el tipo de pago especificado
         */
       
        /**
         * Método para obtener el total de ventas por tipo de pago para un día específico
         * @param fecha Fecha en formato adecuado para la consulta SQL (YYYY-MM-DD)
         * @param idTipoPago ID del tipo de pago (1: Efectivo, 2: Tarjeta, 3: Transferencia, etc.)
         * @return Total de ventas para el tipo de pago especificado
         */
        
    
    // Método para consultar cierres de caja con nombres de columnas correctos
    public ArrayList<String[]> consultarCierreCaja(String fecha) {
        return consultar("CajaCierres", "id, fecha, hora, monto_inicial, monto_final, total_ventas, ventas_efectivo, ventas_tarjeta, ventas_transferencia", 
                         "fecha = '" + fecha + "'");
    }
    
    // Método para cerrar la conexión
    public void cerrarConexion() {
            // No cerramos la conexión para evitar problemas al reutilizarla
            // Solo registramos que se intentó cerrar
            System.out.println("Nota: La conexión se mantendrá abierta para futuras operaciones");
            
            // Si realmente quieres cerrar la conexión (por ejemplo, al finalizar la aplicación),
            // puedes crear un método específico como cerrarConexionDefinitivamente()
        }
        
        // Método para cerrar definitivamente la conexión (usar solo al salir de la aplicación)
        public void cerrarConexionDefinitivamente() {
            try {
                if (Conexion.conexion != null && !Conexion.conexion.isClosed()) {
                    Conexion.cerrarConexion();
                    System.out.println("🔒 Conexión cerrada definitivamente.");
                }
            } catch (SQLException e) {
                System.err.println("❌ Error al verificar el estado de la conexión: " + e.getMessage());
            }
    }
    
    // Método para ejecutar consultas SQL directas que no son consultas de selección
    public boolean ejecutarSQL(String sql) {
        try (Statement stmt = conexion.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ Consulta SQL ejecutada correctamente");
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error al ejecutar SQL: " + e.getMessage());
            System.err.println("   Consulta SQL: " + sql);
            return false;
        }
    }
    
    // ============================================================================
    // MÉTODOS PARA EJECUTAR PROCEDIMIENTOS ALMACENADOS (SPs)
    // ============================================================================
    
    /**
     * Ejecuta un Procedimiento Almacenado sin retornar datos (INSERT, UPDATE, DELETE)
     * @param nombreSP Nombre del procedimiento almacenado
     * @param parametros Array de parámetros de entrada
     * @return true si se ejecutó correctamente, false en caso contrario
     */
    public boolean ejecutarSP(String nombreSP, Object... parametros) {
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < parametros.length; i++) {
            placeholders.append("?");
            if (i < parametros.length - 1) {
                placeholders.append(",");
            }
        }
        
        String call = "{CALL " + nombreSP + "(" + placeholders + ")}";
        
        try (CallableStatement cstmt = conexion.prepareCall(call)) {
            // Establecer parámetros
            for (int i = 0; i < parametros.length; i++) {
                cstmt.setObject(i + 1, parametros[i]);
            }
            
            cstmt.execute();
            System.out.println("✅ SP '" + nombreSP + "' ejecutado correctamente");
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error al ejecutar SP '" + nombreSP + "': " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Ejecuta un Procedimiento Almacenado que retorna un ResultSet (SELECT)
     * @param nombreSP Nombre del procedimiento almacenado
     * @param parametros Array de parámetros de entrada
     * @return ArrayList con los resultados o null si hay error
     */
    public ArrayList<String[]> ejecutarSPConResultado(String nombreSP, Object... parametros) {
        ArrayList<String[]> resultados = new ArrayList<>();
        
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < parametros.length; i++) {
            placeholders.append("?");
            if (i < parametros.length - 1) {
                placeholders.append(",");
            }
        }
        
        String call = "{CALL " + nombreSP + "(" + placeholders + ")}";
        
        try (CallableStatement cstmt = conexion.prepareCall(call)) {
            // Establecer parámetros
            for (int i = 0; i < parametros.length; i++) {
                cstmt.setObject(i + 1, parametros[i]);
            }
            
            // Ejecutar y obtener resultados
            ResultSet rs = cstmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int numColumnas = rsmd.getColumnCount();
            
            while (rs.next()) {
                String[] fila = new String[numColumnas];
                for (int i = 1; i <= numColumnas; i++) {
                    fila[i - 1] = rs.getString(i);
                }
                resultados.add(fila);
            }
            
            System.out.println("✅ SP '" + nombreSP + "' ejecutado correctamente. Registros: " + resultados.size());
            return resultados;
        } catch (SQLException e) {
            System.err.println("❌ Error al ejecutar SP '" + nombreSP + "': " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Ejecuta un SP con parámetro de salida (OUT)
     * @param nombreSP Nombre del procedimiento almacenado
     * @param parametrosIN Parámetros de entrada
     * @param tipoOUT Tipo SQL del parámetro de salida (Types.INTEGER, Types.VARCHAR, etc.)
     * @return Valor del parámetro OUT o null si hay error
     */
    public Object ejecutarSPConOUT(String nombreSP, Object[] parametrosIN, int tipoOUT) {
        StringBuilder placeholders = new StringBuilder();
        int totalParams = parametrosIN.length + 1; // +1 para el parámetro OUT
        
        for (int i = 0; i < totalParams; i++) {
            placeholders.append("?");
            if (i < totalParams - 1) {
                placeholders.append(",");
            }
        }
        
        String call = "{CALL " + nombreSP + "(" + placeholders + ")}";
        
        try (CallableStatement cstmt = conexion.prepareCall(call)) {
            // Establecer parámetros IN
            for (int i = 0; i < parametrosIN.length; i++) {
                cstmt.setObject(i + 1, parametrosIN[i]);
            }
            
            // Registrar parámetro OUT (último parámetro)
            cstmt.registerOutParameter(parametrosIN.length + 1, tipoOUT);
            
            // Ejecutar
            cstmt.execute();
            
            // Obtener valor OUT
            Object valorOUT = cstmt.getObject(parametrosIN.length + 1);
            System.out.println("✅ SP '" + nombreSP + "' ejecutado. Valor OUT: " + valorOUT);
            return valorOUT;
        } catch (SQLException e) {
            System.err.println("❌ Error al ejecutar SP '" + nombreSP + "': " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Verifica si un SP existe en la base de datos
     * @param nombreSP Nombre del procedimiento almacenado
     * @return true si existe, false en caso contrario
     */
    public boolean existeSP(String nombreSP) {
        String sql = "SELECT COUNT(*) FROM information_schema.ROUTINES " +
                     "WHERE ROUTINE_TYPE = 'PROCEDURE' " +
                     "AND ROUTINE_SCHEMA = DATABASE() " +
                     "AND ROUTINE_NAME = ?";
        
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, nombreSP);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al verificar existencia de SP: " + e.getMessage());
        }
        return false;
    }
}