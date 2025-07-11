package es.juventudcomunista.redroja.cjcrest.exception;


import org.springframework.messaging.MessagingException;

public class ErrorAlEnviarEmail extends Exception {

    public ErrorAlEnviarEmail(MessagingException e) {
       super(e);
    }

}
