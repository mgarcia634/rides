package utils;

import domain.EmailMessage;

public class EmailService {

    public static void send(String to, String subject, String body) {
        System.out.println("Simulando envío de correo a " + to + ": " + subject);

        // Guardar el mensaje en el buzón
        EmailMessage message = new EmailMessage(to, subject, body);
        EmailInbox.storeMessage(message);
    }
}
