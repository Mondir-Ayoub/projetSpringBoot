package ma.xproce.springbootproject.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailManager implements EmailService {
    private final JavaMailSender mailSender;

    public void envoyerEmailBadge(String destinataire, String nomUser, Long inscriptionId) {
        String subject = "Votre Badge de participation ENSAM";

        String lienTelechargement = "http://localhost:8090/api/badges/download/" + inscriptionId;

        String content = "<h1>Bonjour " + nomUser + " !</h1>"
                + "<p>Votre inscription est confirmée.</p>"
                + "<p>Veuillez cliquer sur le bouton ci-dessous pour récupérer votre badge :</p>"
                + "<a href=\"" + lienTelechargement + "\" style=\"background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none;\">TÉLÉCHARGER MON BADGE</a>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinataire);
            helper.setSubject(subject);
            helper.setText(content, true); // true = HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur envoi mail", e);
        }
    }
}
