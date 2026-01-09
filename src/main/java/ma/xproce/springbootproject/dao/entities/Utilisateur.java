package ma.xproce.springbootproject.dao.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "utilisateurs")

public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;

    @Column(unique = true)
    private String email;

    private String motDePasse;

    // Si l'utilisateur est un étudiant validé, on stocke son code ici
    private String codeApogee;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;

    @OneToMany(mappedBy = "utilisateur")
    @ToString.Exclude
    @JsonIgnore
    private Set<Adhesion> adhesions;

    @OneToMany(mappedBy = "utilisateur")
    @ToString.Exclude
    @JsonIgnore
    private Set<Inscription> inscriptions;

    @OneToMany(mappedBy = "createur")
    @ToString.Exclude
    @JsonIgnore
    private Set<Evenement> evenementsCrees;

    // --- POINT CRITIQUE ---
    @OneToOne(mappedBy = "president")
    @ToString.Exclude
    @JsonIgnore
    private Club clubGere;

}