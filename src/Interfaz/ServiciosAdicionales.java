//Nayri Batista (328201) / Natalia Peña (242466)

package Interfaz;

import Dominio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Clase que representa la interfaz gráfica para gestionar servicios adicionales en el parking.
 * Permite registrar, visualizar y eliminar servicios como lavado, cambio de ruedas, etc.
 */
public class ServiciosAdicionales extends JFrame {
    // Referencia al sistema principal para acceder a los datos
    private final Sistema sistema;
    
    // Componentes de la interfaz
    private JList<Vehiculo> lstVehiculos;        // Lista de vehículos disponibles
    private JList<Empleado> lstEmpleados;       // Lista de empleados disponibles
    private JComboBox<String> cmbTipoServicio;  // Combo para seleccionar tipo de servicio
    private JTextField txtCosto;                // Campo para ingresar el costo
    private JTextField txtFecha;                // Campo para la fecha del servicio
    private JTextField txtHora;                 // Campo para la hora del servicio
    private JButton btnRegistrar;               // Botón para registrar servicio
    private JList<String> lstServicios;         // Lista de servicios registrados
    private JButton btnEliminar;                // Botón para eliminar servicio seleccionado

    /**
     * Constructor de la clase.
     * @param sistema Referencia al sistema principal para acceder a los datos.
     */
    public ServiciosAdicionales(Sistema sistema) {
        // Configuración del tema visual
        ConfiguracionTema.getInstancia().registrarVentana(this);
        ConfiguracionTema.getInstancia().aplicarTema(this);
        
        this.sistema = sistema;
        inicializarComponentes(); // Inicializa todos los componentes de la interfaz
        cargarDatos();           // Carga los datos iniciales
    }

    /**
     * Método que inicializa todos los componentes de la interfaz gráfica.
     */
    private void inicializarComponentes() {
        setTitle("Servicios Adicionales");
        setSize(900, 600);
        setLayout(new BorderLayout(10, 10));
        
        // Panel superior para datos del servicio
        JPanel panelSuperior = new JPanel(new GridLayout(4, 2, 10, 10));
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Datos del Servicio"));
        
        // Tipo de servicio (combobox con los tipos definidos en el sistema)
        panelSuperior.add(new JLabel("Tipo de servicio:"));
        cmbTipoServicio = new JComboBox<>(sistema.TIPOS_SERVICIOS);
        panelSuperior.add(cmbTipoServicio);
        
        // Fecha del servicio (se inicializa con la fecha actual)
        panelSuperior.add(new JLabel("Fecha (dd/mm/aaaa):"));
        txtFecha = new JTextField(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        panelSuperior.add(txtFecha);
        
        // Hora del servicio (se inicializa con la hora actual)
        panelSuperior.add(new JLabel("Hora (HH:MM):"));
        txtHora = new JTextField(new SimpleDateFormat("HH:mm").format(new Date()));
        panelSuperior.add(txtHora);
        
        // Costo del servicio
        panelSuperior.add(new JLabel("Costo ($):"));
        txtCosto = new JTextField();
        panelSuperior.add(txtCosto);
        
        // Panel central con listas y botón
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        
        // Panel para las listas de vehículos y empleados
        JPanel panelListas = new JPanel(new GridLayout(1, 2, 10, 10));
        
        // Lista de vehículos
        JPanel panelVehiculos = new JPanel(new BorderLayout());
        panelVehiculos.setBorder(BorderFactory.createTitledBorder("Vehículos"));
        lstVehiculos = new JList<>();
        panelVehiculos.add(new JScrollPane(lstVehiculos), BorderLayout.CENTER);
        panelListas.add(panelVehiculos);
        
        // Lista de empleados
        JPanel panelEmpleados = new JPanel(new BorderLayout());
        panelEmpleados.setBorder(BorderFactory.createTitledBorder("Empleados"));
        lstEmpleados = new JList<>();
        panelEmpleados.add(new JScrollPane(lstEmpleados), BorderLayout.CENTER);
        panelListas.add(panelEmpleados);
        
        panelCentral.add(panelListas, BorderLayout.CENTER);
        
        // Botón para registrar servicio
        btnRegistrar = new JButton("Registrar Servicio");
        btnRegistrar.addActionListener(this::registrarServicio);
        panelCentral.add(btnRegistrar, BorderLayout.SOUTH);
        
        // Panel inferior con servicios registrados
        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));
        panelInferior.setBorder(BorderFactory.createTitledBorder("Servicios Registrados"));
        
