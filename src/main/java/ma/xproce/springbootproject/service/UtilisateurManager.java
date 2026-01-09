package ma.xproce.springbootproject.service;

import lombok.AllArgsConstructor;
import ma.xproce.springbootproject.dao.entities.Ensamien;
import ma.xproce.springbootproject.dao.entities.Role;
import ma.xproce.springbootproject.dao.entities.Utilisateur;
import ma.xproce.springbootproject.dao.repositories.EnsamienRepository;
import ma.xproce.springbootproject.dao.repositories.RoleRepository;
import ma.xproce.springbootproject.dao.repositories.UtilisateurRepository;
import ma.xproce.springbootproject.service.dtos.UtilisateurDto;
import ma.xproce.springbootproject.service.mappers.UtilisateurMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class UtilisateurManager implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final EnsamienRepository ensamienRepository;
    private final UtilisateurMapper utilisateurMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UtilisateurDto enregistrerUtilisateur(UtilisateurDto dto) {
        Utilisateur utilisateur = utilisateurMapper.toEntity(dto);

        if (dto.getId() != null) {
            Utilisateur existing = utilisateurRepository.findById(dto.getId()).orElse(null);
            if (existing != null) {
                if (dto.getMotDePasse() == null || dto.getMotDePasse().isEmpty()) {
                    utilisateur.setMotDePasse(existing.getMotDePasse());
                } else {
                    utilisateur.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
                }

                utilisateur.setRoles(existing.getRoles());
                utilisateur.setCodeApogee(existing.getCodeApogee());
            }
        }
        else {
            // Encodage mot de passe
            if (dto.getMotDePasse() != null) {
                utilisateur.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
            }

            Role roleAttribue;

            if (dto.getCodeApogee() != null && !dto.getCodeApogee().trim().isEmpty()) {

                // On vérifie dans la table de référence ENSAMIEN
                Optional<Ensamien> etudiantRef = ensamienRepository.findByCodeApogee(dto.getCodeApogee());

                if (etudiantRef.isPresent()) {
                    // C'est un étudiant valide -> Rôle ENSAMIEN
                    roleAttribue = roleRepository.findByNom("ENSAMIEN")
                            .orElseThrow(() -> new RuntimeException("Erreur : Rôle ENSAMIEN introuvable."));

                    // On valide officiellement le code dans son profil utilisateur
                    utilisateur.setCodeApogee(dto.getCodeApogee());
                } else {
                    // Code saisi mais introuvable -> Rôle EXTERNE
                    roleAttribue = roleRepository.findByNom("EXTERNE")
                            .orElseThrow(() -> new RuntimeException("Erreur : Rôle EXTERNE introuvable."));

                    // On vide le code Apogée
                    utilisateur.setCodeApogee(null);
                }
            } else {
                // Pas de code saisi -> Rôle EXTERNE
                roleAttribue = roleRepository.findByNom("EXTERNE")
                        .orElseThrow(() -> new RuntimeException("Erreur : Rôle EXTERNE introuvable."));
            }

            utilisateur.setRoles(Collections.singleton(roleAttribue));
        }

        return utilisateurMapper.fromEntity(utilisateurRepository.save(utilisateur));
    }


    @Override
    public boolean changerMotDePasse(String email, String ancienMdp, String nouveauMdp) {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElse(null);

        if (user != null) {
            if (passwordEncoder.matches(ancienMdp, user.getMotDePasse())) {
                user.setMotDePasse(passwordEncoder.encode(nouveauMdp));
                utilisateurRepository.save(user);
                return true;
            }
        }
        return false;
    }
    @Override
    public List<UtilisateurDto> chercherUtilisateurs(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return listerUtilisateurs();
        }

        List<Utilisateur> users = utilisateurRepository
                .findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, keyword);

        return users.stream().map(utilisateurMapper::fromEntity).collect(Collectors.toList());
    }

    @Override
    public UtilisateurDto chercherParEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .map(utilisateurMapper::fromEntity)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'email : " + email));
    }

    @Override
    public UtilisateurDto chercherParId(Long id) {
        return utilisateurRepository.findById(id)
                .map(utilisateurMapper::fromEntity)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    @Override
    public List<UtilisateurDto> listerUtilisateurs() {
        return utilisateurRepository.findAll().stream()
                .map(utilisateurMapper::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean supprimerUtilisateur(Long id) {
        if (utilisateurRepository.existsById(id)) {
            utilisateurRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public void promouvoirEnPresident(Long utilisateurId) {
        Utilisateur user = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        Role rolePresident = roleRepository.findByNom("PRESIDENT")
                .orElseThrow(() -> new RuntimeException("Rôle PRESIDENT introuvable"));
        if (!user.getRoles().contains(rolePresident)) {
            user.getRoles().add(rolePresident);
            utilisateurRepository.save(user);
        }
    }

    @Override
    public void devenirAdherent(Long utilisateurId) {
        Utilisateur user = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        Role roleAdherent = roleRepository.findByNom("ADHERENT")
                .orElseThrow(() -> new RuntimeException("Rôle ADHERENT introuvable"));
        if (!user.getRoles().contains(roleAdherent)) {
            user.getRoles().add(roleAdherent);
            utilisateurRepository.save(user);
        }
    }
}