package tool;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * classe qui permet d'envoyer un mail
 */
public class Mail {

    /**
     * fonction qui permet d'envoyer un mail
     * @param subject le sujet du mail
     * @param content le message du mail
     * @param adresseMailTo l'adresse mail du destinataire
     */
    public static void sendMail(String subject, String content, String adresseMailTo) {

        String from = "pgl-01-autoprod@alwaysdata.net";
        final String username = "pgl-01-autoprod@alwaysdata.net";
        final String password = "Poutchy3601";


        String host = "smtp-pgl-01-autoprod.alwaysdata.net";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");


        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);


            message.setFrom(new InternetAddress(from));


            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(adresseMailTo));


            message.setSubject(subject);


            message.setText(content);


            Transport.send(message);


        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}

