package Dominio;
/**
 *
 * @author Natalia Peña 
 */

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.stream.*;
import java.util.concurrent.TimeUnit;
import java.time.Duration;
import java.util.stream.Collectors;

/**
 * Clase principal que representa el sistema de gestión del parking.
 * Contiene todas las operaciones y datos del sistema.
 * Implementa Serializable para permitir la persistencia de datos.
 */
public class Sistema implements Serializable {
    // Listas para almacenar todas las entidades del sistema
    private ArrayList<Cliente> clientes = new ArrayList<>();
    private ArrayList<Vehiculo> vehiculos = new ArrayList<>();
    private ArrayList<Empleado> empleados = new ArrayList<>();
    private ArrayList<Contrato> contratos = new ArrayList<>();
    private ArrayList<Entrada> entradas = new ArrayList<>();
    private ArrayList<Salida> salidas = new ArrayList<>();
    private ArrayList<Servicio> servicios = new ArrayList<>();
    
    // Identificador de versión para serialización
    private static final long serialVersionUID = 1L; 
    
    // Tipos de servicios predefinidos disponibles en el sistema
    public static final String[] TIPOS_SERVICIOS = {
        "Lavado", "Cambio de rueda", "Limpieza de tapizado", "Cambio de luces", "Otro"
    };
    
    // Archivo donde se guardarán los datos serializados
    private static final String ARCHIVO_DATOS = "DATOS.ser";
    
    // ==================== GESTIÓN DE CLIENTES ====================
    
    /**
     * Agrega un nuevo cliente al sistema.
     * @param cliente El cliente a agregar
     */
    public void agregarCliente(Cliente cliente) {
        clientes.add(cliente);
    }
    
    /**
     * Verifica si existe un cliente con la cédula especificada.
     * @param cedula La cédula a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existeCliente(String cedula) {
        return clientes.stream().anyMatch(c -> c.getCedula().equals(cedula));
    }
    
    /**
     * Busca un cliente por su cédula.
     * @param cedula La cédula del cliente a buscar
     * @return El cliente encontrado o null si no existe
     */
    public Cliente buscarCliente(String cedula) {
        return clientes.stream().filter(c -> c.getCedula().equals(cedula)).findFirst().orElse(null);
    }
    
    /**
     * Elimina un cliente del sistema y todos sus contratos asociados.
     * @param cedula La cédula del cliente a eliminar
     */
    public void eliminarCliente(String cedula) {
        Cliente cliente = buscarCliente(cedula);
        if (cliente != null) {
            // Eliminar contratos asociados al cliente
            List<Contrato> contratosAEliminar = contratos.stream()
                .filter(contrato -> contrato.getCliente().equals(cliente))
                .collect(Collectors.toList());
            contratos.removeAll(contratosAEliminar);
            clientes.remove(cliente);
            reordenarContratos();
        }
    }
    
    /**
     * Reordena los contratos para mantener IDs consecutivos.
     */
    private void reordenarContratos() {
        contratos.sort(Comparator.comparingInt(Contrato::getId));
        for (int i = 0; i < contratos.size(); i++) {
            contratos.get(i).id = i + 1;
        }
    }
    
    /**
     * Obtiene una copia de la lista de clientes.
     * @return Lista de clientes
     */
    public List<Cliente> getClientes() {
        return new ArrayList<>(clientes);
    }
    
    // ==================== GESTIÓN DE VEHÍCULOS ====================
    
    /**
     * Agrega un vehículo al sistema.
     * @param vehiculo El vehículo a agregar
     */
    public void agregarVehiculo(Vehiculo vehiculo) {
        vehiculos.add(vehiculo);
    }
    
    /**
     * Verifica si existe un vehículo con la matrícula especificada.
     * @param matricula La matrícula a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existeVehiculo(String matricula) {
        for (Vehiculo vehiculo : vehiculos) {
            if (vehiculo.getMatricula().equals(matricula)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Busca un vehículo por su matrícula.
     * @param matricula La matrícula del vehículo a buscar
     * @return El vehículo encontrado o null si no existe
     */
    public Vehiculo buscarVehiculo(String matricula) {
        for (Vehiculo vehiculo : vehiculos) {
            if (vehiculo.getMatricula().equals(matricula)) {
                return vehiculo;
            }
        }
        return null;
    }

