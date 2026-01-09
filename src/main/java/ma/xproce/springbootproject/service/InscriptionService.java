package ma.xproce.springbootproject.service;

import ma.xproce.springbootproject.service.dtos.InscriptionDto;

import java.util.List;

public interface InscriptionService {
    InscriptionDto creerInscription(InscriptionDto inscriptionDto);
    InscriptionDto modifierInscription(Long id, InscriptionDto inscriptionDto);
    boolean supprimerInscription(Long id);

    InscriptionDto chercherParId(Long id);

    List<InscriptionDto> listerInscriptionsParUtilisateur(Long userId);

    List<InscriptionDto> listerInscriptions();


    List<InscriptionDto> listerInscriptionsParEvenement(Long evenementId);


    public List<InscriptionDto> listerInscriptionsParClub(Long clubId);
}
