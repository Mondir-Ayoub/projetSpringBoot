package ma.xproce.springbootproject.web;

import lombok.AllArgsConstructor;
import ma.xproce.springbootproject.service.InscriptionService;
import ma.xproce.springbootproject.service.UtilisateurService;
import ma.xproce.springbootproject.service.dtos.InscriptionDto;
import ma.xproce.springbootproject.service.dtos.UtilisateurDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@AllArgsConstructor
public class InscriptionController {
    private final InscriptionService inscriptionService;
    private final UtilisateurService utilisateurService;

    @PostMapping("/events/{eventId}/register")
    public String sInscrire(@PathVariable Long eventId, Authentication auth) {

        String email = auth.getName();
        UtilisateurDto user = utilisateurService.chercherParEmail(email);

        InscriptionDto dto = new InscriptionDto();
        dto.setEvenementId(eventId);
        dto.setUtilisateurId(user.getId());
        try {
            inscriptionService.creerInscription(dto);
        } catch (RuntimeException e) {
            return "redirect:/events/" + eventId + "?error=already_registered";
        }

        return "redirect:/events/" + eventId + "?success=true";
    }

    @GetMapping("/user/inscriptions")
    public String mesInscriptions(Model model, Authentication auth) {
        String email = auth.getName();
        UtilisateurDto user = utilisateurService.chercherParEmail(email);

        model.addAttribute("mesInscriptions", inscriptionService.listerInscriptionsParUtilisateur(user.getId()));

        return "user-inscriptions";
    }
}
