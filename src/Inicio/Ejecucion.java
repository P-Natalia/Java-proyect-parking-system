package Inicio;
/**
 *
 * @author Natalia Peña 
 */

import Dominio.Sistema;
import Interfaz.VentanaInicio; 

public class Ejecucion {
    public static void main(String[] args) {
        // Cargar sistema existente o crear uno nuevo
        Sistema sistema = Sistema.cargarDatos();
        VentanaInicio ventanaPrincipal = new VentanaInicio(sistema);
        ventanaPrincipal.setVisible(true);
    }
}
