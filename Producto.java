

    package Modelo;
    
    import java.util.ArrayList;
    import java.util.Objects;
    
    import javax.swing.table.DefaultTableModel;
    
    public class Producto {
    private String idProducto;
    private String sku;
    private String producto;
    private String idCategoria;
    private String idProveedor;
    private double precioVenta;
    private String marca;
    private int stock;
    private String descripcion;
        private boolean activo = true; // Por defecto, todos los productos están activos
    
    // Constructor vacío
    public Producto() {
        super();
    }
    
    // Constructor completo
    public Producto(String idProducto, String sku, String producto, String idCategoria, String idProveedor,
                    double precioVenta, String marca, int stock, String descripcion) {
        super();
        this.idProducto = idProducto;
        this.sku = sku;
        this.producto = producto;
        this.idCategoria = idCategoria;
        this.idProveedor = idProveedor;
        this.precioVenta = precioVenta;
        this.marca = marca;
        this.stock = stock;
        this.descripcion = descripcion;
    }
    
    // Constructor sin idProveedor
    public Producto(String idProducto, String sku, String producto, String idCategoria,
                    double precioVenta, String marca, int stock, String descripcion) {
        this(idProducto, sku, producto, idCategoria, "", precioVenta, marca, stock, descripcion);
    }
    
    // Constructor con parámetro activo
    public Producto(String idProducto, String nombre, String descripcion, double precioVenta, 
                   int stock, String idCategoria, String marca, String sku, boolean activo) {
        this(idProducto, sku, nombre, idCategoria, "", precioVenta, marca, stock, descripcion);
        this.activo = activo;
    }
    
    // Constructor alternativo que acepta Strings para compatibilidad
    public Producto(String sku, String producto, String idCategoria, String idProveedor,
                    String precioVenta, String marca, String stock, String descripcion) {
        super();
        // Generamos un ID temporal basado en el tiempo actual
        this.idProducto = "TEMP_" + System.currentTimeMillis();
        this.sku = sku;
        this.producto = producto;
        this.idCategoria = idCategoria;
        this.idProveedor = idProveedor;
        try {
            this.precioVenta = Double.parseDouble(precioVenta);
        } catch(NumberFormatException e) {
            this.precioVenta = 0.0;
        }
        this.marca = marca;
        try {
            this.stock = Integer.parseInt(stock);
        } catch(NumberFormatException e) {
            this.stock = 0;
        }
        this.descripcion = descripcion;
    }
    
    // Constructor que acepta 7 parámetros (sin ID) para compatibilidad con código existente
    public Producto(String sku, String producto, String idCategoria, 
                    String precioVenta, String marca, String stock, String descripcion) {
        this(sku, producto, idCategoria, "", precioVenta, marca, stock, descripcion);
    }
    
    // Constructor que acepta 6 parámetros (sin ID ni descripción) para compatibilidad con código existente
    public Producto(String sku, String producto, String idCategoria, 
                    String precioVenta, String marca, String stock) {
        this(sku, producto, idCategoria, "", precioVenta, marca, stock, "");
    }
    
    // Getters y Setters
    public String getIdProducto() {
        return idProducto;
    }
    
    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }
    
    public String getSku() {
        return sku;
    }
    
    public void setSku(String sku) {
        this.sku = sku;
    }
    
    public String getProducto() {
        return producto;
    }
    
    public void setProducto(String producto) {
        this.producto = producto;
    }
    
    public String getIdCategoria() {
        return idCategoria;
    }
    
    // Método para compatibilidad con código antiguo
    public String getIdcategoria() {
        return idCategoria;
    }
    
    public void setIdCategoria(String idCategoria) {
        this.idCategoria = idCategoria;
    }
            
    public String getIdProveedor() {
        return idProveedor;
    }
            
    public void setIdProveedor(String idProveedor) {
        this.idProveedor = idProveedor;
    }
    
    public double getPrecioVenta() {
        return precioVenta;
    }
    
    // Método estático para obtener solo los productos activos
    public static ArrayList<Producto> obtenerActivos() {
        ArrayList<Producto> productos = new ArrayList<>();
        BaseDatos bd = new BaseDatos();
        
        ArrayList<String[]> resultados = bd.consultar("Productos", "idProducto, producto, descripcion, precio_venta, stock, idcategoria, marca, sku, activo", "activo = 1 OR activo IS NULL");
        if (resultados != null) {
            for (String[] fila : resultados) {
                try {
                    // Convertir valores de string a tipos adecuados
                    String id = fila[0];
                    String nombre = fila[1];
                    String descripcion = fila[2];
                    double precioVenta = Double.parseDouble(fila[3]);
                    int stock = Integer.parseInt(fila[4]);
                    String idCategoria = fila[5];
                    String marca = fila[6];
                    String sku = fila[7];
                    boolean activo = true; // Por defecto está activo
                    if (fila.length > 8 && fila[8] != null) {
                        activo = "1".equals(fila[8]) || "true".equalsIgnoreCase(fila[8]);
                    }
                    
                    // Crear producto con el constructor existente y luego establecer activo
                    Producto p = new Producto(id, sku, nombre, idCategoria, precioVenta, marca, stock, descripcion);
                    p.setActivo(activo);
                    productos.add(p);
                } catch (NumberFormatException e) {
                    System.err.println("Error al convertir datos numéricos de producto: " + e.getMessage());
                }
            }
        }
        
        bd.cerrarConexion();
        return productos;
    }
    
    // Método para compatibilidad con código antiguo
    public String getPrecio_venta() {
        return String.valueOf(precioVenta);
    }
            
    // Método sobrecargado para establecer el precio desde String
    public void setPrecio_venta(String precioTexto) {
        try {
            if (precioTexto == null || precioTexto.trim().isEmpty()) {
                this.precioVenta = 0.0;
                return;
            }
            
            // Reemplazar comas por puntos para manejar diferentes formatos
            String precioNormalizado = precioTexto.replace(',', '.');
            this.precioVenta = Double.parseDouble(precioNormalizado);
        } catch (NumberFormatException e) {
            // Si hay error en la conversión, asignar 0.0
            System.out.println("Advertencia: Formato de precio inválido: " + precioTexto + " - Se asignó 0.0");
            this.precioVenta = 0.0;
        }
    }
    
    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }
    
    public String getMarca() {
        return marca;
    }
    
    public void setMarca(String marca) {
        this.marca = marca;
    }
    
    public int getStock() {
        return stock;
    }
    
    public void setStock(int stock) {
        this.stock = stock;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    @Override
    public String toString() {
        return this.getProducto();
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idProducto, sku);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Producto))
            return false;
        Producto other = (Producto) obj;
        return Objects.equals(idProducto, other.idProducto) || 
               Objects.equals(sku, other.sku);
    }
    
    // Método para crear un modelo de tabla para mostrar productos
    public static DefaultTableModel crearModeloTabla() {
        String[] columnas = {"ID", "SKU", "Producto", "Categoría", "Proveedor", "Precio", "Marca", "Stock", "Descripción"};
        return new DefaultTableModel(columnas, 0);
    }
    
    // Método para crear un modelo de tabla con datos específicos
    public static DefaultTableModel crearModeloTabla(ArrayList<Producto> productos) {
        DefaultTableModel modelo = crearModeloTabla();
        
        for (Producto p : productos) {
            Object[] fila = {
                p.getIdProducto(),
                p.getSku(),
                p.getProducto(),
                p.getIdCategoria(), // Debería convertirse a nombre de categoría si es posible
                p.getIdProveedor(), // Agregado para mostrar el proveedor
                p.getPrecioVenta(),
                p.getMarca(),
                p.getStock(),
                p.getDescripcion()
            };
            modelo.addRow(fila);
        }
        
        return modelo;
    }
    
    // Método para convertir un String[] a un Producto
    public static Producto fromStringArray(String[] datos) {
        if (datos.length < 8) {
            return null;
        }
        
        try {
            String id = datos[0];
            String sku = datos[1];
            String nombre = datos[2];
            String idCategoria = datos[3];
            String idProveedor = datos.length > 8 ? datos[4] : ""; // Agregamos el proveedor si existe
            double precio = Double.parseDouble(datos.length > 8 ? datos[5] : datos[4]);
            String marca = datos.length > 8 ? datos[6] : datos[5];
            int stock = Integer.parseInt(datos.length > 8 ? datos[7] : datos[6]);
            String descripcion = datos.length > 8 ? datos[8] : datos[7];
            
            Producto producto = new Producto(id, sku, nombre, idCategoria, idProveedor, precio, marca, stock, descripcion);
            
            // Si hay campo activo (puede estar en posición 9)
            if (datos.length > 9 && datos[9] != null) {
                boolean activo = "1".equals(datos[9]) || "true".equalsIgnoreCase(datos[9]);
                producto.setActivo(activo);
            }
            
            return producto;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    }