        // Lista de servicios registrados
        lstServicios = new JList<>();
        lstServicios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panelInferior.add(new JScrollPane(lstServicios), BorderLayout.CENTER);
        
        // Botón para eliminar servicio seleccionado
        btnEliminar = new JButton("Eliminar Servicio Seleccionado");
        btnEliminar.setEnabled(false); // Inicialmente deshabilitado
        btnEliminar.addActionListener(this::eliminarServicio);
        
        // Panel para el botón (para mejor distribución)
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBoton.add(btnEliminar);
        panelInferior.add(panelBoton, BorderLayout.SOUTH);
        
        // Listener para habilitar/deshabilitar el botón de eliminar
        lstServicios.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean haySeleccion = lstServicios.getSelectedIndex() != -1;
                btnEliminar.setEnabled(haySeleccion);
                if (haySeleccion) {
                    mostrarDetallesEnDialogo(); // Muestra detalles al seleccionar un servicio
                }
            }
        });
        
        // Organización general de los paneles en la ventana
        add(panelSuperior, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
        
        cargarServicios(); // Carga los servicios ya registrados
    }

    /**
     * Carga los datos iniciales en las listas de vehículos y empleados.
     */
    private void cargarDatos() {
        // Cargar vehículos disponibles
        DefaultListModel<Vehiculo> modeloVehiculos = new DefaultListModel<>();
        sistema.getVehiculos().forEach(modeloVehiculos::addElement);
        lstVehiculos.setModel(modeloVehiculos);
        
        // Cargar empleados disponibles
        DefaultListModel<Empleado> modeloEmpleados = new DefaultListModel<>();
        sistema.getEmpleados().forEach(modeloEmpleados::addElement);
        lstEmpleados.setModel(modeloEmpleados);
    }

    /**
     * Carga los servicios registrados en la lista correspondiente.
     */
    private void cargarServicios() {
        DefaultListModel<String> modeloServicios = new DefaultListModel<>();
        sistema.getServicios().forEach(servicio -> 
            modeloServicios.addElement(servicio.toString()));
        lstServicios.setModel(modeloServicios);
    }

    /**
     * Registra un nuevo servicio adicional.
     * @param evento Evento de acción del botón.
     */
    private void registrarServicio(ActionEvent evento) {
        // Validación de selección de vehículo y empleado
        if (lstVehiculos.getSelectedValue() == null || lstEmpleados.getSelectedValue() == null) {
            JOptionPane.showMessageDialog(this, 
                "Debe seleccionar un vehículo y un empleado", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validación del costo (debe ser un número válido)
        double costo;
        try {
            costo = Double.parseDouble(txtCosto.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Costo inválido. Ingrese un número válido", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validación de la fecha (formato correcto)
        Date fecha;
        try {
            fecha = new SimpleDateFormat("dd/MM/yyyy").parse(txtFecha.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Fecha inválida. Formato correcto: dd/mm/aaaa", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validación de la hora (formato HH:MM en 24hs)
        if (!txtHora.getText().trim().matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            JOptionPane.showMessageDialog(this, 
                "Hora inválida. Formato correcto: HH:MM (24hs)", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Creación del objeto Servicio con los datos del formulario
        Servicio servicio = new Servicio(
            (String) cmbTipoServicio.getSelectedItem(),
            lstVehiculos.getSelectedValue(),
            lstEmpleados.getSelectedValue(),
            fecha,
            txtHora.getText().trim(),
            costo
        );
        
        // Registro del servicio en el sistema
        sistema.registrarServicio(servicio);
        
        // Actualización de la interfaz
        cargarServicios();
        JOptionPane.showMessageDialog(this, 
            "Servicio registrado exitosamente", 
            "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra los detalles del servicio seleccionado en un diálogo emergente.
     */
    private void mostrarDetallesEnDialogo() {
        int selectedIndex = lstServicios.getSelectedIndex();
        if (selectedIndex != -1) {
            Servicio servicio = sistema.getServicios().get(selectedIndex);
            
            // Formateo de los detalles del servicio en HTML para mejor presentación
            String detalles = String.format(
                "<html><h2>Detalles del Servicio</h2>" +
                "<p><b>Tipo:</b> %s</p>" +
                "<h3>Vehículo</h3>" +
                "<p><b>Matrícula:</b> %s<br>" +
                "<b>Marca:</b> %s<br>" +
                "<b>Modelo:</b> %s</p>" +
                "<h3>Empleado</h3>" +
                "<p><b>Nombre:</b> %s<br>" +
                "<b>Cédula:</b> %s</p>" +
                "<h3>Información</h3>" +
                "<p><b>Fecha:</b> %s<br>" +
                "<b>Hora:</b> %s<br>" +
                "<b>Costo:</b> $%.2f</p></html>",
                servicio.getTipo(),
                servicio.getVehiculo().getMatricula(),
                servicio.getVehiculo().getMarca(),
                servicio.getVehiculo().getModelo(),
                servicio.getEmpleado().getNombre(),
                servicio.getEmpleado().getCedula(),
                new SimpleDateFormat("dd/MM/yyyy").format(servicio.getFecha()),
                servicio.getHora(),
                servicio.getCosto()
            );
            
            // Mostrar diálogo con los detalles
            JOptionPane.showMessageDialog(
                this,
                detalles,
                "Detalles del Servicio",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /**
     * Elimina el servicio seleccionado de la lista.
     * @param evento Evento de acción del botón.
     */
    private void eliminarServicio(ActionEvent evento) {
        int selectedIndex = lstServicios.getSelectedIndex();
        
        // Validación de selección
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, 
                "Debe seleccionar un servicio de la lista", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        DefaultListModel<String> modelo = (DefaultListModel<String>) lstServicios.getModel();
        String servicioStr = modelo.getElementAt(selectedIndex);
        
        // Confirmación de eliminación
        int confirmacion = JOptionPane.showConfirmDialog(
            this, 
            "¿Está seguro que desea eliminar este servicio?\n" + servicioStr, 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            // Eliminación del servicio en el sistema
            if (sistema.eliminarServicio(selectedIndex)) {
                modelo.remove(selectedIndex); // Actualización del modelo
                JOptionPane.showMessageDialog(this, 
                    "Servicio eliminado correctamente", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No se pudo eliminar el servicio", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

/*
Explicación General
La clase ServiciosAdicionales es una ventana de interfaz gráfica que permite gestionar servicios adicionales para vehículos en el parking.

Estructura de la Interfaz:
Panel superior: Formulario para ingresar datos del servicio (tipo, fecha, hora, costo)
Panel central: Listas de vehículos y empleados disponibles, más botón de registro
Panel inferior: Lista de servicios registrados con botón para eliminarlos
Funcionalidades Principales:
Registro de nuevos servicios adicionales (lavado, cambio de ruedas, etc.)
Visualización detallada de servicios existentes
Eliminación de servicios registrados
Validación de datos antes del registro
Validaciones:
Verifica que se haya seleccionado un vehículo y un empleado
Valida que el costo sea un número válido
Comprueba formatos correctos para fecha y hora
Confirma antes de eliminar un servicio
Integración con el Sistema:
Utiliza la referencia al objeto Sistema para acceder a los datos
Actualiza automáticamente las listas después de cada operación
Muestra mensajes de confirmación y éxito/error
Experiencia de Usuario:
Muestra detalles completos al seleccionar un servicio
Formatea la información para mejor legibilidad (usando HTML en los diálogos)
Habilita/deshabilita botones según el contexto
*/