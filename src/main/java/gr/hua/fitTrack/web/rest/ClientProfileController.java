package gr.hua.fitTrack.web.rest;

import gr.hua.fitTrack.core.service.AppointmentService;
import gr.hua.fitTrack.core.service.ClientService;
import gr.hua.fitTrack.core.service.TrainerService;
import gr.hua.fitTrack.core.service.model.ClientView;
import gr.hua.fitTrack.core.service.model.EditGoalsForm;
import gr.hua.fitTrack.core.service.model.EditProgressForm;
import gr.hua.fitTrack.core.service.model.RequestAppointmentForm;
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
    private final TrainerService trainerService;
    private final AppointmentService appointmentService;

    public ClientProfileController(ClientService clientService,
                                   TrainerService trainerService,
                                   AppointmentService appointmentService) {
        this.clientService = clientService;
        this.trainerService = trainerService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/profile")
    public String clientProfile(Model model) {

        Long testClientPersonId = 2L;

        ClientView client =
                clientService.getClientProfileByPersonId(testClientPersonId);

        boolean canRequest =
                appointmentService.canClientCreateAppointment(2L);

        model.addAttribute("client", client);
        model.addAttribute("canRequestAppointment", canRequest);

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

