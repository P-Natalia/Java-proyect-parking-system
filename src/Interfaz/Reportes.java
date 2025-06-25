package Interfaz;
/**
 *
 * @author Natalia Peña 
 */

import Dominio.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * Ventana para generar reportes del sistema de parking.
 * Contiene tres pestañas con diferentes tipos de reportes:
 * 1. Historial por vehículo
 * 2. Grilla de movimientos
 * 3. Estadísticas generales
 */
public class Reportes extends JFrame {
    private final Sistema sistema;
    private JTabbedPane tabbedPane;
    
    /**
     * Constructor de la ventana de reportes.
     * @param sistema Referencia al sistema principal para acceder a los datos
     */
    public Reportes(Sistema sistema) {
        this.sistema = sistema;
        initComponents();
        ConfiguracionTema.getInstancia().registrarVentana(this);
        ConfiguracionTema.getInstancia().aplicarTema(this);
    }

    /**
     * Inicializa los componentes de la interfaz gráfica.
     */
    private void initComponents() {
        setTitle("Reportes del Parking");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Crear el panel de pestañas
        tabbedPane = new JTabbedPane();
        
        // Crear los paneles para cada tipo de reporte
        HistorialPanel historialPanel = new HistorialPanel();
        GrillaPanel grillaPanel = new GrillaPanel();
        EstadisticasPanel estadisticasPanel = new EstadisticasPanel();
        
        // Añadir los paneles al tabbedPane
        tabbedPane.addTab("Historial por Vehículo", historialPanel);
        tabbedPane.addTab("Grilla de Movimientos", grillaPanel);
        tabbedPane.addTab("Estadísticas Generales", estadisticasPanel);
        
        // Configurar el tabbedPane para mantener las pestañas visibles
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        add(tabbedPane, BorderLayout.CENTER);
        
        // Aplicar tema visual
        ConfiguracionTema.getInstancia().aplicarTema(tabbedPane);
    }

    /**
     * Panel para mostrar el historial de movimientos por vehículo.
     */
    private class HistorialPanel extends JPanel {
        private JComboBox<Vehiculo> cbVehiculos;
        private JTable tabla;
        private DefaultTableModel modeloTabla;
        private JCheckBox chkEntradas, chkSalidas, chkServicios;
        private JRadioButton rbAsc, rbDesc;
        private JButton btnExportar;

        public HistorialPanel() {
            setOpaque(true);
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Panel superior: Selección y filtros
            JPanel panelSuperior = new JPanel();
            panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
            panelSuperior.setBorder(BorderFactory.createTitledBorder("Filtros"));
            
            // Selección de vehículo
            JPanel panelVehiculo = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panelVehiculo.add(new JLabel("Vehículo:"));
            cbVehiculos = new JComboBox<>();
            for (Vehiculo vehiculo : sistema.getVehiculos()) {
                cbVehiculos.addItem(vehiculo);
            }
            panelVehiculo.add(cbVehiculos);
            panelSuperior.add(panelVehiculo);
            
            // Filtros de tipo de movimiento
            JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
            chkEntradas = new JCheckBox("Entradas", true);
            chkSalidas = new JCheckBox("Salidas", true);
            chkServicios = new JCheckBox("Servicios", true);
            panelFiltros.add(chkEntradas);
            panelFiltros.add(chkSalidas);
            panelFiltros.add(chkServicios);
            panelSuperior.add(panelFiltros);
            
            // Ordenamiento
            JPanel panelOrden = new JPanel(new FlowLayout(FlowLayout.LEFT));
            rbAsc = new JRadioButton("Ascendente", true);
            rbDesc = new JRadioButton("Descendente");
            ButtonGroup grupoOrden = new ButtonGroup();
            grupoOrden.add(rbAsc);
            grupoOrden.add(rbDesc);
            panelOrden.add(new JLabel("Orden:"));
            panelOrden.add(rbAsc);
            panelOrden.add(rbDesc);
            panelSuperior.add(panelOrden);
            
            // Botón exportar
            btnExportar = new JButton("Exportar a TXT");
            panelSuperior.add(btnExportar);
            add(panelSuperior, BorderLayout.NORTH);
            
            // Configurar tabla de movimientos
            modeloTabla = new DefaultTableModel(new Object[]{"Fecha/Hora", "Tipo", "Detalles", "Notas"}, 0) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    return columnIndex == 0 ? LocalDateTime.class : String.class;
                }
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            tabla = new JTable(modeloTabla);
            tabla.setAutoCreateRowSorter(true);
            tabla.getColumnModel().getColumn(0).setPreferredWidth(150);
            tabla.getColumnModel().getColumn(1).setPreferredWidth(80);
            tabla.getColumnModel().getColumn(2).setPreferredWidth(200);
            tabla.getColumnModel().getColumn(3).setPreferredWidth(200);
            
