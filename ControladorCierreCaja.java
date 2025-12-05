package Controlador;

import Modelo.BaseDatos;
import Vista.VistaCierreCaja;
import Vista.VistaMenu;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Controlador para la funcionalidad de cierre de caja
 */
public class ControladorCierreCaja implements ActionListener {

    private VistaCierreCaja vistaCierreCaja;
    private ControladorMenu controladorMenu;
    private double fondoInicial;
    private double totalVentas;
    private String idApertura;
    private double ventasEfectivo;
    private double ventasTarjeta;
    private double ventasTransferencia;
    private VistaMenu padre;
    private DecimalFormat formatoMoneda = new DecimalFormat("#,##0.00");
    
    /**
     * Método para actualizar la tabla resumen con los datos de ventas
     * @param fecha La fecha para filtrar las ventas
     */
    private void actualizarTablaResumen(String fecha) {
        try {
            BaseDatos bd = new BaseDatos();
            
            // Obtener cantidad de ventas en efectivo
            ArrayList<String[]> cantidadEfectivo = bd.consultar(
                "Ventas", 
                "COUNT(*) AS cantidad", 
                "DATE(fecha) = '" + fecha + "' AND idTipoVenta = '1'"
            );
            int numVentasEfectivo = 0;
            if (cantidadEfectivo != null && !cantidadEfectivo.isEmpty() && cantidadEfectivo.get(0)[0] != null) {
                numVentasEfectivo = Integer.parseInt(cantidadEfectivo.get(0)[0]);
            }
            
            // Obtener cantidad de ventas con tarjeta (incluye transferencia)
            ArrayList<String[]> cantidadTarjeta = bd.consultar(
                "Ventas", 
                "COUNT(*) as cantidad", 
                "DATE(fecha) = '" + fecha + "' AND (idTipoVenta = '2' OR idTipoVenta = '3')"
            );
            int numVentasTarjeta = 0;
            if (cantidadTarjeta != null && !cantidadTarjeta.isEmpty() && cantidadTarjeta.get(0)[0] != null) {
                numVentasTarjeta = Integer.parseInt(cantidadTarjeta.get(0)[0]);
            }
            
            // Actualizar campos individuales de la vista
            vistaCierreCaja.getTVentasEfectivo().setText("$" + formatoMoneda.format(ventasEfectivo));
            vistaCierreCaja.getTVentasTarjeta().setText("$" + formatoMoneda.format(ventasTarjeta));
            vistaCierreCaja.getTVentasTransferencia().setText("$" + formatoMoneda.format(ventasTransferencia));
            
            System.out.println("=== Tabla resumen actualizada ===");
            System.out.println("Cantidad ventas efectivo: " + numVentasEfectivo + " - Total: $" + formatoMoneda.format(ventasEfectivo));
            System.out.println("Cantidad ventas tarjeta/transf: " + numVentasTarjeta + " - Total: $" + formatoMoneda.format(ventasTarjeta + ventasTransferencia));
            
            bd.cerrarConexion();
            
        } catch (Exception e) {
            System.err.println("Error al actualizar tabla resumen: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Constructor que inicializa la vista de cierre de caja
     * @param controladorMenu Referencia al controlador del menú principal
     */
    /**
     * Constructor que inicializa la vista de cierre de caja
     * @param controladorMenu Referencia al controlador del menú principal
     */
    public ControladorCierreCaja(ControladorMenu controladorMenu) {
        this.controladorMenu = controladorMenu;
        this.padre = controladorMenu.getVentana();
        this.vistaCierreCaja = new VistaCierreCaja();
        
        // Configurar listeners
        this.vistaCierreCaja.getBtnCerrarCaja().addActionListener(this);
        this.vistaCierreCaja.getBtnCancelar().addActionListener(this);
        // El botón de imprimir ha sido eliminado
        
        // Añadir listener para el campo de efectivo
        this.vistaCierreCaja.getTEfectivo().addActionListener(this);
        
        // Establecer el nombre del cajero actual
        this.vistaCierreCaja.setCajero(controladorMenu.getNombreUsuarioActual());
        
        // Cargar datos iniciales
        cargarDatosIniciales();
        
        // Calcular ganancia (asumiendo que es la suma total de ventas)
        double ganancia = totalVentas; // 20% de ganancia, ajustar según tu lógica de negocio
        if (vistaCierreCaja.getTGanancias() != null) {
            vistaCierreCaja.getTGanancias().setText(formatoMoneda.format(ganancia));
        }
        
        // Mostrar la vista de cierre de caja
        controladorMenu.getVentana().getEscritorio().add(this.vistaCierreCaja);
        try {
            this.vistaCierreCaja.setMaximum(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
        this.vistaCierreCaja.setVisible(true);
    }
    
    /**
     * Devuelve la ventana de cierre de caja
     * @return JInternalFrame que contiene la vista
     */
    public JInternalFrame getVentana() {
        return this.vistaCierreCaja;
    }
    
    /**
     * Carga datos iniciales para el cierre de caja
     */
    private void cargarDatosIniciales() {
        try {
            // Obtener la apertura de caja más reciente que esté abierta
            BaseDatos bd = new BaseDatos();
            
            // Obtener datos de la apertura de caja
            ArrayList<String[]> aperturas = bd.consultar("CajaAperturas", "*", "estado='1' ORDER BY id DESC LIMIT 1");
            
            if (aperturas != null && !aperturas.isEmpty()) {
                String[] apertura = aperturas.get(0);
                idApertura = apertura[0]; // ID ahora es string
                fondoInicial = Double.parseDouble(apertura[3]);
                String fecha = apertura[1];
                String hora = apertura[2];
                
                // Mostrar información de la apertura
                vistaCierreCaja.getLFecha().setText(fecha);
                vistaCierreCaja.getLHora().setText(hora);
                vistaCierreCaja.getTFondoInicial().setText("$" + formatoMoneda.format(fondoInicial));
                
                // Reiniciar los valores
                ventasEfectivo = 0;
                ventasTarjeta = 0;
                ventasTransferencia = 0;
                totalVentas = 0;
                
                // Usar el nuevo método para obtener directamente el total de ventas del día
                totalVentas = bd.obtenerTotalVentasDia(fecha);
                System.out.println("Total de ventas obtenido directamente: $" + totalVentas);
                
                // Debugging: Imprimir la fecha que estamos consultando
                System.out.println("Consultando ventas para la fecha: " + fecha);
                
                // Verificar todas las ventas de ese día (para depuración)
                ArrayList<String[]> todasLasVentas = bd.consultar(
                        "Ventas", "*", "DATE(fecha) = '" + fecha + "'");
                System.out.println("Total de ventas encontradas: " + (todasLasVentas != null ? todasLasVentas.size() : 0));
                
                // Obtener ventas en efectivo (tipo pago 1)
                ArrayList<String[]> ventasEfectivoQuery = bd.consultar(
                        "Ventas", "SUM(CAST(total AS DECIMAL(10,2))) AS total_ventas", "DATE(fecha) = '" + fecha + "' AND idTipoVenta='1'");
                
                if (ventasEfectivoQuery != null && !ventasEfectivoQuery.isEmpty() && ventasEfectivoQuery.get(0)[0] != null) {
                    ventasEfectivo = Double.parseDouble(ventasEfectivoQuery.get(0)[0]);
                    System.out.println("Ventas en efectivo: " + ventasEfectivo);
                }
                
                // Obtener ventas con tarjeta (tipo pago 2)
                ArrayList<String[]> ventasTarjetaQuery = bd.consultar(
                		"Ventas", "SUM(CAST(total AS DECIMAL(10,2))) AS total_ventas", "DATE(fecha) = '" + fecha + "' AND idTipoVenta='2'");
                
                if (ventasTarjetaQuery != null && !ventasTarjetaQuery.isEmpty() && ventasTarjetaQuery.get(0)[0] != null) {
                    ventasTarjeta = Double.parseDouble(ventasTarjetaQuery.get(0)[0]);
                    System.out.println("Ventas con tarjeta: " + ventasTarjeta);
                }
                
                // Obtener ventas por transferencia (tipo pago 3)
                ArrayList<String[]> ventasTransferenciaQuery = bd.consultar(
                        "Ventas", "SUM(CAST(total AS DECIMAL(10,2))) AS total_ventas", "DATE(fecha) = '" + fecha + "' AND idTipoVenta='3'");
                
                if (ventasTransferenciaQuery != null && !ventasTransferenciaQuery.isEmpty() && ventasTransferenciaQuery.get(0)[0] != null) {
                    ventasTransferencia = Double.parseDouble(ventasTransferenciaQuery.get(0)[0]);
                    System.out.println("Ventas por transferencia: " + ventasTransferencia);
                }
                
                // Calcular el total de ventas
                totalVentas = ventasEfectivo + ventasTarjeta + ventasTransferencia;
                System.out.println("Total de ventas calculado: " + totalVentas);
                
                // Calcular el total que debería haber en caja (fondo inicial + ventas en efectivo)
                double totalEsperado = fondoInicial + ventasEfectivo;
                
                // Actualizar TODOS los campos de la vista una sola vez
                vistaCierreCaja.getTDineroEfectivo().setText(formatoMoneda.format(ventasEfectivo));
                vistaCierreCaja.getTTarjeta().setText(formatoMoneda.format(ventasTarjeta + ventasTransferencia));
                vistaCierreCaja.getTVentasTotal().setText(formatoMoneda.format(totalVentas));
                vistaCierreCaja.getTTotalVentas().setText(formatoMoneda.format(totalVentas));
                vistaCierreCaja.getTTotal().setText("$" + formatoMoneda.format(totalEsperado));
                
                // Mostrar desglose de ventas por tipo de pago en la consola para depuración
                System.out.println("=== Resumen de ventas del día ===");
                System.out.println("Ventas en efectivo: $" + formatoMoneda.format(ventasEfectivo));
                System.out.println("Ventas con tarjeta: $" + formatoMoneda.format(ventasTarjeta));
                System.out.println("Ventas por transferencia: $" + formatoMoneda.format(ventasTransferencia));
                System.out.println("Total de ventas: $" + formatoMoneda.format(totalVentas));
                System.out.println("Total esperado en caja: $" + formatoMoneda.format(totalEsperado));
                
                // Cargar ventas en la tabla (SIN volver a calcular totales)
                cargarVentasDelDia(fecha);
                
                // Actualizar tabla resumen después de cargar ventas
                actualizarTablaResumen(fecha);
            } else {
                JOptionPane.showMessageDialog(vistaCierreCaja, 
                        "No hay una caja abierta en este momento.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                vistaCierreCaja.dispose();
            }
            
            bd.cerrarConexion();
        } catch (Exception e) {
            String mensaje = "Error al cargar los datos iniciales: " + e.getMessage();
            
            // Verificar si el error está relacionado con la sintaxis SQL
            if (e.getMessage() != null && e.getMessage().contains("syntax")) {
                mensaje += "\n\nPosible error de sintaxis SQL. Consulte al administrador del sistema.";
            }
            
            JOptionPane.showMessageDialog(vistaCierreCaja, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Error al cargar datos iniciales: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Carga las ventas del día en la tabla
     */
    private void cargarVentasDelDia(String fecha) {
        try {
            BaseDatos bd = new BaseDatos();
            
            // Mostrar solo las ventas en efectivo (sin incluir el fondo inicial)
            vistaCierreCaja.getTDineroEfectivo().setText(formatoMoneda.format(ventasEfectivo));
            
            // Modificamos la consulta para considerar solo la parte de fecha
            ArrayList<String[]> ventas = bd.consultar("Ventas v INNER JOIN TipoVenta tv ON v.idTipoVenta = tv.id", 
                    "v.idVenta, v.fecha, v.total, tv.etiqueta as tipo_pago", 
                    "DATE(v.fecha) = '" + fecha + "'");
            
            System.out.println("Consulta de ventas - DATE(v.fecha) = '" + fecha + "'");
            System.out.println("Ventas encontradas: " + (ventas != null ? ventas.size() : 0));
            
            // Verificación adicional: listar todas las ventas para depuración
            ArrayList<String[]> todasVentas = bd.consultar("Ventas", "idVenta, fecha, total", null);
            System.out.println("===== TODAS LAS VENTAS =====");
            if (todasVentas != null) {
                for (String[] v : todasVentas) {
                    System.out.println("ID: " + v[0] + ", Fecha: " + v[1] + ", Total: " + v[2]);
                }
            }
            System.out.println("=========================");
            
            // Crear modelo para la tabla
            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[] {"ID", "Fecha/Hora", "Total", "Tipo de Pago"}, 0);
            
            if (ventas != null && !ventas.isEmpty()) {
                // Solo agregar las ventas a la tabla - NO recalcular totales aquí
                for (String[] venta : ventas) {
                    double total = Double.parseDouble(venta[2]);
                    
                    modelo.addRow(new Object[] {
                            venta[0],
                            venta[1],
                            "$" + formatoMoneda.format(total),
                            venta[3]
                    });
                }
                
                System.out.println("=== Ventas cargadas en la tabla: " + ventas.size() + " ===");
            } else {
                System.out.println("No se encontraron ventas para la fecha: " + fecha);
            }
            
            vistaCierreCaja.getTablaResumen().setModel(modelo);
            bd.cerrarConexion();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vistaCierreCaja, 
                    "Error al cargar las ventas del día: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Calcula la diferencia entre el monto físico y el esperado
     */
    private void calcularDiferencia() {
        try {
            // Obtener el monto en efectivo ingresado por el usuario
            String montoTexto = vistaCierreCaja.getTEfectivo().getText().trim().replace(",", "");
            
            if (montoTexto.isEmpty()) {
                JOptionPane.showMessageDialog(vistaCierreCaja, 
                        "Por favor ingrese el monto físico en caja.", 
                        "Campo Requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double montoFisico = Double.parseDouble(montoTexto);
            // Calcular el efectivo esperado (fondo inicial + ventas en efectivo)
            double efectivoEsperado = fondoInicial + ventasEfectivo;
            double diferencia = montoFisico - efectivoEsperado;
            
            // Mostrar la diferencia formateada
            vistaCierreCaja.getTDiferencia().setText("$" + formatoMoneda.format(diferencia));
            
            // Cambiar el color según si hay faltante o sobrante
            if (diferencia < 0) {
                // Cambiar el color a rojo para faltante
                vistaCierreCaja.getTDiferencia().setForeground(new Color(231, 76, 60));
            } else if (diferencia > 0) {
                // Cambiar el color a verde para sobrante
                vistaCierreCaja.getTDiferencia().setForeground(new Color(46, 204, 113));
            } else {
                // Negro si no hay diferencia
                vistaCierreCaja.getTDiferencia().setForeground(Color.BLACK);
            }
            
            // Actualizar el total en caja (efectivo ingresado)
            vistaCierreCaja.getTTotal().setText("$" + formatoMoneda.format(montoFisico));
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vistaCierreCaja, 
                    "Por favor ingrese un monto válido (sólo números y punto decimal).", 
                    "Formato Inválido", JOptionPane.WARNING_MESSAGE);
            vistaCierreCaja.getTDiferencia().setText("Error");
            vistaCierreCaja.getTDiferencia().setForeground(Color.RED);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vistaCierreCaja, 
                    "Error al calcular la diferencia: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Realiza el cierre de caja
     */
    private void cerrarCaja() {
        try {
            // Asegurarnos de que el total de ventas esté actualizado
            System.out.println("Al cerrar caja - Total de ventas: " + totalVentas);
            System.out.println("Al cerrar caja - Ventas en efectivo: " + ventasEfectivo);
            System.out.println("Al cerrar caja - Ventas con tarjeta: " + ventasTarjeta);
            System.out.println("Al cerrar caja - Ventas por transferencia: " + ventasTransferencia);
            System.out.println("Al cerrar caja - Dinero en efectivo (fondo inicial + ventas efectivo): " + (fondoInicial + ventasEfectivo));
            
            String montoTexto = vistaCierreCaja.getTEfectivo().getText().trim().replace(",", "");
            
            if (montoTexto.isEmpty()) {
                JOptionPane.showMessageDialog(vistaCierreCaja, 
                        "Por favor ingrese el monto físico en caja.", 
                        "Campo Requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double montoFisico = Double.parseDouble(montoTexto);
            double totalEsperado = fondoInicial + ventasEfectivo; // Solo se suma el efectivo para la caja física
            double diferencia = montoFisico - totalEsperado;
            
            // Obtener fecha y hora actual
            Date fechaActual = new Date();
            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
            String fecha = formatoFecha.format(fechaActual);
            String hora = formatoHora.format(fechaActual);
            
            // NO generar ID manualmente - MySQL lo auto-incrementa
            // El campo 'id' es AUTO_INCREMENT en MySQL
            
            // Registrar cierre de caja
            BaseDatos bd = new BaseDatos();
            String[] valores = {
                fecha,
                hora,
                String.valueOf(fondoInicial),
                String.valueOf(totalEsperado),
                String.valueOf(ventasEfectivo),
                String.valueOf(ventasTarjeta),
                String.valueOf(ventasTransferencia),
                String.valueOf(totalVentas),
                String.valueOf(montoFisico),  // monto_fisico incluido
                controladorMenu.getNombreUsuarioActual() // Usuario actual
            };
            
            // Omitir el campo 'id' para que MySQL lo genere automáticamente
            boolean resultado = bd.insertar("CajaCierres", 
            	    "fecha,hora,monto_inicial,monto_final,ventas_efectivo,ventas_tarjeta,ventas_transferencia,total_ventas,monto_fisico,id_usuario", 
            	    valores);
            if (resultado) {
                // Actualizar el estado de la apertura de caja
                bd.modificar("CajaAperturas", "estado", "0", "id='" + idApertura + "'");
                
                // Registrar hora de cierre y monto final en CajaAperturas
                bd.modificar("CajaAperturas", "monto_final", String.valueOf(totalEsperado), "id='" + idApertura + "'");
                bd.modificar("CajaAperturas", "hora_cierre", hora, "id='" + idApertura + "'");
                
                // Actualizar el estado de la caja en el controlador de menú
                controladorMenu.actualizarEstadoCaja(false);
                
                JOptionPane.showMessageDialog(vistaCierreCaja, 
                        "Cierre de caja realizado con éxito.", 
                        "Cierre Exitoso", JOptionPane.INFORMATION_MESSAGE);
                
                vistaCierreCaja.dispose();
            } else {
                JOptionPane.showMessageDialog(vistaCierreCaja, 
                        "Error al registrar el cierre de caja. Intente nuevamente.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            bd.cerrarConexion();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vistaCierreCaja, 
                    "Por favor ingrese un monto válido (sólo números y punto decimal).", 
                    "Formato Inválido", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vistaCierreCaja, 
                    "Error al cerrar la caja: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
        
        /**
         * Devuelve la vista de cierre de caja
         * @return Vista de cierre de caja
         */
        public VistaCierreCaja getVistaCierreCaja() {
            return this.vistaCierreCaja;
        }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaCierreCaja.getTEfectivo()) {
            // Cada vez que se modifica el valor del efectivo, recalcular la diferencia
            calcularDiferencia();
        } else if (e.getSource() == vistaCierreCaja.getBtnCerrarCaja()) {
            // Lógica para cerrar la caja
            cerrarCaja();
        } else if (e.getSource() == vistaCierreCaja.getBtnCancelar()) {
            // Cerrar la ventana sin guardar cambios
            vistaCierreCaja.dispose();
        } else if (e.getSource() == vistaCierreCaja.getBtnImprimir()) {
            // Lógica para imprimir el reporte de cierre
            JOptionPane.showMessageDialog(vistaCierreCaja, 
                "Imprimiendo reporte de cierre de caja...",
                "Impresión", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
