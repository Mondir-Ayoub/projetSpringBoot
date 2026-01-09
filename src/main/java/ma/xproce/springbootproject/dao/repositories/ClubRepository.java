package ma.xproce.springbootproject.dao.repositories;

import ma.xproce.springbootproject.dao.entities.Club;
import ma.xproce.springbootproject.dao.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club,Long> {
    Optional<Club> findByPresident(Utilisateur president);
}
