package Dominio;
/**
 *
 * @author Natalia Peña 
 */

import Dominio.Sistema;
import java.io.*;

/**
 * Clase utilitaria para serializar y deserializar el estado del sistema.
 * Permite guardar y cargar el estado completo del parking en un archivo.
 */
public class Serializador {
    
     /**
     * Guarda el estado del sistema en un archivo mediante serialización.
     * 
     * @param sistema Objeto Sistema que contiene todos los datos del parking
     * @param archivo Ruta del archivo donde se guardarán los datos serializados
     * @throws IOException Si ocurre un error durante la escritura del archivo
     */
    public static void guardar(Sistema sistema, String archivo) throws IOException {
        // Usamos try-with-resources para asegurar que el stream se cierre correctamente
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(archivo))) {
            // Serializa el objeto sistema y lo escribe en el archivo
            out.writeObject(sistema);
        }
    }
    
     /**
     * Carga el estado del sistema desde un archivo serializado.
     * 
     * @param archivo Ruta del archivo que contiene los datos serializados
     * @return Objeto Sistema con todos los datos del parking
     * @throws IOException Si ocurre un error durante la lectura del archivo
     * @throws ClassNotFoundException Si la clase del objeto serializado no se encuentra
     */

    public static Sistema cargar(String archivo) throws IOException, ClassNotFoundException {
         // Usamos try-with-resources para asegurar que el stream se cierre correctamente
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
              // Lee y deserializa el objeto Sistema desde el archivo
            return (Sistema) in.readObject();
        }
    }
}

