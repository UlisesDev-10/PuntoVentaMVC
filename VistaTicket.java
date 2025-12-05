
package Vista;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

// Para el manejo de PDFs, se recomienda añadir la librería iText como dependencia
import com.itextpdf.text.Document;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class VistaTicket extends JDialog {

    private static final long serialVersionUID = 1L;
    private final JPanel contentPanel = new JPanel();
    private JTextArea areaTicket;
    private JButton bImprimir, bCerrar;
    private DecimalFormat formatoMoneda = new DecimalFormat("#,##0.00");
    private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private List<Object[]> productos;
    private double subtotal, iva, total, pagoCon, cambio;

    /**
     * Constructor para la ventana de ticket (versión original)
     */
    public VistaTicket(java.awt.Frame parent, List<Object[]> productos, double total, double pagoCon, double cambio) {
        super(parent, "Ticket de Venta", true);
        setBounds(100, 100, 400, 500);
        setLocationRelativeTo(parent);
        
        // Guardar los datos para uso posterior en la exportación a PDF
        this.productos = productos;
        this.total = total;
        this.pagoCon = pagoCon;
        this.cambio = cambio;
        
        // Calcular subtotal e IVA a partir del total para mantener compatibilidad
        this.subtotal = total / 1.16; // Calcular subtotal a partir del total
        this.subtotal = Math.round(this.subtotal * 100.0) / 100.0; // Redondear a 2 decimales
        this.iva = total - this.subtotal; // Calcular IVA como la diferencia
        this.iva = Math.round(this.iva * 100.0) / 100.0; // Redondear a 2 decimales
        
        initComponents();
    }
    
    /**
     * Constructor para la ventana de ticket que acepta subtotal e IVA separados
     */
    public VistaTicket(java.awt.Frame parent, List<Object[]> productos, double subtotal, double iva, double total, double pagoCon, double cambio) {
        super(parent, "Ticket de Venta", true);
        setBounds(100, 100, 400, 500);
        setLocationRelativeTo(parent);
        
        // Guardar los datos para uso posterior en la exportación a PDF
        this.productos = productos;
        this.subtotal = subtotal;
        this.iva = iva;
        this.total = total;
        this.pagoCon = pagoCon;
        this.cambio = cambio;
        
        initComponents();
    }
    
    /**
     * Inicializa los componentes de la interfaz gráfica
     */
    private void initComponents() {
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));
        
        areaTicket = new JTextArea();
        areaTicket.setEditable(false);
        areaTicket.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        // Generar ticket con los valores guardados
        generarTicket();
        
        JScrollPane scrollPane = new JScrollPane(areaTicket);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        
        bImprimir = new JButton("Imprimir");
        bImprimir.setFont(new Font("Tahoma", Font.PLAIN, 12));
        buttonPane.add(bImprimir);
        
        bCerrar = new JButton("Cerrar");
        bCerrar.setFont(new Font("Tahoma", Font.PLAIN, 12));
        buttonPane.add(bCerrar);
        getRootPane().setDefaultButton(bCerrar);
    }
    
    /**
     * Genera el contenido del ticket usando los valores almacenados en la clase
     */
    private void generarTicket() {
        generarTicket(this.productos, this.subtotal, this.iva, this.total, this.pagoCon, this.cambio);
    }
    
    /**
     * Genera el contenido del ticket
     */
    private void generarTicket(List<Object[]> productos, double subtotal, double iva, double total, double pagoCon, double cambio) {
        StringBuilder sb = new StringBuilder();
        
        // Encabezado
        sb.append("            Abarrotes Doña Lupita S.A.            \n");
        sb.append("=========================================\n");
        sb.append("Fecha: ").append(formatoFecha.format(new Date())).append("\n");
        sb.append("=========================================\n\n");
        
        // Productos
        sb.append("PRODUCTO                  CANT   PRECIO   SUBTOTAL\n");
        sb.append("-------------------------------------------------\n");
        
        // Variable para calcular el total real sumando los subtotales de productos
        double totalCalculado = 0;
        
        for (Object[] producto : productos) {
            try {
                String nombre = producto[0].toString();
                // Truncar si es muy largo
                if (nombre.length() > 20) {
                    nombre = nombre.substring(0, 17) + "...";
                }
                
                // Imprimimos la estructura del producto para depuración
                System.out.println("=== INFO PRODUCTO ===");
                System.out.println("Longitud del array: " + producto.length);
                for (int i = 0; i < producto.length; i++) {
                    System.out.println("Índice " + i + ": " + (producto[i] != null ? producto[i].toString() : "null"));
                }
                
                // Manejar posibles valores no numéricos
                int cantidad = 0;
                double precioUnitario = 0;
                
                // Buscar el campo de cantidad (puede estar en diferentes posiciones dependiendo de la implementación)
                // Probar primero en la posición 3 (el índice 3 sería la 4ª columna en la tabla)
                int indexCantidad = 3;
                if (producto.length <= indexCantidad || producto[indexCantidad] == null) {
                    // Si no está en la posición esperada, intentar con la posición 1
                    indexCantidad = 1;
                }
                
                if (indexCantidad < producto.length && producto[indexCantidad] != null) {
                    String cantidadStr = producto[indexCantidad].toString().trim();
                    System.out.println("Intentando convertir cantidad desde índice " + indexCantidad + ": " + cantidadStr);
                    
                    try {
                        cantidad = Integer.parseInt(cantidadStr);
                    } catch (NumberFormatException e) {
                        System.err.println("Error al convertir cantidad: " + cantidadStr);
                        
                        // Es posible que tengamos el nombre del producto y no la cantidad
                        if (cantidadStr.matches(".*[a-zA-Z].*") && !cantidadStr.matches(".*\\d+.*")) {
                            System.out.println("El valor parece ser texto sin números, buscando la cantidad en otro campo");
                            
                            // Buscar en otros índices por un valor numérico
                            for (int i = 0; i < producto.length; i++) {
                                if (i != indexCantidad && producto[i] != null) {
                                    String posibleCantidad = producto[i].toString().trim();
                                    if (posibleCantidad.matches("\\d+")) {
                                        try {
                                            cantidad = Integer.parseInt(posibleCantidad);
                                            System.out.println("Cantidad encontrada en índice " + i + ": " + cantidad);
                                            break;
                                        } catch (NumberFormatException ex) {
                                            // Ignorar si no podemos convertir
                                        }
                                    }
                                }
                            }
                            
                            // Si aún no tenemos cantidad, usar 1 como valor predeterminado
                            if (cantidad == 0) {
                                cantidad = 1;
                                System.out.println("No se encontró cantidad numérica, usando predeterminado: 1");
                            }
                        } else if (cantidadStr.matches(".*\\d+.*")) {
                            // Extraer solo los dígitos si contiene números
                            cantidadStr = cantidadStr.replaceAll("[^0-9]", "");
                            try {
                                cantidad = Integer.parseInt(cantidadStr);
                                System.out.println("Cantidad extraída de texto: " + cantidad);
                            } catch (NumberFormatException ex) {
                                cantidad = 1; // Si todo falla, asumimos 1 como cantidad mínima
                                System.out.println("Usando cantidad predeterminada: 1");
                            }
                        } else {
                            cantidad = 1; // Si no hay dígitos, asumimos 1 como cantidad mínima
                            System.out.println("No se encontraron dígitos, usando cantidad predeterminada: 1");
                        }
                    }
                } else {
                    cantidad = 1;
                    System.out.println("Índice de cantidad fuera de rango o null, usando cantidad predeterminada: 1");
                }
                
                // Obtener precio unitario - probar en diferentes posiciones
                int indexPrecio = 2; // Primero intentamos en el índice 2
                
                if (producto.length <= indexPrecio || producto[indexPrecio] == null) {
                    // Si no está en la posición esperada, buscar un campo que parezca precio (con $ o formato numérico)
                    for (int i = 0; i < producto.length; i++) {
                        if (producto[i] != null) {
                            String posiblePrecio = producto[i].toString();
                            if (posiblePrecio.contains("$") || posiblePrecio.matches("\\d+(\\.\\d+)?")) {
                                indexPrecio = i;
                                break;
                            }
                        }
                    }
                }
                
                if (indexPrecio < producto.length && producto[indexPrecio] != null) {
                    try {
                        String precioStr = producto[indexPrecio].toString().replace("$", "").replace(",", "").trim();
                        System.out.println("Intentando convertir precio desde índice " + indexPrecio + ": " + precioStr);
                        precioUnitario = Double.parseDouble(precioStr);
                    } catch (NumberFormatException e) {
                        System.err.println("Error al convertir precio unitario desde índice " + indexPrecio + ": " + producto[indexPrecio]);
                        
                        // Intento buscar en otros índices
                        for (int i = 0; i < producto.length; i++) {
                            if (i != indexPrecio && producto[i] != null) {
                                try {
                                    String posiblePrecio = producto[i].toString().replace("$", "").replace(",", "").trim();
                                    // Verificar si parece un número
                                    if (posiblePrecio.matches("\\d+(\\.\\d+)?")) {
                                        precioUnitario = Double.parseDouble(posiblePrecio);
                                        System.out.println("Precio encontrado en índice " + i + ": " + precioUnitario);
                                        break;
                                    }
                                } catch (Exception ex) {
                                    // Ignorar errores e intentar con el siguiente
                                }
                            }
                        }
                        
                        if (precioUnitario <= 0) {
                            System.err.println("No se pudo determinar un precio válido, usando 0");
                        }
                    }
                } else {
                    System.err.println("Índice de precio fuera de rango o null");
                }
                
                // Calcular el precio con IVA incluido para cada producto
                double precioConIva = precioUnitario * 1.16; // Añadir 16% de IVA
                double subtotalConIva = cantidad * precioConIva;
                
                // Acumular al total calculado
                totalCalculado += subtotalConIva;
                
                // Formatear y añadir la línea al ticket
                sb.append(String.format("%-24s %3d   $%-7s $%s\n",
                        nombre,
                        cantidad,
                        formatoMoneda.format(precioUnitario),
                        formatoMoneda.format(subtotalConIva) // Subtotal con IVA de este producto
                ));
                
                // Imprimir información detallada para depuración
                System.out.println("Producto: " + nombre + 
                                  ", Cantidad: " + cantidad +
                                  ", Precio: " + precioUnitario + 
                                  ", Subtotal con IVA: " + subtotalConIva +
                                  ", Total acumulado: " + totalCalculado);
                
            } catch (Exception e) {
                // Manejar cualquier otra excepción inesperada
                System.err.println("Error al procesar producto: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // Validar si el total calculado es cero (posible error)
        if (totalCalculado <= 0) {
            System.out.println("ADVERTENCIA: Total calculado es cero o negativo. Verificando productos...");
            
            // Intentar recalcular con valores predeterminados si es necesario
            for (Object[] producto : productos) {
                try {
                    if (producto.length >= 3) {
                        String nombre = producto[0].toString();
                        // Por defecto asumimos cantidad 1 si hay error
                        int cantidadForzada = 1;
                        double precioForzado = 0;
                        
                        // Intentar obtener precio
                        if (producto[2] != null) {
                            try {
                                String precioStr = producto[2].toString().replace("$", "").replace(",", "").trim();
                                precioForzado = Double.parseDouble(precioStr);
                            } catch (Exception e) {
                                System.err.println("Error al obtener precio para " + nombre + ": " + e.getMessage());
                            }
                        }
                        
                        // Calcular con valores forzados
                        double subtotalForzado = cantidadForzada * precioForzado * 1.16;
                        totalCalculado += subtotalForzado;
                        
                        System.out.println("Recálculo forzado: " + nombre + 
                                           ", Cantidad: " + cantidadForzada + 
                                           ", Precio: " + precioForzado + 
                                           ", Subtotal: " + subtotalForzado);
                    }
                } catch (Exception e) {
                    System.err.println("Error en recálculo forzado: " + e.getMessage());
                }
            }
        }
        
        // Si después de todo, el total sigue siendo cero, usamos los valores proporcionados
       
        
        // Totales
        sb.append("-------------------------------------------------\n");
        
        // Mostrar los totales calculados, no los pasados como parámetro
       
        sb.append(String.format("%37s $%s\n", "TOTAL:", formatoMoneda.format(totalCalculado)));
        sb.append(String.format("%37s $%s\n", "PAGO CON:", formatoMoneda.format(pagoCon)));
        sb.append(String.format("%37s $%s\n", "CAMBIO:", formatoMoneda.format(cambio)));
        sb.append("-------------------------------------------------\n\n");
        
        // Pie de ticket
        sb.append("          ¡GRACIAS POR SU COMPRA!          \n");
        sb.append("          Vuelva pronto                     \n");
        
        areaTicket.setText(sb.toString());
    }
    
    /**
     * Genera e imprime el ticket como PDF
     */
    public void imprimirTicket() {
        try {
            // Crear la carpeta de reportes si no existe
            File carpetaReportes = new File("reportes");
            if (!carpetaReportes.exists()) {
                carpetaReportes.mkdir();
            }
            
            // Crear el nombre del archivo con timestamp para hacerlo único
            String nombreArchivo = "Ticket_" + System.currentTimeMillis() + ".pdf";
            File archivo = new File(carpetaReportes, nombreArchivo);
            
            // Crear documento PDF
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(archivo));
            document.open();
            
            // Configurar fuente monoespaciada para mantener el formato del ticket
            com.itextpdf.text.Font font = FontFactory.getFont(FontFactory.COURIER, 10);
            
            // Dividir el texto del ticket en líneas y añadir cada una como párrafo
            String[] lineas = areaTicket.getText().split("\n");
            for (String linea : lineas) {
                document.add(new Paragraph(linea, font));
            }
            
            document.close();
            
            // Abrir el PDF generado automáticamente
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivo);
                JOptionPane.showMessageDialog(this, 
                        "El ticket se ha generado y abierto correctamente.", 
                        "Ticket impreso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                        "El ticket se ha guardado en: " + archivo.getAbsolutePath(), 
                        "Ticket generado", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "Error al generar el PDF: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Establecer listeners para los botones
     */
    public void setImprimirListener(ActionListener listener) {
        // Limpiamos los listener anteriores para evitar duplicados
        ActionListener[] listeners = bImprimir.getActionListeners();
        for (ActionListener l : listeners) {
            bImprimir.removeActionListener(l);
        }
        
        // Por defecto, añadimos la funcionalidad de imprimir PDF
        bImprimir.addActionListener(e -> imprimirTicket());
        
        // Si se proporciona un listener adicional, también lo añadimos
        if (listener != null) {
            bImprimir.addActionListener(listener);
        }
    }
    
    public void setCerrarListener(ActionListener listener) {
        bCerrar.addActionListener(listener);
    }
}
