package Interfaz;
/**
 *
 * @author Natalia Peña 
 */

import Dominio.Reporte;
import Dominio.ConfiguracionTema;
import Dominio.Sistema;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Clase que representa la ventana principal del sistema de parking.
 * Sirve como punto de entrada y menú principal para acceder a todas las funcionalidades.
 */
public class VentanaInicio extends JFrame {
    private Sistema sistema; // Referencia al sistema principal
    private JButton btnCambiarModo; // Botón para cambiar entre modo claro/oscuro
    
    /**
     * Constructor de la ventana principal.
     * @param sistema Referencia al sistema principal que contiene la lógica y datos.
     */
    public VentanaInicio(Sistema sistema) {
        // Configuración inicial del tema visual
        ConfiguracionTema.getInstancia().registrarVentana(this);
        ConfiguracionTema.getInstancia().aplicarTema(this);
        
        this.sistema = sistema;
        initComponents(); // Inicializa los componentes de la interfaz
        
        // Configuración adicional del tema para asegurar consistencia
        ConfiguracionTema.getInstancia().registrarVentana(this);
        if (this.getJMenuBar() != null) {
            ConfiguracionTema.getInstancia().registrarVentana(this.getJMenuBar());
        }
        ConfiguracionTema.getInstancia().aplicarTema(this);
    }

    /**
     * Método que inicializa y configura todos los componentes visuales.
     */
    private void initComponents() {
        setTitle("Sistema de Parking");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        
        // Panel principal con BorderLayout
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        
        // Configuración de la barra de menú principal
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createEmptyBorder(10, 150, 10, 10));
        
        // Menú de Gestión
        JMenu menuGestion = new JMenu("Gestión");
        menuGestion.setFont(new Font("Arial", Font.BOLD, 14));
        addMenuItem(menuGestion, "Gestión de Clientes", () -> new GestionClientes(sistema).setVisible(true));
        addMenuItem(menuGestion, "Gestión de Vehículos", () -> new GestionVehiculo(sistema).setVisible(true));
        addMenuItem(menuGestion, "Gestión de Empleados", () -> new GestionEmpleados(sistema).setVisible(true));
        addMenuItem(menuGestion, "Gestión de Contratos", () -> new GestionContratos(sistema).setVisible(true));
        menuBar.add(menuGestion);
        
        // Menú de Movimientos
        JMenu menuMovimientos = new JMenu("Movimientos");
        menuMovimientos.setFont(new Font("Arial", Font.BOLD, 14));
        addMenuItem(menuMovimientos, "Entradas", () -> new Entradas(sistema).setVisible(true));
        addMenuItem(menuMovimientos, "Salidas", () -> new Salidas(sistema).setVisible(true));
        addMenuItem(menuMovimientos, "Servicios Adicionales", () -> new ServiciosAdicionales(sistema).setVisible(true));
        menuBar.add(menuMovimientos);
        
        // Menú Varios
        JMenu menuVarios = new JMenu("Varios");
        menuVarios.setFont(new Font("Arial", Font.BOLD, 14));
        addMenuItem(menuVarios, "Reportes", () -> new Reportes(sistema).setVisible(true));
        addMenuItem(menuVarios, "Recuperación de datos", this::recuperarDatos);
        addMenuItem(menuVarios, "Grabación de datos", this::grabarDatos);
        addMenuItem(menuVarios, "Mini Juego", () -> new MiniJuego().setVisible(true));
        addMenuItem(menuVarios, "Información de Autores", () -> new InformacionAutor().setVisible(true));
        menuBar.add(menuVarios);
        
        // Menú Terminar
        JMenu menuTerminar = new JMenu("Terminar");
        menuTerminar.setFont(new Font("Arial", Font.BOLD, 14));
        addMenuItem(menuTerminar, "Salir", this::salir);
        menuBar.add(menuTerminar);
        
        setJMenuBar(menuBar); // Establece la barra de menú en la ventana
        
        // Panel de bienvenida central
        JPanel panelBienvenida = new JPanel(new GridBagLayout());
        panelBienvenida.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Mensaje de bienvenida con formato HTML para mejor presentación
        JLabel lblBienvenida = new JLabel("<html><div style='text-align: center;'>"
                + "<h1>Bienvenido al Sistema de Parking</h1>"
                + "<p style='font-size: 16px; margin-top: 20px;'>Seleccione una opción del menú superior</p>"
                + "</div></html>");
        lblBienvenida.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Configuración de constraints para posicionamiento
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.CENTER;
        panelBienvenida.add(lblBienvenida, gbc);
        panelPrincipal.add(panelBienvenida, BorderLayout.CENTER);
        
