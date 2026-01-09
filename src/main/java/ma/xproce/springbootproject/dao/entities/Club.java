package ma.xproce.springbootproject.dao.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clubs")
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    @Column(columnDefinition = "VARCHAR(MAX)")
    private String description;
    private String categorie;
    private String photo;

    @OneToMany(mappedBy = "club")
    @ToString.Exclude
    @JsonIgnore
    private Set<Adhesion> adhesions;

    @OneToMany(mappedBy = "club")
    @ToString.Exclude
    @JsonIgnore // Évite de charger tout l'historique des événements
    private Set<Evenement> evenements;

    @OneToOne
    @JoinColumn(name = "president_id", referencedColumnName = "id")

    private Utilisateur president;
}