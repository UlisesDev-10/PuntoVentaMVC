
package Controlador;

import java.awt.Desktop;
import java.io.File;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import Modelo.BaseDatos;
import Vista.VistaReportes;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import Modelo.BaseDatos;
import Vista.VistaReportes;
import com.itextpdf.text.BaseColor;

public class ControladorReportes implements ActionListener {
    
    private VistaReportes vistaReportes;
    private NumberFormat formatoMoneda = new DecimalFormat("$#,##0.00");
    
    /**
     * Constructor sin parámetros para ser llamado desde ControladorMenuReportes
     */
    public ControladorReportes() {
        // Constructor sin parámetros
    }
    
    /**
     * Constructor con parámetros para otras implementaciones
     */
    public ControladorReportes(VistaReportes vistaReportes) {
        this.vistaReportes = vistaReportes;
        this.vistaReportes.getRbSoloNoCanceladas().setSelected(true);
        // Agregar ActionListener para los botones
        this.vistaReportes.getBtnGenerar().addActionListener(this);
        this.vistaReportes.getBtnCancelar().addActionListener(this);
        
        // Agregar ActionListener para el ComboBox de tipo de reporte
        this.vistaReportes.getComboTipoReporte().addActionListener(e -> {
            String tipoSeleccionado = (String) this.vistaReportes.getComboTipoReporte().getSelectedItem();
            this.vistaReportes.mostrarPanelConfiguracion(tipoSeleccionado);
        });
    }
    
    /**
     * Carga los productos para el reporte de inventario
     */
    public DefaultTableModel cargarProductos() {
        try {
            BaseDatos bd = new BaseDatos();
            // Filtrar solo productos activos
            ArrayList<String[]> productos = bd.consultar("Productos", 
                "idProducto, sku, producto, descripcion, precio_venta, stock, idcategoria, marca", 
                "activo = 1");
            
            DefaultTableModel modelo = new DefaultTableModel(
                new Object[] {"ID", "SKU", "Producto", "Descripción", "Precio", "Stock", "Categoría", "Marca"}, 0);
            
            if (productos != null && !productos.isEmpty()) {
                for (String[] producto : productos) {
                    modelo.addRow(new Object[] {
                        producto[0], // idProducto
                        producto[1], // sku
                        producto[2], // producto (nombre)
                        producto[3], // descripcion
                        producto[4], // precio_venta
                        producto[5], // stock
                        producto[6], // idcategoria
                        producto[7]  // marca
                    });
                }
            }
            
            bd.cerrarConexion();
            return modelo;
        } catch (Exception e) {
            e.printStackTrace();
            return new DefaultTableModel();
        }
    }

    
    /**
     * Carga las ventas para el reporte en el rango de fechas especificado
     */
    public DefaultTableModel cargarVentas(Date fechaInicio, Date fechaFin) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaInicioStr = sdf.format(fechaInicio);
            String fechaFinStr = sdf.format(fechaFin);
            
            BaseDatos bd = new BaseDatos();
            ArrayList<String[]> ventas = bd.consultarVentas(fechaInicioStr, fechaFinStr);
            
