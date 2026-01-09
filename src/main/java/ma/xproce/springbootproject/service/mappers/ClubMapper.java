package ma.xproce.springbootproject.service.mappers;

import ma.xproce.springbootproject.dao.entities.Club;
import ma.xproce.springbootproject.service.dtos.ClubDto;
import org.springframework.stereotype.Component;

@Component
public class ClubMapper {

    public ClubDto fromEntity(Club club) {
        if (club == null) return null;

        return ClubDto.builder()
                .id(club.getId())
                .nom(club.getNom())
                .description(club.getDescription())
                .photo(club.getPhoto())
                .categorie(club.getCategorie())
                .presidentId(club.getPresident() != null ? club.getPresident().getId() : null)
                .presidentNom(club.getPresident() != null ? club.getPresident().getNom() : "Aucun")
                .build();
    }

    public Club toEntity(ClubDto dto) {
        if (dto == null) return null;

        Club club = new Club();
        club.setId(dto.getId());
        club.setNom(dto.getNom());
        club.setPhoto(dto.getPhoto());
        club.setDescription(dto.getDescription());
        club.setCategorie(dto.getCategorie());
        return club;
    }
}