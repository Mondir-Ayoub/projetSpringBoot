package ma.xproce.springbootproject.dao.repositories;

import ma.xproce.springbootproject.dao.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByNom(String nom);}
