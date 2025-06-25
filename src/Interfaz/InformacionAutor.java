package Interfaz;
/**
 *
 * @author Natalia Peña 
 */
import Dominio.ConfiguracionTema;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class InformacionAutor extends JFrame {

    public InformacionAutor() {
        ConfiguracionTema.getInstancia().registrarVentana(this);
        ConfiguracionTema.getInstancia().aplicarTema(this);
        setTitle("Información del Autor");
        setSize(500, 300);

        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Autor 1
        panel.add(crearPanelAutor("Natalia Peña", "https://github.com/P-Natalia", "parkingFoto.jpeg"));


        add(panel);
        setLocationRelativeTo(null);
    }

    private JPanel crearPanelAutor(String nombre, String git, String nombreImagen) {
        JPanel panelAutor = new JPanel(new BorderLayout());
        panelAutor.setBorder(BorderFactory.createEtchedBorder());

        JLabel lblNombre = new JLabel(nombre, SwingConstants.CENTER);
        JLabel lblGit = new JLabel(git, SwingConstants.CENTER);
        panelAutor.add(lblNombre, BorderLayout.NORTH);
        panelAutor.add(lblGit, BorderLayout.SOUTH);

        try {
            URL url = getClass().getResource("/imagenParking/" + nombreImagen);
            if (url != null) {
                ImageIcon iconoOriginal = new ImageIcon(url);
                Image imagenRedimensionada = iconoOriginal.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                JLabel lblImagen = new JLabel(new ImageIcon(imagenRedimensionada));
                lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
                panelAutor.add(lblImagen, BorderLayout.CENTER);
            } else {
                throw new IllegalArgumentException("No se encontró la imagen: " + nombreImagen);
            }
        } catch (IllegalArgumentException e) {
            panelAutor.add(new JLabel("Imagen no encontrada", SwingConstants.CENTER), BorderLayout.CENTER);
            System.err.println("Error al cargar imagen: " + e.getMessage());
        }

        return panelAutor;
    }
}
