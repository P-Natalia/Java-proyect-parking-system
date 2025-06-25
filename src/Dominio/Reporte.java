package Dominio;

/**
 *
 * @author Natalia Peña 
 */

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;


/*
  Clase utilitaria para generar reportes y realizar conversiones de fechas/horas.
  Proporciona métodos estáticos para manipulación de fechas, horas y cálculos de duración.
 */

public class Reporte {
    // Formateador constante para horas en formato HH:mm (24 horas)
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
     /**
     * Convierte un objeto Date y una cadena de hora a LocalDateTime.
     * 
     * @param fecha Objeto Date que representa la fecha
     * @param horaStr String que representa la hora en formato HH:mm
     * @return LocalDateTime combinando la fecha y hora, o null si alguno de los parámetros es null
     */
    
    public static LocalDateTime toLocalDateTime(Date fecha, String horaStr) {
        if (fecha == null || horaStr == null) return null;
        LocalDate localDate = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime localTime = LocalTime.parse(horaStr, TIME_FORMATTER);
        return LocalDateTime.of(localDate, localTime);
    }
    
     /**
     * Obtiene la hora (0-23) de un movimiento (Entrada, Salida o Servicio).
     * 
     * @param movimiento Objeto que puede ser Entrada, Salida o Servicio
     * @return La hora del movimiento como entero (0-23), o -1 si el tipo no es reconocido
     */
    
    public static int getHoraFromMovimiento(Object movimiento) {
        if (movimiento instanceof Entrada e) {
            return LocalTime.parse(e.getHora(), TIME_FORMATTER).getHour();
        } else if (movimiento instanceof Salida s) {
            return LocalTime.parse(s.getHora(), TIME_FORMATTER).getHour();
        } else if (movimiento instanceof Servicio s) {
            return LocalTime.parse(s.getHora(), TIME_FORMATTER).getHour();
        }
        return -1;
    }
    /**
     * Convierte un objeto Date a LocalDate.
     * 
     * @param fecha Objeto Date a convertir
     * @return LocalDate correspondiente, o null si el parámetro es null
     */
    public static LocalDate toLocalDate(Date fecha) {
        if (fecha == null) return null;
        return fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    
    // Calcula la duración entre entrada y salida en minutos
    public static long calcularDuracionMinutos(Entrada entrada, Salida salida) {
        if (entrada == null || salida == null) return 0;
        
        LocalDateTime entradaDT = toLocalDateTime(entrada.getFecha(), entrada.getHora());
        LocalDateTime salidaDT = toLocalDateTime(salida.getFecha(), salida.getHora());
        
        return Duration.between(entradaDT, salidaDT).toMinutes();
    }
    
    // Formatea LocalDateTime a String legible
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTime.format(formatter);
    }
    
    // Convierte minutos a formato "Xh Ym"
    public static String formatDuracion(long minutos) {
        long horas = minutos / 60;
        long minRest = minutos % 60;
        return horas + "h " + minRest + "m";
    }
}