    /**
     * Obtiene una copia de la lista de vehículos.
     * @return Lista de vehículos
     */
    public List<Vehiculo> getVehiculos() {
        return new ArrayList<>(vehiculos);
    }
    
    /**
     * Obtiene vehículos que no tienen una entrada activa (no están en el parking).
     * @return Lista de vehículos sin entrada activa
     */
    public List<Vehiculo> vehiculosSinEntradaActual() {
        Set<String> matriculasEnParking = entradas.stream()
            .filter(e -> !entradaTieneSalida(e))
            .map(e -> e.getVehiculo().getMatricula())
            .collect(Collectors.toSet());
        return vehiculos.stream()
            .filter(v -> !matriculasEnParking.contains(v.getMatricula()))
            .collect(Collectors.toList());
    }

    /**
     * Verifica si un vehículo tiene un contrato activo.
     * @param vehiculo El vehículo a verificar
     * @return true si tiene contrato activo, false en caso contrario
     */
    public boolean vehiculoTieneContrato(Vehiculo vehiculo) {
        if (vehiculo == null){
            return false;
        }
        return contratos.stream()
            .anyMatch(c -> c.getVehiculo().equals(vehiculo) && c.getActivo());
    }

    /**
     * Verifica si un vehículo está actualmente en el parking.
     * @param matricula La matrícula del vehículo a verificar
     * @return true si está en el parking, false en caso contrario
     */
    public boolean estaEnParking(String matricula) {
        for (Entrada entrada : entradas) {
            if (!entradaTieneSalida(entrada) && 
                entrada.getVehiculo().getMatricula().equals(matricula)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Elimina un vehículo del sistema y todos sus datos asociados.
     * @param vehiculo El vehículo a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public synchronized boolean eliminarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null || !vehiculos.contains(vehiculo)) {
            return false;
        }
        boolean eliminado = vehiculos.remove(vehiculo);
        // Eliminar contratos, entradas, salidas y servicios asociados
        contratos.removeIf(contrato -> contrato.getVehiculo().equals(vehiculo));
        entradas.removeIf(entrada -> entrada.getVehiculo().equals(vehiculo));
        salidas.removeIf(salida -> salida.getEntrada().getVehiculo().equals(vehiculo));
        servicios.removeIf(servicio -> servicio.getVehiculo().equals(vehiculo));
        return eliminado;
    }
    
    // ==================== GESTIÓN DE EMPLEADOS ====================
    
    /**
     * Agrega un empleado al sistema.
     * @param empleado El empleado a agregar
     */
    public void agregarEmpleado(Empleado empleado) {
        empleados.add(empleado);
    }
    
    /**
     * Verifica si existe un empleado con el número especificado.
     * @param numeroEmpleado El número de empleado a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existeEmpleado(int numeroEmpleado) {
        return empleados.stream().anyMatch(e -> e.getNumeroEmpleado() == numeroEmpleado);
    }
    
    /**
     * Busca un empleado por su número de empleado.
     * @param numeroEmpleado El número de empleado a buscar
     * @return El empleado encontrado o null si no existe
     */
    public Empleado buscarEmpleado(int numeroEmpleado) {
        return empleados.stream().filter(e -> e.getNumeroEmpleado() == numeroEmpleado).findFirst().orElse(null);
    }

    /**
     * Obtiene una copia de la lista de empleados.
     * @return Lista de empleados
     */
    public List<Empleado> getEmpleados() {
        return new ArrayList<>(empleados);
    }
    
    /**
     * Obtiene un empleado por su índice en la lista.
     * @param index Índice del empleado
     * @return El empleado en la posición especificada o null si el índice es inválido
     */
    public Empleado getEmpleado(int index) {
        if (index >= 0 && index < empleados.size()) {
            return empleados.get(index);
        }
        return null;
    }

    // ==================== GESTIÓN DE CONTRATOS ====================
    
    /**
     * Agrega un nuevo contrato al sistema.
     * @param contrato El contrato a agregar
     * @throws IllegalArgumentException Si el contrato es nulo
     * @throws IllegalStateException Si el vehículo ya tiene un contrato activo
     */
    public synchronized void agregarContrato(Contrato contrato) {
        if (contrato == null) {
            throw new IllegalArgumentException("El contrato no puede ser nulo");
        }
        boolean existeContrato = contratos.stream()
            .anyMatch(c -> c.getVehiculo().equals(contrato.getVehiculo()) && c.getActivo());
        if (existeContrato) {
            throw new IllegalStateException("El vehículo ya tiene un contrato activo");
        }
        // Asignar ID autoincremental
        contrato.id = contratos.isEmpty() ? 1 : contratos.stream()
            .mapToInt(Contrato::getId)
            .max()
            .getAsInt() + 1;
        contratos.add(contrato);
    }
    
    /**
     * Obtiene el próximo ID disponible para un nuevo contrato.
     * @return El próximo ID disponible
     */
    public int getProximoIdContrato() {
        return contratos.stream()
            .mapToInt(Contrato::getId)
            .max()
            .orElse(0) + 1;
    }

    /**
     * Obtiene una copia de la lista de contratos.
     * @return Lista de contratos
     */
    public List<Contrato> getContratos() {
        return new ArrayList<>(contratos);
    }
    
    /**
     * Busca un contrato por su ID.
     * @param idContrato El ID del contrato a buscar
     * @return El contrato encontrado o null si no existe
     */
    public Contrato buscarContratoPorId(int idContrato) {
        return contratos.stream()
            .filter(c -> c.getId() == idContrato)
            .findFirst()
            .orElse(null);
    }
    
    // ==================== GESTIÓN DE ENTRADAS/SALIDAS ====================
    
    /**
     * Registra una entrada de vehículo al parking.
     * @param entrada La entrada a registrar
     */
    public void registrarEntrada(Entrada entrada) {
        entradas.add(entrada);
    }
    
    /**
     * Registra una salida de vehículo del parking.
     * @param salida La salida a registrar
     * @throws IllegalStateException Si ya existe una salida para esta entrada
     * @throws IllegalArgumentException Si la entrada o empleado no existen
     */
    public synchronized void registrarSalida(Salida salida) {
        if (salidas.stream().anyMatch(s -> s.getEntrada().equals(salida.getEntrada()))) {
            throw new IllegalStateException("Ya existe una salida registrada para esta entrada");
        }
        if (!entradas.contains(salida.getEntrada())) {
            throw new IllegalArgumentException("La entrada asociada no existe en el sistema");
        }
        if (!empleados.contains(salida.getEmpleado())) {
            throw new IllegalArgumentException("El empleado asociado no existe en el sistema");
        }
        salidas.add(salida);
    }
    
    /**
     * Obtiene las entradas que no tienen salida registrada (vehículos aún en el parking).
     * @return Lista de entradas sin salida
     */
    public List<Entrada> getEntradasSinSalida() {
        return entradas.stream()
            .filter(e -> !entradaTieneSalida(e))
            .collect(Collectors.toList());
    }
    
    /**
     * Verifica si una entrada tiene salida registrada.
     * @param entrada La entrada a verificar
     * @return true si tiene salida registrada, false en caso contrario
     */
    private boolean entradaTieneSalida(Entrada entrada) {
        return salidas.stream()
            .anyMatch(s -> s.getEntrada().equals(entrada));
    }

    /**
     * Busca una entrada activa por matrícula de vehículo.
     * @param matricula La matrícula del vehículo
     * @return La entrada encontrada o null si no existe
     */
    public Entrada buscarEntradaPorMatricula(String matricula) {
        for (Entrada entrada : entradas) {
            if (entrada.getVehiculo().getMatricula().equals(matricula) && !entradaTieneSalida(entrada)) {
                return entrada;
            }
        }
        return null;
    }

    // ==================== GESTIÓN DE SERVICIOS ====================
    
    /**
     * Registra un servicio adicional para un vehículo.
     * @param servicio El servicio a registrar
     */
    public void registrarServicio(Servicio servicio) {
        servicios.add(servicio);
    }
    
    /**
     * Obtiene una copia de la lista de servicios.
     * @return Lista de servicios
     */
    public List<Servicio> getServicios() {
        return new ArrayList<>(servicios);
    }
    
    /**
     * Elimina un servicio por su índice en la lista.
     * @param index Índice del servicio a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public synchronized boolean eliminarServicio(int index) {
        try {
            if (index >= 0 && index < servicios.size()) {
                servicios.remove(index);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error al eliminar servicio: " + e.getMessage());
            return false;
        }
    }
    
    // ==================== REPORTES Y ESTADÍSTICAS ====================
    
    /**
     * Obtiene todos los movimientos de un vehículo (entradas, salidas y servicios).
     * @param vehiculo El vehículo del cual obtener los movimientos
     * @return Lista de movimientos del vehículo
     */
    public List<Servicio> getMovimientosPorVehiculo(Vehiculo vehiculo) {
        List<Servicio> movimientos = new ArrayList<>();
        // Agregar entradas como servicios especiales
        entradas.stream()
            .filter(e -> e.getVehiculo().equals(vehiculo))
            .forEach(e -> movimientos.add(
                new Servicio(
                    "Entrada al parking", 
                    e.getVehiculo(), 
                    e.getEmpleado(), 
                    e.getFecha(), 
                    e.getHora(), 
                    0.0 
                )
            ));
        // Agregar salidas como servicios especiales
        salidas.stream()
            .filter(s -> s.getEntrada().getVehiculo().equals(vehiculo))
            .forEach(s -> movimientos.add(
                new Servicio(
                    "Salida del parking", 
                    s.getEntrada().getVehiculo(), 
                    s.getEmpleado(), 
                    s.getFecha(), 
                    s.getHora(), 
                    0.0 
                )
            ));
        // Agregar servicios reales
        servicios.stream()
            .filter(s -> s.getVehiculo().equals(vehiculo))
            .forEach(movimientos::add);
        return movimientos;
    }
    
    /**
     * Obtiene un mapa con la cantidad de veces que se ha usado cada tipo de servicio.
     * @return Mapa donde la clave es el tipo de servicio y el valor es la cantidad
     */
    public Map<String, Long> getServiciosMasUtilizados() {
        return servicios.stream().collect(Collectors.groupingBy(Servicio::getTipo, Collectors.counting()));
    }
    
    /**
     * Obtiene un mapa con la cantidad de vehículos que tiene cada cliente.
     * @return Mapa donde la clave es el cliente y el valor es la cantidad de vehículos
     */
    public Map<Cliente, Long> getClientesConMasVehiculos() {
        return contratos.stream().collect(Collectors.groupingBy(Contrato::getCliente, Collectors.counting()));
    }
    
    /**
     * Obtiene un mapa con la cantidad de movimientos que ha realizado cada empleado.
     * @return Mapa donde la clave es el empleado y el valor es la cantidad de movimientos
     */
    public Map<Empleado, Long> getEmpleadosConMenosMovimientos() {
        Map<Empleado, Long> movimientos = new HashMap<>();
        entradas.forEach(e -> 
            movimientos.merge(e.getEmpleado(), 1L, Long::sum));
        salidas.forEach(s -> 
            movimientos.merge(s.getEmpleado(), 1L, Long::sum));
        servicios.forEach(s -> 
            movimientos.merge(s.getEmpleado(), 1L, Long::sum));
        return movimientos;
    }
    
    // ==================== PERSISTENCIA DE DATOS ====================
    
    /**
     * Guarda el estado actual del sistema en un archivo.
     */
    public void guardarDatos() {
        try {
            java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(
                new java.io.FileOutputStream(ARCHIVO_DATOS));
            out.writeObject(this);
            out.close();
        } catch (Exception e) {
            System.err.println("Error al guardar datos: " + e.getMessage());
        }
    }
    
    /**
     * Carga el sistema desde un archivo guardado previamente.
     * @return El sistema cargado o un nuevo sistema si no existe archivo
     */
    public static Sistema cargarDatos() {
        try {
            java.io.ObjectInputStream in = new java.io.ObjectInputStream(
                new java.io.FileInputStream(ARCHIVO_DATOS));
            Sistema sistema = (Sistema) in.readObject();
            in.close();
            return sistema;
        } catch (Exception e) {
            System.out.println("Creando nuevo sistema. Motivo: " + e.getMessage());
            return new Sistema();
        }
    }
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    /**
     * Reinicia el sistema, eliminando todos los datos.
     */
    public void reiniciarSistema() {
        clientes.clear();
        vehiculos.clear();
        empleados.clear();
        contratos.clear();
        entradas.clear();
        salidas.clear();
        servicios.clear();
    }
    
    // ==================== MÉTODOS PARA REPORTES Y LOS MOVIMIENTOS ====================
    
    /**
     * Obtiene el historial completo de movimientos de un vehículo.
     * @param matricula La matrícula del vehículo
     * @return Lista de arrays de objetos con los datos de cada movimiento
     */
    public List<Object[]> getHistorialMovimientos(String matricula) {
        List<Object[]> movimientos = new ArrayList<>();
        // Agregar entradas
        entradas.stream()
            .filter(e -> e.getVehiculo().getMatricula().equals(matricula))
            .forEach(e -> movimientos.add(new Object[]{
                Reporte.toLocalDateTime(e.getFecha(), e.getHora()),
                "Entrada",
                "Recibido por: " + e.getEmpleado().getNombre() + " (#" + e.getEmpleado().getNumeroEmpleado() + ")",
                e.getNotas()
            }));
        // Agregar salidas
        salidas.stream()
            .filter(s -> s.getEntrada().getVehiculo().getMatricula().equals(matricula))
            .forEach(s -> movimientos.add(new Object[]{
                Reporte.toLocalDateTime(s.getFecha(), s.getHora()),
                "Salida",
                "Atendido por: " + s.getEmpleado().getNombre() + " (#" + s.getEmpleado().getNumeroEmpleado() + ")",
                s.getComentario()
            }));
        // Agregar servicios
        servicios.stream()
            .filter(s -> s.getVehiculo().getMatricula().equals(matricula))
            .forEach(s -> movimientos.add(new Object[]{
                Reporte.toLocalDateTime(s.getFecha(), s.getHora()),
                "Servicio: " + s.getTipo(),
                "Realizado por: " + s.getEmpleado().getNombre() + " (#" + s.getEmpleado().getNumeroEmpleado() + ")",
                "Costo: $" + s.getCosto()
            }));
        return movimientos;
    }

    /**
     * Cuenta los movimientos en un intervalo de tiempo específico.
     * @param fecha La fecha a consultar
     * @param intervalo El intervalo de tiempo (0: 0-5:59, 1: 6-11:59, 2: 12-17:59, 3: 18-23:59)
     * @return Cantidad de movimientos en el intervalo
     */
    public int contarMovimientos(LocalDate fecha, int intervalo) {
        return (int) Stream.concat(entradas.stream(), 
                  Stream.concat(salidas.stream(), servicios.stream()))
            .filter(mov -> {
                LocalDate movFecha = Reporte.toLocalDate(getFechaFromMovimiento(mov));
                if (movFecha == null || !movFecha.equals(fecha)) return false;
                int hora = Reporte.getHoraFromMovimiento(mov);
                return (intervalo == 0 && hora < 6) ||
                       (intervalo == 1 && hora >= 6 && hora < 12) ||
                       (intervalo == 2 && hora >= 12 && hora < 18) ||
                       (intervalo == 3 && hora >= 18);
            })
            .count();
    }
    
    /**
     * Método auxiliar para obtener la fecha de cualquier movimiento.
     * @param movimiento El movimiento (Entrada, Salida o Servicio)
     * @return La fecha del movimiento o null si no es un tipo válido
     */
    private Date getFechaFromMovimiento(Object movimiento) {
        if (movimiento instanceof Entrada e) return e.getFecha();
        if (movimiento instanceof Salida s) return s.getFecha();
        if (movimiento instanceof Servicio s) return s.getFecha();
        return null;
    }

    /**
     * Obtiene los movimientos en un intervalo de tiempo específico.
     * @param fecha La fecha a consultar
     * @param intervalo El intervalo de tiempo (0: 0-5:59, 1: 6-11:59, 2: 12-17:59, 3: 18-23:59)
     * @return Lista de arrays de objetos con los datos de cada movimiento
     */
    public List<Object[]> getMovimientosIntervalo(LocalDate fecha, int intervalo) {
        List<Object[]> movimientos = new ArrayList<>();
        // Filtrar entradas en el intervalo
        entradas.stream()
            .filter(e -> {
                LocalDate movFecha = Reporte.toLocalDate(e.getFecha());
                if (movFecha == null || !movFecha.equals(fecha)) return false;
                int hora = Reporte.getHoraFromMovimiento(e);
                return (intervalo == 0 && hora < 6) ||
                       (intervalo == 1 && hora >= 6 && hora < 12) ||
                       (intervalo == 2 && hora >= 12 && hora < 18) ||
                       (intervalo == 3 && hora >= 18);
            })
            .forEach(e -> movimientos.add(new Object[]{
                Reporte.toLocalDateTime(e.getFecha(), e.getHora()),
                e.getVehiculo().getMatricula(),
                "Entrada",
                e.getEmpleado().getNombre() + " (#" + e.getEmpleado().getNumeroEmpleado() + ")"
            }));
        // Filtrar salidas en el intervalo
        salidas.stream()
            .filter(s -> {
                LocalDate movFecha = Reporte.toLocalDate(s.getFecha());
                if (movFecha == null || !movFecha.equals(fecha)) return false;
                int hora = Reporte.getHoraFromMovimiento(s);
                return (intervalo == 0 && hora < 6) ||
                       (intervalo == 1 && hora >= 6 && hora < 12) ||
                       (intervalo == 2 && hora >= 12 && hora < 18) ||
                       (intervalo == 3 && hora >= 18);
            })
            .forEach(s -> movimientos.add(new Object[]{
                Reporte.toLocalDateTime(s.getFecha(), s.getHora()),
                s.getEntrada().getVehiculo().getMatricula(),
                "Salida",
                s.getEmpleado().getNombre() + " (#" + s.getEmpleado().getNumeroEmpleado() + ")"
            }));
        // Filtrar servicios en el intervalo
        servicios.stream()
            .filter(s -> {
                LocalDate movFecha = Reporte.toLocalDate(s.getFecha());
                if (movFecha == null || !movFecha.equals(fecha)) return false;
                int hora = Reporte.getHoraFromMovimiento(s);
                return (intervalo == 0 && hora < 6) ||
                       (intervalo == 1 && hora >= 6 && hora < 12) ||
                       (intervalo == 2 && hora >= 12 && hora < 18) ||
                       (intervalo == 3 && hora >= 18);
            })
            .forEach(s -> movimientos.add(new Object[]{
                Reporte.toLocalDateTime(s.getFecha(), s.getHora()),
                s.getVehiculo().getMatricula(),
                "Servicio: " + s.getTipo(),
                s.getEmpleado().getNombre() + " (#" + s.getEmpleado().getNumeroEmpleado() + ")"
            }));
        // Ordenar movimientos por fecha/hora
        movimientos.sort(Comparator.comparing(mov -> (LocalDateTime) mov[0]));
        return movimientos;
    }

    /**
     * Obtiene el servicio más utilizado en el parking.
     * @return String con el tipo de servicio más usado y su cantidad
     */
    public String getServicioMasUtilizado() {
        if (servicios.isEmpty()) {
            return "No hay servicios registrados";
        }
        Map<String, Long> conteoServicios = servicios.stream()
            .collect(Collectors.groupingBy(Servicio::getTipo, Collectors.counting()));
        Map.Entry<String, Long> maxEntry = conteoServicios.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .orElse(null);
        if (maxEntry == null) {
            return "No se pudo determinar";
        }
        return maxEntry.getKey() + " (" + maxEntry.getValue() + " veces)";
    }

    /**
     * Obtiene el empleado con menos movimientos registrados.
     * @return String con el nombre del empleado y su cantidad de movimientos
     */
    public String getEmpleadoMenosMovimientos() {
        Map<Empleado, Long> conteoMovimientos = new HashMap<>();
        entradas.stream()
            .filter(e -> e.getEmpleado().getActivo())
            .forEach(e -> conteoMovimientos.merge(e.getEmpleado(), 1L, Long::sum));
        salidas.stream()
            .filter(s -> s.getEmpleado().getActivo())
            .forEach(s -> conteoMovimientos.merge(s.getEmpleado(), 1L, Long::sum));
        servicios.stream()
            .filter(s -> s.getEmpleado().getActivo())
            .forEach(s -> conteoMovimientos.merge(s.getEmpleado(), 1L, Long::sum));
        if (conteoMovimientos.isEmpty()) {
            return "No hay empleados con movimientos";
        }
        Map.Entry<Empleado, Long> minEntry = conteoMovimientos.entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .orElse(null);
        if (minEntry == null) {
            return "No se pudo determinar";
        }
        Empleado emp = minEntry.getKey();
        return emp.getNombre() + " (#" + emp.getNumeroEmpleado() + ") - " + minEntry.getValue() + " movimientos";
    }

    /**
     * Obtiene el empleado con más movimientos registrados.
     * @return String con el nombre del empleado y su cantidad de movimientos
     */
    public String getEmpleadoMasMovimientos() {
        Map<Empleado, Long> conteoMovimientos = new HashMap<>();
        entradas.forEach(e -> conteoMovimientos.merge(e.getEmpleado(), 1L, Long::sum));
        salidas.forEach(s -> conteoMovimientos.merge(s.getEmpleado(), 1L, Long::sum));
        servicios.forEach(s -> conteoMovimientos.merge(s.getEmpleado(), 1L, Long::sum));
        if (conteoMovimientos.isEmpty()) {
            return "No hay empleados con movimientos";
        }
        Map.Entry<Empleado, Long> maxEntry = conteoMovimientos.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .orElse(null);
        if (maxEntry == null) {
            return "No se pudo determinar";
        }
        Empleado emp = maxEntry.getKey();
        return emp.getNombre() + " (#" + emp.getNumeroEmpleado() + ") - " + maxEntry.getValue() + " movimientos";
    }

    /**
     * Obtiene el cliente con más vehículos registrados.
     * @return String con el nombre del cliente y su cantidad de vehículos
     */
    public String getClienteMasVehiculos() {
        Map<Cliente, Long> conteoPorCliente = contratos.stream()
            .filter(Contrato::getActivo)
            .collect(Collectors.groupingBy(Contrato::getCliente, Collectors.counting()));
        if (conteoPorCliente.isEmpty()) {
            return "No hay contratos activos";
        }
        Map.Entry<Cliente, Long> maxEntry = conteoPorCliente.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .orElse(null);
        if (maxEntry == null) {
            return "No se pudo determinar";
        }
        Cliente cliente = maxEntry.getKey();
        return cliente.getNombre() + " (" + maxEntry.getValue() + " vehículos)";
    }

    /**
     * Obtiene estadísticas generales sobre los contratos.
     * @return String con cantidad de contratos activos, inactivos y valor promedio
     */
    public String getEstadisticasContratos() {
        long totalContratos = contratos.size();
        long activos = contratos.stream().filter(Contrato::getActivo).count();
        long inactivos = totalContratos - activos;
        double valorPromedio = contratos.stream()
            .filter(Contrato::getActivo)
            .mapToDouble(Contrato::getValorMensual)
            .average()
            .orElse(0.0);
        return String.format(
            "Contratos activos: %d | Contratos inactivos: %d | Valor promedio: $%.2f",
            activos, inactivos, valorPromedio
        );
    }
    
    /**
     * Obtiene la estadía más larga registrada en el parking.
     * @return String con información del vehículo y duración de la estadía
     */
    public String getEstadiaMasLarga() {
        if (salidas.isEmpty()) {
            return "No hay salidas registradas";
        }
        Optional<Salida> salidaMasLarga = salidas.stream()
            .filter(s -> s.getEntrada() != null)
            .max(Comparator.comparingLong(s -> {
                Entrada e = s.getEntrada();
                return Duration.between(
                    Reporte.toLocalDateTime(e.getFecha(), e.getHora()),
                    Reporte.toLocalDateTime(s.getFecha(), s.getHora())
                ).toMinutes();
            }));
        if (!salidaMasLarga.isPresent()) {
            return "No se pudo determinar";
        }
        Salida salida = salidaMasLarga.get();
        Entrada entrada = salida.getEntrada();
        long minutos = Duration.between(
            Reporte.toLocalDateTime(entrada.getFecha(), entrada.getHora()),
            Reporte.toLocalDateTime(salida.getFecha(), salida.getHora())
        ).toMinutes();
        long horas = minutos / 60;
        long minRest = minutos % 60;
        Vehiculo v = entrada.getVehiculo();
        return v.getMatricula() + " (" + v.getMarca() + " " + v.getModelo() + 
               ") - " + horas + "h " + minRest + "m";
    }    
}

/*
Funcionalidades clave de la clase Sistema:
1.Gestión de entidades:
(Crear, Leer, Actualizar, Eliminar) para clientes, vehículos, empleados, etc.
Validación de unicidad (cédulas, matrículas, etc.)
Relaciones entre entidades (contratos que vinculan clientes y vehículos)
2.Operaciones del parking:
Registro de entradas y salidas de vehículos
Gestión de servicios adicionales
Control de vehículos actualmente en el parking
3.Reportes y estadísticas:
Historial de movimientos por vehículo
Grilla de movimientos por intervalos horarios
Estadísticas de servicios más utilizados
Empleados con más/menos movimientos
Estadías
*/