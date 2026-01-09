package ma.xproce.springbootproject.service;

import lombok.AllArgsConstructor;
import ma.xproce.springbootproject.dao.entities.Club;
import ma.xproce.springbootproject.dao.entities.Evenement;
import ma.xproce.springbootproject.dao.entities.Utilisateur;
import ma.xproce.springbootproject.dao.entities.enums.StatutEvenement;
import ma.xproce.springbootproject.dao.repositories.AdhesionRepository;
import ma.xproce.springbootproject.dao.repositories.ClubRepository;
import ma.xproce.springbootproject.dao.repositories.EvenementRepository;
import ma.xproce.springbootproject.dao.repositories.UtilisateurRepository;
import ma.xproce.springbootproject.service.dtos.EvenementDto;
import ma.xproce.springbootproject.service.mappers.EvenementMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class EvenementManager implements EvenementService {
    private final EvenementRepository evenementRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final AdhesionRepository adhesionRepository;
    private final EvenementMapper evenementMapper;
    private final ClubRepository clubRepository;

    @Override
    public List<EvenementDto> recupererEvenementsPourUtilisateur(String email) {

        UserData droits = calculerDroits(email);

        if (droits.isAdmin) {
            return listerEvenements();
        }

        List<Evenement> events = evenementRepository.findAllAllowedEvents(droits.isEnsamien, droits.clubIds);
        return events.stream().map(evenementMapper::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<EvenementDto> rechercherEvenements(String motCle, String email) {
        if (motCle == null || motCle.trim().isEmpty()) {
            return recupererEvenementsPourUtilisateur(email);
        }

        UserData droits = calculerDroits(email);

        if (droits.isAdmin) {
            return evenementRepository.findAll().stream()
                    .filter(e -> e.getTitre().toLowerCase().contains(motCle.toLowerCase()))
                    .map(evenementMapper::fromEntity)
                    .collect(Collectors.toList());
        }

        List<Evenement> events = evenementRepository.searchAllowedEvents(motCle, droits.isEnsamien, droits.clubIds);
        return events.stream().map(evenementMapper::fromEntity).collect(Collectors.toList());
    }

    private UserData calculerDroits(String email) {
        Utilisateur user = null;
        if (email != null && !email.equals("anonymousUser")) {
            user = utilisateurRepository.findByEmail(email).orElse(null);
        }

        boolean isAdmin = false;
        boolean isEnsamien = false;
        List<Long> clubIds = new ArrayList<>();

        if (user != null) {
            isAdmin = user.getRoles().stream().anyMatch(r -> r.getNom().equals("ADMIN"));
            isEnsamien = user.getRoles().stream().anyMatch(r -> r.getNom().equals("ENSAMIEN"));
            clubIds = adhesionRepository.findClubIdsByUserId(user.getId());
        }

        if (clubIds.isEmpty()) clubIds.add(-1L); // Évite erreur SQL IN ()

        return new UserData(isAdmin, isEnsamien, clubIds);
    }

    // classe interne pour transporter les droits
    private static class UserData {
        boolean isAdmin;
        boolean isEnsamien;
        List<Long> clubIds;

        public UserData(boolean isAdmin, boolean isEnsamien, List<Long> clubIds) {
            this.isAdmin = isAdmin;
            this.isEnsamien = isEnsamien;
            this.clubIds = clubIds;
        }
    }

    @Override
    public EvenementDto creerEvenement(EvenementDto dto) {
        Utilisateur createur = utilisateurRepository.findById(dto.getCreateurId())
                .orElseThrow(() -> new RuntimeException("Créateur introuvable"));

        Evenement evenement = evenementMapper.toEntity(dto);
        evenement.setCreateur(createur);


        if (dto.getClubId() != null) {
            Club club = clubRepository.findById(dto.getClubId())
                    .orElseThrow(() -> new RuntimeException("Club introuvable"));
            evenement.setClub(club);
        }

        if (dto.getStatutEvenement() != null) {
            evenement.setStatutEvenement(dto.getStatutEvenement());
        } else {

            boolean isAdmin = createur.getRoles().stream()
                    .anyMatch(role -> role.getNom().equals("ADMIN"));
            evenement.setStatutEvenement(isAdmin ? StatutEvenement.PUBLIC : StatutEvenement.EN_ATTENTE_VALIDATION);
        }

        Evenement savedEvent = evenementRepository.save(evenement);

        return evenementMapper.fromEntity(savedEvent);
    }

    @Override
    public void validerEvenement(Long id) {
        Evenement event = evenementRepository.findById(id).orElseThrow();
        event.setStatutEvenement(StatutEvenement.PUBLIC);
        evenementRepository.save(event);
    }

    @Override
    public List<EvenementDto> listerEvenements() {
        return evenementRepository.findAll().stream().map(evenementMapper::fromEntity).collect(Collectors.toList());
    }

    @Override
    public EvenementDto chercherParId(Long id) {
        return evenementRepository.findById(id).map(evenementMapper::fromEntity).orElseThrow();
    }

    @Override
    public EvenementDto modifierEvenement(Long id, EvenementDto dto) {
        Evenement existing = evenementRepository.findById(id).orElseThrow();
        existing.setTitre(dto.getTitre());
        existing.setDescription(dto.getDescription());
        existing.setDateDebut(dto.getDateDebut());
        existing.setLocalisation(dto.getLocalisation());
        existing.setVisibiliteEvenement(dto.getVisibiliteEvenement());
        return evenementMapper.fromEntity(evenementRepository.save(existing));
    }

    @Override
    public boolean supprimerEvenement(Long id) {
        if (evenementRepository.existsById(id)) {
            evenementRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<EvenementDto> listerEvenementsParClub(Long clubId) {
        return evenementRepository.findByClubId(clubId).stream().map(evenementMapper::fromEntity).collect(Collectors.toList());
    }
}