package ma.xproce.springbootproject.service.mappers;

import ma.xproce.springbootproject.dao.entities.Evenement;
import ma.xproce.springbootproject.service.dtos.EvenementDto;
import org.springframework.stereotype.Component;

@Component
public class EvenementMapper {

    public EvenementDto fromEntity(Evenement evenement) {
        if (evenement == null) return null;

        return EvenementDto.builder()
                .id(evenement.getId())
                .titre(evenement.getTitre())
                .description(evenement.getDescription())
                .dateDebut(evenement.getDateDebut())
                .dateFin(evenement.getDateFin())
                .localisation(evenement.getLocalisation())
                .imagePath(evenement.getImagePath())
                .statutEvenement(evenement.getStatutEvenement())
                .visibiliteEvenement(evenement.getVisibiliteEvenement())

                .clubId(evenement.getClub() != null ? evenement.getClub().getId() : null)
                .nomClub(evenement.getClub() != null ? evenement.getClub().getNom() : "Hors Club")
                .createurId(evenement.getCreateur() != null ? evenement.getCreateur().getId() : null)
                .createurNom(evenement.getCreateur() != null ? evenement.getCreateur().getNom() : "Inconnu")
                .build();
    }

    public Evenement toEntity(EvenementDto dto) {
        if (dto == null) return null;

        Evenement evenement = new Evenement();
        evenement.setId(dto.getId());
        evenement.setTitre(dto.getTitre());
        evenement.setDescription(dto.getDescription());
        evenement.setDateDebut(dto.getDateDebut());
        evenement.setDateFin(dto.getDateFin());
        evenement.setLocalisation(dto.getLocalisation());
        evenement.setImagePath(dto.getImagePath());

        evenement.setStatutEvenement(dto.getStatutEvenement());
        evenement.setVisibiliteEvenement(dto.getVisibiliteEvenement());
        return evenement;
    }
}