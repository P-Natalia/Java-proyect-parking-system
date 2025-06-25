package Dominio;
/**
 *
 * @author Natalia Peña 
 */

import java.io.Serializable;
import java.util.Date;

//Representa una salida del parking, con entrada, empleado, fecha, hora, comentario, si tiene contrato y tiempo de estadía.
public class Salida implements Serializable {
    private final Entrada entrada;
    private final Empleado empleado;
    private final Date fecha;
    private final String hora;
    private final String comentario;
    private final boolean teniaContrato;
    private final String tiempoEstadia;

    /** Crea un objeto de tipo Salida. Se asume que los datos son correctos; unaEntrada registro de entrada asociado,
    unEmpleado empleado que registra la salida, unaFecha fecha de salida, unaHora hora de salida (formato HH:MM 24hs),
    unComentario observaciones sobre el estado del vehículo, teniaContrato indica si el vehículo tenía contrato activo,
    tiempoEstadia tiempo total de estadía (formato Xh Ym). */
    public Salida(Entrada unaEntrada, Empleado unEmpleado, Date unaFecha, String unaHora, String unComentario, boolean teniaContrato, 
                 String tiempoEstadia) {
        this.entrada = unaEntrada;
        this.empleado = unEmpleado;
        this.fecha = unaFecha;
        this.hora = unaHora;
        this.comentario = unComentario;
        this.teniaContrato = teniaContrato;
        this.tiempoEstadia = tiempoEstadia;
    }
    
    //Geters
    public Entrada getEntrada() {
        return this.entrada;
    }
 
    public Empleado getEmpleado() {
        return this.empleado;
    }
  
    public Date getFecha() {
        return this.fecha;
    }
   
    public String getHora() {
        return this.hora;
    }
   
    public String getComentario() {
        return this.comentario;
    }
   
    public boolean getTeniaContrato() {
        return this.teniaContrato;
    }
   
    public String getTiempoEstadia() {
        return this.tiempoEstadia;
    }
    
    //Representación en String de la salida.
    @Override
    public String toString() {
        return "Salida: " + this.entrada.getVehiculo().getMatricula() + 
               " - Estadia: " + this.tiempoEstadia + 
               " - " + this.fecha.toString() + " " + this.hora + 
               " (" + this.empleado.getNombre() + ")";
    }
}