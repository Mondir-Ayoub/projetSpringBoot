package ma.xproce.springbootproject.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import ma.xproce.springbootproject.dao.entities.Club;
import ma.xproce.springbootproject.dao.entities.Role;
import ma.xproce.springbootproject.dao.entities.Utilisateur;
import ma.xproce.springbootproject.dao.repositories.ClubRepository;
import ma.xproce.springbootproject.dao.repositories.RoleRepository;
import ma.xproce.springbootproject.dao.repositories.UtilisateurRepository;
import ma.xproce.springbootproject.service.mappers.ClubMapper;
import ma.xproce.springbootproject.service.dtos.ClubDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ClubManager implements ClubService {

    private final ClubRepository clubRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ClubMapper clubMapper;
    private final RoleRepository roleRepository;

    @Override
    public ClubDto creerClub(ClubDto clubDto, MultipartFile imageFile) {
        Club club = clubMapper.toEntity(clubDto);

        if (clubDto.getPresidentId() != null) {
            Utilisateur president = utilisateurRepository.findById(clubDto.getPresidentId())
                    .orElseThrow(() -> new RuntimeException("Président introuvable"));
            club.setPresident(president);
        }

        // GESTION DE LA PHOTO (AJOUT)
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Chemin absolu vers src/main/resources/static/img/clubs
                String dossierUpload = System.getProperty("user.dir") + "/src/main/resources/static/images/clubs";
                Path cheminDossier = Paths.get(dossierUpload);

                // Création du dossier s'il n'existe pas
                if (!Files.exists(cheminDossier)) {
                    Files.createDirectories(cheminDossier);
                }

                String nomFichier = "club_" + UUID.randomUUID() + ".jpg";
                Path cheminFichier = cheminDossier.resolve(nomFichier);

                // Sauvegarde
                Files.copy(imageFile.getInputStream(), cheminFichier, StandardCopyOption.REPLACE_EXISTING);

                // Mettre à jour l'entité avec le chemin relatif (pour le HTML)
                club.setPhoto("/images/clubs/" + nomFichier);

            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de l'upload de l'image", e);
            }
        }

        Club clubSauvegarde = clubRepository.save(club);
        return clubMapper.fromEntity(clubSauvegarde);
    }

    @Override
    public ClubDto modifierClub(Long id, ClubDto clubDto, MultipartFile imageFile) {
        Club clubExistant = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club introuvable"));

        if (clubDto.getNom() != null && !clubDto.getNom().isEmpty()) {
            clubExistant.setNom(clubDto.getNom());
        }
        if (clubDto.getDescription() != null) {
            clubExistant.setDescription(clubDto.getDescription());
        }
        if (clubDto.getCategorie() != null) {
            clubExistant.setCategorie(clubDto.getCategorie());
        }

        // GESTION DE LA PHOTO (NOUVEAU BLOC)
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Définir le dossier de stockage (src/main/resources/static/img/clubs)
                String dossierUpload = System.getProperty("user.dir") + "/src/main/resources/static/images/clubs";
                Path cheminDossier = Paths.get(dossierUpload);

                // Création du dossier s'il n'existe pas
                if (!Files.exists(cheminDossier)) {
                    Files.createDirectories(cheminDossier);
                }

                // Générer un nom unique pour éviter les conflits (ex: club_15_uuid.jpg)
                String nomFichier = "club_" + id + "_" + UUID.randomUUID() + ".jpg";
                Path cheminFichier = cheminDossier.resolve(nomFichier);

                // Sauvegarder
                Files.copy(imageFile.getInputStream(), cheminFichier, StandardCopyOption.REPLACE_EXISTING);

                // Mettre à jour le chemin dans la base de données
                clubExistant.setPhoto("/images/clubs/" + nomFichier);

            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de la mise à jour de l'image du club", e);
            }
        }

        if (clubDto.getPresidentId() != null) {
            Utilisateur nouveauPresident = utilisateurRepository.findById(clubDto.getPresidentId())
                    .orElseThrow(() -> new RuntimeException("Nouveau président introuvable"));
            clubExistant.setPresident(nouveauPresident);
        }

        //  Sauvegarder
        Club updatedClub = clubRepository.save(clubExistant);
        return clubMapper.fromEntity(updatedClub);
    }

    @Override
    public boolean supprimerClub(Long id) {
        if (clubRepository.existsById(id)) {
            clubRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public ClubDto chercherParId(Long id) {
        return clubRepository.findById(id)
                .map(clubMapper::fromEntity)
                .orElseThrow(() -> new RuntimeException("Club introuvable"));
    }

    @Override
    public List<ClubDto> listerClubs() {
        return clubRepository.findAll().stream()
                .map(clubMapper::fromEntity)
                .collect(Collectors.toList());
    }

    public void nommerPresident(Long clubId, Long userId) {
        Club club = clubRepository.findById(clubId).orElseThrow(() -> new RuntimeException("Club introuvable"));
        Utilisateur user = utilisateurRepository.findById(userId).orElseThrow(() -> new RuntimeException("User introuvable"));
        Role rolePresident = roleRepository.findByNom("PRESIDENT").orElseThrow(() -> new RuntimeException("Role PRESIDENT introuvable"));

        club.setPresident(user);
        clubRepository.save(club);

        user.getRoles().add(rolePresident);
        utilisateurRepository.save(user);
    }

    @Override
    public ClubDto trouverClubParEmailPresident(String email) {
        Utilisateur president = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Club club = clubRepository.findByPresident(president)
                .orElseThrow(() -> new RuntimeException("Vous n'êtes président d'aucun club"));

        return clubMapper.fromEntity(club);
    }

    @Override
    public void destituerPresident(Long utilisateurId) {
        Utilisateur user = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Trouver le club qu'il dirige
        Club club = clubRepository.findAll().stream()
                .filter(c -> c.getPresident() != null && c.getPresident().getId().equals(utilisateurId))
                .findFirst()
                .orElse(null);

        if (club != null) {
            club.setPresident(null);
            clubRepository.save(club);
        }
        Role rolePresident = roleRepository.findByNom("PRESIDENT").orElse(null);
        if (rolePresident != null && user.getRoles().contains(rolePresident)) {
            user.getRoles().remove(rolePresident);
            utilisateurRepository.save(user);
        }
    }
}
