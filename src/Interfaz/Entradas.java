package Interfaz;
/**
 *
 * @author Natalia Peña 
 */
import java.util.List;
import Dominio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Ventana para registrar entradas de vehículos al parking.
 * Permite seleccionar un vehículo, empleado y registrar la entrada con fecha, hora y observaciones.
 */
public class Entradas extends JFrame {
    // Referencia al sistema principal
    private final Sistema sistema;
    
    // Componentes de la interfaz
    private JList<Vehiculo> lstVehiculos;          // Lista de vehículos disponibles
    private JComboBox<Empleado> cmbEmpleados;      // Combo de empleados disponibles
    private JTextField txtNotas;                    // Campo para notas/observaciones
    private JTextField txtFecha;                    // Campo para fecha de entrada
    private JTextField txtHora;                     // Campo para hora de entrada
    private JButton btnRegistrar;                   // Botón para registrar entrada
    private JScrollPane scrollVehiculos;            // Panel con scroll para la lista de vehículos

    /**
     * Constructor de la ventana de entradas.
     * @param sistema Referencia al sistema principal para acceder a los datos
     */
    public Entradas(Sistema sistema) {
        // Configuración inicial del tema
        ConfiguracionTema.getInstancia().registrarVentana(this);
        ConfiguracionTema.getInstancia().aplicarTema(this);
        
        this.sistema = sistema;
        initComponents();  // Inicializa los componentes de la interfaz
        cargarDatos();     // Carga los datos iniciales en la interfaz
    }
        
    /**
     * Carga los datos iniciales en los componentes de la interfaz:
     * - Lista de vehículos disponibles (sin entrada activa)
     * - Lista de empleados
     * - Fecha y hora actuales
     */
    private void cargarDatos() {
        // Cargar vehículos disponibles
        DefaultListModel<Vehiculo> modeloVehiculos = new DefaultListModel<>();
        java.util.List<Vehiculo> vehiculosDisponibles = sistema.vehiculosSinEntradaActual();
        
        if (vehiculosDisponibles.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No hay vehículos disponibles para registrar entrada",
                "Sin vehículos", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        // Añadir vehículos al modelo
        for (Vehiculo v : vehiculosDisponibles) {
            modeloVehiculos.addElement(v);
        }
        lstVehiculos.setModel(modeloVehiculos);
        
        // Cargar empleados
        cmbEmpleados.removeAllItems();
        List<Empleado> empleados = sistema.getEmpleados();
        
        if (empleados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No hay empleados registrados en el sistema",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            btnRegistrar.setEnabled(false);
            return;
        }
        
        for (Empleado emp : empleados) {
            cmbEmpleados.addItem(emp);
        }
        
        if (cmbEmpleados.getItemCount() > 0) {
            cmbEmpleados.setSelectedIndex(0);
        }
        
        // Establecer fecha y hora actuales
        txtFecha.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        txtHora.setText(new SimpleDateFormat("HH:mm").format(new Date()));
        
        // Habilitar botón solo si hay datos válidos
        btnRegistrar.setEnabled(!vehiculosDisponibles.isEmpty() && !empleados.isEmpty());
    }    
    
    /**
     * Registra una nueva entrada en el sistema con los datos proporcionados.
     * Valida los datos antes de realizar el registro.
     */
    private void registrarEntrada() {
        // Validar selección de vehículo
        Vehiculo vehiculo = lstVehiculos.getSelectedValue();
        if (vehiculo == null) {
            JOptionPane.showMessageDialog(this, 
                "Debe seleccionar un vehículo de la lista",
                "Datos incompletos",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validar selección de empleado
        Empleado empleado;
        try {
            Object itemSeleccionado = cmbEmpleados.getSelectedItem();
            if (itemSeleccionado == null) {
                throw new NullPointerException("No se ha seleccionado ningún empleado");
            }
            if (!(itemSeleccionado instanceof Empleado)) {
                throw new ClassCastException("El elemento seleccionado no es un Empleado válido");
            }
            empleado = (Empleado) itemSeleccionado;
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar un empleado responsable",
                "Empleado no seleccionado",
                JOptionPane.ERROR_MESSAGE);
            return;
        } catch (ClassCastException e) {
            JOptionPane.showMessageDialog(this,
                "Error en los datos de empleado: " + e.getMessage(),
                "Error de datos",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Validar y parsear fecha
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setLenient(false);
            Date fecha = dateFormat.parse(txtFecha.getText());
            
            // Validar formato de hora
            String hora = txtHora.getText().trim();
            if (!hora.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                throw new IllegalArgumentException("Formato de hora debe ser HH:MM (24 horas)");
            }
            
            // Crear y registrar la nueva entrada
            Entrada nuevaEntrada = new Entrada(
                vehiculo,
                empleado,
                fecha,
                hora,
                txtNotas.getText(),
                sistema.vehiculoTieneContrato(vehiculo)
            );
            
            sistema.registrarEntrada(nuevaEntrada);
            
            // Mostrar mensaje de confirmación
            String mensaje = String.format(
               "<html><b>Entrada registrada exitosamente</b><br><br>" +
               "Vehículo: %s %s (%s)<br>" +
               "Empleado: %s<br>" +
               "Fecha: %s<br>" +
               "Hora: %s</html>",
               vehiculo.getMarca(),
               vehiculo.getModelo(),
               vehiculo.getMatricula(),
               empleado.getNombre(),
               txtFecha.getText(),
               hora);
            
            JOptionPane.showMessageDialog(this, mensaje,
                "Registro exitoso",
                JOptionPane.INFORMATION_MESSAGE);
            
            dispose();  // Cerrar la ventana después del registro exitoso
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al registrar: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Inicializa y configura los componentes de la interfaz gráfica.
     */
    private void initComponents() {
        setTitle("Registro de Entrada de Vehículo");
        setSize(600, 500); 
        setLayout(new BorderLayout(10, 10));
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel para lista de vehículos
        JPanel panelVehiculos = new JPanel(new BorderLayout());
        panelVehiculos.setBorder(BorderFactory.createTitledBorder("Vehículos disponibles"));
        panelVehiculos.add(new JLabel("Seleccione vehículo:"), BorderLayout.NORTH);
        
        // Configurar lista de vehículos
        lstVehiculos = new JList<>();
        lstVehiculos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstVehiculos.setVisibleRowCount(8);
        
        // Render personalizado para los vehículos
        lstVehiculos.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                        boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Vehiculo) {
                    Vehiculo v = (Vehiculo) value;
                    setText(String.format("%s - %s %s (%s)", 
                            v.getMatricula(), v.getMarca(), v.getModelo(), v.getEstado()));
                    
                    // Resaltar vehículos con contrato
                    if (sistema.vehiculoTieneContrato(v)) {
                        setFont(getFont().deriveFont(Font.BOLD));
                        setForeground(new Color(0, 100, 0)); 
                    }
                }
                return this;
            }
        });
        
        scrollVehiculos = new JScrollPane(lstVehiculos);
        panelVehiculos.add(scrollVehiculos, BorderLayout.CENTER);
        
        // Panel para formulario de entrada
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos de entrada"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Configurar combo de empleados
        cmbEmpleados = new JComboBox<>();
        cmbEmpleados.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                                                        int index, boolean isSelected, 
                                                        boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Empleado) {
                    Empleado emp = (Empleado) value;
                    setText(String.format("%d - %s", emp.getNumeroEmpleado(), emp.getNombre()));
                }
                return this;
            }
        });
        
        // Añadir componentes al formulario
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(new JLabel("Empleado responsable:"), gbc);
        
        gbc.gridx = 1;
        panelFormulario.add(cmbEmpleados, gbc);
        
        // Campo fecha
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelFormulario.add(new JLabel("Fecha (dd/MM/yyyy):"), gbc);
        
        gbc.gridx = 1;
        txtFecha = new JTextField();
        panelFormulario.add(txtFecha, gbc);
        
        // Campo hora
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelFormulario.add(new JLabel("Hora (HH:mm):"), gbc);
        
        gbc.gridx = 1;
        txtHora = new JTextField();
        panelFormulario.add(txtHora, gbc);
        
        // Campo notas
        gbc.gridx = 0;
        gbc.gridy = 3;
        panelFormulario.add(new JLabel("Notas/Observaciones:"), gbc);
        
        gbc.gridx = 1;
        txtNotas = new JTextField();
        panelFormulario.add(txtNotas, gbc);
        
        // Botón de registro
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        btnRegistrar = new JButton("Registrar Entrada");
        btnRegistrar.addActionListener((ActionEvent e) -> {
            registrarEntrada();
        });
        panelFormulario.add(btnRegistrar, gbc);
        
        // Organización final de componentes
        panelPrincipal.add(panelVehiculos, BorderLayout.NORTH);
        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);
        add(panelPrincipal);
        
        // Configuración final de la ventana
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        ConfiguracionTema.getInstancia().aplicarTema(this);
    }
}

