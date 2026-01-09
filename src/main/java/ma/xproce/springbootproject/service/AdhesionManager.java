package ma.xproce.springbootproject.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import ma.xproce.springbootproject.dao.entities.Adhesion;
import ma.xproce.springbootproject.dao.entities.Club;
import ma.xproce.springbootproject.dao.entities.Role;
import ma.xproce.springbootproject.dao.entities.Utilisateur;
import ma.xproce.springbootproject.dao.entities.enums.StatutAdhesion;
import ma.xproce.springbootproject.dao.repositories.AdhesionRepository;
import ma.xproce.springbootproject.dao.repositories.ClubRepository;
import ma.xproce.springbootproject.dao.repositories.RoleRepository;
import ma.xproce.springbootproject.dao.repositories.UtilisateurRepository;
import ma.xproce.springbootproject.service.mappers.AdhesionMapper;
import ma.xproce.springbootproject.service.dtos.AdhesionDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class AdhesionManager implements AdhesionService {

    private final AdhesionRepository adhesionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ClubRepository clubRepository;
    private final AdhesionMapper adhesionMapper;
    private final RoleRepository roleRepository;

    @Override
    public AdhesionDto creerAdhesion(AdhesionDto dto) {
        // 1. Récupération des objets (Vérification existence)
        Utilisateur utilisateur = utilisateurRepository.findById(dto.getUtilisateurId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        Club club = clubRepository.findById(dto.getClubId())
                .orElseThrow(() -> new RuntimeException("Club introuvable"));

        // 2. Vérifier si déjà une demande ou une adhésion existe
        if (adhesionRepository.existsByUtilisateurAndClub(utilisateur, club)) {
            throw new RuntimeException("Une demande d'adhésion existe déjà pour cet utilisateur et ce club.");
        }

        // 3. Création
        Adhesion adhesion = adhesionMapper.toEntity(dto);
        adhesion.setUtilisateur(utilisateur);
        adhesion.setClub(club);

        // Par défaut, si pas précisé, c'est EN_ATTENTE
        if (adhesion.getStatutAdhesion() == null) {
            adhesion.setStatutAdhesion(StatutAdhesion.EN_ATTENTE);
        }
        if (adhesion.getDateDemande() == null) {
            adhesion.setDateDemande(LocalDate.now());
        }

        return adhesionMapper.fromEntity(adhesionRepository.save(adhesion));
    }


    @Override
    public AdhesionDto modifierAdhesion(Long id, AdhesionDto dto) {
        Adhesion adhesion = adhesionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adhésion introuvable"));

        if (dto.getStatutAdhesion() != null) adhesion.setStatutAdhesion(dto.getStatutAdhesion());
        if (dto.getDateDemande() != null) adhesion.setDateDemande(dto.getDateDemande());

        return adhesionMapper.fromEntity(adhesionRepository.save(adhesion));
    }

    @Override
    public boolean supprimerAdhesion(Long id) {
        if (adhesionRepository.existsById(id)) {
            adhesionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public AdhesionDto chercherParId(Long id) {
        return adhesionRepository.findById(id)
                .map(adhesionMapper::fromEntity)
                .orElseThrow(() -> new RuntimeException("Adhésion introuvable"));
    }

    @Override
    public List<AdhesionDto> listerAdhesions() {
        return adhesionRepository.findAll().stream()
                .map(adhesionMapper::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void forcerAdhesion(Long clubId, Long userId) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club introuvable"));

        // Vérification doublon avant de forcer
        if (adhesionRepository.existsByUtilisateurAndClub(utilisateur, club)) {
            throw new RuntimeException("L'utilisateur est déjà membre ou a une demande en cours.");
        }

        // Création directe
        Adhesion adhesion = new Adhesion();
        adhesion.setClub(club);
        adhesion.setUtilisateur(utilisateur);
        adhesion.setStatutAdhesion(StatutAdhesion.ACTIVE);
        adhesion.setDateDemande(LocalDate.now());
        adhesionRepository.save(adhesion);

        // Ajouter le rôle ADHERENT au user
        Role roleAdherent = roleRepository.findByNom("ADHERENT").orElseThrow();
        if (!utilisateur.getRoles().contains(roleAdherent)) {
            utilisateur.getRoles().add(roleAdherent);
            utilisateurRepository.save(utilisateur);
        }
    }

    @Override
    public void validerAdhesion(Long adhesionId) {
        Adhesion adhesion = adhesionRepository.findById(adhesionId)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));

        adhesion.setStatutAdhesion(StatutAdhesion.ACTIVE);
        adhesionRepository.save(adhesion);

        Utilisateur user = adhesion.getUtilisateur();
        Role roleAdherent = roleRepository.findByNom("ADHERENT").orElseThrow();

        if (!user.getRoles().contains(roleAdherent)) {
            user.getRoles().add(roleAdherent);
            utilisateurRepository.save(user);
        }
    }

    @Override
    public List<AdhesionDto> listerDemandesEnAttenteParClub(Long clubId) {
        // Correction : On récupère le vrai objet Club pour éviter les erreurs JPA
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club introuvable"));

        return adhesionRepository.findByClubAndStatutAdhesion(club, StatutAdhesion.EN_ATTENTE)
                .stream()
                .map(adhesionMapper::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdhesionDto> listerMembresParClub(Long clubId) {
        // On cherche ceux qui ont le statut ACTIVE
        return adhesionRepository.findByClubIdAndStatutAdhesion(clubId, StatutAdhesion.ACTIVE)
                .stream()
                .map(adhesionMapper::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdhesionDto> listerClubsParUtilisateur(Long userId) {
        // On cherche les adhésions ACTIVE pour cet utilisateur
        return adhesionRepository.findByUtilisateurIdAndStatutAdhesion(userId, StatutAdhesion.ACTIVE)
                .stream()
                .map(adhesionMapper::fromEntity)
                .collect(Collectors.toList());
    }
}
