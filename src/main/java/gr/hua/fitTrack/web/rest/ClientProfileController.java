package gr.hua.fitTrack.web.rest;

import gr.hua.fitTrack.core.service.AppointmentService;
import gr.hua.fitTrack.core.service.ClientService;
import gr.hua.fitTrack.core.service.TrainerService;
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

import java.security.Principal;

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
    public String clientProfile(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }



        String email = principal.getName();

        ClientView client = clientService.getViewByEmail(email);
        boolean canRequest = appointmentService.canClientCreateAppointment(email);

        model.addAttribute("client", client);
        model.addAttribute("canRequestAppointment", canRequest);

        return "client/profile";
    }

    @GetMapping("/edit/goals")
    public String editGoals(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }


        String email = principal.getName();
        ClientView client = clientService.getViewByEmail(email);

        EditGoalsForm form = new EditGoalsForm();

        if (client.goals() != null) {
            form.setWeightGoal(client.goals().getWeightGoal());
            form.setRunningTimeGoal(client.goals().getRunningTimeGoal());
            form.setBodyFatPercentageGoal(client.goals().getBodyFatPercentageGoal());
        }

        model.addAttribute("goalsForm", form);
        return "client/edit/goals";
    }

    @PostMapping("/edit/goals")
    public String saveGoals(@ModelAttribute EditGoalsForm goalsForm, Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }


        String email = principal.getName();

        clientService.updateGoals(
                email,
                goalsForm.getWeightGoal(),
                goalsForm.getRunningTimeGoal(),
                goalsForm.getBodyFatPercentageGoal()
        );

        return "redirect:/client/profile";
    }

    @GetMapping("/progress/new")
    public String newProgress(Model model) {
        model.addAttribute("progressForm", new EditProgressForm());
        return "client/progress/new";
    }

    @PostMapping("/progress/new")
    public String saveProgress(
            @Valid @ModelAttribute("progressForm") EditProgressForm progressForm,
            BindingResult bindingResult,
            Principal principal
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            return "client/progress/new";
        }

        clientService.addProgress(principal.getName(), progressForm);

        return "redirect:/client/profile";
    }
}