/*
Explicación General de la Clase Entradas
La clase Entradas es una ventana de interfaz gráfica que permite registrar la entrada de vehículos al parking.

Funcionalidades clave:
Selección de vehículo: Muestra una lista de vehículos disponibles (sin entrada activa)
Selección de empleado: Permite elegir al empleado responsable de la entrada
Registro de fecha/hora: Campos para especificar cuándo se produce la entrada
Observaciones: Campo para registrar notas adicionales sobre el vehículo

Validaciones implementadas:
Verifica que se haya seleccionado un vehículo
Valida que se haya seleccionado un empleado
Comprueba el formato correcto de la fecha (dd/MM/yyyy)
Valida el formato de hora (HH:mm en formato 24 horas)

Características de la interfaz:
Render personalizado: Muestra información detallada de vehículos y empleados
Resaltado visual: Vehículos con contrato aparecen en negrita y color verde
Mensajes de error: Informa al usuario sobre problemas en los datos ingresados
Confirmación: Muestra un resumen del registro exitoso

Integración con el sistema:
Recibe una referencia al sistema principal (Sistema) para acceder a los datos
Registra las nuevas entradas llamando a sistema.registrarEntrada()
Sincroniza con el sistema de temas visuales (ConfiguracionTema)
Esta ventana sigue el patrón de diseño MVC (Modelo-Vista-Controlador), donde:
Modelo: La clase Sistema que contiene los datos
Vista: Esta clase que muestra la interfaz gráfica
Controlador: Los métodos que manejan las acciones del usuario y validan los datos
*/