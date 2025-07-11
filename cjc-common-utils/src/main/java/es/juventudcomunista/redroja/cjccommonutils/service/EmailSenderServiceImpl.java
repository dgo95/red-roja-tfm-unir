package es.juventudcomunista.redroja.cjccommonutils.service;


import es.juventudcomunista.redroja.cjccommonutils.email.EmailDetails;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderServiceImpl implements EmailSenderService{


    private final KafkaTemplate<String, EmailDetails> kafkaTemplate;

    @Override
    public void sendEmail(String topic, EmailDetails email) {
        log.info("Enviando mensaje al tema: {} con el correo: {}", topic, email);
        CompletableFuture<SendResult<String, EmailDetails>> future = kafkaTemplate.send(topic, email);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Mensaje enviado exitosamente al topic: {} con offset: {}", topic, result.getRecordMetadata().offset());
            } else {
                log.error("Error al enviar el mensaje al topic: {}", topic, ex);
            }
        });
    }
}