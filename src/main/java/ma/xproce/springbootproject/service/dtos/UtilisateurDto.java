package ma.xproce.springbootproject.service.dtos;

import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilisateurDto {
    private Long id;
    private String nom;
    private String prenom;
    private String codeApogee;
    private String email;
    private String motDePasse; // Sera null quand on envoie les données au front (sécurité)

    private String nomClubGere;

    private Set<String> roles;

    public String getRole() {
        if (roles != null && !roles.isEmpty()) {
            // Pour un Set, on utilise l'itérateur pour récupérer le premier élément
            Object premierRole = roles.iterator().next();

            // On le transforme en texte
            return premierRole.toString();
        }
        return "AUCUN"; // si la liste est vide
    }
}