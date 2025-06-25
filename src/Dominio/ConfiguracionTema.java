package Dominio;
/**
 *
 * @author Natalia Peña 
 */

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;
import javax.swing.MenuElement;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Clase encargada de la configuración de temas claro/oscuro (creada con asistencia e investigación en DeepSeek).
public class ConfiguracionTema implements Serializable {
    private static final long serialVersionUID = 1L;
    private static ConfiguracionTema instancia;
    private boolean modoOscuro = false;
    private transient List<Component> ventanas = new ArrayList<>();
    // Colores para el modo oscuro
    private final Color DARK_BACKGROUND = new Color(45, 45, 45);
    private final Color DARK_FOREGROUND = new Color(220, 220, 220);
    private final Color DARK_SELECTION_BG = new Color(70, 70, 70);
    private final Color DARK_SELECTION_FG = Color.WHITE;
    private final Color DARK_GRID_COLOR = new Color(80, 80, 80);
    private final Color DARK_BORDER = new Color(100, 100, 100);
    private final Color DARK_MENU_BG = new Color(30, 30, 30);
    private final Color DARK_TOOLTIP_BG = new Color(60, 60, 60);
    private final Color DARK_TOOLTIP_FG = Color.WHITE;
    // Colores para el modo claro
    private final Color LIGHT_BACKGROUND = Color.WHITE;
    private final Color LIGHT_FOREGROUND = Color.BLACK;
    private final Color LIGHT_SELECTION_BG = new Color(200, 200, 255);
    private final Color LIGHT_SELECTION_FG = Color.BLACK;
    private final Color LIGHT_GRID_COLOR = Color.LIGHT_GRAY;
    private final Color LIGHT_BORDER = new Color(200, 200, 200);
    private final Color LIGHT_MENU_BG = new Color(240, 240, 240);
    private final Color LIGHT_TOOLTIP_BG = new Color(255, 255, 225);
    private final Color LIGHT_TOOLTIP_FG = Color.BLACK;

    private ConfiguracionTema() {}

