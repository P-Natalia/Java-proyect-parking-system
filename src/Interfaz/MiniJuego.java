package Interfaz;
/**
 *
 * @author Natalia Peña 
 */

import Dominio.ConfiguracionTema;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

//Clase creada con DeepSeek
public class MiniJuego extends JFrame {
    private int score = 0;
    private JLabel lblScore;
    
    public MiniJuego() {
        ConfiguracionTema.getInstancia().registrarVentana(this);
        ConfiguracionTema.getInstancia().aplicarTema(this);
        setTitle("Mini Juego");
        setSize(600, 400);
        initComponents();
    }
    
    private void initComponents() {
        JPanel panel = new JPanel(null);
        panel.setPreferredSize(new Dimension(600, 400));
        lblScore = new JLabel("Puntuación: 0");
        lblScore.setBounds(10, 10, 150, 20);
        panel.add(lblScore);
        Timer timer = new Timer(1000, e -> crearObjetivo(panel));
        timer.start();
        add(panel);
        ConfiguracionTema.getInstancia().aplicarTema(this);
    }
    
    private void crearObjetivo(JPanel panel) {
        JButton objetivo = new JButton();
        objetivo.setSize(40, 40);
        objetivo.setOpaque(true);
        objetivo.setBackground(Color.GREEN);
        Random rand = new Random();
        int x = rand.nextInt(panel.getWidth() - 50);
        int y = rand.nextInt(panel.getHeight() - 50);
        objetivo.setLocation(x, y);
        objetivo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                panel.remove(objetivo);
                score++;
                lblScore.setText("Puntuación: " + score);
                panel.revalidate();
                panel.repaint();
            }
        });
        panel.add(objetivo);
        panel.revalidate();
        panel.repaint();
        new Timer(2000, evt -> {
            panel.remove(objetivo);
            panel.revalidate();
            panel.repaint();
        }).start();
    }
}
