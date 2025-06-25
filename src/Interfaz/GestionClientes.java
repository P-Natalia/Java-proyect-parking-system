package Interfaz;
/**
 *
 * @author Natalia Peña 
 */

import Dominio.Sistema;
import Dominio.Cliente;
import Dominio.ConfiguracionTema;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Ventana para la gestión de clientes del parking.
 * Permite agregar, eliminar y visualizar detalles de clientes.
 */
public class GestionClientes extends JFrame {
    // Referencia al sistema principal
    private final Sistema sistema;
    
    // Modelo para la lista de clientes
    private DefaultListModel<String> modeloLista;
    
    // Componentes de la interfaz
    private JTextField txtNombre;       // Campo para nombre del cliente
    private JTextField txtCedula;      // Campo para cédula del cliente
    private JTextField txtDireccion;    // Campo para dirección del cliente
    private JTextField txtCelular;     // Campo para celular del cliente
    private JTextField txtAnioIngreso;  // Campo para año de ingreso del cliente
    private JList<String> listaClientes; // Lista visual de clientes
    private JButton btnVaciar;          // Botón para vaciar campos
    private JButton btnAgregar;         // Botón para agregar cliente
    private JButton btnEliminar;        // Botón para eliminar cliente
    private JButton btnCambiarModo;     // Botón para cambiar modo claro/oscuro

    /**
     * Constructor de la ventana de gestión de clientes.
     * @param sistema Referencia al sistema principal para acceder a los datos
     */
    public GestionClientes(Sistema sistema) {
        // Configuración inicial del tema visual
        ConfiguracionTema.getInstancia().registrarVentana(this);
        ConfiguracionTema.getInstancia().aplicarTema(this);
        
        this.sistema = sistema;
        inicializarComponentes();  // Configura los componentes de la interfaz
        cargarClientes();          // Carga los clientes existentes
    }

