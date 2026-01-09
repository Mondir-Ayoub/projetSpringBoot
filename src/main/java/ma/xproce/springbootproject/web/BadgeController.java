package ma.xproce.springbootproject.web;

import lombok.AllArgsConstructor;
import ma.xproce.springbootproject.dao.entities.Inscription;
import ma.xproce.springbootproject.dao.entities.enums.StatutBadge;
import ma.xproce.springbootproject.dao.repositories.InscriptionRepository;
import ma.xproce.springbootproject.service.BadgeManager;
import ma.xproce.springbootproject.service.dtos.InscriptionDto;
import ma.xproce.springbootproject.service.mappers.InscriptionMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // On utilise RestController pour renvoyer un flux de fichier
@RequestMapping("/api/badges")
@AllArgsConstructor
public class BadgeController {
    private final InscriptionRepository inscriptionRepository;
    private final BadgeManager badgeManager;
    private final InscriptionMapper inscriptionMapper;

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> telechargerBadge(@PathVariable Long id) {
        Inscription inscription = inscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable"));

        // MISE À JOUR DU STATUT
        if (inscription.getStatutBadge() != StatutBadge.RECUPERE) {
            inscription.setStatutBadge(StatutBadge.RECUPERE);
            inscriptionRepository.save(inscription);
        }

        // Générer le PDF à la volée
        InscriptionDto dto = inscriptionMapper.fromEntity(inscription);
        String eventName = inscription.getEvenement().getTitre();
        String userName = inscription.getUtilisateur().getNom() + " " + inscription.getUtilisateur().getPrenom();

        byte[] pdfContent = badgeManager.genererBadgePdf(dto, eventName, userName);

        // Renvoyer le fichier au navigateur
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=badge_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }
}