            add(new JScrollPane(tabla), BorderLayout.CENTER);
            
            // Configurar listeners para actualizar la tabla cuando cambian los filtros
            ActionListener actualizarListener = e -> actualizarTabla();
            cbVehiculos.addActionListener(actualizarListener);
            chkEntradas.addActionListener(actualizarListener);
            chkSalidas.addActionListener(actualizarListener);
            chkServicios.addActionListener(actualizarListener);
            rbAsc.addActionListener(actualizarListener);
            rbDesc.addActionListener(actualizarListener);
            btnExportar.addActionListener(e -> exportarTXT());
            
            // Cargar datos iniciales
            actualizarTabla();
        }

        /**
         * Actualiza la tabla con los movimientos del vehículo seleccionado,
         * aplicando los filtros y orden seleccionados.
         */
        private void actualizarTabla() {
            modeloTabla.setRowCount(0);
            Vehiculo vehiculo = (Vehiculo) cbVehiculos.getSelectedItem();
            if (vehiculo == null) return;
            
            // Obtener movimientos del sistema
            List<Object[]> movimientos = sistema.getHistorialMovimientos(vehiculo.getMatricula());
            
            // Aplicar filtros según selección del usuario
            movimientos.removeIf(mov -> {
                String tipo = (String) mov[1];
                return (!chkEntradas.isSelected() && tipo.equals("Entrada")) ||
                       (!chkSalidas.isSelected() && tipo.equals("Salida")) ||
                       (!chkServicios.isSelected() && tipo.startsWith("Servicio"));
            });
            
            // Ordenar movimientos
            movimientos.sort((mov1, mov2) -> {
                LocalDateTime dt1 = (LocalDateTime) mov1[0];
                LocalDateTime dt2 = (LocalDateTime) mov2[0];
                return rbAsc.isSelected() ? dt1.compareTo(dt2) : dt2.compareTo(dt1);
            });
            
            // Llenar tabla con los movimientos filtrados y ordenados
            for (Object[] mov : movimientos) {
                modeloTabla.addRow(mov);
            }
        }

