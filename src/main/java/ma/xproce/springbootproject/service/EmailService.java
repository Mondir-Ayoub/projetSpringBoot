package ma.xproce.springbootproject.service;

public interface EmailService {
    void envoyerEmailBadge(String destinataire, String nomUser, Long inscriptionId);
}
