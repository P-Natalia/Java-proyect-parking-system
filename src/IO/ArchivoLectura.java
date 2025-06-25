package IO;

/**
 *
 * @author Natalia Peña 
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Clase utilitaria para la lectura de archivos de texto.
 * Proporciona métodos simplificados para leer líneas de un archivo.
 */
public class ArchivoLectura {
    // Objeto BufferedReader para leer el archivo eficientemente
    private BufferedReader br;

    /**
     * Constructor que abre el archivo para lectura.
     * @param nombreArchivo Ruta del archivo a leer
     * @throws IOException Si ocurre un error al abrir el archivo
     */
    public ArchivoLectura(String nombreArchivo) throws IOException {
        // Inicializa el BufferedReader con un FileReader
        br = new BufferedReader(new FileReader(nombreArchivo));
    }

    /**
     * Lee una línea del archivo.
     * @return La línea leída, o null si se llegó al final del archivo
     * @throws IOException Si ocurre un error durante la lectura
     */
    public String leerLinea() throws IOException {
        return br.readLine();
    }

    /**
     * Cierra el archivo liberando los recursos asociados.
     * @throws IOException Si ocurre un error al cerrar el archivo
     */
    public void cerrar() throws IOException {
        br.close();
    }
}