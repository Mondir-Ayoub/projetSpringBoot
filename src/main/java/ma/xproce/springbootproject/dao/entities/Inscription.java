package ma.xproce.springbootproject.dao.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.xproce.springbootproject.dao.entities.enums.StatutBadge;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inscriptions")
public class Inscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate datePostulation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutBadge statutBadge;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;
}