        /**
         * Exporta el historial del vehículo seleccionado a un archivo de texto.
         */
        private void exportarTXT() {
            Vehiculo vehiculo = (Vehiculo) cbVehiculos.getSelectedItem();
            if (vehiculo == null) return;
            
            String filename = vehiculo.getMatricula().replace(" ", "_") + ".txt";
            
            try (PrintWriter pw = new PrintWriter(filename)) {
                // Escribir encabezado con información del vehículo
                pw.println("Historial de Vehículo: " + vehiculo.getMatricula());
                pw.println("Marca: " + vehiculo.getMarca());
                pw.println("Modelo: " + vehiculo.getModelo());
                pw.println("Estado: " + vehiculo.getEstado());
                pw.println("Contrato activo: " + (vehiculo.getTieneContrato() ? "Sí" : "No"));
                
                if (vehiculo.getCliente() != null) {
                    pw.println("Cliente: " + vehiculo.getCliente().getNombre());
                    pw.println("Cédula: " + vehiculo.getCliente().getCedula());
                }
                
                pw.println("----------------------------------------");
                pw.println();
                
                // Escribir encabezados de columnas
                pw.printf("%-20s | %-10s | %-50s | %s%n", 
                         "Fecha/Hora", "Tipo", "Detalles", "Notas");
                pw.println("------------------------------------------------------------------------------------------------");
                
                // Escribir cada movimiento
                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    LocalDateTime fechaHora = (LocalDateTime) modeloTabla.getValueAt(i, 0);
                    String tipo = (String) modeloTabla.getValueAt(i, 1);
                    String detalles = (String) modeloTabla.getValueAt(i, 2);
                    String notas = (String) modeloTabla.getValueAt(i, 3);
                    
                    pw.printf("%-20s | %-10s | %-50s | %s%n",
                        Reporte.formatDateTime(fechaHora),
                        tipo,
                        detalles,
                        notas);
                }
                
                JOptionPane.showMessageDialog(this, "Archivo exportado: " + filename);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage());
            }
        }
    }
        
    /**
     * Panel para mostrar la grilla de movimientos por intervalos horarios.
     */
    private class GrillaPanel extends JPanel {
        private JSpinner spinnerFecha;
        private JPanel panelGrilla;
        private JLabel lblFechaSeleccionada;

        public GrillaPanel() {
            setOpaque(true);
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Panel superior: Selección de fecha
            JPanel panelFecha = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            panelFecha.setBorder(BorderFactory.createTitledBorder("Selección de fecha base"));
            
            panelFecha.add(new JLabel("Fecha:"));
            spinnerFecha = new JSpinner(new SpinnerDateModel());
            spinnerFecha.setEditor(new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy"));
            spinnerFecha.setValue(new Date());
            panelFecha.add(spinnerFecha);
            
            JButton btnGenerar = new JButton("Generar Grilla");
            panelFecha.add(btnGenerar);
            
            lblFechaSeleccionada = new JLabel();
            panelFecha.add(lblFechaSeleccionada);
            
            add(panelFecha, BorderLayout.NORTH);
            
            // Panel de grilla con 4 filas (intervalos) y 3 columnas (días)
            panelGrilla = new JPanel(new GridLayout(4, 3, 5, 5));
            panelGrilla.setBorder(BorderFactory.createTitledBorder("Movimientos por intervalo horario"));
            
            JScrollPane scrollPane = new JScrollPane(panelGrilla);
            scrollPane.setBorder(null);
            add(scrollPane, BorderLayout.CENTER);
            
            btnGenerar.addActionListener(e -> generarGrilla());
            generarGrilla();
        }

        /**
         * Genera la grilla de movimientos basada en la fecha seleccionada.
         */
        private void generarGrilla() {
            panelGrilla.removeAll();
            
            // Obtener fecha seleccionada
            Date fecha = (Date) spinnerFecha.getValue();
            LocalDate fechaBase = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            lblFechaSeleccionada.setText("Fecha base: " + fechaBase.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            
            // Nombres de los días (hoy, mañana, pasado)
            String[] nombresDias = {"Día solicitado", "Día siguiente", "Dos días después"};
            
            // Crear botones para cada intervalo y día
            for (int intervalo = 0; intervalo < 4; intervalo++) {
                for (int dia = 0; dia < 3; dia++) {
                    LocalDate fechaDia = fechaBase.plusDays(dia);
                    String textoIntervalo = getTextoIntervalo(intervalo);
                    String textoDia = nombresDias[dia] + " (" + fechaDia.format(DateTimeFormatter.ofPattern("dd/MM")) + ")";
                    
                    JButton btn = new JButton("<html><center>" + textoIntervalo + "<br>" + textoDia + "</center></html>");
                    btn.setMargin(new Insets(5, 5, 5, 5));
                    btn.setVerticalTextPosition(SwingConstants.CENTER);
                    btn.setHorizontalTextPosition(SwingConstants.CENTER);
                    
                    // Contar movimientos y colorear según la cantidad
                    int count = sistema.contarMovimientos(fechaDia, intervalo);
                    
                    if (count < 5) {
                        btn.setBackground(Color.GREEN);
                    } else if (count <= 8) {
                        btn.setBackground(Color.YELLOW);
                    } else {
                        btn.setBackground(Color.RED);
                    }
                    
                    btn.setToolTipText(count + " movimientos en este intervalo");
                    btn.addActionListener(new MovimientoListener(fechaDia, intervalo));
                    panelGrilla.add(btn);
                }
            }
            
            panelGrilla.revalidate();
            panelGrilla.repaint();
        }

        /**
         * Devuelve el texto descriptivo para un intervalo horario.
         * @param intervalo Índice del intervalo (0-3)
         * @return Cadena con el rango horario
         */
        private String getTextoIntervalo(int intervalo) {
            switch (intervalo) {
                case 0: return "00:00 - 05:59";
                case 1: return "06:00 - 11:59";
                case 2: return "12:00 - 17:59";
                case 3: return "18:00 - 23:59";
                default: return "";
            }
        }

        /**
         * Listener para mostrar los movimientos de un intervalo específico.
         */
        private class MovimientoListener implements ActionListener {
            private final LocalDate fecha;
            private final int intervalo;

            public MovimientoListener(LocalDate fecha, int intervalo) {
                this.fecha = fecha;
                this.intervalo = intervalo;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtener movimientos del sistema para este intervalo
                List<Object[]> movimientos = sistema.getMovimientosIntervalo(fecha, intervalo);
                
                // Crear ventana de diálogo para mostrar los movimientos
                JDialog dialog = new JDialog();
                dialog.setTitle("Movimientos: " + fecha + " - " + getTextoIntervalo(intervalo));
                dialog.setSize(600, 400);
                dialog.setLayout(new BorderLayout());
                dialog.setLocationRelativeTo(Reportes.this);
                
                // Configurar tabla de movimientos
                String[] columnas = {"Fecha/Hora", "Matrícula", "Tipo", "Empleado"};
                DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
                
                for (Object[] mov : movimientos) {
                    modelo.addRow(new Object[]{
                        Reporte.formatDateTime((LocalDateTime) mov[0]),
                        mov[1],
                        mov[2],
                        mov[3]
                    });
                }
                
                JTable tabla = new JTable(modelo);
                tabla.setAutoCreateRowSorter(true);
                
                JScrollPane scrollPane = new JScrollPane(tabla);
                scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                dialog.add(scrollPane, BorderLayout.CENTER);
                
                // Botón para cerrar
                JPanel panelPie = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton btnCerrar = new JButton("Cerrar");
                btnCerrar.addActionListener(ev -> dialog.dispose());
                panelPie.add(btnCerrar);
                dialog.add(panelPie, BorderLayout.SOUTH);
                
                dialog.setVisible(true);
            }
        }
    }

    /**
     * Panel para mostrar estadísticas generales del sistema.
     */
    private class EstadisticasPanel extends JPanel {
        public EstadisticasPanel() {
            setOpaque(true);
            setLayout(new GridLayout(6, 1, 15, 15));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Agregar cada estadística como un panel separado
            add(crearPanelEstadistica(
                "Servicio más utilizado", 
                sistema.getServicioMasUtilizado()
            ));
            add(crearPanelEstadistica(
                "Estadía más larga", 
                sistema.getEstadiaMasLarga()
            ));
            add(crearPanelEstadistica(
                "Empleado con menos movimientos", 
                sistema.getEmpleadoMenosMovimientos()
            ));
            add(crearPanelEstadistica(
                "Empleado con más movimientos", 
                sistema.getEmpleadoMasMovimientos()
            ));
            add(crearPanelEstadistica(
                "Cliente con más vehículos", 
                sistema.getClienteMasVehiculos()
            ));
            add(crearPanelEstadistica(
                "Estadísticas de contratos", 
                sistema.getEstadisticasContratos()
            ));
        }

        /**
         * Crea un panel para mostrar una estadística individual.
         * @param titulo Título de la estadística
         * @param valor Valor de la estadística
         * @return Panel configurado para mostrar la estadística
         */
        private JPanel crearPanelEstadistica(String titulo, String valor) {
            JPanel panel = new JPanel(new BorderLayout());
            
            // Configurar borde con título
            TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
                titulo
            );
            border.setTitleFont(new Font("SansSerif", Font.BOLD, 14));
            panel.setBorder(border);
            
            // Configurar etiqueta con el valor
            JLabel lblValor = new JLabel(valor);
            lblValor.setFont(new Font("SansSerif", Font.PLAIN, 14));
            lblValor.setHorizontalAlignment(SwingConstants.CENTER);
            lblValor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            panel.add(lblValor, BorderLayout.CENTER);
            return panel;
        }
    }
}

/*
Explicación General de la Clase Reportes
La clase Reportes es una ventana de interfaz gráfica que proporciona tres tipos diferentes de reportes sobre el funcionamiento del parking:

Pestañas principales:
Historial por Vehículo:
Muestra todos los movimientos (entradas, salidas, servicios) de un vehículo seleccionado
Permite filtrar por tipo de movimiento
Opciones de ordenamiento (ascendente/descendente por fecha)
Exportación a archivo de texto

Grilla de Movimientos:
Visualización de movimientos por intervalos horarios (4 intervalos por día)
Muestra 3 días consecutivos (día seleccionado + 2 días siguientes)
Coloración según cantidad de movimientos (verde <5, amarillo 5-8, rojo >8)
Detalle de movimientos al hacer clic en cada intervalo

Estadísticas Generales:
Servicio más utilizado
Estadía más larga registrada
Empleados con más/menos movimientos
Cliente con más vehículos
Estadísticas de contratos (activos/inactivos, valor promedio)

*/
