package ma.xproce.springbootproject.service.dtos;

import lombok.*;
import ma.xproce.springbootproject.dao.entities.enums.StatutBadge;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InscriptionDto {
    private Long id;
    private LocalDate datePostulation;
    private StatutBadge statutBadge;

    // Infos pour savoir QUI s'est inscrit
    private Long utilisateurId;
    private String utilisateurNomPrenom;

    // Infos pour savoir à QUOI il s'est inscrit
    private Long evenementId;
    private String evenementTitre;
}