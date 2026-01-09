package ma.xproce.springbootproject.dao.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.xproce.springbootproject.dao.entities.enums.StatutAdhesion;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "adhesions")
public class Adhesion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // EAGER pour simplifier les tests
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutAdhesion statutAdhesion;

    private LocalDate dateDemande;
    private String photo ;
}