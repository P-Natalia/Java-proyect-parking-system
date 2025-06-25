package Interfaz;
/**
 *
 * @author Natalia Peña 
 */

import Dominio.ConfiguracionTema;
import Dominio.Sistema;
import Dominio.Empleado;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Ventana para la gestión de empleados del parking.
 * Permite agregar, eliminar y visualizar detalles de empleados.
 */
public class GestionEmpleados extends JFrame {
    // Referencia al sistema principal
    private final Sistema sistema;
    
    // Modelo para la lista de empleados
    private DefaultListModel<String> modeloLista;
    
    // Componentes de la interfaz
    private JTextField txtNombre;          // Campo para nombre del empleado
    private JTextField txtCedula;         // Campo para cédula del empleado
    private JTextField txtDireccion;      // Campo para dirección del empleado
    private JTextField txtNumeroEmpleado; // Campo para número de empleado
    private JList<String> listaEmpleados; // Lista visual de empleados
    private JButton btnVaciar;            // Botón para vaciar campos
    private JButton btnAgregar;           // Botón para agregar empleado
    private JButton btnEliminar;          // Botón para eliminar empleado

    /**
     * Constructor de la ventana de gestión de empleados.
     * @param sistema Referencia al sistema principal para acceder a los datos
     */
    public GestionEmpleados(Sistema sistema) {
        // Configuración inicial del tema visual
        ConfiguracionTema.getInstancia().registrarVentana(this);
        ConfiguracionTema.getInstancia().aplicarTema(this);
        
        this.sistema = sistema;
        initComponents();  // Configura los componentes de la interfaz
        cargarEmpleados(); // Carga los empleados existentes
    }

    /**
     * Inicializa y configura todos los componentes de la interfaz gráfica.
     */
    private void initComponents() {
        setTitle("Gestión de Empleados");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Panel de formulario (parte superior)
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Campos del formulario
        formPanel.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        formPanel.add(txtNombre);
        
        formPanel.add(new JLabel("Cédula:"));
        txtCedula = new JTextField();
        formPanel.add(txtCedula);
        
        formPanel.add(new JLabel("Dirección:"));
        txtDireccion = new JTextField();
        formPanel.add(txtDireccion);
        
        formPanel.add(new JLabel("Número de Empleado:"));
        txtNumeroEmpleado = new JTextField();
        formPanel.add(txtNumeroEmpleado);
        
        add(formPanel, BorderLayout.NORTH);
        
        // Lista de empleados (parte central)
        modeloLista = new DefaultListModel<>();
        listaEmpleados = new JList<>(modeloLista);
        listaEmpleados.setBorder(BorderFactory.createTitledBorder("Lista de empleados"));
        listaEmpleados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Listener para mostrar detalles al seleccionar un empleado
        listaEmpleados.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    mostrarDetallesEmpleado();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(listaEmpleados);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones (parte inferior)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        btnVaciar = new JButton("Vaciar Campos");
        btnVaciar.addActionListener(this::vaciarCampos);
        buttonPanel.add(btnVaciar);
        
        btnAgregar = new JButton("Agregar Empleado");
        btnAgregar.addActionListener(this::agregarEmpleado);
        buttonPanel.add(btnAgregar);
        
        btnEliminar = new JButton("Eliminar Empleado");
        btnEliminar.addActionListener(this::eliminarEmpleado);
        buttonPanel.add(btnEliminar);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Aplicar tema visual
        ConfiguracionTema.getInstancia().aplicarTema(this);
    }

    /**
     * Carga los empleados del sistema en la lista visual.
     */
    private void cargarEmpleados() {
        modeloLista.clear(); 
        for (Empleado empleado : sistema.getEmpleados()) {
            modeloLista.addElement(empleado.toString());
        }
    }

    /**
     * Muestra los detalles del empleado seleccionado en una ventana emergente.
     */
    private void mostrarDetallesEmpleado() {
        int indiceSeleccionado = listaEmpleados.getSelectedIndex();
        
        if (indiceSeleccionado >= 0) {
            Empleado empleado = sistema.getEmpleados().get(indiceSeleccionado);
            
            // Crear ventana emergente modal
            JDialog detallesDialog = new JDialog(this, "Detalles del Empleado", true);
            detallesDialog.setSize(400, 300);
            detallesDialog.setLayout(new GridLayout(5, 2, 10, 10));
            detallesDialog.setLocationRelativeTo(this);
            
            // Mostrar todos los datos del empleado
            detallesDialog.add(new JLabel("Nombre: " + empleado.getNombre()));
            detallesDialog.add(new JLabel("Cédula: " + empleado.getCedula()));
            detallesDialog.add(new JLabel("Dirección: " + empleado.getDireccion()));
            detallesDialog.add(new JLabel("Número de Empleado: " + empleado.getNumeroEmpleado()));
            detallesDialog.add(new JLabel("Estado: " + (empleado.getActivo() ? "Activo" : "Inactivo")));
            
            detallesDialog.setVisible(true);
        }
    }

