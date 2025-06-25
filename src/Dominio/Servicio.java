package Dominio;
/**
 *
 * @author Natalia Peña 
 */

import java.io.Serializable;
import java.util.Date;

//Representa un servicio del parking, con tipo, vehiculo, empleado, fecha, hora y costo.
public class Servicio implements Serializable {
    private String tipo;
    private Vehiculo vehiculo;
    private Empleado empleado;
    private Date fecha;
    private String hora;
    private double costo;

    public Servicio(String tipo, Vehiculo vehiculo, Empleado empleado, Date fecha,String hora, double costo) {
        this.tipo = tipo;
        this.vehiculo = vehiculo;
        this.empleado = empleado;
        this.fecha = fecha;
        this.hora = hora;
        this.costo = costo;
    }

    // Getters
    public String getTipo() { 
        return tipo; 
    }
    public Vehiculo getVehiculo() { 
        return vehiculo; 
    }
    public Empleado getEmpleado() { 
        return empleado; 
    }
    public Date getFecha() { 
        return fecha; 
    }
    public String getHora() { 
        return hora; 
    }
    public double getCosto() { 
        return costo; 
    }
    
    //Representación en String del servicio..
    @Override
    public String toString() {
        return String.format("%s - %s - %s - $%.2f", 
            tipo, vehiculo.getMatricula(), empleado.getNombre(), costo);
    }
}
