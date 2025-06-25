
package Dominio;
/**
 *
 * @author Natalia Peña 
 */
import java.io.Serializable;

//Representa un cliente del parking, con cédula, nombre, dirección, celular y año de ingreso
public class Cliente implements Serializable {
    private String cedula;
    private String nombre;
    private String direccion;
    private String celular;
    private int añoIngreso;
    
    /* Crea un objeto de tipo Cliente. Se asume que los datos son correctos; unaCedula cédula de identidad del cliente,
    unNombre nombre completo del cliente, unaDireccion dirección del cliente, unCelular número de celular del cliente,
    unAñoIngreso año en que el cliente comenzó a usar el parking. */
    public Cliente(String unaCedula, String unNombre, String unaDireccion, String unCelular, int unAñoIngreso) {
        this.cedula = unaCedula;
        this.nombre = unNombre;
        this.direccion = unaDireccion;
        this.celular = unCelular;
        this.añoIngreso = unAñoIngreso;
    }
    
    //Geters
    public String getCedula() {
        return cedula;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public String getDireccion() {
        return direccion;
    }
    
    public String getCelular() {
        return celular;
    }
    
    public int getAñoIngreso() {
        return this.añoIngreso;
    }
    
    //Seters
    public void setCedula(String unaCedula) {
        this.cedula = unaCedula;
    }
    
    public void setNombre(String unNombre) {
        this.nombre = unNombre;
    }
   
    public void setDireccion(String unaDireccion) {
        this.direccion = unaDireccion;
    }
    
    public void setCelular(String unCelular) {
        this.celular = unCelular;
    }
    
    public void setAñoIngreso(int unAñoIngreso) {
        this.añoIngreso = unAñoIngreso;
    }
    
    // Representación en String del cliente
    @Override
    public String toString() {
        return nombre + " ( " + cedula + " ) ";
    }
}