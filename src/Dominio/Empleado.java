package Dominio;
/**
 *
 * @author Natalia Peña 
 */

import java.io.Serializable;

//Representa a un empleado del parking, con su cédula, nombre, dirección y número de empleado.
public class Empleado implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cedula;
    private String nombre;
    private String direccion; 
    private int numeroEmpleado;
    private boolean activo;

    /* Crea un objeto de tipo Empleado con validación de parámetros: unaCedula cédula de identidad del empleado,
    unNombre nombre completo del empleado, unaDireccion dirección del empleado, unNumero número único de empleado,
    IllegalArgumentException si los parámetros no son válidos */
    public Empleado(String unaCedula, String unNombre, String unaDireccion, int unNumero) {
        if (unaCedula == null || unaCedula.trim().isEmpty()) {
            throw new IllegalArgumentException("La cédula no puede ser nula o vacía");
        }
        if (unNombre == null || unNombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
        }
        if (unNumero <= 0) {
            throw new IllegalArgumentException("El número de empleado debe ser positivo");
        }
        this.cedula = unaCedula.trim();
        this.nombre = unNombre.trim();
        this.direccion = (unaDireccion != null) ? unaDireccion.trim() : "";
        this.numeroEmpleado = unNumero;
        this.activo = true;
    }

    
    //Geters
    public String getCedula() {
        return this.cedula;
    }

    public String getNombre() {
        return this.nombre;
    }
 
    public String getDireccion() {
        return this.direccion;
    }
 
    public int getNumeroEmpleado() {
        return this.numeroEmpleado;
    }
 
    public boolean getActivo() {
        return this.activo;
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
   
    public void setNumeroEmpleado(int unNumero) {
        this.numeroEmpleado = unNumero;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    // Representación en String del empleado.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Empleado empleado = (Empleado) o;
        return numeroEmpleado == empleado.numeroEmpleado;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(numeroEmpleado);
    }
    
    @Override
    public String toString() {
        return this.numeroEmpleado + " - " + this.nombre + " (" + this.cedula + ")";
    }
}


