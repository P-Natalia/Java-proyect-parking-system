package Dominio;
/**
 *
 * @author Natalia Peña 
 */

import java.io.Serializable;

//Reprecenta un contrato del parking, con id, cliente, vehiculo, empleado, valorMensual y estado.
public class Contrato implements Serializable {
    private static final long serialVersionUID = 1L;
    public int id;
    private Cliente cliente;
    private Vehiculo vehiculo;
    private Empleado empleado;
    private double valorMensual;
    private boolean activo;

    /* Crea un objeto de tipo Contrato. Se asume que los datos son correctos; id, cliente, vehiculo, empleado, valorMensual*/
    public Contrato(int id, Cliente cliente, Vehiculo vehiculo, Empleado empleado, double valorMensual) {
        if (cliente == null || vehiculo == null || empleado == null) {
            throw new IllegalArgumentException("Cliente, vehículo y empleado no pueden ser nulos");
        }
        this.id = id;
        this.cliente = cliente;
        this.vehiculo = vehiculo;
        this.empleado = empleado;
        this.valorMensual = valorMensual;
        this.activo = true;
        this.vehiculo.setTieneContrato(true);
        this.vehiculo.setCliente(cliente);
    }

    // Getters
    public int getId() {
        return id; 
    }
    
    public Cliente getCliente() {
        return cliente; 
    }
    
    public Vehiculo getVehiculo() { 
        return vehiculo; 
    }
    
    public Empleado getEmpleado() { 
        return empleado; 
    }
    
    public double getValorMensual() { 
        return valorMensual; 
    }
    
    public boolean getActivo(){
        return activo;
    }
    
    // Setters 
    public void setValorMensual(double valorMensual) {
        this.valorMensual = valorMensual;
    }
    
    public void setActivo(boolean activo) {
        if (this.activo != activo) {
            this.activo = activo;
            this.vehiculo.setTieneContrato(activo);
            if (!activo) {
                this.vehiculo.setCliente(null);
            } else {
                this.vehiculo.setCliente(cliente);
            }
        }
    }
   
    // Representación en String del contrato
    @Override
    public String toString() {
        String estado = activo ? "ACTIVO" : "INACTIVO";
        return String.format("Contrato #%d - %s (%s) - %s - $%.2f/mes", 
                             id, cliente.getNombre(), vehiculo.getMatricula(), estado, valorMensual);
    }
}