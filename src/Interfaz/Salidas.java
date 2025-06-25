package Interfaz;

/**
 *
 * @author Natalia Peña 
 */

import Dominio.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Clase que representa la interfaz gráfica para registrar salidas de vehículos del parking.
 * Extiende JFrame para crear una ventana independiente.
 */
public class Salidas extends JFrame {
    // Referencia al sistema principal para acceder a los datos
    private final Sistema sistema;
    
    // Componentes de la interfaz
    private JTextField txtFecha;
    private JTextField txtHora;
    private JTextArea txtComentario;
    private JButton btnRegistrarSalida;
    private JList<Entrada> lstEntradas; 
    private JList<Empleado> lstEmpleados;  
    private DefaultListModel<Entrada> modeloEntradas;  
    private DefaultListModel<Empleado> modeloEmpleados;  
    private JLabel lblTiempoEstadia;
    private JLabel lblEstadoContrato;
    private JLabel lblCliente;

    /**
     * Constructor de la clase.
     * @param sistema Referencia al sistema principal para acceder a los datos.
     */
    public Salidas(Sistema sistema) {
        // Configuración del tema visual
        ConfiguracionTema.getInstancia().registrarVentana(this);
        ConfiguracionTema.getInstancia().aplicarTema(this);
        
        this.sistema = sistema;
        inicializarComponentes(); // Inicializa todos los componentes de la interfaz
        cargarDatos(); // Carga los datos iniciales
    }

    /**
     * Método que inicializa todos los componentes de la interfaz gráfica.
     */
    private void inicializarComponentes() {
        setTitle("Registro de Salidas de Vehículos");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Panel principal con dos paneles divididos verticalmente
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300); // Posición del divisor
        splitPane.setResizeWeight(0.5); // Proporción de espacio asignado
        
        // Panel superior: Entradas sin salida
        JPanel panelEntradas = new JPanel(new BorderLayout());
        panelEntradas.setBorder(BorderFactory.createTitledBorder("Vehículos en parking (seleccione uno para registrar salida)"));
        