    public static synchronized ConfiguracionTema getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracionTema();
        }
        return instancia;
    }

    public boolean isModoOscuro() {
        return modoOscuro;
    }

    public void setModoOscuro(boolean modoOscuro) {
        this.modoOscuro = modoOscuro;
        actualizarTodasLasVentanas();
    }

    public void cambiarModo() {
        modoOscuro = !modoOscuro;
        actualizarTodasLasVentanas();
    }

    private void actualizarTodasLasVentanas() {
        if (ventanas != null) {
            for (Component ventana : ventanas) {
                aplicarTema(ventana);
                if (ventana instanceof JFrame) {
                    JFrame frame = (JFrame) ventana;
                    JMenuBar menuBar = frame.getJMenuBar();
                    if (menuBar != null) {
                        for (int i = 0; i < menuBar.getMenuCount(); i++) {
                            JMenu menu = menuBar.getMenu(i);
                            if (menu != null) {
                                menu.getPopupMenu().setUI(menu.getPopupMenu().getUI());
                            }
                        }
                    }
                }
            }
        }
    }

    public void registrarVentana(Component ventana) {
        if (ventanas == null) {
            ventanas = new ArrayList<>();
        }
        if (!ventanas.contains(ventana)) {
            ventanas.add(ventana);
        }
        if (ventana instanceof JFrame) {
            JFrame frame = (JFrame) ventana;
            JMenuBar menuBar = frame.getJMenuBar();
            if (menuBar != null) {
                registrarComponentesDeMenu(menuBar);
            }
        }
        aplicarTema(ventana);
        if (ventana instanceof Window) {
            ((Window)ventana).addWindowListener(new WindowAdapter() {
                @Override
                public void windowOpened(WindowEvent e) {
                    aplicarTema(ventana);
                }
            });
        }
    }

    private void registrarComponentesDeMenu(MenuElement menuElement) {
        if (menuElement instanceof Component) {
            registrarVentana((Component) menuElement);
        }
        for (MenuElement subElement : menuElement.getSubElements()) {
            registrarComponentesDeMenu(subElement);
        }
    }

    public void aplicarTema(Component componente) {
        if (componente == null) return;
        if (modoOscuro) {
            aplicarModoOscuro(componente);
        } else {
            aplicarModoClaro(componente);
        }
        if (componente instanceof Window) {
            componente.revalidate();
            componente.repaint();
        }
    }

    private boolean esBotonConColorEspecial(Component componente) {
        if (!(componente instanceof JButton)) return false;
        JButton boton = (JButton) componente;
        Color bg = boton.getBackground();
        return bg.equals(Color.GREEN) || bg.equals(Color.YELLOW) || bg.equals(Color.RED) ||
               bg.equals(new Color(0, 128, 0)) || bg.equals(new Color(255, 165, 0));
    }

    private void aplicarModoOscuro(Component componente) {
        if (esBotonConColorEspecial(componente)) {
            componente.setForeground(Color.BLACK);
            return;
        }
        componente.setBackground(DARK_BACKGROUND);
        componente.setForeground(DARK_FOREGROUND);
        if (componente instanceof JComponent) {
            JComponent jComponent = (JComponent) componente;
            jComponent.setOpaque(true);
            if (jComponent.getBorder() instanceof TitledBorder) {
                TitledBorder titledBorder = (TitledBorder) jComponent.getBorder();
                titledBorder.setTitleColor(DARK_FOREGROUND);
            } else if (jComponent.getBorder() instanceof LineBorder) {
                jComponent.setBorder(BorderFactory.createLineBorder(DARK_BORDER));
            }
            if (componente instanceof JMenuBar || componente instanceof JMenu || componente instanceof JMenuItem) {
                componente.setBackground(DARK_MENU_BG);
                componente.setForeground(DARK_FOREGROUND);
                if (componente instanceof JMenuItem) {
                    JMenuItem menuItem = (JMenuItem) componente;
                    menuItem.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    menuItem.setOpaque(true);  
                    if (menuItem instanceof JMenu) {
                        JMenu menu = (JMenu) menuItem;
                        for (Component c : menu.getMenuComponents()) {
                            if (c instanceof JSeparator) {
                                c.setForeground(DARK_BORDER);
                            }
                        }
                    }
                }
                if (componente instanceof JPopupMenu) {
                    JPopupMenu popupMenu = (JPopupMenu) componente;
                    popupMenu.setBorder(BorderFactory.createLineBorder(DARK_BORDER));
                }
            }
            if (componente instanceof JTable) {
                JTable tabla = (JTable) componente;
                tabla.setBackground(DARK_BACKGROUND);
                tabla.setForeground(DARK_FOREGROUND);
                tabla.setGridColor(DARK_GRID_COLOR);
                tabla.setSelectionBackground(DARK_SELECTION_BG);
                tabla.setSelectionForeground(DARK_SELECTION_FG);
                JTableHeader header = tabla.getTableHeader();
                if (header != null) {
                    header.setBackground(DARK_MENU_BG);
                    header.setForeground(DARK_FOREGROUND);
                    header.setBorder(BorderFactory.createLineBorder(DARK_BORDER));
                }
            }
            if (componente instanceof JTabbedPane) {
                JTabbedPane tabbedPane = (JTabbedPane) componente;
                tabbedPane.setBackground(DARK_BACKGROUND);
                tabbedPane.setForeground(DARK_FOREGROUND);
                tabbedPane.setUI(new javax.swing.plaf.metal.MetalTabbedPaneUI() {
                    @Override
                    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, 
                            int x, int y, int w, int h, boolean isSelected) {
                        g.setColor(isSelected ? DARK_SELECTION_BG : DARK_BACKGROUND);
                        g.fillRect(x, y, w, h);
                    }
                    @Override
                    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, 
                            int x, int y, int w, int h, boolean isSelected) {
                        g.setColor(DARK_BORDER);
                        g.drawRect(x, y, w, h);
                    }
                    @Override
                    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                        g.setColor(DARK_BORDER);
                        g.fillRect(0, 0, tabbedPane.getWidth(), tabbedPane.getHeight());
                    }
                });
            }
            if (componente instanceof AbstractButton && !esBotonConColorEspecial(componente)) {
                AbstractButton button = (AbstractButton) componente;
                button.setBackground(DARK_BACKGROUND);
                button.setForeground(DARK_FOREGROUND);
                button.setBorder(BorderFactory.createLineBorder(DARK_BORDER));
                button.setContentAreaFilled(false);
                button.setOpaque(true);
            }
            if (componente instanceof JComboBox) {
                JComboBox<?> comboBox = (JComboBox<?>) componente;
                comboBox.setBackground(DARK_BACKGROUND);
                comboBox.setForeground(DARK_FOREGROUND);
                comboBox.setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, 
                            int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        setBackground(isSelected ? DARK_SELECTION_BG : DARK_BACKGROUND);
                        setForeground(isSelected ? DARK_SELECTION_FG : DARK_FOREGROUND);
                        return this;
                    }
                });
            }
            if (componente instanceof JToggleButton) {
                JToggleButton toggleButton = (JToggleButton) componente;
                toggleButton.setBackground(DARK_BACKGROUND);
                toggleButton.setForeground(DARK_FOREGROUND);
            }
            
            // Manejo para JLabel
            if (componente instanceof JLabel) {
                JLabel label = (JLabel) componente;
                label.setForeground(DARK_FOREGROUND);
            }
            if (componente instanceof JToolTip) {
                componente.setBackground(DARK_TOOLTIP_BG);
                componente.setForeground(DARK_TOOLTIP_FG);
            }
            if (componente instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) componente;
                scrollPane.getViewport().setBackground(DARK_BACKGROUND);
                scrollPane.setBorder(BorderFactory.createLineBorder(DARK_BORDER));
                scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.metal.MetalScrollBarUI() {
                    @Override
                    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                        g.setColor(DARK_BORDER);
                        g.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);
                    }
                    @Override
                    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                        g.setColor(DARK_BACKGROUND);
                        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
                    }
                });
                scrollPane.getHorizontalScrollBar().setUI(new javax.swing.plaf.metal.MetalScrollBarUI() {
                    @Override
                    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                        g.setColor(DARK_BORDER);
                        g.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);
                    }
                    @Override
                    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                        g.setColor(DARK_BACKGROUND);
                        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
                    }
                });
            }
            if (componente instanceof JPanel) {
                JPanel panel = (JPanel) componente;
                panel.setBackground(DARK_BACKGROUND);
                panel.setForeground(DARK_FOREGROUND);
            }
            
            // Manejo para JSpinner
            if (componente instanceof JSpinner) {
                JSpinner spinner = (JSpinner) componente;
                spinner.setBackground(DARK_BACKGROUND);
                spinner.setForeground(DARK_FOREGROUND);
                
                JComponent editor = spinner.getEditor();
                if (editor instanceof JSpinner.DefaultEditor) {
                    JTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
                    textField.setBackground(DARK_BACKGROUND);
                    textField.setForeground(DARK_FOREGROUND);
                    textField.setBorder(BorderFactory.createLineBorder(DARK_BORDER));
                    textField.setCaretColor(DARK_FOREGROUND);
                }
            }
            if (componente instanceof JTextComponent) {
                JTextComponent textComponent = (JTextComponent) componente;
                textComponent.setBackground(DARK_BACKGROUND);
                textComponent.setForeground(DARK_FOREGROUND);
                textComponent.setCaretColor(DARK_FOREGROUND);
                textComponent.setBorder(BorderFactory.createLineBorder(DARK_BORDER));
            }
            if (componente instanceof JList) {
                JList<?> list = (JList<?>) componente;
                list.setBackground(DARK_BACKGROUND);
                list.setForeground(DARK_FOREGROUND);
                list.setSelectionBackground(DARK_SELECTION_BG);
                list.setSelectionForeground(DARK_SELECTION_FG);
                list.setCellRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, 
                            int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        setBackground(isSelected ? DARK_SELECTION_BG : DARK_BACKGROUND);
                        setForeground(isSelected ? DARK_SELECTION_FG : DARK_FOREGROUND);
                        return this;
                    }
                });
            }
        }
        if (componente instanceof Container) {
            for (Component child : ((Container) componente).getComponents()) {
                aplicarModoOscuro(child);
            }
        }
    }

    private void aplicarModoClaro(Component componente) {
        if (esBotonConColorEspecial(componente)) {
            componente.setForeground(Color.BLACK);
            return;
        }
        componente.setBackground(LIGHT_BACKGROUND);
        componente.setForeground(LIGHT_FOREGROUND);
        if (componente instanceof JComponent) {
            JComponent jComponent = (JComponent) componente;
            jComponent.setOpaque(true);
            if (jComponent.getBorder() instanceof TitledBorder) {
                TitledBorder titledBorder = (TitledBorder) jComponent.getBorder();
                titledBorder.setTitleColor(LIGHT_FOREGROUND);
            } else if (jComponent.getBorder() instanceof LineBorder) {
                jComponent.setBorder(BorderFactory.createLineBorder(LIGHT_BORDER));
            }
            if (componente instanceof JMenuBar || componente instanceof JMenu || componente instanceof JMenuItem) {
                componente.setBackground(LIGHT_MENU_BG);
                componente.setForeground(LIGHT_FOREGROUND);
                if (componente instanceof JMenuItem) {
                    JMenuItem menuItem = (JMenuItem) componente;
                    menuItem.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    menuItem.setOpaque(true);
                    if (menuItem instanceof JMenu) {
                        JMenu menu = (JMenu) menuItem;
                        for (Component c : menu.getMenuComponents()) {
                            if (c instanceof JSeparator) {
                                c.setForeground(LIGHT_BORDER);
                            }
                        }
                    }
                }
                if (componente instanceof JPopupMenu) {
                    JPopupMenu popupMenu = (JPopupMenu) componente;
                    popupMenu.setBorder(BorderFactory.createLineBorder(LIGHT_BORDER));
                }
            }
            if (componente instanceof JTable) {
                JTable tabla = (JTable) componente;
                tabla.setBackground(LIGHT_BACKGROUND);
                tabla.setForeground(LIGHT_FOREGROUND);
                tabla.setGridColor(LIGHT_GRID_COLOR);
                tabla.setSelectionBackground(LIGHT_SELECTION_BG);
                tabla.setSelectionForeground(LIGHT_SELECTION_FG);
                JTableHeader header = tabla.getTableHeader();
                if (header != null) {
                    header.setBackground(LIGHT_MENU_BG);
                    header.setForeground(LIGHT_FOREGROUND);
                    header.setBorder(BorderFactory.createLineBorder(LIGHT_BORDER));
                }
            }
            if (componente instanceof JTabbedPane) {
                JTabbedPane tabbedPane = (JTabbedPane) componente;
                tabbedPane.setBackground(LIGHT_BACKGROUND);
                tabbedPane.setForeground(LIGHT_FOREGROUND);
                tabbedPane.setUI(new javax.swing.plaf.metal.MetalTabbedPaneUI());
            }
            if (componente instanceof AbstractButton && !esBotonConColorEspecial(componente)) {
                AbstractButton button = (AbstractButton) componente;
                button.setBackground(LIGHT_BACKGROUND);
                button.setForeground(LIGHT_FOREGROUND);
                button.setBorder(BorderFactory.createLineBorder(LIGHT_BORDER));
                button.setContentAreaFilled(true);
                button.setOpaque(true);
            }
            if (componente instanceof JComboBox) {
                JComboBox<?> comboBox = (JComboBox<?>) componente;
                comboBox.setBackground(LIGHT_BACKGROUND);
                comboBox.setForeground(LIGHT_FOREGROUND);
                comboBox.setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, 
                            int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        setBackground(isSelected ? LIGHT_SELECTION_BG : LIGHT_BACKGROUND);
                        setForeground(isSelected ? LIGHT_SELECTION_FG : LIGHT_FOREGROUND);
                        return this;
                    }
                });
            }
            if (componente instanceof JToggleButton) {
                JToggleButton toggleButton = (JToggleButton) componente;
                toggleButton.setBackground(LIGHT_BACKGROUND);
                toggleButton.setForeground(LIGHT_FOREGROUND);
            }
            if (componente instanceof JLabel) {
                JLabel label = (JLabel) componente;
                label.setForeground(LIGHT_FOREGROUND);
            }
            if (componente instanceof JToolTip) {
                componente.setBackground(LIGHT_TOOLTIP_BG);
                componente.setForeground(LIGHT_TOOLTIP_FG);
            }
            if (componente instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) componente;
                scrollPane.getViewport().setBackground(LIGHT_BACKGROUND);
                scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_BORDER));
                scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.metal.MetalScrollBarUI());
                scrollPane.getHorizontalScrollBar().setUI(new javax.swing.plaf.metal.MetalScrollBarUI());
            }
            if (componente instanceof JPanel) {
                JPanel panel = (JPanel) componente;
                panel.setBackground(LIGHT_BACKGROUND);
                panel.setForeground(LIGHT_FOREGROUND);
            }
            if (componente instanceof JSpinner) {
                JSpinner spinner = (JSpinner) componente;
                spinner.setBackground(LIGHT_BACKGROUND);
                spinner.setForeground(LIGHT_FOREGROUND);
                JComponent editor = spinner.getEditor();
                if (editor instanceof JSpinner.DefaultEditor) {
                    JTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
                    textField.setBackground(LIGHT_BACKGROUND);
                    textField.setForeground(LIGHT_FOREGROUND);
                    textField.setBorder(BorderFactory.createLineBorder(LIGHT_BORDER));
                    textField.setCaretColor(LIGHT_FOREGROUND);
                }
            }
            if (componente instanceof JTextComponent) {
                JTextComponent textComponent = (JTextComponent) componente;
                textComponent.setBackground(LIGHT_BACKGROUND);
                textComponent.setForeground(LIGHT_FOREGROUND);
                textComponent.setCaretColor(LIGHT_FOREGROUND);
                textComponent.setBorder(BorderFactory.createLineBorder(LIGHT_BORDER));
            }
            if (componente instanceof JList) {
                JList<?> list = (JList<?>) componente;
                list.setBackground(LIGHT_BACKGROUND);
                list.setForeground(LIGHT_FOREGROUND);
                list.setSelectionBackground(LIGHT_SELECTION_BG);
                list.setSelectionForeground(LIGHT_SELECTION_FG);
                list.setCellRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, 
                            int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        setBackground(isSelected ? LIGHT_SELECTION_BG : LIGHT_BACKGROUND);
                        setForeground(isSelected ? LIGHT_SELECTION_FG : LIGHT_FOREGROUND);
                        return this;
                    }
                });
            }
        }
        if (componente instanceof Container) {
            for (Component child : ((Container) componente).getComponents()) {
                aplicarModoClaro(child);
            }
        }
    }
}