    /**
     * Vacía todos los campos del formulario y deselecciona la lista.
     * @param evt Evento de acción del botón
     */
    private void vaciarCampos(ActionEvent evt) {
        txtNombre.setText("");
        txtCedula.setText("");
        txtDireccion.setText("");
        txtNumeroEmpleado.setText("");
        listaEmpleados.clearSelection();
    }

    /**
     * Agrega un nuevo empleado al sistema después de validar los datos.
     * @param evt Evento de acción del botón
     */
    private void agregarEmpleado(ActionEvent evt) {
        // Obtener y limpiar datos del formulario
        String nombre = txtNombre.getText().trim();
        String cedula = txtCedula.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String numeroStr = txtNumeroEmpleado.getText().trim();
        
        // Validar campos obligatorios
        if (nombre.isEmpty() || cedula.isEmpty() || direccion.isEmpty() || numeroStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Todos los campos son obligatorios", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Validar que el número de empleado sea numérico
            int numeroEmpleado = Integer.parseInt(numeroStr);
            
            // Validar que no exista un empleado con el mismo número
            if (sistema.existeEmpleado(numeroEmpleado)) {
                JOptionPane.showMessageDialog(this, 
                    "El número de empleado ya está registrado", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validar que no exista un empleado con la misma cédula
            for (Empleado emp : sistema.getEmpleados()) {
                if (emp.getCedula().equals(cedula)) {
                    JOptionPane.showMessageDialog(this, 
                        "La cédula ya está registrada", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Crear y agregar el nuevo empleado
            Empleado nuevoEmpleado = new Empleado(cedula, nombre, direccion, numeroEmpleado);
            sistema.agregarEmpleado(nuevoEmpleado);
            
            // Actualizar interfaz
            cargarEmpleados();
            vaciarCampos(null);
            
            JOptionPane.showMessageDialog(this, 
                "Empleado agregado exitosamente", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "El número de empleado debe ser un valor numérico", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Elimina el empleado seleccionado después de confirmación.
     * @param evt Evento de acción del botón
     */
    private void eliminarEmpleado(ActionEvent evt) {
        int indiceSeleccionado = listaEmpleados.getSelectedIndex();
        
        // Validar selección
        if (indiceSeleccionado < 0) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un empleado para eliminar", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Empleado empleado = sistema.getEmpleados().get(indiceSeleccionado);
        
        // Pedir confirmación
        int confirmacion = JOptionPane.showConfirmDialog(
            this, 
            "¿Está seguro de eliminar al empleado " + empleado.getNombre() + 
            " (N° " + empleado.getNumeroEmpleado() + ")?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            // Eliminar empleado y actualizar interfaz
            sistema.getEmpleados().remove(empleado);
            modeloLista.remove(indiceSeleccionado);
            vaciarCampos(null);
            
            JOptionPane.showMessageDialog(this, 
                "Empleado eliminado exitosamente", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

/*
Explicación General de la Clase GestionEmpleados
La clase GestionEmpleados es una ventana de interfaz gráfica para administrar los empleados del sistema de parking.

Funcionalidades clave:
Visualización de empleados: Muestra una lista de todos los empleados registrados
Detalles de empleados: Permite ver toda la información de un empleado seleccionado
Agregar empleados: Formulario para registrar nuevos empleados con validación de datos
Eliminar empleados: Elimina empleados existentes con confirmación previa
Limpieza de formulario: Botón para vaciar todos los campos

Validaciones implementadas:
Campos obligatorios (todos los campos deben estar completos)
Número de empleado debe ser numérico
Unicidad del número de empleado (no puede haber dos empleados con el mismo número)
Unicidad de la cédula (no puede haber dos empleados con la misma cédula)
Confirmación antes de eliminar un empleado

Características de la interfaz:
Organización clara: Formulario en la parte superior, lista en el centro, botones abajo
Selección única: Solo se puede seleccionar un empleado a la vez
Ventana modal: Muestra los detalles del empleado en una ventana emergente
Mensajes de feedback: Informa al usuario sobre operaciones exitosas o errores
Tema visual: Se integra con el sistema de temas claro/oscuro

Integración con el sistema:
Recibe una referencia al sistema principal (Sistema) para acceder a los datos
Llama a métodos del sistema para agregar/eliminar empleados
Sincroniza con el sistema de temas visuales (ConfiguracionTema)
Esta ventana sigue el patrón de diseño MVC (Modelo-Vista-Controlador), donde:
Modelo: La clase Sistema que contiene los datos de empleados
Vista: Esta clase que muestra la interfaz gráfica
Controlador: Los métodos que manejan las acciones del usuario y validan los datos
*/