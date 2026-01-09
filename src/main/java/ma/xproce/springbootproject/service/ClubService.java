package ma.xproce.springbootproject.service;

import ma.xproce.springbootproject.service.dtos.ClubDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ClubService {
    ClubDto creerClub(ClubDto clubDto ,  MultipartFile imageFile);
    public ClubDto modifierClub(Long id, ClubDto clubDto, MultipartFile imageFile) ;
    boolean supprimerClub(Long id);

    ClubDto chercherParId(Long id);
    List<ClubDto> listerClubs();
    void nommerPresident(Long clubId, Long userId);
    ClubDto trouverClubParEmailPresident(String email);
    public void destituerPresident(Long utilisateurId);
}
