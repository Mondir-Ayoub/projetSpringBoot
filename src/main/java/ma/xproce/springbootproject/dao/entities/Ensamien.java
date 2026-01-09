package ma.xproce.springbootproject.dao.entities;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ensamien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codeApogee; // la clé de vérification

    private String nom;
    private String prenom;
    private String filiere;
    private String niveau;


}