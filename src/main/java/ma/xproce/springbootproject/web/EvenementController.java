package ma.xproce.springbootproject.web;

import lombok.AllArgsConstructor;
import ma.xproce.springbootproject.service.EvenementManager;

import ma.xproce.springbootproject.service.EvenementService;
import ma.xproce.springbootproject.service.dtos.EvenementDto;
import ma.xproce.springbootproject.service.dtos.InscriptionDto;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
public class EvenementController {
    private final EvenementService evenementService;

    @GetMapping("/events")
    public String listerEvenements(Model model,
                                   Authentication authentication,
                                   @RequestParam(name = "search", required = false) String search) {

        List<EvenementDto> events;

        String email = (authentication != null) ? authentication.getName() : null;

        if (search != null && !search.isEmpty()) {
            events = evenementService.rechercherEvenements(search, email);
            model.addAttribute("searchKeyword", search); // Pour réafficher le mot dans l'input
        } else {
            events = evenementService.recupererEvenementsPourUtilisateur(email);
        }
        model.addAttribute("listEvenements", events);
        return "events-list";
    }

    @GetMapping("/events/{id}")
    public String detailsEvenement(@PathVariable Long id, Model model) {

        EvenementDto event = evenementService.chercherParId(id);

        model.addAttribute("evenement", event);
        model.addAttribute("inscription", new InscriptionDto());

        return "event-detail";
    }
}
