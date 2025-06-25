package Interfaz;
/**
 *
 * @author Natalia Peña 
 */

import Dominio.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Ventana para la gestión de contratos del parking.
 * Permite crear nuevos contratos y visualizar los existentes.
 */
public class GestionContratos extends JFrame {
    // Referencia al sistema principal
    private final Sistema sistema;
    
    // Componentes de la interfaz
    private JComboBox<Cliente> cmbClientes;      // Combo para seleccionar cliente
    private JComboBox<Vehiculo> cmbVehiculos;    // Combo para seleccionar vehículo
    private JComboBox<Empleado> cmbEmpleados;    // Combo para seleccionar empleado
    private JTextField txtValor;                 // Campo para valor mensual
    private JButton btnGuardar;                  // Botón para guardar contrato
    private JButton btnCancelar;                 // Botón para cancelar/cerrar
    private JTable tablaContratos;               // Tabla para listar contratos
    private DefaultTableModel modeloTabla;       // Modelo para la tabla de contratos

    /**
     * Constructor de la ventana de gestión de contratos.
     * @param sistema Referencia al sistema principal
     */
    public GestionContratos(Sistema sistema) {
        // Configuración inicial del tema visual
        ConfiguracionTema.getInstancia().registrarVentana(this);
        ConfiguracionTema.getInstancia().aplicarTema(this);
        
        this.sistema = sistema;
        initComponents();  // Inicializa los componentes de la interfaz
        setTitle("Gestión de Contratos");
        pack();           // Ajusta el tamaño de la ventana
        setLocationRelativeTo(null);  // Centra la ventana
        setVisible(true); // Hace visible la ventana
    }
    
