package gr.hua.fitTrack.web.rest;

import gr.hua.fitTrack.core.service.ClientService;
import gr.hua.fitTrack.core.service.model.ClientView;
import gr.hua.fitTrack.core.service.model.EditGoalsForm;
import gr.hua.fitTrack.core.service.model.EditProgressForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/client")
public class ClientProfileController {

    private final ClientService clientService;

    public ClientProfileController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/profile")
    public String clientProfile(Model model) {

        // 🔧 προσωρινά hardcoded για DEV / testing
        Long testClientPersonId = 2L; // ή ό,τι id έχει ο test client

        ClientView client =
                clientService.getClientProfileByPersonId(testClientPersonId);

        model.addAttribute("client", client);

        return "client/profile";
    }

    @GetMapping("/edit/goals")
    public String editGoals(Model model) {

        EditGoalsForm form = new EditGoalsForm();

        // (προαιρετικά) αν υπάρχουν goals → preload
        ClientView client = clientService.getClientProfileByPersonId(2L); // προσωρινά
        if (client.goals() != null) {
            form.setWeightGoal(client.goals().getWeightGoal());
            form.setRunningTimeGoal(client.goals().getRunningTimeGoal());
            form.setBodyFatPercentageGoal(
                    client.goals().getBodyFatPercentageGoal()
            );
        }

        model.addAttribute("goalsForm", form);
        return "client/edit/goals";
    }

    @PostMapping("/edit/goals")
    public String saveGoals(@ModelAttribute EditGoalsForm goalsForm) {

        clientService.updateGoalsForTestClient(
                goalsForm.getWeightGoal(),
                goalsForm.getRunningTimeGoal(),
                goalsForm.getBodyFatPercentageGoal()
        );

        return "redirect:/client/profile";
    }

    @GetMapping("/progress/new")
    public String newProgress(Model model) {

        EditProgressForm form = new EditProgressForm();
        model.addAttribute("progressForm", form);
        return "client/progress/new";
    }

    @PostMapping("/progress/new")
    public String saveProgress(
            @Valid @ModelAttribute("progressForm") EditProgressForm progressForm,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            return "client/progress/new";
        }

        clientService.addProgressForTestClient(progressForm);

        return "redirect:/client/profile";
    }






}

