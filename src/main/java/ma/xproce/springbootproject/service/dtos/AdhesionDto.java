package ma.xproce.springbootproject.service.dtos;

import lombok.*;
import ma.xproce.springbootproject.dao.entities.enums.StatutAdhesion;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdhesionDto {
    private Long id;
    private LocalDate dateDemande;
    private StatutAdhesion statutAdhesion;

    private Long utilisateurId;
    private String utilisateurNomPrenom;
    private String utilisateurEmail;
    private Long clubId;
    private String clubNom;
    private String photo;
}