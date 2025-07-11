package es.juventudcomunista.redroja.cjcrest.scheduler;

import es.juventudcomunista.redroja.cjcrest.entity.Militante;
import es.juventudcomunista.redroja.cjcrest.repository.MilitanteRepository;
import es.juventudcomunista.redroja.cjcrest.repository.ReunionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import es.juventudcomunista.redroja.cjccommonutils.enums.EstadoCuota;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TareasProgramadas {
	

    private final ReunionRepository reunionRepository;
    private final MilitanteRepository militanteRepository;


    @Scheduled(cron = "0 0 10-22 * * *") // Se ejecuta cada hora entre las 10:00 y las 22:00
    public void finalizarReuniones() {
        log.info("Ejecutando la tarea finalizarReuniones...");

        var hoy = LocalDateTime.now();
        var reuniones = reunionRepository.findByTerminadaFalseAndFechaInicioBefore(hoy);
        
        reuniones.stream().forEach(reunion -> {
            reunion.setTerminada(true);
            reunionRepository.save(reunion);
        });

        log.info("Reuniones finalizadas: {}", reuniones.size());
    }
}

