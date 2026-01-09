package ma.xproce.springbootproject.service.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubDto {
    private Long id;
    private String nom;
    private String description;
    private String categorie;
    private String photo;
    // Infos simplifiées du président de club
    private Long presidentId;
    private String presidentNom;
}