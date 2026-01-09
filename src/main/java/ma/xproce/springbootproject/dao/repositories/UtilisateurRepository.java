package ma.xproce.springbootproject.dao.repositories;

import ma.xproce.springbootproject.dao.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur,Long> {

    Optional<Utilisateur> findByEmail(String email);
    List<Utilisateur> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(String nom, String prenom, String email);

}
