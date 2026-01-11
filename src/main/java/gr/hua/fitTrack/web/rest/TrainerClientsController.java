package gr.hua.fitTrack.web.rest;

import gr.hua.fitTrack.core.service.TrainerClientNotesService;
import gr.hua.fitTrack.core.service.TrainerService;
import gr.hua.fitTrack.core.service.model.TrainerView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/trainer/clients")
public class TrainerClientsController {

    private final TrainerService trainerService;
    private final TrainerClientNotesService notesService;

    public TrainerClientsController(
            TrainerService trainerService,
            TrainerClientNotesService notesService
    ) {
        this.trainerService = trainerService;
        this.notesService = notesService;
    }

    /* ----------------------------------------
       VIEW CLIENTS & NOTES
    ----------------------------------------- */

    @GetMapping
    public String viewClients(Model model, Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        TrainerView trainer = trainerService.getTrainerProfileByEmail(email);

        model.addAttribute(
                "clientNotes",
                notesService.getNotesForTrainer(trainer.personId())
        );

        return "trainer/clients";
    }

    /* ----------------------------------------
       UPDATE NOTES
    ----------------------------------------- */

    @PostMapping("/notes/{id}")
    public String updateNotes(
            @PathVariable Long id,
            @RequestParam String notes,
            Principal principal
    ) {

        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        TrainerView trainer = trainerService.getTrainerProfileByEmail(email);

        notesService.updateNotes(
                trainer.personId(),
                notes
        );

        return "redirect:/trainer/clients";
    }
}
