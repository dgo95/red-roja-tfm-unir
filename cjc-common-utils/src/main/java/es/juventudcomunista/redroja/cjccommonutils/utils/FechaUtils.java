package es.juventudcomunista.redroja.cjccommonutils.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@UtilityClass
public class FechaUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LocalDate convertirALocalDate(LocalDateTime dateTime) {
        return dateTime.toLocalDate();
    }

    public LocalDateTime convertirADateTime(LocalDate date) {
        return date.atStartOfDay();
    }

    public String formatearFecha(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    public String formatearFechaHora(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    public LocalDate parsearFecha(String fecha) {
        return LocalDate.parse(fecha, DATE_FORMATTER);
    }

    public LocalDateTime parsearFechaHora(String fechaHora) {
        return LocalDateTime.parse(fechaHora, DATE_TIME_FORMATTER);
    }

    public LocalDate obtenerFechaActual() {
        return LocalDate.now();
    }

    public LocalDateTime obtenerFechaHoraActual() {
        return LocalDateTime.now();
    }

    public long calcularDiasEntreFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return ChronoUnit.DAYS.between(fechaInicio, fechaFin);
    }

    public long calcularHorasEntreFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ChronoUnit.HOURS.between(fechaInicio, fechaFin);
    }

    public LocalDate sumarDias(LocalDate fecha, long dias) {
        return fecha.plusDays(dias);
    }

    public LocalDate restarDias(LocalDate fecha, long dias) {
        return fecha.minusDays(dias);
    }

    public LocalDateTime sumarHoras(LocalDateTime fechaHora, long horas) {
        return fechaHora.plusHours(horas);
    }

    public LocalDateTime restarHoras(LocalDateTime fechaHora, long horas) {
        return fechaHora.minusHours(horas);
    }

    public LocalDateTime convertirADateTime(Date fecha) {
        return fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public Date convertirADate(LocalDate fecha) {
        return Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public Date convertirADate(LocalDateTime fechaHora) {
        return Date.from(fechaHora.atZone(ZoneId.systemDefault()).toInstant());
    }
}
