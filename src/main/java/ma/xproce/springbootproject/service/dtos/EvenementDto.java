package ma.xproce.springbootproject.service.dtos;

import lombok.*;
import ma.xproce.springbootproject.dao.entities.enums.StatutEvenement;
import ma.xproce.springbootproject.dao.entities.enums.VisibiliteEvenement;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvenementDto {
    private Long id;
    private String titre;
    private String description;
    private String localisation;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String imagePath;

    private StatutEvenement statutEvenement;
    private VisibiliteEvenement visibiliteEvenement;

    // Relations
    private String nomClub;
    private Long createurId;
    private String createurNom;
    private Long clubId;
}