        // Modelo y lista para mostrar entradas activas (vehículos en el parking)
        modeloEntradas = new DefaultListModel<>();
        lstEntradas = new JList<>(modeloEntradas);
        lstEntradas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstEntradas.setCellRenderer(new EntradaListCellRenderer()); // Renderer personalizado
        lstEntradas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarInfoVehiculo(); // Actualiza la info cuando se selecciona una entrada
            }
        });
        
        JScrollPane scrollEntradas = new JScrollPane(lstEntradas);
        panelEntradas.add(scrollEntradas, BorderLayout.CENTER);
        
        // Panel de información del vehículo seleccionado
        JPanel panelInfo = new JPanel(new GridLayout(1, 3));
        lblTiempoEstadia = new JLabel("Tiempo en parking: -");
        lblEstadoContrato = new JLabel("Contrato: -");
        lblCliente = new JLabel("Cliente: -");
        panelInfo.add(lblTiempoEstadia);
        panelInfo.add(lblEstadoContrato);
        panelInfo.add(lblCliente);
        panelEntradas.add(panelInfo, BorderLayout.SOUTH);
        
        // Panel medio: Empleados disponibles
        JPanel panelEmpleados = new JPanel(new BorderLayout());
        panelEmpleados.setBorder(BorderFactory.createTitledBorder("Empleados disponibles (seleccione quien registra la salida)"));
        
        // Modelo y lista para mostrar empleados
        modeloEmpleados = new DefaultListModel<>();
        lstEmpleados = new JList<>(modeloEmpleados);
        lstEmpleados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstEmpleados.setCellRenderer(new EmpleadoListCellRenderer()); // Renderer personalizado
        
        JScrollPane scrollEmpleados = new JScrollPane(lstEmpleados);
        panelEmpleados.add(scrollEmpleados, BorderLayout.CENTER);
        
        // Panel inferior: Datos de salida
        JPanel panelDatos = new JPanel(new GridBagLayout());
        panelDatos.setBorder(BorderFactory.createTitledBorder("Datos de la Salida"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campos para fecha y hora de salida
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panelDatos.add(new JLabel("Fecha salida (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        txtFecha = new JTextField(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        panelDatos.add(txtFecha, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelDatos.add(new JLabel("Hora salida (HH:MM):"), gbc);
        gbc.gridx = 1;
        txtHora = new JTextField(new SimpleDateFormat("HH:mm").format(new Date()));
        panelDatos.add(txtHora, gbc);
        
        // Campo para comentarios
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        panelDatos.add(new JLabel("Comentarios sobre el estado:"), gbc);
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        txtComentario = new JTextArea(3, 40);
        txtComentario.setLineWrap(true);
        panelDatos.add(new JScrollPane(txtComentario), gbc);
        
        // Botón para registrar la salida
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.gridwidth = 3;
        btnRegistrarSalida = new JButton("Registrar Salida");
        btnRegistrarSalida.addActionListener(this::registrarSalida);
        panelDatos.add(btnRegistrarSalida, gbc);
        
        // Configuración final de los paneles divididos
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelEntradas, BorderLayout.CENTER);
        
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(panelEmpleados, BorderLayout.CENTER);
        panelInferior.add(panelDatos, BorderLayout.SOUTH);
        
        splitPane.setTopComponent(panelSuperior);
        splitPane.setBottomComponent(panelInferior);
        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Carga los datos iniciales en las listas de la interfaz.
     */
    private void cargarDatos() {
        System.out.println("[DEBUG] Cargando datos para ventana de Salidas");
        modeloEntradas.clear();
        modeloEmpleados.clear();
        
        // Obtiene las entradas activas (vehículos en el parking sin salida registrada)
        List<Entrada> entradasActivas = sistema.getEntradasSinSalida();
        System.out.println("[DEBUG] Número de entradas activas encontradas: " + entradasActivas.size());
        
        if (entradasActivas.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No hay vehículos registrados actualmente en el parking",
                "Información", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Entrada entrada : entradasActivas) {
                modeloEntradas.addElement(entrada);
            }
        }
        
        // Carga todos los empleados disponibles
        for (Empleado empleado : sistema.getEmpleados()) {
            modeloEmpleados.addElement(empleado);
        }
    }

    /**
     * Renderer personalizado para mostrar las entradas en la lista.
     */
    private static class EntradaListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                     boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Entrada) {
                Entrada entrada = (Entrada) value;
                Vehiculo vehiculo = entrada.getVehiculo();
                Cliente cliente = vehiculo.getCliente();
                
                // Formatea la información a mostrar para cada entrada
                String texto = String.format("%s - %s %s - Entrada: %s %s - Cliente: %s - Contrato: %s",
                    vehiculo.getMatricula(),
                    vehiculo.getMarca(),
                    vehiculo.getModelo(),
                    new SimpleDateFormat("dd/MM/yyyy").format(entrada.getFecha()),
                    entrada.getHora(),
                    (cliente != null) ? cliente.getNombre() : "Sin cliente",
                    entrada.getTieneContrato() ? "SÍ" : "NO");
                
                return super.getListCellRendererComponent(list, texto, index, isSelected, cellHasFocus);
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
    
    /**
     * Renderer personalizado para mostrar los empleados en la lista.
     */
    private static class EmpleadoListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                     boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Empleado) {
                Empleado empleado = (Empleado) value;
                
                // Formatea la información a mostrar para cada empleado
                String texto = String.format("%d - %s - %s",
                    empleado.getNumeroEmpleado(),
                    empleado.getNombre(),
                    empleado.getCedula());                                  
                
                return super.getListCellRendererComponent(list, texto, index, isSelected, cellHasFocus);
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }

    /**
     * Actualiza la información del vehículo seleccionado en los labels correspondientes.
     */
    private void actualizarInfoVehiculo() {
        Entrada entrada = lstEntradas.getSelectedValue();
        if (entrada != null) {
            // Calcula el tiempo de estadía usando la fecha y hora actuales
            String tiempo = calcularTiempoEstadia(entrada, 
                new SimpleDateFormat("dd/MM/yyyy").format(new Date()),
                new SimpleDateFormat("HH:mm").format(new Date()));
            
            Vehiculo vehiculo = entrada.getVehiculo();
            Cliente cliente = vehiculo.getCliente();
            String clienteInfo = (cliente != null) ? cliente.getNombre() : "Sin cliente asociado";
            
            // Actualiza los labels con la información
            lblTiempoEstadia.setText("Tiempo en parking: " + tiempo);
            lblEstadoContrato.setText("Contrato: " + (entrada.getTieneContrato() ? "SÍ" : "NO"));
            lblCliente.setText("Cliente: " + clienteInfo);
        }
    }

    /**
     * Valida los datos ingresados antes de registrar la salida.
     * @return true si los datos son válidos, false en caso contrario.
     */
    private boolean validarDatos() {
        // Validación de vehículo seleccionado
        if (lstEntradas.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, 
                "Debe seleccionar un vehículo de la lista superior", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validación de empleado seleccionado
        if (lstEmpleados.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, 
                "Debe seleccionar un empleado de la lista inferior", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validación del formato de hora (HH:MM)
        if (!txtHora.getText().trim().matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            JOptionPane.showMessageDialog(this, 
                "Formato de hora inválido. Debe ser HH:MM en formato 24 horas (ej. 14:30).", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validación del formato de fecha (dd/MM/yyyy)
        try {
            new SimpleDateFormat("dd/MM/yyyy").parse(txtFecha.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Formato de fecha inválido. Debe ser dd/MM/yyyy (ej. 25/12/2023).", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validación de que la fecha/hora de salida sea posterior a la de entrada
        try {
            Entrada entrada = lstEntradas.getSelectedValue();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String fechaEntradaStr = new SimpleDateFormat("dd/MM/yyyy").format(entrada.getFecha());
            Date fechaHoraEntrada = sdf.parse(fechaEntradaStr + " " + entrada.getHora());
            Date fechaHoraSalida = sdf.parse(txtFecha.getText().trim() + " " + txtHora.getText().trim());
            
            if (!fechaHoraSalida.after(fechaHoraEntrada)) {
                JOptionPane.showMessageDialog(this, 
                    "La fecha/hora de salida debe ser posterior a la de entrada.\n" +
                    "Entrada registrada: " + sdf.format(fechaHoraEntrada), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al comparar fechas: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    /**
     * Calcula el tiempo de estadía de un vehículo en el parking.
     * @param entrada La entrada del vehículo.
     * @param fechaSalida Fecha de salida en formato dd/MM/yyyy.
     * @param horaSalida Hora de salida en formato HH:MM.
     * @return String con el tiempo formateado (días, horas y minutos).
     */
    private String calcularTiempoEstadia(Entrada entrada, String fechaSalida, String horaSalida) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            
            // Obtiene las fechas como objetos Date
            String fechaEntrada = new SimpleDateFormat("dd/MM/yyyy").format(entrada.getFecha());
            Date entradaDate = sdf.parse(fechaEntrada + " " + entrada.getHora());
            Date salidaDate = sdf.parse(fechaSalida + " " + horaSalida);
            
            // Calcula la diferencia en milisegundos
            long diffMillis = salidaDate.getTime() - entradaDate.getTime();
            
            // Convierte la diferencia a días, horas y minutos
            long dias = diffMillis / (24 * 60 * 60 * 1000);
            long horas = (diffMillis % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
            long minutos = (diffMillis % (60 * 60 * 1000)) / (60 * 1000);
            
            // Formatea el resultado según la duración
            if (dias > 0) {
                return String.format("%d días, %d horas y %02d minutos", dias, horas, minutos);
            } else {
                return String.format("%d horas y %02d minutos", horas, minutos);
            }
        } catch (Exception e) {
            return "Error en cálculo";
        }
    }

    /**
     * Método que se ejecuta al presionar el botón "Registrar Salida".
     * @param evento Evento de acción del botón.
     */
    private void registrarSalida(ActionEvent evento) {
        System.out.println("[DEBUG] Iniciando registro de salida");
        
        // Valida los datos antes de continuar
        if (!validarDatos()) {
            System.out.println("[DEBUG] Validación de datos fallida");
            return;
        }
        
        // Obtiene los datos del formulario
        Entrada entrada = lstEntradas.getSelectedValue();
        Empleado empleado = lstEmpleados.getSelectedValue();
        String fecha = txtFecha.getText().trim();
        String hora = txtHora.getText().trim();
        String comentario = txtComentario.getText().trim();
        String tiempoEstadia = calcularTiempoEstadia(entrada, fecha, hora);
        
        // Prepara mensaje de confirmación con los datos
        String mensaje = "<html><b>Confirmar registro de salida</b><br><br>" +
            "<table border='0' cellspacing='5'>" +
            "<tr><td><b>Vehículo:</b></td><td>" + entrada.getVehiculo().getMatricula() + "</td></tr>" +
            "<tr><td><b>Cliente:</b></td><td>" + lblCliente.getText().replace("Cliente: ", "") + "</td></tr>" +
            "<tr><td><b>Tiempo en parking:</b></td><td>" + tiempoEstadia + "</td></tr>" +
            "<tr><td><b>Contrato activo:</b></td><td>" + (entrada.getTieneContrato() ? "SÍ" : "NO") + "</td></tr>" +
            "<tr><td><b>Empleado:</b></td><td>" + empleado.getNombre() + "</td></tr>" +
            "<tr><td><b>Fecha/hora salida:</b></td><td>" + fecha + " " + hora + "</td></tr>" +
            "</table></html>";
        
        // Muestra diálogo de confirmación
        int confirmacion = JOptionPane.showConfirmDialog(
            this, mensaje, "Confirmar Salida", 
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion != JOptionPane.YES_OPTION) {
            System.out.println("[DEBUG] Usuario canceló el registro");
            return;
        }
        
        try {
            System.out.println("[DEBUG] Creando objeto Salida");
            
            // Crea el objeto Salida con los datos del formulario
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date fechaHoraSalida = sdf.parse(fecha + " " + hora);
            
            Salida salida = new Salida(
                entrada,
                empleado,
                fechaHoraSalida,
                hora,
                comentario,
                entrada.getTieneContrato(),
                tiempoEstadia
            );
            
            System.out.println("[DEBUG] Registrando salida en el sistema");
            sistema.registrarSalida(salida); // Registra la salida en el sistema
            System.out.println("[DEBUG] Salida registrada exitosamente");
            
            // Muestra mensaje de éxito
            JOptionPane.showMessageDialog(this, 
                "<html><b>Salida registrada exitosamente</b><br><br>" +
                "<table border='0' cellspacing='5'>" +
                "<tr><td><b>Vehículo:</b></td><td>" + entrada.getVehiculo().getMatricula() + "</td></tr>" +
                "<tr><td><b>Tiempo total:</b></td><td>" + tiempoEstadia + "</td></tr>" +
                "<tr><td><b>Registrado por:</b></td><td>" + empleado.getNombre() + "</td></tr>" +
                "</table></html>", 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
            cargarDatos(); // Recarga los datos para actualizar la interfaz
        } catch (Exception ex) {
            System.err.println("[ERROR] Al registrar salida: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al registrar salida: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


/*

Explicación General
La clase Salidas es una ventana de interfaz gráfica que permite registrar la salida de vehículos de un parking.

Estructura de la Interfaz:
Divide la pantalla en dos secciones principales con un JSplitPane
La parte superior muestra los vehículos que están actualmente en el parking (entradas sin salida registrada)
La parte inferior muestra los empleados disponibles y el formulario para registrar la salida

Funcionalidades Principales:
Visualización de vehículos en el parking con información detallada
Selección de empleado que registra la salida
Registro de fecha, hora y comentarios sobre el estado del vehículo
Cálculo automático del tiempo de estadía
Validación de datos antes del registro
Confirmación visual antes de registrar la salida

Validaciones:
Verifica que se haya seleccionado un vehículo y un empleado
Valida el formato de fecha y hora
Comprueba que la fecha/hora de salida sea posterior a la de entrada

Integración con el Sistema:
Utiliza la referencia al objeto Sistema para acceder a los datos y registrar las salidas
Actualiza automáticamente la interfaz después de cada operación

*/