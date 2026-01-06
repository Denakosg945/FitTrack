package gr.hua.fitTrack.web.rest;

import gr.hua.fitTrack.core.model.TrainerProfile;
import gr.hua.fitTrack.core.service.TrainerClientNotesService;
import gr.hua.fitTrack.core.service.TrainerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String viewClients(Model model) {

        Long TEST_TRAINER_PERSON_ID = 1L;

        model.addAttribute(
                "clientNotes",
                notesService.getNotesForTrainer(TEST_TRAINER_PERSON_ID)
        );


        return "trainer/clients";
    }

    /* ----------------------------------------
       UPDATE NOTES
    ----------------------------------------- */

    @PostMapping("/notes/{id}")
    public String updateNotes(
            @PathVariable Long id,
            @RequestParam String notes
    ) {
        notesService.updateNotes(id, notes);
        return "redirect:/trainer/clients";
    }
}
