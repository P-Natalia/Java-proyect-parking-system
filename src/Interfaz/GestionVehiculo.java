package Interfaz;

/**
 *
 * @author Natalia Peña 
 */
import Dominio.ConfiguracionTema;
import Dominio.Sistema;
import Dominio.Vehiculo;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Ventana para la gestión de vehículos del parking.
 * Permite agregar, eliminar y visualizar detalles de vehículos.
 */
public class GestionVehiculo extends JFrame {
    // Referencia al sistema principal
    private final Sistema sistema;
    
    // Modelo para la lista de vehículos
    private DefaultListModel<String> modeloLista;
    
    // Componentes de la interfaz
    private JTextField txtMatricula;  // Campo para matrícula del vehículo
    private JTextField txtMarca;      // Campo para marca del vehículo
    private JTextField txtModelo;     // Campo para modelo del vehículo
    private JTextField txtEstado;     // Campo para estado del vehículo
    private JList<String> listaVehiculos; // Lista visual de vehículos
    private JButton btnVaciar;        // Botón para vaciar campos
    private JButton btnAgregar;       // Botón para agregar vehículo
    private JButton btnEliminar;      // Botón para eliminar vehículo

    /**
     * Constructor de la ventana de gestión de vehículos.
     * @param sistema Referencia al sistema principal para acceder a los datos
     */
    public GestionVehiculo(Sistema sistema) {
        // Configuración inicial del tema visual
        ConfiguracionTema.getInstancia().registrarVentana(this);
        ConfiguracionTema.getInstancia().aplicarTema(this);
        
        this.sistema = sistema;
        inicializarComponentes();  // Configura los componentes de la interfaz
        cargarVehiculos();         // Carga los vehículos existentes
    }

