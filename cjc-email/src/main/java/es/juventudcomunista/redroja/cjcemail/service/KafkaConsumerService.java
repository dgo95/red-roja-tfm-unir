package es.juventudcomunista.redroja.cjcemail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import es.juventudcomunista.redroja.cjccommonutils.email.EmailDetails;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
	private final EmailService emailService;

	@KafkaListener(topics = "email-activacion", groupId = "email-consumers")
	public void consumeActivacion(ConsumerRecord<String, EmailDetails> record) {
		EmailDetails message = record.value();
		log.info("Consuming activacion email: {}", message);
		emailService.sendMailWithAttachment(message, "activacion-template");
	}

	@KafkaListener(topics = "email-convocatoria", groupId = "email-consumers")
	public void consumeConvocatoria(ConsumerRecord<String, EmailDetails> record) {
		EmailDetails message = record.value();
		log.info("Consuming convocatoria email: {}", message);
		emailService.sendMailWithAttachment(message, "convocatoria-template");
	}

	@KafkaListener(topics = "email-edita-reunion", groupId = "email-consumers")
	public void consumeEditaConvocatoria(ConsumerRecord<String, EmailDetails> record) {
		EmailDetails message = record.value();
		log.info("Consuming edita-reunion email: {}", message);
		emailService.sendMailWithAttachment(message, "edita-reunion-template");
	}

	@KafkaListener(topics = "email-olvidado-pass", groupId = "email-consumers")
	public void consumeHeOlvidadoPass(ConsumerRecord<String, EmailDetails> record) {
		EmailDetails message = record.value();
		log.info("Consuming he olvidado mi contraseña email: {}", message);
		emailService.sendMailWithAttachment(message, "restablecer-template");
	}
}
