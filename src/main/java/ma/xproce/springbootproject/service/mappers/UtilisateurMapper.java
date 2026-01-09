package ma.xproce.springbootproject.service.mappers;

import ma.xproce.springbootproject.dao.entities.Ensamien;
import ma.xproce.springbootproject.dao.entities.Role;
import ma.xproce.springbootproject.dao.entities.Utilisateur;
import ma.xproce.springbootproject.service.dtos.UtilisateurDto;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class UtilisateurMapper {

    // Entity -> DTO
    public UtilisateurDto fromEntity(Utilisateur utilisateur) {
        if (utilisateur == null) return null;

        return UtilisateurDto.builder()
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .email(utilisateur.getEmail())
                .motDePasse(null)
                .codeApogee(utilisateur.getCodeApogee())
                .roles(utilisateur.getRoles().stream()
                        .map(Role::getNom)
                        .collect(Collectors.toSet()))
                .build();
    }

    public Utilisateur toEntity(UtilisateurDto dto) {
        if (dto == null) return null;

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(dto.getId());
        utilisateur.setNom(dto.getNom());
        utilisateur.setPrenom(dto.getPrenom());
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setMotDePasse(dto.getMotDePasse());
        utilisateur.setCodeApogee(dto.getCodeApogee());

        return utilisateur;
    }
}