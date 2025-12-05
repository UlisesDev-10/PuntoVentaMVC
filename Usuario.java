package Modelo;

import java.util.ArrayList;

public class Usuario {
    private String usuario;
    private String contraseña;
    private String rol;
    
    public Usuario(String usuario, String contraseña, String rol) {
        this.usuario = usuario;
        this.contraseña = contraseña;
        this.rol = rol;
    }
    
    public String getUsuario() {
        return usuario;
    }
    
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
        
        /**
         * Obtiene todos los usuarios desde la base de datos
         * @return ArrayList con los usuarios encontrados
         */
        public static ArrayList<Usuario> obtenerTodos() {
            ArrayList<Usuario> listaUsuarios = new ArrayList<>();
            BaseDatos bd = new BaseDatos();
            
            try {
                ArrayList<String[]> resultados = bd.consultar("Usuarios", "*", null);
                
                if (resultados != null && !resultados.isEmpty()) {
                    for (String[] fila : resultados) {
                        // La estructura esperada es: [usuario, contraseña, rol]
                        Usuario usuario = new Usuario(fila[0], fila[1], fila[2]);
                        listaUsuarios.add(usuario);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al obtener usuarios: " + e.getMessage());
            } finally {
                bd.cerrarConexion();
            }
            
            return listaUsuarios;
        }
    
    public String getContraseña() {
        return contraseña;
    }
    
    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }
    
    public String getRol() {
        return rol;
    }
    
    public void setRol(String rol) {
        this.rol = rol;
    }
    
    public boolean autenticar(String user, String pass) {
        return this.usuario.equals(user) && this.contraseña.equals(pass);
    }
    
    public boolean guardarEnBD() {
        BaseDatos bd = new BaseDatos();
        boolean resultado = false;
        
        try {
            // Verificar directamente si existe el usuario consultando la tabla
            ArrayList<String[]> usuarioExistente = bd.consultar("Usuarios", "usuario", "usuario = '" + this.usuario + "'");
            
            if (usuarioExistente != null && !usuarioExistente.isEmpty()) {
                // Si existe, actualizar sus datos (no modificar el usuario que es clave primaria)
                resultado = bd.modificar("Usuarios", "contraseña", this.contraseña, "usuario = '" + this.usuario + "'");
                if (resultado) {
                    resultado = bd.modificar("Usuarios", "rol", this.rol, "usuario = '" + this.usuario + "'");
                }
            } else {
                // Si no existe, insertar nuevo registro
                String[] valores = {this.usuario, this.contraseña, this.rol};
                resultado = bd.insertar("Usuarios", "usuario,contraseña,rol", valores);
            }
        } catch (Exception e) {
            System.err.println("❌ Error en guardarEnBD: " + e.getMessage());
            resultado = false;
        } finally {
            bd.cerrarConexion();
        }
        
        return resultado;
    }
    
    @Override
    public String toString() {
        return this.usuario + " (" + this.rol + ")";
    }
}
   
   