            // Crear modelo para la tabla
            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[] {"ID Venta", "Fecha", "Total", "Tipo de Venta"}, 0);
            
            if (ventas != null && !ventas.isEmpty()) {
                for (String[] venta : ventas) {
                    modelo.addRow(new Object[] {
                            venta[0], // idVenta
                            venta[1], // fecha
                            venta[2], // total
                            venta[3]  // idTipoVenta
                    });
                }
            }
            
            bd.cerrarConexion();
            return modelo;
            
        } catch (Exception e) {
            e.printStackTrace();
            return new DefaultTableModel();
        }
    }
    
    /**
     * Carga los cierres de caja para la fecha especificada
     */
    public DefaultTableModel cargarCierresCaja(Date fecha) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaStr = sdf.format(fecha);
            
            BaseDatos bd = new BaseDatos();
            ArrayList<String[]> cierres = bd.consultarCierreCaja(fechaStr);
            
            // Crear modelo para la tabla
            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[] {"ID", "Fecha", "Hora", "Monto Inicial", "Monto Final", "Total Ventas", 
                                 "Ventas Efectivo", "Ventas Tarjeta", "Ventas Transferencia"}, 0);
            
            if (cierres != null && !cierres.isEmpty()) {
                for (String[] cierre : cierres) {
                    modelo.addRow(new Object[] {
                            cierre[0], // id
                            cierre[1], // fecha
                            cierre[2], // hora
                            cierre[3], // monto_inicial
                            cierre[4], // monto_final
                            cierre[5], // total_ventas
                            cierre[6], // ventas_efectivo
                            cierre[7], // ventas_tarjeta
                            cierre[8]  // ventas_transferencia
                    });
                }
            }
            
            bd.cerrarConexion();
            return modelo;
            
        } catch (Exception e) {
            e.printStackTrace();
            return new DefaultTableModel();
        }
    }
    
    /**
     * Genera un reporte con inner join entre Ventas y DetalleVentas
     */
    public DefaultTableModel generarReporteVentasDetallado(Date fechaInicio, Date fechaFin) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaInicioStr = sdf.format(fechaInicio);
            String fechaFinStr = sdf.format(fechaFin);
            
            BaseDatos bd = new BaseDatos();
            
            // Consulta con inner join
            String condicion = "v.fecha BETWEEN '" + fechaInicioStr + "' AND '" + fechaFinStr + "' AND " +
                              "dv.idVenta = v.idVenta AND p.idProducto = dv.idProducto " +
                              "ORDER BY v.fecha, v.idVenta";
            
            ArrayList<String[]> detalles = bd.consultar(
            		   "Ventas v " +
            				    "INNER JOIN DetalleVentas dv ON dv.idVenta = v.idVenta " +
            				    "INNER JOIN Productos p ON p.idProducto = dv.idProducto",

            				    "v.idVenta, v.fecha, p.producto, dv.cantidad, dv.precioUnitario, " +
            				    "(CAST(dv.cantidad AS FLOAT) * CAST(dv.precioUnitario AS FLOAT) * 1.16) AS subtotal, v.total",

            				    condicion
            				);
            // Crear modelo para la tabla
            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[] {"ID Venta", "Fecha", "Producto", "Cantidad", "Precio Unit.", "Subtotal", "Total Venta"}, 0);
            
            if (detalles != null && !detalles.isEmpty()) {
                for (String[] detalle : detalles) {
                    modelo.addRow(new Object[] {
                            detalle[0], // idVenta
                            detalle[1], // fecha
                            detalle[2], // producto
                            detalle[3], // cantidad
                            detalle[4], // precioUnitario
                            detalle[5], // subtotal
                            detalle[6]  // total
                    });
                }
            }
            
            bd.cerrarConexion();
            return modelo;
            
        } catch (Exception e) {
            e.printStackTrace();
            return new DefaultTableModel();
        }
    }
    
    /**
     * Genera un reporte de productos
     * @param incluyeStockCero true para incluir todos los productos, false para solo con stock
     * @return ruta del reporte generado o null si no hay datos
     */
    public String generarReporteProductos(boolean incluyeStockCero) {
    	 try {
    	        BaseDatos bd = new BaseDatos();
    	        // Siempre incluir productos activos
    	        String condicion = incluyeStockCero ? "activo = 1" : "activo = 1 AND stock > 0";
    	        ArrayList<String[]> productos = bd.consultar("Productos", 
    	            "idProducto, sku, producto, idcategoria, precio_venta, marca, stock, descripcion", 
    	            condicion);
    	        bd.cerrarConexion();
    	        
    	        if (productos == null || productos.isEmpty()) {
    	            return null;
    	        }
            
            // Crear directorio si no existe
            File dir = new File("reportes");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // Nombre del archivo con timestamp para hacerlo único
            String nombreArchivo = "reportes/productos_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";
            
            // Crear documento PDF
            Document documento = new Document();
            PdfWriter.getInstance(documento, new FileOutputStream(nombreArchivo));
            documento.open();
            
            // Título del reporte
            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph titulo = new Paragraph("Reporte de Productos", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);
            
            // Fecha del reporte
            Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
            Paragraph fechaReporte = new Paragraph("Generado el: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), fuenteSubtitulo);
            fechaReporte.setAlignment(Element.ALIGN_CENTER);
            fechaReporte.setSpacingAfter(20);
            documento.add(fechaReporte);
            
            // Crear tabla
            PdfPTable tabla = new PdfPTable(6);
            tabla.setWidthPercentage(100);
            
            // Encabezados
            String[] encabezados = {"ID", "SKA", "Producto", "Categoria", "Precio", "Marca"};
            Font fuenteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
            
            for (String encabezado : encabezados) {
                PdfPCell celda = new PdfPCell(new Phrase(encabezado, fuenteEncabezado));
                celda.setBackgroundColor(BaseColor.DARK_GRAY);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                celda.setPadding(9);
                tabla.addCell(celda);
            }
            
            // Datos de productos
            Font fuenteNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);
            
            for (String[] producto : productos) {
                tabla.addCell(new Phrase(producto[0], fuenteNormal)); // ID
                // SKA generado con el ID
                tabla.addCell(new Phrase(producto[1], fuenteNormal)); // Nombre
                tabla.addCell(new Phrase(producto[2], fuenteNormal)); // Descripción
                tabla.addCell(new Phrase(producto[3], fuenteNormal));
                // Formatear precio con símbolo de moneda
                try {
                    double precio = Double.parseDouble(producto[4]);
                    tabla.addCell(new Phrase(formatoMoneda.format(precio), fuenteNormal));
                } catch (Exception e) {
                    tabla.addCell(new Phrase(producto[4], fuenteNormal));
                }
                
              
                tabla.addCell(new Phrase(producto[5], fuenteNormal)); // Categoría
                
            }
            
            documento.add(tabla);
            
            // Resumen
            Paragraph resumen = new Paragraph("Total de productos: " + productos.size(), fuenteSubtitulo);
            resumen.setSpacingBefore(20);
            documento.add(resumen);
            
            documento.close();
            return nombreArchivo;
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                    "Error al generar reporte de productos: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Genera un reporte de ventas en el rango de fechas especificado
     * @param fechaInicio fecha de inicio en formato yyyy-MM-dd
     * @param fechaFin fecha de fin en formato yyyy-MM-dd
         * @param incluirCanceladas true para incluir todas las ventas (incluso canceladas), false para solo ventas activas
         * @param incluirCanceladas true para incluir ventas canceladas, false para excluirlas
         * @return ruta del reporte generado o null si no hay datos
         */
        public String generarReporteVentas(String fechaInicio, String fechaFin, boolean incluirCanceladas) {
        try {
            BaseDatos bd = new BaseDatos();
                        ArrayList<String[]> ventas;
                        
                        // Elegir la consulta adecuada según si se incluyen ventas canceladas o no
                        if (incluirCanceladas) {
                            ventas = bd.consultarVentas(fechaInicio, fechaFin);
                        } else {
                            ventas = bd.consultarVentasNoCanceladas(fechaInicio, fechaFin);
                        }
                
                // También obtenemos los detalles de ventas para un reporte más completo
                ArrayList<String[]> detallesVentas = bd.consultar(
                		"Ventas v " +
                			    "INNER JOIN DetalleVentas dv ON dv.idVenta = v.idVenta " +
                			    "INNER JOIN Productos p ON p.idProducto = dv.idProducto",
    
                			    "v.idVenta, v.fecha, p.producto, dv.cantidad, dv.precioUnitario, " +
                			    "(CAST(dv.cantidad AS FLOAT) * CAST(dv.precioUnitario AS FLOAT) * 1.16) AS subtotal",
    
                			    "v.fecha BETWEEN '" + fechaInicio + "' AND '" + fechaFin + "' " +
                			    (incluirCanceladas ? "" : "AND (v.cancelada IS NULL OR v.cancelada != 1) ") +
            			    "ORDER BY v.fecha, v.idVenta"
            			);
            
            bd.cerrarConexion();
            
            if (ventas == null || ventas.isEmpty()) {
                return null;
            }
            
            // Crear directorio si no existe
            File dir = new File("reportes");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // Nombre del archivo
            String tipoReporte = incluirCanceladas ? "todas_ventas" : "ventas_no_canceladas";
            String nombreArchivo = "reportes/" + tipoReporte + "_" + fechaInicio.replaceAll("-", "") + "_a_" + 
                                  fechaFin.replaceAll("-", "") + ".pdf";
            
            // Crear documento PDF
            Document documento = new Document();
            PdfWriter.getInstance(documento, new FileOutputStream(nombreArchivo));
            documento.open();
            
            // Título del reporte
            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            String tituloTexto = incluirCanceladas ? "Reporte de Todas las Ventas" : "Reporte de Ventas No Canceladas";
            Paragraph titulo = new Paragraph(tituloTexto, fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);
            
            // Fecha del reporte
            Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
            Paragraph periodoReporte = new Paragraph("Período: " + fechaInicio + " a " + fechaFin, fuenteSubtitulo);
            periodoReporte.setAlignment(Element.ALIGN_CENTER);
            
            Paragraph fechaGeneracion = new Paragraph("Generado el: " + 
                                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), fuenteSubtitulo);
            fechaGeneracion.setAlignment(Element.ALIGN_CENTER);
            fechaGeneracion.setSpacingAfter(20);
            
            documento.add(periodoReporte);
            documento.add(fechaGeneracion);
            
            // Tabla de resumen de ventas
            PdfPTable tablaResumen = new PdfPTable(4);
            tablaResumen.setWidthPercentage(100);
            
            // Encabezados de resumen
            String[] encabezadosResumen = {"ID Venta", "Fecha", "Total", "Tipo de Venta"};
            Font fuenteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
            
            for (String encabezado : encabezadosResumen) {
                PdfPCell celda = new PdfPCell(new Phrase(encabezado, fuenteEncabezado));
                celda.setBackgroundColor(BaseColor.DARK_GRAY);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                celda.setPadding(8);
                tablaResumen.addCell(celda);
            }
            
            // Datos de resumen de ventas
            Font fuenteNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);
            double totalVentas = 0;
            
            for (String[] venta : ventas) {
                tablaResumen.addCell(new Phrase(venta[0], fuenteNormal)); // ID Venta
                tablaResumen.addCell(new Phrase(venta[1], fuenteNormal)); // Fecha
                
                // Formatear total con símbolo de moneda
                try {
                    double total = Double.parseDouble(venta[2]);
                    totalVentas += total;
                    tablaResumen.addCell(new Phrase(formatoMoneda.format(total), fuenteNormal));
                } catch (Exception e) {
                    tablaResumen.addCell(new Phrase(venta[2], fuenteNormal));
                }
                
                // Tipo de venta (mostrar descripción en lugar de ID)
                String tipoVenta = "Desconocido";
                switch (venta[3]) {
                    case "1": tipoVenta = "Efectivo"; break;
                    case "2": tipoVenta = "Tarjeta"; break;
                    case "3": tipoVenta = "Transferencia"; break;
                }
                tablaResumen.addCell(new Phrase(tipoVenta, fuenteNormal));
            }
            
            documento.add(tablaResumen);
            
            // Añadir total general
            Paragraph totalGeneral = new Paragraph("Total General: " + formatoMoneda.format(totalVentas), 
                                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
            totalGeneral.setAlignment(Element.ALIGN_RIGHT);
            totalGeneral.setSpacingBefore(10);
            totalGeneral.setSpacingAfter(20);
            documento.add(totalGeneral);
            
            // Si tenemos detalles, mostramos tabla de detalles
            if (detallesVentas != null && !detallesVentas.isEmpty()) {
                Paragraph tituloDetalles = new Paragraph("Detalle de Productos Vendidos", 
                                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
                tituloDetalles.setSpacingAfter(10);
                documento.add(tituloDetalles);
                
                PdfPTable tablaDetalles = new PdfPTable(5);
                tablaDetalles.setWidthPercentage(100);
                
                // Encabezados de detalles
                String[] encabezadosDetalles = {"ID Venta", "Fecha", "Producto", "Cantidad", "Subtotal"};
                
                for (String encabezado : encabezadosDetalles) {
                    PdfPCell celda = new PdfPCell(new Phrase(encabezado, fuenteEncabezado));
                    celda.setBackgroundColor(BaseColor.DARK_GRAY);
                    celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                    celda.setPadding(8);
                    tablaDetalles.addCell(celda);
                }
                
                // Datos de detalles
                for (String[] detalle : detallesVentas) {
                    tablaDetalles.addCell(new Phrase(detalle[0], fuenteNormal)); // ID Venta
                    tablaDetalles.addCell(new Phrase(detalle[1], fuenteNormal)); // Fecha
                    tablaDetalles.addCell(new Phrase(detalle[2], fuenteNormal)); // Producto
                    tablaDetalles.addCell(new Phrase(detalle[3], fuenteNormal)); // Cantidad
                    
                    // Formatear subtotal
                    try {
                        double subtotal = Double.parseDouble(detalle[5]);
                        tablaDetalles.addCell(new Phrase(formatoMoneda.format(subtotal), fuenteNormal));
                    } catch (Exception e) {
                        tablaDetalles.addCell(new Phrase(detalle[5], fuenteNormal));
                    }
                }
                
                documento.add(tablaDetalles);
            }
            
            documento.close();
            return nombreArchivo;
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                    "Error al generar reporte de ventas: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Genera un reporte de caja para la fecha especificada
     * @param fecha fecha en formato yyyy-MM-dd
     * @return ruta del reporte generado o null si no hay datos
     */
    public String generarReporteCaja(String fecha) {
        try {
            BaseDatos bd = new BaseDatos();
            ArrayList<String[]> cierres = bd.consultarCierreCaja(fecha);
            
            // También obtenemos las ventas del día para el reporte
            ArrayList<String[]> ventasDia = bd.consultarVentas(fecha, fecha);
            
            bd.cerrarConexion();
            
            if (cierres == null || cierres.isEmpty()) {
                return null;
            }
            
            // Crear directorio si no existe
            File dir = new File("reportes");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // Nombre del archivo
            String nombreArchivo = "reportes/caja_" + fecha.replaceAll("-", "") + ".pdf";
            
            // Crear documento PDF
            Document documento = new Document();
            PdfWriter.getInstance(documento, new FileOutputStream(nombreArchivo));
            documento.open();
            
            // Título del reporte
            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph titulo = new Paragraph("Reporte de Cierre de Caja", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);
            
            // Fecha del reporte
            Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
            Paragraph fechaReporte = new Paragraph("Fecha: " + fecha, fuenteSubtitulo);
            fechaReporte.setAlignment(Element.ALIGN_CENTER);
            
            Paragraph fechaGeneracion = new Paragraph("Generado el: " + 
                                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), fuenteSubtitulo);
            fechaGeneracion.setAlignment(Element.ALIGN_CENTER);
            fechaGeneracion.setSpacingAfter(20);
            
            documento.add(fechaReporte);
            documento.add(fechaGeneracion);
            
            // Tabla de cierres de caja
            PdfPTable tablaCierres = new PdfPTable(7);
            tablaCierres.setWidthPercentage(100);
            
            // Encabezados
            String[] encabezados = {"ID", "Hora", "Monto Inicial", "Monto Final", "Total Ventas", 
                                   "Ventas Efectivo", "Ventas Tarjeta"};
            Font fuenteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
            
            for (String encabezado : encabezados) {
                PdfPCell celda = new PdfPCell(new Phrase(encabezado, fuenteEncabezado));
                celda.setBackgroundColor(BaseColor.DARK_GRAY);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                celda.setPadding(8);
                tablaCierres.addCell(celda);
            }
            
            // Datos de cierres
            Font fuenteNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);
            
            for (String[] cierre : cierres) {
                tablaCierres.addCell(new Phrase(cierre[0], fuenteNormal)); // ID
                tablaCierres.addCell(new Phrase(cierre[2], fuenteNormal)); // Hora
                
                // Formatear montos con símbolo de moneda
                try {
                    double montoInicial = Double.parseDouble(cierre[3]);
                    tablaCierres.addCell(new Phrase(formatoMoneda.format(montoInicial), fuenteNormal));
                } catch (Exception e) {
                    tablaCierres.addCell(new Phrase(cierre[3], fuenteNormal));
                }
                
                try {
                    double montoFinal = Double.parseDouble(cierre[4]);
                    tablaCierres.addCell(new Phrase(formatoMoneda.format(montoFinal), fuenteNormal));
                } catch (Exception e) {
                    tablaCierres.addCell(new Phrase(cierre[4], fuenteNormal));
                }
                
                try {
                    double totalVentas = Double.parseDouble(cierre[5]);
                    tablaCierres.addCell(new Phrase(formatoMoneda.format(totalVentas), fuenteNormal));
                } catch (Exception e) {
                    tablaCierres.addCell(new Phrase(cierre[5], fuenteNormal));
                }
                
                try {
                    double ventasEfectivo = Double.parseDouble(cierre[6]);
                    tablaCierres.addCell(new Phrase(formatoMoneda.format(ventasEfectivo), fuenteNormal));
                } catch (Exception e) {
                    tablaCierres.addCell(new Phrase(cierre[6], fuenteNormal));
                }
                
                try {
                    double ventasTarjeta = Double.parseDouble(cierre[7]);
                    tablaCierres.addCell(new Phrase(formatoMoneda.format(ventasTarjeta), fuenteNormal));
                } catch (Exception e) {
                    tablaCierres.addCell(new Phrase(cierre[7], fuenteNormal));
                }
            }
            
            documento.add(tablaCierres);
            
            // Si hay ventas del día, añadimos una tabla de resumen de ventas
            if (ventasDia != null && !ventasDia.isEmpty()) {
                Paragraph tituloVentas = new Paragraph("Resumen de Ventas del Día", 
                                       FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
                tituloVentas.setSpacingBefore(20);
                tituloVentas.setSpacingAfter(10);
                documento.add(tituloVentas);
                
                PdfPTable tablaVentas = new PdfPTable(4);
                tablaVentas.setWidthPercentage(100);
                
                // Encabezados de ventas
                String[] encabezadosVentas = {"ID Venta", "Hora", "Total", "Tipo de Venta"};
                
                for (String encabezado : encabezadosVentas) {
                    PdfPCell celda = new PdfPCell(new Phrase(encabezado, fuenteEncabezado));
                    celda.setBackgroundColor(BaseColor.DARK_GRAY);
                    celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                    celda.setPadding(8);
                    tablaVentas.addCell(celda);
                }
                
                // Datos de ventas
                double totalVentasDia = 0;
                
                for (String[] venta : ventasDia) {
                    tablaVentas.addCell(new Phrase(venta[0], fuenteNormal)); // ID Venta
                    
                    // Extraer solo la hora de la fecha completa
                    String fechaCompleta = venta[1];
                    String hora = fechaCompleta.contains(" ") ? fechaCompleta.split(" ")[1] : fechaCompleta;
                    tablaVentas.addCell(new Phrase(hora, fuenteNormal));
                    
                    // Formatear total con símbolo de moneda
                    try {
                        double total = Double.parseDouble(venta[2]);
                        totalVentasDia += total;
                        tablaVentas.addCell(new Phrase(formatoMoneda.format(total), fuenteNormal));
                    } catch (Exception e) {
                        tablaVentas.addCell(new Phrase(venta[2], fuenteNormal));
                    }
                    
                    // Tipo de venta
                    String tipoVenta = "Desconocido";
                    switch (venta[3]) {
                        case "1": tipoVenta = "Efectivo"; break;
                        case "2": tipoVenta = "Tarjeta"; break;
                        case "3": tipoVenta = "Transferencia"; break;
                    }
                    tablaVentas.addCell(new Phrase(tipoVenta, fuenteNormal));
                }
                
                documento.add(tablaVentas);
                
                // Añadir total de ventas del día
                Paragraph totalVentas = new Paragraph("Total de Ventas del Día: " + formatoMoneda.format(totalVentasDia), 
                                      FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
                totalVentas.setAlignment(Element.ALIGN_RIGHT);
                totalVentas.setSpacingBefore(10);
                documento.add(totalVentas);
            }
            
            documento.close();
            return nombreArchivo;
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                    "Error al generar reporte de caja: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Abre un archivo utilizando la aplicación predeterminada del sistema
     * @param rutaArchivo ruta al archivo que se desea abrir
     */
    public void abrirReporte(String rutaArchivo) {
        try {
            File archivo = new File(rutaArchivo);
            if (archivo.exists()) {
                Desktop.getDesktop().open(archivo);
            } else {
                JOptionPane.showMessageDialog(null, 
                        "No se pudo encontrar el archivo: " + rutaArchivo, 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                    "Error al abrir el archivo: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Carga las ventas agrupadas por categoría para el reporte
     * @param fechaInicio fecha de inicio en formato Date
     * @param fechaFin fecha de fin en formato Date
     * @return modelo de tabla con los datos de ventas por categoría
     */
    public DefaultTableModel cargarVentasPorCategoria(Date fechaInicio, Date fechaFin) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaInicioStr = sdf.format(fechaInicio);
            String fechaFinStr = sdf.format(fechaFin);
            
            BaseDatos bd = new BaseDatos();
            
            // Consulta con inner joins y group by para obtener ventas por categoría
            String consulta = "v.fecha BETWEEN '" + fechaInicioStr + "' AND '" + fechaFinStr + "' " +
                             "AND dv.idVenta = v.idVenta " + 
                             "AND p.idProducto = dv.idProducto " +
                             "AND c.idCategoria = p.idCategoria " +
                             "GROUP BY c.idCategoria, c.nombre " +
                             "ORDER BY total_ventas DESC";
            
            ArrayList<String[]> ventasPorCategoria = bd.consultar(
            		 "Ventas v " +
            				    "INNER JOIN DetalleVentas dv ON dv.idVenta = v.idVenta " +
            				    "INNER JOIN Productos p ON p.idProducto = dv.idProducto " +
            				    "INNER JOIN Categorias c ON c.idCategoria = p.idCategoria",

            				    "c.idCategoria, c.nombre, COUNT(DISTINCT v.idVenta) AS num_ventas, " +
            				    "SUM(CAST(dv.cantidad AS FLOAT)) AS cantidad_productos, " +
            				    "SUM((CAST(dv.cantidad AS FLOAT) * CAST(dv.precioUnitario AS FLOAT) * 1.16)) AS total_ventas",

            				    consulta
            				);
            
            // Crear modelo para la tabla
            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[] {"ID Categoría", "Categoría", "Número de Ventas", "Cantidad de Productos", "Total Ventas"}, 0);
            
            if (ventasPorCategoria != null && !ventasPorCategoria.isEmpty()) {
                for (String[] venta : ventasPorCategoria) {
                    try {
                        double totalVentas = Double.parseDouble(venta[4]);
                        modelo.addRow(new Object[] {
                                venta[0], // idCategoria
                                venta[1], // nombre categoría
                                venta[2], // número de ventas
                                venta[3], // cantidad de productos
                                formatoMoneda.format(totalVentas) // total ventas formateado
                        });
                    } catch (Exception e) {
                        modelo.addRow(new Object[] {
                                venta[0], venta[1], venta[2], venta[3], venta[4]
                        });
                    }
                }
            }
            
            bd.cerrarConexion();
            return modelo;
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                    "Error al cargar ventas por categoría: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return new DefaultTableModel();
        }
    }
    
    /**
     * Genera un reporte de ventas por categoría en el rango de fechas especificado
     * @param fechaInicio fecha de inicio en formato yyyy-MM-dd
     * @param fechaFin fecha de fin en formato yyyy-MM-dd
     * @return ruta del reporte generado o null si no hay datos
     */
    public String generarReporteVentasPorCategoria(String fechaInicio, String fechaFin) {
        try {
            // Convertir las fechas de String a Date para usar el método cargarVentasPorCategoria
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaInicioDate = sdf.parse(fechaInicio);
            Date fechaFinDate = sdf.parse(fechaFin);
            
            // Obtener los datos
            DefaultTableModel modeloDatos = cargarVentasPorCategoria(fechaInicioDate, fechaFinDate);
            
            if (modeloDatos.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, 
                        "No hay datos para generar el reporte en el período seleccionado", 
                        "Información", JOptionPane.INFORMATION_MESSAGE);
                return null;
            }
            
            // Crear directorio si no existe
            File dir = new File("reportes");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // Nombre del archivo
            String nombreArchivo = "reportes/ventas_por_categoria_" + fechaInicio.replaceAll("-", "") + 
                                  "_a_" + fechaFin.replaceAll("-", "") + ".pdf";
            
            // Crear documento PDF
            Document documento = new Document();
            PdfWriter.getInstance(documento, new FileOutputStream(nombreArchivo));
            documento.open();
            
            // Título del reporte
            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph titulo = new Paragraph("Reporte de Ventas por Categoría", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);
            
            // Fecha del reporte
            Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
            Paragraph periodoReporte = new Paragraph("Período: " + fechaInicio + " a " + fechaFin, fuenteSubtitulo);
            periodoReporte.setAlignment(Element.ALIGN_CENTER);
            
            Paragraph fechaGeneracion = new Paragraph("Generado el: " + 
                                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), fuenteSubtitulo);
            fechaGeneracion.setAlignment(Element.ALIGN_CENTER);
            fechaGeneracion.setSpacingAfter(20);
            
            documento.add(periodoReporte);
            documento.add(fechaGeneracion);
            
            // Tabla de ventas por categoría
            PdfPTable tabla = new PdfPTable(5);
            tabla.setWidthPercentage(100);
            
            // Encabezados
            String[] encabezados = {"ID", "Categoría", "Número de Ventas", "Cantidad de Productos", "Total Ventas"};
            Font fuenteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
            
            for (String encabezado : encabezados) {
                PdfPCell celda = new PdfPCell(new Phrase(encabezado, fuenteEncabezado));
                celda.setBackgroundColor(BaseColor.DARK_GRAY);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                celda.setPadding(8);
                tabla.addCell(celda);
            }
            
            // Datos de ventas por categoría
            Font fuenteNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);
            double totalGeneral = 0;
            
            for (int i = 0; i < modeloDatos.getRowCount(); i++) {
                tabla.addCell(new Phrase(modeloDatos.getValueAt(i, 0).toString(), fuenteNormal)); // ID
                tabla.addCell(new Phrase(modeloDatos.getValueAt(i, 1).toString(), fuenteNormal)); // Categoría
                tabla.addCell(new Phrase(modeloDatos.getValueAt(i, 2).toString(), fuenteNormal)); // Número de Ventas
                tabla.addCell(new Phrase(modeloDatos.getValueAt(i, 3).toString(), fuenteNormal)); // Cantidad de Productos
                
                // Para el total, extraemos el valor numérico del formato de moneda
                String totalStr = modeloDatos.getValueAt(i, 4).toString().replace("$", "")
                                                                     .replace(",", "");
                try {
                    double total = Double.parseDouble(totalStr);
                    totalGeneral += total;
                } catch (Exception e) {
                    // Si no podemos parsear, usamos el valor tal cual
                }
                
                tabla.addCell(new Phrase(modeloDatos.getValueAt(i, 4).toString(), fuenteNormal)); // Total Ventas
            }
            
            documento.add(tabla);
            
            // Añadir total general
            Paragraph totalGeneralParagraph = new Paragraph("Total General: " + formatoMoneda.format(totalGeneral), 
                                            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
            totalGeneralParagraph.setAlignment(Element.ALIGN_RIGHT);
            totalGeneralParagraph.setSpacingBefore(10);
            documento.add(totalGeneralParagraph);
            
            // Añadir gráfico de barras para visualización
            Paragraph graficaNota = new Paragraph("Nota: Este reporte puede complementarse con una representación " +
                                 "gráfica generada por el sistema para una mejor visualización de los datos.", 
                                 FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10));
            graficaNota.setSpacingBefore(30);
            documento.add(graficaNota);
            
            documento.close();
            return nombreArchivo;
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                    "Error al generar reporte de ventas por categoría: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Carga los productos más vendidos en el rango de fechas especificado
     * @param fechaInicio fecha de inicio en formato Date
     * @param fechaFin fecha de fin en formato Date
     * @param limite número máximo de productos a mostrar (top N)
     * @return modelo de tabla con los datos de productos más vendidos
     */
    public DefaultTableModel cargarProductosMasVendidos(Date fechaInicio, Date fechaFin, int limite) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaInicioStr = sdf.format(fechaInicio);
            String fechaFinStr = sdf.format(fechaFin);

            BaseDatos bd = new BaseDatos();

            ArrayList<String[]> productosMasVendidos = bd.consultar(
                "Ventas v " +
                "INNER JOIN DetalleVentas dv ON dv.idVenta = v.idVenta " +
                "INNER JOIN Productos p ON p.idProducto = dv.idProducto",

                "TOP " + limite + " p.idProducto, p.producto, p.precio_venta, " +
                "SUM(CAST(dv.cantidad AS FLOAT)) AS cantidad_vendida, " +
                "SUM((CAST(dv.cantidad AS FLOAT) * CAST(dv.precioUnitario AS FLOAT) * 1.16)) AS total_ventas",

                "v.fecha BETWEEN '" + fechaInicioStr + "' AND '" + fechaFinStr + "' " +
                "GROUP BY p.idProducto, p.producto, p.precio_venta " +
                "ORDER BY cantidad_vendida DESC"
            );

            DefaultTableModel modelo = new DefaultTableModel(
                new Object[] {"ID", "Producto", "Precio Unitario", "Cantidad Vendida", "Total Ventas"}, 0
            );

            if (productosMasVendidos != null && !productosMasVendidos.isEmpty()) {
                for (String[] producto : productosMasVendidos) {
                    try {
                        double precioUnitario = Double.parseDouble(producto[2]);
                        double totalVentas = Double.parseDouble(producto[4]);

                        modelo.addRow(new Object[] {
                            producto[0],
                            producto[1],
                            formatoMoneda.format(precioUnitario),
                            producto[3],
                            formatoMoneda.format(totalVentas)
                        });
                    } catch (Exception e) {
                        modelo.addRow(new Object[] {
                            producto[0], producto[1], producto[2], producto[3], producto[4]
                        });
                    }
                }
            }

            bd.cerrarConexion();
            return modelo;

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al cargar productos más vendidos: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            return new DefaultTableModel();
        }
    }
    
    /**
     * Genera un reporte de productos más vendidos en el rango de fechas especificado
     * @param fechaInicio fecha de inicio en formato yyyy-MM-dd
     * @param fechaFin fecha de fin en formato yyyy-MM-dd
     * @param limite número máximo de productos a mostrar (top N)
     * @return ruta del reporte generado o null si no hay datos
     */
    public String generarReporteProductosMasVendidos(String fechaInicio, String fechaFin, int limite) {
        try {
            // Convertir las fechas de String a Date para usar el método cargarProductosMasVendidos
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaInicioDate = sdf.parse(fechaInicio);
            Date fechaFinDate = sdf.parse(fechaFin);
            
            // Obtener los datos
            DefaultTableModel modeloDatos = cargarProductosMasVendidos(fechaInicioDate, fechaFinDate, limite);
            
            if (modeloDatos.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, 
                        "No hay datos para generar el reporte en el período seleccionado", 
                        "Información", JOptionPane.INFORMATION_MESSAGE);
                return null;
            }
            
            // Crear directorio si no existe
            File dir = new File("reportes");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // Nombre del archivo
            String nombreArchivo = "reportes/productos_mas_vendidos_" + fechaInicio.replaceAll("-", "") + 
                                  "_a_" + fechaFin.replaceAll("-", "") + ".pdf";
            
            // Crear documento PDF
            Document documento = new Document();
            PdfWriter.getInstance(documento, new FileOutputStream(nombreArchivo));
            documento.open();
            
            // Título del reporte
            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph titulo = new Paragraph("Top " + limite + " Productos Más Vendidos", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);
            
            // Fecha del reporte
            Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
            Paragraph periodoReporte = new Paragraph("Período: " + fechaInicio + " a " + fechaFin, fuenteSubtitulo);
            periodoReporte.setAlignment(Element.ALIGN_CENTER);
            
            Paragraph fechaGeneracion = new Paragraph("Generado el: " + 
                                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), fuenteSubtitulo);
            fechaGeneracion.setAlignment(Element.ALIGN_CENTER);
            fechaGeneracion.setSpacingAfter(20);
            
            documento.add(periodoReporte);
            documento.add(fechaGeneracion);
            
            // Tabla de productos más vendidos
            PdfPTable tabla = new PdfPTable(5);
            tabla.setWidthPercentage(100);
            
            // Encabezados
            String[] encabezados = {"ID", "Producto", "Precio Unitario", "Cantidad Vendida", "Total Ventas"};
            Font fuenteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
            
            for (String encabezado : encabezados) {
                PdfPCell celda = new PdfPCell(new Phrase(encabezado, fuenteEncabezado));
                celda.setBackgroundColor(BaseColor.DARK_GRAY);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                celda.setPadding(8);
                tabla.addCell(celda);
            }
            
            // Datos de productos más vendidos
            Font fuenteNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);
            double totalGeneral = 0;
            
            for (int i = 0; i < modeloDatos.getRowCount(); i++) {
                tabla.addCell(new Phrase(modeloDatos.getValueAt(i, 0).toString(), fuenteNormal)); // ID
                tabla.addCell(new Phrase(modeloDatos.getValueAt(i, 1).toString(), fuenteNormal)); // Producto
                tabla.addCell(new Phrase(modeloDatos.getValueAt(i, 2).toString(), fuenteNormal)); // Precio Unitario
                tabla.addCell(new Phrase(modeloDatos.getValueAt(i, 3).toString(), fuenteNormal)); // Cantidad Vendida
                
                // Para el total, extraemos el valor numérico del formato de moneda
                String totalStr = modeloDatos.getValueAt(i, 4).toString().replace("$", "")
                                                                     .replace(",", "");
                try {
                    double total = Double.parseDouble(totalStr);
                    totalGeneral += total;
                } catch (Exception e) {
                    // Si no podemos parsear, usamos el valor tal cual
                }
                
                tabla.addCell(new Phrase(modeloDatos.getValueAt(i, 4).toString(), fuenteNormal)); // Total Ventas
            }
            
            documento.add(tabla);
            
            // Añadir total general
            Paragraph totalGeneralParagraph = new Paragraph("Total General: " + formatoMoneda.format(totalGeneral), 
                                            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
            totalGeneralParagraph.setAlignment(Element.ALIGN_RIGHT);
            totalGeneralParagraph.setSpacingBefore(10);
            documento.add(totalGeneralParagraph);
            
            documento.close();
            return nombreArchivo;
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                    "Error al generar reporte de productos más vendidos: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Gestiona los eventos de los componentes de la vista
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaReportes.getBtnGenerar()) {
            generarReporte();
        } else if (e.getSource() == vistaReportes.getBtnCancelar()) {
            vistaReportes.dispose();
        }
    }
    
    /**
     * Genera el reporte correspondiente según la selección del usuario
     */
    private void generarReporte() {
        String tipoReporte = (String) vistaReportes.getComboTipoReporte().getSelectedItem();
        String rutaReporte = null;
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        try {
            switch (tipoReporte) {
                case "Listado de Productos":
                    boolean incluyeStockCero = vistaReportes.getRbTodosProductos().isSelected();
                    rutaReporte = generarReporteProductos(incluyeStockCero);
                    break;
                    
                case "Reporte de Ventas":
                    // Obtener fechas seleccionadas
                    String fechaInicio = sdf.format(vistaReportes.getFechaInicioVentas().getDate());
                    String fechaFin = sdf.format(vistaReportes.getFechaFinVentas().getDate());
                    
                    // Verificar qué opción de filtro está seleccionada
                    if (vistaReportes.getRbSoloNoCanceladas().isSelected()) {
                        rutaReporte = generarReporteVentasNoCanceladas(fechaInicio, fechaFin);
                    } else {
                        boolean incluirCanceladas = true; // Si no es "SoloNoCanceladas", incluimos las canceladas
                        rutaReporte = generarReporteVentas(fechaInicio, fechaFin, incluirCanceladas);
                    }
                    break;
                    
                case "Reporte de Caja":
                    String fecha = sdf.format(vistaReportes.getFechaCaja().getDate());
                    rutaReporte = generarReporteCaja(fecha);
                    break;
                    
                case "Productos Mas Vendidos":
                	
                						// Obtener fechas seleccionadas
					String fechaInicioProductos = sdf.format(vistaReportes.getFechaInicioProductosMasVendidos().getDate());
					String fechaFinProductos = sdf.format(vistaReportes.getFechaFinProductosMasVendidos().getDate());
					
					// Obtener el límite de productos a mostrar
					int limite = Integer.parseInt(vistaReportes.getTxtLimiteProductos().getText());
					
					rutaReporte = generarReporteProductosMasVendidos(fechaInicioProductos, fechaFinProductos, limite);
					break;
            }
            
            // Abrir el reporte generado si no es nulo
            if (rutaReporte != null) {
                abrirReporte(rutaReporte);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vistaReportes, 
                    "Error al generar el reporte: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    /**
         * Genera el reporte según las opciones seleccionadas en la vista
         * @param vista La vista de reportes con las opciones seleccionadas
         * @return La ruta del archivo generado o null si hubo error
         */
        public String generarReporteDesdeVista(VistaReportes vista) {
            try {
                if (vista.getComboTipoReporte().getSelectedItem().equals("Reporte de Ventas")) {
                    // Convertir fechas del DateChooser a String en formato yyyy-MM-dd
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String fechaInicio = sdf.format(vista.getFechaInicioVentas().getDate());
                    String fechaFin = sdf.format(vista.getFechaFinVentas().getDate());
                    
                    // Determinar si se incluyen ventas canceladas según la opción seleccionada
                    boolean incluirCanceladas = vista.getRbTodasVentas().isSelected();
                    
                    // Generar el reporte con la opción correspondiente
                    return generarReporteVentas(fechaInicio, fechaFin, incluirCanceladas);
                }
                // Añadir manejo para otros tipos de reportes aquí
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                        "Error al generar el reporte: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        
        /**
     * Carga las ventas no canceladas para el reporte en el rango de fechas especificado
     */
    public DefaultTableModel cargarVentasNoCanceladas(Date fechaInicio, Date fechaFin) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaInicioStr = sdf.format(fechaInicio);
            String fechaFinStr = sdf.format(fechaFin);
            
            BaseDatos bd = new BaseDatos();
            
            // Consulta con condición para excluir ventas canceladas
            ArrayList<String[]> ventas = bd.consultar("Ventas", 
                    "idVenta, fecha, total, idTipoVenta", 
                    "fecha BETWEEN '" + fechaInicioStr + "' AND '" + fechaFinStr + "' AND " +
                    "(cancelada IS NULL OR cancelada != 1) ORDER BY fecha");
            
            // Crear modelo para la tabla
            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[] {"ID Venta", "Fecha", "Total", "Tipo de Venta"}, 0);
            
            if (ventas != null && !ventas.isEmpty()) {
                for (String[] venta : ventas) {
                    modelo.addRow(new Object[] {
                            venta[0], // idVenta
                            venta[1], // fecha
                            venta[2], // total
                            venta[3]  // idTipoVenta
                    });
                }
            }
            
            bd.cerrarConexion();
            return modelo;
            
        } catch (Exception e) {
            e.printStackTrace();
            return new DefaultTableModel();
        }
    }
    
    /**
     * Genera un reporte de ventas no canceladas en el rango de fechas especificado
     * @param fechaInicio fecha de inicio en formato yyyy-MM-dd
     * @param fechaFin fecha de fin en formato yyyy-MM-dd
     * @return ruta del reporte generado o null si no hay datos
     */
    public String generarReporteVentasNoCanceladas(String fechaInicio, String fechaFin) {
        try {
            // Convertir las fechas de String a Date para usar el método cargarVentasNoCanceladas
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaInicioDate = sdf.parse(fechaInicio);
            Date fechaFinDate = sdf.parse(fechaFin);
            
            // Obtener los datos de ventas no canceladas
            DefaultTableModel modeloDatos = cargarVentasNoCanceladas(fechaInicioDate, fechaFinDate);
            
            if (modeloDatos.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, 
                        "No hay ventas no canceladas en el período seleccionado", 
                        "Información", JOptionPane.INFORMATION_MESSAGE);
                return null;
            }
            
            // Crear directorio si no existe
            File dir = new File("reportes");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // Nombre del archivo
            String nombreArchivo = "reportes/ventas_no_canceladas_" + fechaInicio.replaceAll("-", "") + 
                                  "_a_" + fechaFin.replaceAll("-", "") + ".pdf";
            
            // Crear documento PDF
            Document documento = new Document();
            PdfWriter.getInstance(documento, new FileOutputStream(nombreArchivo));
            documento.open();
            
            // Título del reporte
            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph titulo = new Paragraph("Reporte de Ventas No Canceladas", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);
            
            // Fecha del reporte
            Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
            Paragraph periodoReporte = new Paragraph("Período: " + fechaInicio + " a " + fechaFin, fuenteSubtitulo);
            periodoReporte.setAlignment(Element.ALIGN_CENTER);
            
            Paragraph fechaGeneracion = new Paragraph("Generado el: " + 
                                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), fuenteSubtitulo);
            fechaGeneracion.setAlignment(Element.ALIGN_CENTER);
            fechaGeneracion.setSpacingAfter(20);
            
            documento.add(periodoReporte);
            documento.add(fechaGeneracion);
            
            // Tabla de resumen de ventas
            PdfPTable tablaResumen = new PdfPTable(4);
            tablaResumen.setWidthPercentage(100);
            
            // Encabezados de resumen
            String[] encabezadosResumen = {"ID Venta", "Fecha", "Total", "Tipo de Venta"};
            Font fuenteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
            
            for (String encabezado : encabezadosResumen) {
                PdfPCell celda = new PdfPCell(new Phrase(encabezado, fuenteEncabezado));
                celda.setBackgroundColor(BaseColor.DARK_GRAY);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                celda.setPadding(8);
                tablaResumen.addCell(celda);
            }
            
            // Datos de resumen de ventas
            Font fuenteNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);
            double totalVentas = 0;
            
            for (int i = 0; i < modeloDatos.getRowCount(); i++) {
                String idVenta = modeloDatos.getValueAt(i, 0).toString();
                String fecha = modeloDatos.getValueAt(i, 1).toString();
                String totalStr = modeloDatos.getValueAt(i, 2).toString();
                String idTipoVenta = modeloDatos.getValueAt(i, 3).toString();
                
                tablaResumen.addCell(new Phrase(idVenta, fuenteNormal)); // ID Venta
                tablaResumen.addCell(new Phrase(fecha, fuenteNormal)); // Fecha
                
                // Formatear total con símbolo de moneda
                try {
                    double total = Double.parseDouble(totalStr);
                    totalVentas += total;
                    tablaResumen.addCell(new Phrase(formatoMoneda.format(total), fuenteNormal));
                } catch (Exception e) {
                    tablaResumen.addCell(new Phrase(totalStr, fuenteNormal));
                }
                
                // Tipo de venta (mostrar descripción en lugar de ID)
                String tipoVenta = "Desconocido";
                switch (idTipoVenta) {
                    case "1": tipoVenta = "Efectivo"; break;
                    case "2": tipoVenta = "Tarjeta"; break;
                    case "3": tipoVenta = "Transferencia"; break;
                }
                tablaResumen.addCell(new Phrase(tipoVenta, fuenteNormal));
            }
            
            documento.add(tablaResumen);
            
            // Añadir total general
            Paragraph totalGeneral = new Paragraph("Total General: " + formatoMoneda.format(totalVentas), 
                                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
            totalGeneral.setAlignment(Element.ALIGN_RIGHT);
            totalGeneral.setSpacingBefore(10);
            documento.add(totalGeneral);
            
            // También obtenemos los detalles de ventas para un reporte más completo
            BaseDatos bd = new BaseDatos();
            ArrayList<String[]> detallesVentas = bd.consultar(
            		"Ventas v " +
            			    "INNER JOIN DetalleVentas dv ON dv.idVenta = v.idVenta " +
            			    "INNER JOIN Productos p ON p.idProducto = dv.idProducto",

            			    "v.idVenta, v.fecha, p.producto, dv.cantidad, dv.precioUnitario, " +
            			    "(CAST(dv.cantidad AS FLOAT) * CAST(dv.precioUnitario AS FLOAT) * 1.16) AS subtotal",

            			    "v.fecha BETWEEN '" + fechaInicio + "' AND '" + fechaFin + "' AND " +
            			    "(v.cancelada IS NULL OR v.cancelada != 1) " +
            			    "ORDER BY v.fecha, v.idVenta"
            			);
            bd.cerrarConexion();
            
            // Si tenemos detalles, mostramos tabla de detalles
            if (detallesVentas != null && !detallesVentas.isEmpty()) {
                Paragraph tituloDetalles = new Paragraph("Detalle de Productos Vendidos", 
                                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
                tituloDetalles.setSpacingBefore(20);
                tituloDetalles.setSpacingAfter(10);
                documento.add(tituloDetalles);
                
                PdfPTable tablaDetalles = new PdfPTable(5);
                tablaDetalles.setWidthPercentage(100);
                
                // Encabezados de detalles
                String[] encabezadosDetalles = {"ID Venta", "Fecha", "Producto", "Cantidad", "Subtotal"};
                
                for (String encabezado : encabezadosDetalles) {
                    PdfPCell celda = new PdfPCell(new Phrase(encabezado, fuenteEncabezado));
                    celda.setBackgroundColor(BaseColor.DARK_GRAY);
                    celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                    celda.setPadding(8);
                    tablaDetalles.addCell(celda);
                }
                
                // Datos de detalles
                for (String[] detalle : detallesVentas) {
                    tablaDetalles.addCell(new Phrase(detalle[0], fuenteNormal)); // ID Venta
                    tablaDetalles.addCell(new Phrase(detalle[1], fuenteNormal)); // Fecha
                    tablaDetalles.addCell(new Phrase(detalle[2], fuenteNormal)); // Producto
                    tablaDetalles.addCell(new Phrase(detalle[3], fuenteNormal)); // Cantidad
                    
                    // Formatear subtotal
                    try {
                        double subtotal = Double.parseDouble(detalle[5]);
                        tablaDetalles.addCell(new Phrase(formatoMoneda.format(subtotal), fuenteNormal));
                    } catch (Exception e) {
                        tablaDetalles.addCell(new Phrase(detalle[5], fuenteNormal));
                    }
                }
                
                documento.add(tablaDetalles);
            }
            
            documento.close();
            return nombreArchivo;
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                    "Error al generar reporte de ventas no canceladas: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}