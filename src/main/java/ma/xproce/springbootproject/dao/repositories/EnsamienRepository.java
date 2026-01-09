package ma.xproce.springbootproject.dao.repositories;

import ma.xproce.springbootproject.dao.entities.Ensamien;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnsamienRepository extends JpaRepository<Ensamien,String> {

    // cette methode sert à vérifier si l'étudiant existe dans la base de l'école
    Optional<Ensamien> findByCodeApogee(String codeApogee);

}
