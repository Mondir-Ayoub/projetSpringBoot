package ma.xproce.springbootproject.dao.repositories;

import ma.xproce.springbootproject.dao.entities.Adhesion;
import ma.xproce.springbootproject.dao.entities.Club;
import ma.xproce.springbootproject.dao.entities.Utilisateur;
import ma.xproce.springbootproject.dao.entities.enums.StatutAdhesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdhesionRepository extends JpaRepository<Adhesion,Long> {
    boolean existsByUtilisateurAndClub(Utilisateur utilisateur, Club club);
    List<Adhesion> findByClubAndStatutAdhesion(Club club, StatutAdhesion statutAdhesion);


    // On récupére juste les IDs des clubs où l'user est "ACTIVE"
    @Query("SELECT a.club.id FROM Adhesion a WHERE a.utilisateur.id = :userId AND a.statutAdhesion = 'ACTIVE'")
    List<Long> findClubIdsByUserId(@Param("userId") Long userId);

    List<Adhesion> findByClubIdAndStatutAdhesion(Long clubId, StatutAdhesion statutAdhesion);
    List <Adhesion> findByUtilisateurIdAndStatutAdhesion(Long userId, StatutAdhesion statutAdhesion);
}
