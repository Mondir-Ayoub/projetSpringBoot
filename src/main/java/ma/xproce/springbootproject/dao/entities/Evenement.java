package ma.xproce.springbootproject.dao.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import ma.xproce.springbootproject.dao.entities.enums.StatutEvenement;
import ma.xproce.springbootproject.dao.entities.enums.VisibiliteEvenement;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "evenements")
public class Evenement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    @Column(columnDefinition = "VARCHAR(MAX)")
    private String description;

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String localisation;
    private String imagePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutEvenement statutEvenement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisibiliteEvenement visibiliteEvenement;


    @OneToMany(mappedBy = "evenement")
    @ToString.Exclude
    @JsonIgnore // On coupe la boucle infinie ici
    private Set<Inscription> inscriptions;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @ManyToOne
    @JoinColumn(name = "createur_id")

    private Utilisateur createur;
}