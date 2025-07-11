package es.juventudcomunista.redroja.cjcemail.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.event.ConsumerStartedEvent;
import org.springframework.kafka.event.ConsumerStoppedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaEventListener {

    @EventListener
    public void handleConsumerStarted(ConsumerStartedEvent event) {
        log.info("Conexión exitosa a Kafka. Consumidor iniciado: {}",event);
    }

    @EventListener
    public void handleConsumerStopped(ConsumerStoppedEvent event) {
        log.warn("El consumidor de Kafka se ha detenido: {}",event);
    }
}
