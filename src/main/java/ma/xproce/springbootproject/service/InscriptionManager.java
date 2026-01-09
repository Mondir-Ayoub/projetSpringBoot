package ma.xproce.springbootproject.service;

import lombok.AllArgsConstructor;
import ma.xproce.springbootproject.dao.entities.Evenement;
import ma.xproce.springbootproject.dao.entities.Inscription;
import ma.xproce.springbootproject.dao.entities.Utilisateur;
import ma.xproce.springbootproject.dao.entities.enums.StatutBadge;
import ma.xproce.springbootproject.dao.repositories.EvenementRepository;
import ma.xproce.springbootproject.dao.repositories.InscriptionRepository;
import ma.xproce.springbootproject.dao.repositories.UtilisateurRepository;
import ma.xproce.springbootproject.service.dtos.InscriptionDto;
import ma.xproce.springbootproject.service.mappers.InscriptionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class InscriptionManager implements InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EvenementRepository evenementRepository;
    private final InscriptionMapper inscriptionMapper;
    private final EmailService emailService;

    @Override
    public InscriptionDto creerInscription(InscriptionDto dto) {
        boolean dejaInscrit = inscriptionRepository.existsByUtilisateurIdAndEvenementId(
                dto.getUtilisateurId(), dto.getEvenementId());

        if (dejaInscrit) {
            throw new RuntimeException("Vous êtes déjà inscrit à cet événement !");
        }

        Inscription inscription = inscriptionMapper.toEntity(dto);
        inscription.setDatePostulation(LocalDate.now());

        Utilisateur utilisateur = utilisateurRepository.findById(dto.getUtilisateurId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        inscription.setUtilisateur(utilisateur);

        Evenement evenement = evenementRepository.findById(dto.getEvenementId())
                .orElseThrow(() -> new RuntimeException("Événement introuvable"));
        inscription.setEvenement(evenement);

        inscription.setStatutBadge(StatutBadge.CONFIRME);
        inscription = inscriptionRepository.save(inscription);

        try {
            emailService.envoyerEmailBadge(utilisateur.getEmail(), utilisateur.getPrenom(), inscription.getId());
        } catch (Exception e) {

            System.err.println("Erreur d'envoi de mail : " + e.getMessage());
        }

        return inscriptionMapper.fromEntity(inscription);
    }

    @Override
    public InscriptionDto modifierInscription(Long id, InscriptionDto dto) {
        Inscription inscription = inscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable"));

        if (dto.getStatutBadge() != null) inscription.setStatutBadge(dto.getStatutBadge());
        if (dto.getDatePostulation() != null) inscription.setDatePostulation(dto.getDatePostulation());

        return inscriptionMapper.fromEntity(inscriptionRepository.save(inscription));
    }

    @Override
    public boolean supprimerInscription(Long id) {
        if (inscriptionRepository.existsById(id)) {
            inscriptionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public InscriptionDto chercherParId(Long id) {
        return inscriptionRepository.findById(id)
                .map(inscriptionMapper::fromEntity)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable"));
    }

    @Override
    public List<InscriptionDto> listerInscriptions() {
        return inscriptionRepository.findAll().stream()
                .map(inscriptionMapper::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<InscriptionDto> listerInscriptionsParUtilisateur(Long userId) {
        return inscriptionRepository.findByUtilisateurId(userId)
                .stream()
                .map(inscriptionMapper::fromEntity)
                .collect(Collectors.toList());
    }


    @Override
    public List<InscriptionDto> listerInscriptionsParEvenement(Long evenementId) {
        return inscriptionRepository.findByEvenementId(evenementId)
                .stream()
                .map(inscriptionMapper::fromEntity)
                .collect(Collectors.toList());
    }



    @Override
    public List<InscriptionDto> listerInscriptionsParClub(Long clubId) {
        return inscriptionRepository.findByEvenementClubId(clubId)
                .stream()
                .map(inscriptionMapper::fromEntity)
                .collect(Collectors.toList());
    }
}