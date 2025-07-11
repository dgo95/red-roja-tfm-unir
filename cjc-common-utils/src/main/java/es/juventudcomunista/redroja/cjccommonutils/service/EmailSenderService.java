package es.juventudcomunista.redroja.cjccommonutils.service;

import es.juventudcomunista.redroja.cjccommonutils.email.EmailDetails;

// Interface
public interface EmailSenderService {

    void sendEmail(String topic, EmailDetails email);
    
}