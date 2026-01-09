package ma.xproce.springbootproject.service;

import ma.xproce.springbootproject.dao.entities.Utilisateur;
import ma.xproce.springbootproject.service.dtos.UtilisateurDto;

import java.util.List;

public interface UtilisateurService {
    UtilisateurDto enregistrerUtilisateur(UtilisateurDto utilisateurDto);

    UtilisateurDto chercherParEmail(String email);
    UtilisateurDto chercherParId(Long id);

    List<UtilisateurDto> listerUtilisateurs();

    boolean supprimerUtilisateur(Long id);

    void promouvoirEnPresident(Long utilisateurId);

    void devenirAdherent(Long utilisateurId);

    List<UtilisateurDto> chercherUtilisateurs(String keyword);

    public boolean changerMotDePasse(String email, String ancienMdp, String nouveauMdp) ;

    }
