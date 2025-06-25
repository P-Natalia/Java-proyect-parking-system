package Dominio;
/**
 *
 * @author Natalia Peña 
 */
import java.io.Serializable;
import java.util.Date;

//Representa a una entrada al parking, con vehiculo, fecha, hora, notas, empleado y si tiene contato.
public class Entrada implements Serializable {
    private final Vehiculo vehiculo;
    private final Date fecha;
    private final String hora;
    private final String notas;
    private final Empleado empleado;
    private final boolean tieneContrato;

    public Entrada(Vehiculo vehiculo, Empleado empleado, Date fecha, String hora, String notas, boolean tieneContrato) {
        this.vehiculo = vehiculo;
        this.empleado = empleado;
        this.fecha = fecha;
        this.hora = hora;
        this.notas = notas;
        this.tieneContrato = tieneContrato;
    }
    
    // Getters 
    public Vehiculo getVehiculo() { return vehiculo; }
    public Date getFecha() { return fecha; }
    public String getHora() { return hora; }
    public String getNotas() { return notas; }
    public Empleado getEmpleado() { return empleado; }
    public boolean getTieneContrato() { return tieneContrato; }
}