    /**
     * Inicializa y configura todos los componentes de la interfaz gráfica.
     */
    private void inicializarComponentes() {
        setTitle("Gestión de Clientes");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel principal con borde y espaciado
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(panelPrincipal);
        
        // Panel de formulario para datos del cliente (parte superior)
        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));
        
        // Campos del formulario
        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);
        
        panelFormulario.add(new JLabel("Cédula:"));
        txtCedula = new JTextField();
        panelFormulario.add(txtCedula);
        
        panelFormulario.add(new JLabel("Dirección:"));
        txtDireccion = new JTextField();
        panelFormulario.add(txtDireccion);
        
        panelFormulario.add(new JLabel("Celular:"));
        txtCelular = new JTextField();
        panelFormulario.add(txtCelular);
        
        panelFormulario.add(new JLabel("Año de Ingreso:"));
        txtAnioIngreso = new JTextField();
        panelFormulario.add(txtAnioIngreso);
        
        panelPrincipal.add(panelFormulario, BorderLayout.NORTH);
        
        // Lista de clientes (parte central)
        modeloLista = new DefaultListModel<>();
        listaClientes = new JList<>(modeloLista);
        listaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Listener para mostrar detalles al seleccionar un cliente
        listaClientes.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    mostrarDetallesCliente();
                }
            }
        });
        
        JScrollPane scrollLista = new JScrollPane(listaClientes);
        scrollLista.setBorder(BorderFactory.createTitledBorder("Lista de clientes"));
        panelPrincipal.add(scrollLista, BorderLayout.CENTER);
        
        // Panel de botones (parte inferior)
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        btnVaciar = new JButton("Vaciar Campos");
        btnVaciar.addActionListener(this::vaciarCampos);
        btnVaciar.setPreferredSize(new Dimension(150, 30));
        panelBotones.add(btnVaciar);
        
        btnAgregar = new JButton("Agregar Cliente");
        btnAgregar.addActionListener(this::agregarCliente);
        btnAgregar.setPreferredSize(new Dimension(150, 30));
        panelBotones.add(btnAgregar);
        
        btnEliminar = new JButton("Eliminar Cliente");
        btnEliminar.addActionListener(this::eliminarCliente);
        btnEliminar.setPreferredSize(new Dimension(150, 30));
        panelBotones.add(btnEliminar);
        
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        
        // Aplicar tema visual
        ConfiguracionTema.getInstancia().aplicarTema(this);
    }

    /**
     * Carga los clientes del sistema en la lista visual.
     */
    private void cargarClientes() {
        modeloLista.clear(); 
        for (Cliente cliente : sistema.getClientes()) {
            modeloLista.addElement(cliente.toString());
        }
    }

    /**
     * Muestra los detalles del cliente seleccionado en una ventana emergente.
     */
    private void mostrarDetallesCliente() {
        int indiceSeleccionado = listaClientes.getSelectedIndex();
        if (indiceSeleccionado >= 0) {
            Cliente cliente = sistema.getClientes().get(indiceSeleccionado);
            
            // Crear ventana emergente modal
            JDialog dialog = new JDialog(this, "Detalles del Cliente", true);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);
            
            JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));
            
            // Mostrar todos los datos del cliente
            panel.add(new JLabel("Nombre:"));
            panel.add(new JLabel(cliente.getNombre()));
            
            panel.add(new JLabel("Cédula:"));
            panel.add(new JLabel(cliente.getCedula()));
            
            panel.add(new JLabel("Dirección:"));
            panel.add(new JLabel(cliente.getDireccion()));
            
            panel.add(new JLabel("Celular:"));
            panel.add(new JLabel(cliente.getCelular()));
            
            panel.add(new JLabel("Año de Ingreso:"));
            panel.add(new JLabel(String.valueOf(cliente.getAñoIngreso())));
            
            dialog.add(panel);
            dialog.setVisible(true);
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
        txtCelular.setText("");
        txtAnioIngreso.setText("");
        listaClientes.clearSelection();
    }

    /**
     * Agrega un nuevo cliente al sistema después de validar los datos.
     * @param evt Evento de acción del botón
     */
    private void agregarCliente(ActionEvent evt) {
        // Obtener y limpiar datos del formulario
        String nombre = txtNombre.getText().trim();
        String cedula = txtCedula.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String celular = txtCelular.getText().trim();
        String anioStr = txtAnioIngreso.getText().trim();
        
        // Validar campos obligatorios
        if (nombre.isEmpty() || cedula.isEmpty() || direccion.isEmpty() || celular.isEmpty() || anioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Todos los campos son obligatorios", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validar que el año sea numérico
        int anio;
        try {
            anio = Integer.parseInt(anioStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "El año debe ser un número válido", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validar que no exista un cliente con la misma cédula
        if (sistema.existeCliente(cedula)) {
            JOptionPane.showMessageDialog(this, 
                "Ya existe un cliente con esta cédula", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Crear y agregar el nuevo cliente
        Cliente nuevoCliente = new Cliente(cedula, nombre, direccion, celular, anio);
        sistema.agregarCliente(nuevoCliente);
        
        // Actualizar interfaz
        cargarClientes();
        vaciarCampos(null);
        
        JOptionPane.showMessageDialog(this, 
            "Cliente agregado exitosamente", 
            "Éxito", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Elimina el cliente seleccionado después de confirmación.
     * @param evt Evento de acción del botón
     */
    private void eliminarCliente(ActionEvent evt) {
        int indiceSeleccionado = listaClientes.getSelectedIndex();
        
        // Validar selección
        if (indiceSeleccionado < 0) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un cliente de la lista", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Cliente cliente = sistema.getClientes().get(indiceSeleccionado);
        
        // Pedir confirmación (se eliminarán también los contratos asociados)
        int confirmacion = JOptionPane.showConfirmDialog(
            this, 
            "¿Está seguro de eliminar al cliente " + cliente.getNombre() + " (Cédula: " + cliente.getCedula() + ")?\n" +
            "Esta acción también eliminará sus contratos asociados.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            sistema.eliminarCliente(cliente.getCedula());
            cargarClientes();
            vaciarCampos(null);
            
            JOptionPane.showMessageDialog(this, 
                "Cliente eliminado exitosamente", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

/*
Explicación General de la Clase GestionClientes
La clase GestionClientes es una ventana de interfaz gráfica para administrar los clientes del sistema de parking.


Funcionalidades clave:
Visualización de clientes: Muestra una lista de todos los clientes registrados
Detalles de clientes: Permite ver toda la información de un cliente seleccionado
Agregar clientes: Formulario para registrar nuevos clientes con validación de datos
Eliminar clientes: Elimina clientes existentes con confirmación previa
Limpieza de formulario: Botón para vaciar todos los campos

Validaciones implementadas:
Campos obligatorios (todos los campos deben estar completos)
Formato numérico para el año de ingreso
Unicidad de la cédula (no puede haber dos clientes con la misma cédula)
Confirmación antes de eliminar un cliente

Características de la interfaz:
Organización clara: Formulario en la parte superior, lista en el centro, botones abajo
Selección única: Solo se puede seleccionar un cliente a la vez
Ventana modal: Muestra los detalles del cliente en una ventana emergente
Mensajes de feedback: Informa al usuario sobre operaciones exitosas o errores
Tema visual: Se integra con el sistema de temas claro/oscuro

Integración con el sistema:
Recibe una referencia al sistema principal (Sistema) para acceder a los datos
Llama a métodos del sistema para agregar/eliminar clientes
Sincroniza con el sistema de temas visuales (ConfiguracionTema)
Esta ventana sigue el patrón de diseño MVC (Modelo-Vista-Controlador), donde:
Modelo: La clase Sistema que contiene los datos de clientes
Vista: Esta clase que muestra la interfaz gráfica
Controlador: Los métodos que manejan las acciones del usuario y validan los datos
*/