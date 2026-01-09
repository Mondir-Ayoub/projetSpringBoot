package ma.xproce.springbootproject.service;

import ma.xproce.springbootproject.service.dtos.EvenementDto;

import java.util.List;

public interface EvenementService {
    EvenementDto creerEvenement(EvenementDto evenementDto);
    EvenementDto modifierEvenement(Long id, EvenementDto evenementDto);
    boolean supprimerEvenement(Long id);

    EvenementDto chercherParId(Long id);
    List<EvenementDto> listerEvenements(); // Pour l'Admin (tout voir)
    List<EvenementDto> listerEvenementsParClub(Long clubId); // Pour le Président

    List<EvenementDto> recupererEvenementsPourUtilisateur(String emailUtilisateur);
    void validerEvenement(Long id);
    public List<EvenementDto> rechercherEvenements(String motCle, String email);
}