package ma.xproce.springbootproject.service;

import lombok.AllArgsConstructor;
import ma.xproce.springbootproject.dao.entities.Utilisateur;
import ma.xproce.springbootproject.dao.repositories.UtilisateurRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service // INDISPENSABLE : C'est pour ça que tu avais l'erreur !
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UtilisateurRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println(">>> AUTHENTIFICATION : Recherche de l'utilisateur avec l'email : " + email);

        // Chercher l'utilisateur dans la base de données
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println(">>> ECHEC : Utilisateur introuvable !");
                    return new UsernameNotFoundException("Utilisateur introuvable avec l'email : " + email);
                });

        System.out.println(">>> SUCCES : Utilisateur trouvé. Mot de passe haché en base : " + utilisateur.getMotDePasse());

        // Transformer les rôles (ex: "ADMIN") en autorités Spring (ex: "ROLE_ADMIN")
        List<SimpleGrantedAuthority> authorities = utilisateur.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getNom()))
                .collect(Collectors.toList());

        System.out.println(">>> ROLES CHARGÉS : " + authorities);

        // Retourner l'objet User de Spring Security
        return new User(
                utilisateur.getEmail(),
                utilisateur.getMotDePasse(),
                authorities
        );
    }
}
