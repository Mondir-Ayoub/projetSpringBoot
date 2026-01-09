package ma.xproce.springbootproject.web;

import lombok.AllArgsConstructor;
import ma.xproce.springbootproject.service.AdhesionService;
import ma.xproce.springbootproject.service.ClubService;
import ma.xproce.springbootproject.service.UtilisateurService;
import ma.xproce.springbootproject.service.dtos.AdhesionDto;
import ma.xproce.springbootproject.service.dtos.ClubDto;
import ma.xproce.springbootproject.service.dtos.UtilisateurDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class PublicClubController {
    private final ClubService clubService;
    private final AdhesionService adhesionService;
    private final UtilisateurService utilisateurService;

    @GetMapping("/clubs")
    public String listerClubsPourEtudiants(Model model) {
        List<ClubDto> clubs = clubService.listerClubs();
        model.addAttribute("clubs", clubs);

        return "clubs-list";
    }

    @PostMapping("/clubs/{id}/join")
    public String rejoindreClub(@PathVariable("id") Long clubId, Authentication auth) {

        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        try {
            String email = auth.getName();
            UtilisateurDto utilisateur = utilisateurService.chercherParEmail(email);

            AdhesionDto adhesionDto = new AdhesionDto();
            adhesionDto.setUtilisateurId(utilisateur.getId());
            adhesionDto.setClubId(clubId);

            adhesionService.creerAdhesion(adhesionDto);

            // Succès
            return "redirect:/clubs?success=join_sent";

        } catch (RuntimeException e) {
            return "redirect:/clubs?error=" + e.getMessage();
        }
    }
}
