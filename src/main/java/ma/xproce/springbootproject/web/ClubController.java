package ma.xproce.springbootproject.web;

import lombok.AllArgsConstructor;

import ma.xproce.springbootproject.dao.entities.enums.StatutEvenement;
import ma.xproce.springbootproject.service.*;

import ma.xproce.springbootproject.service.dtos.*;
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
@RequestMapping("/president")
@PreAuthorize("hasRole('PRESIDENT')")
@AllArgsConstructor
public class ClubController {
    private final ClubService clubService;
    private final EvenementService evenementService;
    private final AdhesionService adhesionService;
    private final UtilisateurService utilisateurService;
    private final InscriptionService inscriptionService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        String email = auth.getName();

        ClubDto club = clubService.trouverClubParEmailPresident(email);

        if (club == null) {
            return "redirect:/home?error=no_club";
        }

        List<AdhesionDto> demandesAdhesion = adhesionService.listerDemandesEnAttenteParClub(club.getId());
        List<EvenementDto> evenements = evenementService.listerEvenementsParClub(club.getId());
        List<InscriptionDto> inscriptions = inscriptionService.listerInscriptionsParClub(club.getId());

        List<AdhesionDto> membres = adhesionService.listerMembresParClub(club.getId());

        model.addAttribute("demandesAdhesion", demandesAdhesion);
        model.addAttribute("club", club);
        model.addAttribute("evenements", evenements);
        model.addAttribute("inscriptions", inscriptions);

        model.addAttribute("nbrEvenements", evenements.size());
        model.addAttribute("nbrInscriptions", inscriptions.size());
        model.addAttribute("nbrAdherents", membres.size());

        model.addAttribute("membres", membres);

        return "president-dashboard";
    }

    @PostMapping("/events")
    public String creerEvent(@ModelAttribute EvenementDto dto, Authentication auth) {
        ClubDto monClub = clubService.trouverClubParEmailPresident(auth.getName());
        dto.setClubId(monClub.getId());

        UtilisateurDto president = utilisateurService.chercherParEmail(auth.getName());
        dto.setCreateurId(president.getId());

        evenementService.creerEvenement(dto);

        return "redirect:/president/dashboard";
    }

    @PostMapping("/adhesions/{id}/valider")
    public String validerAdherent(@PathVariable Long id) {
        adhesionService.validerAdhesion(id);
        return "redirect:/president/dashboard";
    }


    @PostMapping("/events/add")
    public String ajouterEvenementParPresident(@ModelAttribute EvenementDto eventDto,
                                               @RequestParam("imageFile") MultipartFile file, // 2. Récupération du fichier
                                               Authentication auth) {

        String email = auth.getName();
        ClubDto club = clubService.trouverClubParEmailPresident(email); // Ta méthode existante

        if (club == null) {
            return "redirect:/president/dashboard?error=noclub";
        }

        if (file != null && !file.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/images/";

                String fileName = "event_" + UUID.randomUUID().toString() + ".jpg";

                Path path = Paths.get(uploadDir + fileName);
                Files.createDirectories(path.getParent());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                eventDto.setImagePath("/images/" + fileName);

            } catch (IOException e) {
                e.printStackTrace();
                eventDto.setImagePath("/images/event-default.jpg");
            }
        } else {
            eventDto.setImagePath("/images/event-default.jpg");
        }

        eventDto.setClubId(club.getId());
        eventDto.setNomClub(club.getNom());

        UtilisateurDto president = utilisateurService.chercherParEmail(email);
        eventDto.setCreateurId(president.getId());


        if (eventDto.getVisibiliteEvenement() != null &&
                "PRIVE_CLUB".equals(eventDto.getVisibiliteEvenement().name())) {
            eventDto.setStatutEvenement(StatutEvenement.PUBLIC);
        } else {
            eventDto.setStatutEvenement(StatutEvenement.EN_ATTENTE_VALIDATION);
        }

        evenementService.creerEvenement(eventDto);

        return "redirect:/president/dashboard?success=true";
    }

}
