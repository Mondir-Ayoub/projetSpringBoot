package ma.xproce.springbootproject.web;

import lombok.AllArgsConstructor;
import ma.xproce.springbootproject.service.AdhesionService;
import ma.xproce.springbootproject.service.UtilisateurService;
import ma.xproce.springbootproject.service.dtos.AdhesionDto;
import ma.xproce.springbootproject.service.dtos.UtilisateurDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
public class AuthController {

    private final UtilisateurService utilisateurService;
    private final AdhesionService adhesionService;

    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("utilisateur", new UtilisateurDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute UtilisateurDto utilisateurDto, Model model) {
        try {
            utilisateurService.enregistrerUtilisateur(utilisateurDto);
            return "redirect:/login?success";
        } catch (Exception e) {
            model.addAttribute("error", "Cet email est déjà utilisé ou une erreur est survenue.");
            model.addAttribute("utilisateur", utilisateurDto); // On garde les champs remplis
            return "signup";
        }
    }

    @GetMapping("/profile")
    public String monProfil(Model model, Authentication authentication) {
        String email = authentication.getName();

        UtilisateurDto user = utilisateurService.chercherParEmail(email);
        List<AdhesionDto> mesClubs = adhesionService.listerClubsParUtilisateur(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("mesClubs", mesClubs);


        if (mesClubs == null) mesClubs = new    ArrayList<>();

        model.addAttribute("mesClubs", mesClubs);
        model.addAttribute("user", user);

        return "profile";
    }


    @GetMapping("/profile/edit")
    public String afficherFormulaireModif(Model model, Authentication auth) {
        UtilisateurDto user = utilisateurService.chercherParEmail(auth.getName());
        model.addAttribute("user", user);
        return "profile-edit";
    }

    @PostMapping("/profile/update")
    public String traiterModifInfos(@ModelAttribute UtilisateurDto userDto, Authentication auth) {
        UtilisateurDto currentUser = utilisateurService.chercherParEmail(auth.getName());

        userDto.setId(currentUser.getId());
        userDto.setEmail(currentUser.getEmail());
        userDto.setCodeApogee(currentUser.getCodeApogee());

        utilisateurService.enregistrerUtilisateur(userDto);

        return "redirect:/profile?updated=true";
    }


    @GetMapping("/profile/password")
    public String afficherFormulaireMdp() {
        return "profile-password";
    }

    @PostMapping("/profile/change-password")
    public String traiterChangementMdp(@RequestParam("ancien") String ancien,
                                       @RequestParam("nouveau") String nouveau,
                                       Authentication auth) {
        boolean succes = utilisateurService.changerMotDePasse(auth.getName(), ancien, nouveau);

        if (succes) {
            return "redirect:/profile?passwordChanged=true";
        } else {
            return "redirect:/profile/password?error=bad_password";
        }
    }
}
