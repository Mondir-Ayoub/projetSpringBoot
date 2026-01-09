package ma.xproce.springbootproject.web;

import lombok.AllArgsConstructor;
import ma.xproce.springbootproject.dao.entities.Utilisateur;
import ma.xproce.springbootproject.dao.entities.enums.StatutEvenement;
import ma.xproce.springbootproject.service.*;
import ma.xproce.springbootproject.service.dtos.ClubDto;
import ma.xproce.springbootproject.service.dtos.EvenementDto;
import ma.xproce.springbootproject.service.dtos.UtilisateurDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')") // Sécurisé
@AllArgsConstructor
public class AdminController {
    private final EvenementService evenementService;
    private final ClubService clubService;
    private final UtilisateurService utilisateurService;
    private final AdhesionService adhesionService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("events", evenementService.listerEvenements());

        model.addAttribute("clubs", clubService.listerClubs());

        model.addAttribute("users", utilisateurService.listerUtilisateurs());

        return "admin-dashboard";
    }


    @PostMapping("/events/{id}/validate")
    public String validerEvent(@PathVariable Long id) {
        evenementService.validerEvenement(id);
        return "redirect:/admin/dashboard";
    }


    @PostMapping("/assign-president")
    public String nommerPresident(@RequestParam Long clubId, @RequestParam Long userId) {
        clubService.nommerPresident(clubId, userId);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/assign-adherent")
    public String nommerAdherent(@RequestParam Long clubId, @RequestParam Long userId) {
        adhesionService.forcerAdhesion(clubId, userId);
        return "redirect:/admin/dashboard";
    }


    @PostMapping("/clubs/add")
    public String ajouterClub(@ModelAttribute ClubDto clubDto,
                              @RequestParam("imageFile") MultipartFile file) {
        clubService.creerClub(clubDto, file);

        return "redirect:/admin/dashboard?success=club_created";
    }


    @PostMapping("/events/add")
    public String creerEventAdmin(@ModelAttribute EvenementDto evenementDto,
                                  @RequestParam("imageFile") MultipartFile file,
                                  Authentication auth) {

        if (file != null && !file.isEmpty()) {
            try {

                String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/images/";

                String fileName = "event_" + UUID.randomUUID().toString() + ".jpg";

                Path path = Paths.get(uploadDir + fileName);
                Files.createDirectories(path.getParent());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                evenementDto.setImagePath("/images/" + fileName);

            } catch (IOException e) {
                e.printStackTrace();
                evenementDto.setImagePath("/images/event-default.jpg");
            }
        } else {
            evenementDto.setImagePath("/images/event-default.jpg");
        }

        String email = auth.getName();
        UtilisateurDto admin = utilisateurService.chercherParEmail(email);

        evenementDto.setCreateurId(admin.getId());
        evenementDto.setClubId(null);
        evenementDto.setNomClub(null);

        evenementDto.setStatutEvenement(StatutEvenement.PUBLIC);

        evenementService.creerEvenement(evenementDto);

        return "redirect:/admin/dashboard";
    }

    @GetMapping("/users")
    public String listeUtilisateurs(@RequestParam(name = "keyword", defaultValue = "") String keyword, Model model) {

        List<UtilisateurDto> users = utilisateurService.chercherUtilisateurs(keyword);
        model.addAttribute("users", users);

        model.addAttribute("clubs", clubService.listerClubs());

        model.addAttribute("keyword", keyword);

        return "admin-users-list"; // Nouvelle vue
    }

    @PostMapping("/revoke-president")
    public String destituerPresident(@RequestParam Long userId) {
        clubService.destituerPresident(userId);
        return "redirect:/admin/users";
    }

    @PostMapping("/clubs/edit/{id}")
    public String modifierClub(@PathVariable Long id,
                               @ModelAttribute ClubDto clubDto,
                               @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        clubService.modifierClub(id, clubDto, imageFile);

        return "redirect:/admin/dashboard?success=club_updated";
    }
}
