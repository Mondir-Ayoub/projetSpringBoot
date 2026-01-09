package ma.xproce.springbootproject.dao.repositories;

import ma.xproce.springbootproject.dao.entities.Evenement;
import ma.xproce.springbootproject.dao.entities.enums.StatutEvenement;
import ma.xproce.springbootproject.dao.entities.enums.VisibiliteEvenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EvenementRepository extends JpaRepository<Evenement,Long> {

    // Pour le Président (Voir les events de son club)
    List<Evenement> findByClubId(Long clubId);

    // Affiche les événements selon les droits de l'utilisateur
    @Query("SELECT e FROM Evenement e WHERE " +
            "e.statutEvenement = 'PUBLIC' AND (" +
            "   e.visibiliteEvenement = 'PUBLIC' " +
            "   OR (e.visibiliteEvenement = 'INTERNE_ENSAM' AND :isEnsamien = true) " +
            "   OR (e.visibiliteEvenement = 'PRIVE_CLUB' AND e.club.id IN :clubIds) " +
            ")")
    List<Evenement> findAllAllowedEvents(
            @Param("isEnsamien") boolean isEnsamien,
            @Param("clubIds") List<Long> clubIds
    );

    @Query("SELECT e FROM Evenement e WHERE " +
            "(LOWER(e.titre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND e.statutEvenement = 'PUBLIC' " +
            "AND (" +
            "   e.visibiliteEvenement = 'PUBLIC' " +
            "   OR (e.visibiliteEvenement = 'INTERNE_ENSAM' AND :isEnsamien = true) " +
            "   OR (e.visibiliteEvenement = 'PRIVE_CLUB' AND e.club.id IN :clubIds) " +
            ")")
    List<Evenement> searchAllowedEvents(
            @Param("keyword") String keyword,
            @Param("isEnsamien") boolean isEnsamien,
            @Param("clubIds") List<Long> clubIds
    );
}