    /**
     * Inicializa y configura todos los componentes de la interfaz gráfica.
     */
    private void inicializarComponentes() {
        setTitle("Gestión de Vehículos");
        setSize(600, 400); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Panel de formulario (parte superior)
        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Campos del formulario
        panelFormulario.add(new JLabel("Matrícula*:"));
        txtMatricula = new JTextField();
        panelFormulario.add(txtMatricula);
        
        panelFormulario.add(new JLabel("Marca:"));
        txtMarca = new JTextField();
        panelFormulario.add(txtMarca);
        
        panelFormulario.add(new JLabel("Modelo:"));
        txtModelo = new JTextField();
        panelFormulario.add(txtModelo);
        
        panelFormulario.add(new JLabel("Estado:"));
        txtEstado = new JTextField();
        panelFormulario.add(txtEstado);
        
        add(panelFormulario, BorderLayout.NORTH);
        
        // Lista de vehículos (parte central)
        modeloLista = new DefaultListModel<>();
        listaVehiculos = new JList<>(modeloLista);
        listaVehiculos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaVehiculos.setBorder(BorderFactory.createTitledBorder("Lista de vehículos (por matrícula)"));
        
        // Listener para mostrar detalles al seleccionar un vehículo
        listaVehiculos.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    mostrarDetallesVehiculo();
                }
            }
        });
        
        JScrollPane panelScroll = new JScrollPane(listaVehiculos);
        add(panelScroll, BorderLayout.CENTER);
        
        // Panel de botones (parte inferior)
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        btnVaciar = new JButton("Vaciar Campos");
        btnVaciar.addActionListener(this::vaciarCampos);
        panelBotones.add(btnVaciar);
        
        btnAgregar = new JButton("Agregar Vehículo");
        btnAgregar.addActionListener(this::agregarVehiculo);
        panelBotones.add(btnAgregar);
        
        btnEliminar = new JButton("Eliminar Vehículo");
        btnEliminar.addActionListener(this::eliminarVehiculo);
        panelBotones.add(btnEliminar);
        
        add(panelBotones, BorderLayout.SOUTH);
        
        // Aplicar tema visual
        ConfiguracionTema.getInstancia().aplicarTema(this);
    }

    /**
     * Carga los vehículos del sistema en la lista visual.
     */
    private void cargarVehiculos() {
        modeloLista.clear();
        for (Vehiculo vehiculo : sistema.getVehiculos()) {
            modeloLista.addElement(vehiculo.toString()); 
        }
    }

    /**
     * Muestra los detalles del vehículo seleccionado en una ventana emergente.
     */
    private void mostrarDetallesVehiculo() {
        int indiceSeleccionado = listaVehiculos.getSelectedIndex();
        
        if (indiceSeleccionado >= 0) {
            Vehiculo vehiculo = sistema.getVehiculos().get(indiceSeleccionado);
            
            // Crear ventana emergente modal
            JDialog dialog = new JDialog(this, "Detalles del Vehículo", true);
            dialog.setLayout(new GridLayout(5, 3, 10, 10));
            dialog.setSize(400, 300);
            
            // Mostrar todos los datos del vehículo
            dialog.add(new JLabel("Matrícula:"));
            dialog.add(new JLabel(vehiculo.getMatricula()));
            
            dialog.add(new JLabel("Marca:"));
            dialog.add(new JLabel(vehiculo.getMarca()));
            
            dialog.add(new JLabel("Modelo:"));
            dialog.add(new JLabel(vehiculo.getModelo()));
            
            dialog.add(new JLabel("Estado:"));
            dialog.add(new JLabel(vehiculo.getEstado()));
            
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        }
    }

    /**
     * Vacía todos los campos del formulario y deselecciona la lista.
     * @param evento Evento de acción del botón
     */
    private void vaciarCampos(ActionEvent evento) {
        txtMatricula.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        txtEstado.setText("");
        listaVehiculos.clearSelection();
    }

    /**
     * Agrega un nuevo vehículo al sistema después de validar los datos.
     * @param evento Evento de acción del botón
     */
    private void agregarVehiculo(ActionEvent evento) {
        // Obtener y limpiar datos del formulario
        String matricula = txtMatricula.getText().trim();
        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String estado = txtEstado.getText().trim();
        
        // Validar campo obligatorio (matrícula)
        if (matricula.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "La matrícula es obligatoria", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validar que no exista un vehículo con la misma matrícula
        if (sistema.existeVehiculo(matricula)) {
            JOptionPane.showMessageDialog(this, 
                "La matrícula " + matricula + " ya está registrada", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Crear y agregar el nuevo vehículo con valores por defecto si es necesario
        Vehiculo nuevoVehiculo = new Vehiculo(
            matricula, 
            marca.isEmpty() ? "Sin especificar" : marca,
            modelo.isEmpty() ? "Sin especificar" : modelo,
            estado.isEmpty() ? "Disponible" : estado
        );
        
        sistema.agregarVehiculo(nuevoVehiculo);
        
        // Actualizar interfaz
        cargarVehiculos();
        vaciarCampos(null);
        
        JOptionPane.showMessageDialog(this, "Vehículo agregado exitosamente");
    }

    /**
     * Elimina el vehículo seleccionado después de validar condiciones.
     * @param evento Evento de acción del botón
     */
    private void eliminarVehiculo(ActionEvent evento) {
        int indiceSeleccionado = listaVehiculos.getSelectedIndex();
        
        // Validar selección
        if (indiceSeleccionado < 0) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un vehículo para eliminar", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Vehiculo vehiculo = sistema.getVehiculos().get(indiceSeleccionado);
        
        // Validar que el vehículo no tenga contrato activo
        if (sistema.vehiculoTieneContrato(vehiculo)) {
            JOptionPane.showMessageDialog(this,
                "No se puede eliminar: El vehículo tiene un contrato activo",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validar que el vehículo no esté en el parking
        if (sistema.estaEnParking(vehiculo.getMatricula())) {
            JOptionPane.showMessageDialog(this, 
                "No se puede eliminar: El vehículo está actualmente en el parking", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Pedir confirmación
        int confirmacion = JOptionPane.showConfirmDialog(
            this, 
            "¿Está seguro de eliminar el vehículo con matrícula " + vehiculo.getMatricula() + "?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            // Eliminar vehículo y actualizar interfaz
            sistema.eliminarVehiculo(vehiculo);
            cargarVehiculos();
            vaciarCampos(null);
            
            JOptionPane.showMessageDialog(this, "Vehículo eliminado exitosamente");
        }
    }
}

/*

Explicación General de la Clase GestionVehiculo
La clase GestionVehiculo es una ventana de interfaz gráfica para administrar los vehículos del sistema de parking.

Funcionalidades clave:
Visualización de vehículos: Muestra una lista de todos los vehículos registrados
Detalles de vehículos: Permite ver toda la información de un vehículo seleccionado
Agregar vehículos: Formulario para registrar nuevos vehículos con validación de datos
Eliminar vehículos: Elimina vehículos existentes con validación de condiciones previas
Limpieza de formulario: Botón para vaciar todos los campos

Validaciones implementadas:
Matrícula obligatoria (campo requerido)
Unicidad de la matrícula (no puede haber dos vehículos con la misma matrícula)
Validación antes de eliminar:
No puede tener contrato activo
No puede estar actualmente en el parking
Confirmación antes de eliminar un vehículo

Características de la interfaz:
Organización clara: Formulario en la parte superior, lista en el centro, botones abajo
Selección única: Solo se puede seleccionar un vehículo a la vez
Ventana modal: Muestra los detalles del vehículo en una ventana emergente
Mensajes de feedback: Informa al usuario sobre operaciones exitosas o errores
Tema visual: Se integra con el sistema de temas claro/oscuro

Integración con el sistema:
Recibe una referencia al sistema principal (Sistema) para acceder a los datos
Llama a métodos del sistema para agregar/eliminar vehículos
Verifica condiciones del sistema antes de operaciones críticas
Sincroniza con el sistema de temas visuales (ConfiguracionTema)
Esta ventana sigue el patrón de diseño MVC (Modelo-Vista-Controlador), donde:
Modelo: La clase Sistema que contiene los datos de vehículos
Vista: Esta clase que muestra la interfaz gráfica
Controlador: Los métodos que manejan las acciones del usuario y validan los datos
*/