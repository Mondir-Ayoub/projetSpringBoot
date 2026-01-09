package ma.xproce.springbootproject.service.mappers;

import ma.xproce.springbootproject.dao.entities.Inscription;
import ma.xproce.springbootproject.service.dtos.InscriptionDto;
import org.springframework.stereotype.Component;

@Component
public class InscriptionMapper {

    public InscriptionDto fromEntity(Inscription inscription) {
        if (inscription == null) return null;

        return InscriptionDto.builder()
                .id(inscription.getId())
                .datePostulation(inscription.getDatePostulation())
                .statutBadge(inscription.getStatutBadge())
                .utilisateurId(inscription.getUtilisateur() != null ? inscription.getUtilisateur().getId() : null)
                .utilisateurNomPrenom(inscription.getUtilisateur() != null ?
                        inscription.getUtilisateur().getNom() + " " + inscription.getUtilisateur().getPrenom() : "Inconnu")

                .evenementId(inscription.getEvenement() != null ? inscription.getEvenement().getId() : null)
                .evenementTitre(inscription.getEvenement() != null ? inscription.getEvenement().getTitre() : "Événement inconnu")
                .build();
    }

    public Inscription toEntity(InscriptionDto dto) {
        if (dto == null) return null;

        Inscription inscription = new Inscription();
        inscription.setId(dto.getId());
        inscription.setDatePostulation(dto.getDatePostulation());
        inscription.setStatutBadge(dto.getStatutBadge());
        return inscription;
    }
}