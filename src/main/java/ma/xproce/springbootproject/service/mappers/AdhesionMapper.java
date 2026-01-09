package ma.xproce.springbootproject.service.mappers;

import ma.xproce.springbootproject.dao.entities.Adhesion;
import ma.xproce.springbootproject.service.dtos.AdhesionDto;
import org.springframework.stereotype.Component;

@Component
public class AdhesionMapper {

    public AdhesionDto fromEntity(Adhesion adhesion) {
        if (adhesion == null) return null;

        return AdhesionDto.builder()
                .id(adhesion.getId())
                .dateDemande(adhesion.getDateDemande())
                .statutAdhesion(adhesion.getStatutAdhesion())

                .photo(adhesion.getClub() != null ? adhesion.getClub().getPhoto() : null)

                // INFOS UTILISATEUR
                .utilisateurId(adhesion.getUtilisateur() != null ? adhesion.getUtilisateur().getId() : null)
                .utilisateurNomPrenom(adhesion.getUtilisateur() != null ?
                        adhesion.getUtilisateur().getNom() + " " + adhesion.getUtilisateur().getPrenom() : "Inconnu")
                .utilisateurEmail(adhesion.getUtilisateur() != null ? adhesion.getUtilisateur().getEmail() : "")

                // INFOS CLUB
                .clubId(adhesion.getClub() != null ? adhesion.getClub().getId() : null)
                .clubNom(adhesion.getClub() != null ? adhesion.getClub().getNom() : "Inconnu")
                .build();
    }

    public Adhesion toEntity(AdhesionDto dto) {
        if (dto == null) return null;

        Adhesion adhesion = new Adhesion();
        adhesion.setId(dto.getId());
        adhesion.setDateDemande(dto.getDateDemande());
        adhesion.setStatutAdhesion(dto.getStatutAdhesion());
        return adhesion;
    }
}