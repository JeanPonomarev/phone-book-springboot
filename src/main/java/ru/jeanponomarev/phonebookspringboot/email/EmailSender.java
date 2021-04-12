package ru.jeanponomarev.phonebookspringboot.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Component
public class EmailSender {

    @Value("${email.sender}")
    private String sender;

    @Value("${email.password}")
    private String password;

    @Value("${email.recipient}")
    private String recipient;

    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    public void sendEmail(String subject, String content) {

        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.status.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, password);
            }
        });

        Message message = prepareMessage(session, subject, content);

        try {
            if (message != null) {
                Transport.send(message);

                logger.debug(String.format("Email to %s has been successfully sent", recipient));
                logger.debug("Subject: " + message.getSubject());
            } else {
                logger.error("Email message is null");
            }
        } catch (MessagingException exception) {
            logger.error("MessageException: ", exception);
        }
    }

    private Message prepareMessage(Session session, String subject, String content) {
        Message message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(sender));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);
            message.setContent(content, "text/html");

            return message;
        } catch (MessagingException exception) {
            logger.error("MessageException: ", exception);

            return null;
        }
    }
}
