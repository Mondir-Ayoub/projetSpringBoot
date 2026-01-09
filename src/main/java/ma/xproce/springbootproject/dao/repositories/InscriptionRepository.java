package ma.xproce.springbootproject.dao.repositories;

import ma.xproce.springbootproject.dao.entities.Evenement;
import ma.xproce.springbootproject.dao.entities.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InscriptionRepository extends JpaRepository<Inscription,Long> {
    List<Inscription> findByEvenementId(Long evenementId);
    boolean existsByUtilisateurIdAndEvenementId(Long userId, Long evenementId); // Pour éviter les doublons
    List<Inscription> findByUtilisateurId(Long userId);

    // pour trouve les inscriptions dont l'événement appartient au club donné
    List<Inscription> findByEvenementClubId(Long clubId);
}
