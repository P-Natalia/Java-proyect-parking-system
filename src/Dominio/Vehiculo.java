package Dominio;
/**
 *
 * @author Natalia Peña 
 */

import java.io.Serializable;

// Representa un vehículo, con su matrícula, marca, modelo, estado y cliente asociado.
public class Vehiculo implements Serializable {
    private static final long serialVersionUID = 1L;  
    private String matricula;
    private String marca;
    private String modelo;
    private String estado;
    private Cliente cliente; 
    private boolean tieneContrato;

    /* Crea un objeto de tipo Vehiculo. Se asume que los datos son correctos; unaMatricula matrícula del vehículo (identificador único),
    unaMarca marca del vehículo, unModelo modelo del vehículo, unEstado estado actual del vehículo (ej: "Buen estado", "Dañado", etc).*/
    public Vehiculo(String unaMatricula, String unaMarca, String unModelo, String unEstado) {
        this.matricula = unaMatricula;
        this.marca = unaMarca;
        this.modelo = unModelo;
        this.estado = unEstado;
        this.tieneContrato = false;
    }
    
    //Geters
    public String getMatricula() {
        return this.matricula;
    }
    
    public String getMarca() {
        return this.marca;
    }
    
    public String getModelo() {
        return this.modelo;
    }
    
    public String getEstado() {
        return this.estado;
    }
    
    public Cliente getCliente() {
        return this.cliente;
    }
    
    public boolean getTieneContrato() {
        return tieneContrato;
    }
    
    //Seters
    public void setMatricula(String unaMatricula) {
        this.matricula = unaMatricula;
    }
    
    public void setMarca(String unaMarca) {
        this.marca = unaMarca;
    }
    
    public void setModelo(String unModelo) {
        this.modelo = unModelo;
    }
    
    public void setEstado(String unEstado) {
        this.estado = unEstado;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setTieneContrato(boolean tieneContrato) {
        this.tieneContrato = tieneContrato;
    }
    
    //Representación en String del vehículo
    @Override
    public String toString() {
        String clienteInfo = (cliente != null) ? cliente.getNombre() : "Sin cliente";
        return this.matricula + " - " + this.marca + " " + this.modelo + 
               " (" + this.estado + ") - Cliente: " + clienteInfo;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehiculo vehiculo = (Vehiculo) o;
        return matricula.equals(vehiculo.matricula);
    }
    
    @Override
    public int hashCode() {
        return matricula.hashCode();
    }
}