    /**
     * Inicializa y configura todos los componentes de la interfaz.
     */
    private void initComponents() {
        // Panel principal con borde y espaciado
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel para el formulario de nuevo contrato (usando GridBagLayout)
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Configuración de los combos (inicialmente sin selección)
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(new JLabel("Cliente:"), gbc);
        
        gbc.gridx = 1;
        cmbClientes = new JComboBox<>();
        cmbClientes.setSelectedIndex(-1); // Sin selección inicial
        panelFormulario.add(cmbClientes, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelFormulario.add(new JLabel("Vehículo:"), gbc);
        
        gbc.gridx = 1;
        cmbVehiculos = new JComboBox<>();
        cmbVehiculos.setSelectedIndex(-1); // Sin selección inicial
        panelFormulario.add(cmbVehiculos, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelFormulario.add(new JLabel("Empleado:"), gbc);
        
        gbc.gridx = 1;
        cmbEmpleados = new JComboBox<>();
        cmbEmpleados.setSelectedIndex(-1); // Sin selección inicial
        panelFormulario.add(cmbEmpleados, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panelFormulario.add(new JLabel("Valor Mensual:"), gbc);
        
        gbc.gridx = 1;
        txtValor = new JTextField(10);
        txtValor.setText(""); // Campo vacío inicialmente
        panelFormulario.add(txtValor, gbc);
        
        // Panel para botones (Guardar y Cancelar)
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        
        // Asignar acciones a los botones
        btnGuardar.addActionListener(e -> guardarContrato());
        btnCancelar.addActionListener(e -> dispose()); // Cierra la ventana
        
        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        panelFormulario.add(panelBotones, gbc);
        
        // Panel para la tabla de contratos
        JPanel panelTabla = new JPanel(new BorderLayout());
        
        // Configuración de la tabla de contratos
        modeloTabla = new DefaultTableModel(new Object[]{"N° Contrato"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hace que la tabla no sea editable
            }
        };
        
        tablaContratos = new JTable(modeloTabla);
        tablaContratos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Listener para mostrar detalles al seleccionar un contrato
        tablaContratos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetallesContrato();
            }
        });
        
        JScrollPane scrollTabla = new JScrollPane(tablaContratos);
        panelTabla.add(new JLabel("Lista de Contratos:"), BorderLayout.NORTH);
        panelTabla.add(scrollTabla, BorderLayout.CENTER);
        
        // Dividir la ventana en dos partes (formulario y tabla)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelFormulario, panelTabla);
        splitPane.setResizeWeight(0.5); // Divide el espacio equitativamente
        
        panelPrincipal.add(splitPane, BorderLayout.CENTER);
        add(panelPrincipal);
        
        // Cargar datos iniciales
        cargarDatos();
        actualizarTablaContratos();
        
        // Configuración final de la ventana
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(600, 400));
        ConfiguracionTema.getInstancia().aplicarTema(this);
    }
    
    /**
     * Carga los datos iniciales en los combos (clientes, vehículos y empleados).
     */
    private void cargarDatos() {
        // Limpiar combos
        cmbClientes.removeAllItems();
        cmbVehiculos.removeAllItems();
        cmbEmpleados.removeAllItems();
        
        // Cargar clientes
        for (Cliente c : sistema.getClientes()) {
            cmbClientes.addItem(c);
        }
        
        // Cargar vehículos
        for (Vehiculo v : sistema.getVehiculos()) {
            cmbVehiculos.addItem(v);
        }
        
        // Cargar empleados
        for (Empleado e : sistema.getEmpleados()) {
           cmbEmpleados.addItem(e);
        }
        
        // Dejar sin selección inicial
        cmbClientes.setSelectedIndex(-1);
        cmbVehiculos.setSelectedIndex(-1);
        cmbEmpleados.setSelectedIndex(-1);
    }
    
    /**
     * Actualiza la tabla de contratos con los datos del sistema.
     */
    private void actualizarTablaContratos() {
        modeloTabla.setRowCount(0); // Limpiar tabla
        
        // Obtener y ordenar contratos
        List<Contrato> contratos = sistema.getContratos();
        contratos.sort((c1, c2) -> Integer.compare(c1.getId(), c2.getId()));
        
        // Agregar contratos a la tabla
        for (Contrato c : contratos) {
            modeloTabla.addRow(new Object[]{"Contrato" + c.getId()});
        }
    }
    
    /**
     * Muestra los detalles del contrato seleccionado en la tabla.
     */
    private void mostrarDetallesContrato() {
        int filaSeleccionada = tablaContratos.getSelectedRow();
        
        if (filaSeleccionada >= 0) {
            try {
                Object valor = modeloTabla.getValueAt(filaSeleccionada, 0);
                
                if (valor != null) {
                    // Extraer ID del contrato del texto "ContratoX"
                    String textoContrato = valor.toString();
                    int idContrato = Integer.parseInt(textoContrato.replace("Contrato", ""));
                    
                    if (idContrato > 0) {
                        // Buscar y mostrar el contrato
                        Contrato contratoSeleccionado = sistema.buscarContratoPorId(idContrato);
                        
                        if (contratoSeleccionado != null) {
                            mostrarVentanaDetalles(contratoSeleccionado);
                        }
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error al obtener el contrato seleccionado", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Muestra una ventana emergente con los detalles completos de un contrato.
     * @param contrato El contrato a mostrar
     */
    private void mostrarVentanaDetalles(Contrato contrato) {
        JDialog dialog = new JDialog(this, "Detalles del Contrato #" + contrato.getId(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setPreferredSize(new Dimension(500, 400));
        
        // Panel principal 
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel para los detalles del contrato
        JPanel panelDetalles = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Encabezado
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panelDetalles.add(new JLabel("Detalles del Contrato #" + contrato.getId()), gbc);
        
        // Separador
        gbc.gridy++;
        panelDetalles.add(new JSeparator(), gbc);
        
        // Detalles del cliente
        gbc.gridy++;
        gbc.gridwidth = 1;
        panelDetalles.add(new JLabel("Cliente:"), gbc);
        gbc.gridx = 1;
        panelDetalles.add(new JLabel(contrato.getCliente().getNombre() + " (C.I. " + contrato.getCliente().getCedula() + ")"), gbc);
        
        // Detalles del vehículo
        gbc.gridy++;
        gbc.gridx = 0;
        panelDetalles.add(new JLabel("Vehículo:"), gbc);
        gbc.gridx = 1;
        Vehiculo v = contrato.getVehiculo();
        panelDetalles.add(new JLabel(v.getMatricula() + " - " + v.getMarca() + " " + v.getModelo()), gbc);
        
        // Detalles del empleado
        gbc.gridy++;
        gbc.gridx = 0;
        panelDetalles.add(new JLabel("Empleado:"), gbc);
        gbc.gridx = 1;
        Empleado e = contrato.getEmpleado();
        panelDetalles.add(new JLabel(e.getNombre() + " (N° " + e.getNumeroEmpleado() + ")"), gbc);
        
        // Valor mensual
        gbc.gridy++;
        gbc.gridx = 0;
        panelDetalles.add(new JLabel("Valor mensual:"), gbc);
        gbc.gridx = 1;
        panelDetalles.add(new JLabel(String.format("$%.2f", contrato.getValorMensual())), gbc);
        
        // Botón para cerrar
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(ev -> dialog.dispose());
        panelBoton.add(btnCerrar);
        
        // Agregar componentes al panel principal
        panelPrincipal.add(panelDetalles, BorderLayout.CENTER);
        panelPrincipal.add(panelBoton, BorderLayout.SOUTH);
        
        dialog.add(panelPrincipal);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    /**
     * Guarda un nuevo contrato con los datos del formulario.
     * Realiza validaciones antes de crear el contrato.
     */
    private void guardarContrato() {
        try {
            // Validar campos obligatorios
            if (cmbClientes.getSelectedIndex() == -1 || cmbVehiculos.getSelectedIndex() == -1 || 
                cmbEmpleados.getSelectedIndex() == -1 || txtValor.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Todos los campos son obligatorios", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validar valor numérico positivo
            double valor;
            try {
                valor = Double.parseDouble(txtValor.getText());
                if (valor <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "El valor mensual debe ser un número positivo", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Obtener objetos seleccionados
            Cliente cliente = (Cliente) cmbClientes.getSelectedItem();
            Vehiculo vehiculo = (Vehiculo) cmbVehiculos.getSelectedItem();
            Empleado empleado = (Empleado) cmbEmpleados.getSelectedItem();
            
            // Verificar que el vehículo no tenga contrato activo
            boolean tieneContrato = sistema.getContratos().stream()
                .anyMatch(c -> c.getVehiculo().equals(vehiculo) && c.getActivo());
            
            if (tieneContrato) {
                JOptionPane.showMessageDialog(this, 
                    "Este vehículo ya tiene un contrato activo", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Crear y guardar el nuevo contrato
            Contrato nuevo = new Contrato(
                sistema.getProximoIdContrato(),
                cliente,
                vehiculo,
                empleado,
                valor
            );
            
            sistema.agregarContrato(nuevo);
            
            JOptionPane.showMessageDialog(this, 
                "Contrato #" + nuevo.getId() + " creado exitosamente!", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Actualizar interfaz
            actualizarTablaContratos();
            cmbClientes.setSelectedIndex(-1);
            cmbVehiculos.setSelectedIndex(-1);
            cmbEmpleados.setSelectedIndex(-1);
            txtValor.setText("");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al crear el contrato: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); 
        }
    }
}

/*
Explicación General de la Clase GestionContratos
La clase GestionContratos es una ventana de interfaz gráfica para administrar los contratos del sistema de parking.

Funcionalidades clave:
Creación de contratos: Formulario para asociar clientes, vehículos y empleados
Listado de contratos: Tabla que muestra todos los contratos existentes
Visualización de detalles: Muestra información completa de un contrato seleccionado
Validaciones: Verifica datos antes de crear un nuevo contrato

Validaciones implementadas:
Campos obligatorios (todos los combos y el valor deben estar completos)
Valor numérico positivo para el monto mensual
Unicidad de contrato por vehículo (no puede haber dos contratos activos para el mismo vehículo)

Características de la interfaz:
Diseño dividido: Formulario a la izquierda, lista de contratos a la derecha
Combos desplegables: Para selección de clientes, vehículos y empleados
Tabla no editable: Solo para visualización de contratos
Ventana modal de detalles: Muestra información completa del contrato seleccionado
Mensajes de feedback: Informa al usuario sobre operaciones exitosas o errores

Integración con el sistema:
Recibe una referencia al sistema principal (Sistema) para acceder a los datos
Llama a métodos del sistema para crear contratos y obtener listados
Sincroniza con el sistema de temas visuales (ConfiguracionTema)
Genera IDs automáticos para nuevos contratos
Esta ventana sigue el patrón de diseño MVC (Modelo-Vista-Controlador), donde:
Modelo: La clase Sistema que contiene los datos de contratos
Vista: Esta clase que muestra la interfaz gráfica
Controlador: Los métodos que manejan las acciones del usuario y validan los datos
*/