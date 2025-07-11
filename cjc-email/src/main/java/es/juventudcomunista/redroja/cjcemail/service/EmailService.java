package es.juventudcomunista.redroja.cjcemail.service;

import java.io.File;
import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import es.juventudcomunista.redroja.cjccommonutils.email.EmailDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import es.juventudcomunista.redroja.cjcemail.entity.email.EmailEnviado;
import es.juventudcomunista.redroja.cjcemail.entity.email.EmailStatus;
import es.juventudcomunista.redroja.cjcemail.repositorio.EmailEnviadoRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String desde;

	@Value("classpath:/static/Cjc.png")
	Resource resourceFile;

	private final TemplateEngine templateEngine;
	

	private final EmailEnviadoRepository emailEnviadoRepository;

	public boolean sendMailWithAttachment(EmailDetails email, String plantilla) {
	    var context = new Context();
	    email.getVariables().forEach(context::setVariable);

	    var mimeMessage = javaMailSender.createMimeMessage();
	    String body = templateEngine.process(plantilla + ".html", context);
	    var registro = inicializarRegistro(email, body);

	    try {
	        var mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            log.debug("Preparando mensaje MIME para el envío");
	        mimeMessageHelper.setFrom(desde);
	        mimeMessageHelper.setTo(email.getTo());
	        mimeMessageHelper.setText(body, true);
	        mimeMessageHelper.addInline("attachment.png", resourceFile);
	        mimeMessageHelper.setSubject(email.getSubject());

	        addAttachmentIfPresent(email, mimeMessageHelper);

	        javaMailSender.send(mimeMessage);
	        updateRegistro(registro, EmailStatus.ENVIADO, "Enviado correctamente", 0);
            log.info("Email enviado correctamente a: {}", email.getTo());
	    } catch (MessagingException | MailAuthenticationException e) {
            log.warn("Error al construir el email: {}", e.getMessage());
	        handleSendMailException(registro, e, "Error al construir el email.");
	    } catch (MailSendException e) {
            log.warn("Error al enviar el email: {}", e.getMessage());
	        handleMailSendException(registro, e, mimeMessage);
	    }

	    return registro.getEstado() == EmailStatus.ENVIADO;
	}

	private void addAttachmentIfPresent(EmailDetails email, MimeMessageHelper helper) throws MessagingException {
	    if (email.getAttachment() != null) {
	        FileSystemResource resource = new FileSystemResource(new File(email.getAttachment()));
	        helper.addAttachment(resource.getFilename(), resource);
            log.debug("Adjuntando archivo: {}", resource.getFilename());
	    }
	}

	private void updateRegistro(EmailEnviado registro, EmailStatus estado, String comentarios, int numeroReintentos) {
	    registro.setEstado(estado);
	    registro.setUltimaModificacion(LocalDateTime.now());
	    registro.setComentarios(comentarios);
	    registro.setNumeroReintentos(numeroReintentos);
	    emailEnviadoRepository.save(registro);
        log.info("Registro de email actualizado: Estado - {}, Comentarios - {}", estado, comentarios);
	}

	private void handleSendMailException(EmailEnviado registro, Exception e, String errorMessage) {
	    updateRegistro(registro, EmailStatus.FALLIDO, errorMessage, 0);
	}

	private void handleMailSendException(EmailEnviado registro, MailSendException e, MimeMessage mimeMessage) {
	    int intentos = reintentarEnvio(mimeMessage);
	    String comentario = intentos < 3 ? "Enviado correctamente al intento: " + intentos : "No se ha podido enviar en 3 reintentos";
	    updateRegistro(registro, intentos < 3 ? EmailStatus.ENVIADO : EmailStatus.FALLIDO, comentario, intentos);
	}

	private int reintentarEnvio(MimeMessage mimeMessage) {
	    int intentos;
	    for (intentos = 0; intentos < 3; intentos++) {
	        try {
	            javaMailSender.send(mimeMessage);
	            break;
	        } catch (MailSendException ignored) {
                log.debug("Reintentando envío, intento número {}", intentos + 1);
	        }
	    }
	    return intentos;
	}


	private EmailEnviado inicializarRegistro(EmailDetails email, String body) {
		var registro = new EmailEnviado();
    	registro.setTo(email.getTo());
    	registro.setSubject(email.getSubject());
    	registro.setNumeroReintentos(0);
    	registro.setFrom(desde);
    	registro.setFechaEnvio(LocalDateTime.now());
    	registro.setUltimaModificacion(LocalDateTime.now());
    	registro.setEstado(EmailStatus.PENDIENTE);
    	registro.setComentarios("Recibido en servicio email.");
    	registro.setBody("");
        log.debug("Inicializando registro para el envío de email a: {}", email.getTo());
    	return emailEnviadoRepository.save(registro);
	}
}