        // Panel inferior con botón para cambiar tema
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Botón para cambiar entre modo claro y oscuro
        btnCambiarModo = new JButton();
        actualizarTextoBotonTema(); // Establece el texto inicial del botón
        btnCambiarModo.addActionListener(e -> {
            ConfiguracionTema.getInstancia().cambiarModo();
            actualizarTextoBotonTema();
            ConfiguracionTema.getInstancia().aplicarTema(this);
        });
        panelInferior.add(btnCambiarModo);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);
        
        add(panelPrincipal); // Añade el panel principal a la ventana
        ConfiguracionTema.getInstancia().aplicarTema(this); // Aplica el tema visual
    }
    
    /**
     * Actualiza el texto del botón de cambio de tema según el modo actual.
     */
    private void actualizarTextoBotonTema() {
        btnCambiarModo.setText(ConfiguracionTema.getInstancia().isModoOscuro() ? 
                              "Cambiar a Modo Claro" : "Cambiar a Modo Oscuro");
    }

    /**
     * Método auxiliar para agregar items al menú con una acción asociada.
     * @param menu Menú al que se agregará el item
     * @param texto Texto que se mostrará en el item
     * @param accion Acción a ejecutar cuando se seleccione el item
     */
    private void addMenuItem(JMenu menu, String texto, Runnable accion) {
        JMenuItem menuItem = new JMenuItem(texto);
        menuItem.setFont(new Font("Arial", Font.PLAIN, 13));
        menuItem.addActionListener(e -> accion.run());
        menu.add(menuItem);
    }

    /**
     * Método para grabar los datos del sistema en archivo.
     * Muestra diálogos de confirmación y resultado.
     */
    private void grabarDatos() {
        int confirmacion = JOptionPane.showConfirmDialog(
            this, 
            "¿Desea guardar todos los datos actuales?", 
            "Guardar Datos", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            sistema.guardarDatos(); // Llama al método del sistema para guardar
            JOptionPane.showMessageDialog(this, "Datos guardados exitosamente");
        }
    }

    /**
     * Método para recuperar datos del sistema desde archivo.
     * Muestra diálogos de confirmación y resultado.
     */
    private void recuperarDatos() {
        int confirmacion = JOptionPane.showConfirmDialog(
            this, 
            "¿Desea recuperar los datos guardados?\nSe perderán los cambios no guardados.", 
            "Recuperar Datos", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            Sistema sistemaRecuperado = Sistema.cargarDatos(); // Intenta cargar los datos
            if (sistemaRecuperado != null) {
                this.sistema = sistemaRecuperado; // Reemplaza la referencia al sistema
                JOptionPane.showMessageDialog(this, "Datos recuperados exitosamente");
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No se encontraron datos guardados", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Método para salir del sistema.
     * Pide confirmación y guarda los datos antes de salir.
     */
    private void salir() {
        int confirmacion = JOptionPane.showConfirmDialog(
            this, 
            "¿Está seguro que desea salir del sistema?", 
            "Confirmar Salida", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            sistema.guardarDatos(); // Guarda los datos antes de salir
            System.exit(0); // Termina la aplicación
        }
    }
}

/*
Explicación General
La clase VentanaInicio es el punto de entrada principal del sistema de parking y ofrece:

Estructura Principal:
Barra de menú superior organizada en categorías (Gestión, Movimientos, Varios, Terminar)
Área central con mensaje de bienvenida
Botón inferior para cambiar entre modo claro/oscuro

Funcionalidades Clave:
Acceso a todas las ventanas del sistema a través del menú
Cambio dinámico de tema visual (claro/oscuro)
Opciones para guardar y recuperar datos del sistema
Confirmaciones antes de acciones importantes

Organización del Código:
Métodos bien estructurados y especializados
Uso de expresiones lambda para manejar acciones de menú
Validaciones y confirmaciones para operaciones críticas
Integración con el sistema de temas visuales

Esta clase actúa como el núcleo central de la interfaz gráfica,
coordinando el acceso a todas las demás funcionalidades del sistema.
*/
