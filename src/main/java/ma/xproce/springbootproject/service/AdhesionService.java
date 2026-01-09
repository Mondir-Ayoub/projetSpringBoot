package ma.xproce.springbootproject.service;

import ma.xproce.springbootproject.service.dtos.AdhesionDto;
import ma.xproce.springbootproject.service.dtos.UtilisateurDto;

import java.util.List;

public interface AdhesionService {
    AdhesionDto creerAdhesion(AdhesionDto adhesionDto);
    AdhesionDto modifierAdhesion(Long id, AdhesionDto adhesionDto);
    boolean supprimerAdhesion(Long id);

    AdhesionDto chercherParId(Long id);
    List<AdhesionDto> listerAdhesions();

    void forcerAdhesion(Long clubId, Long userId);
    void validerAdhesion(Long adhesionId);
    List<AdhesionDto> listerDemandesEnAttenteParClub(Long clubId);

    public List<AdhesionDto> listerMembresParClub(Long clubId) ;


    public List<AdhesionDto> listerClubsParUtilisateur(Long userId) ;
    }
