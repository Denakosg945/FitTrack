package gr.hua.fitTrack.web.rest.model;

import gr.hua.fitTrack.core.service.AppointmentService;
import gr.hua.fitTrack.core.service.TrainerService;
import gr.hua.fitTrack.core.service.model.TrainerView;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/trainer/appointments")
public class TrainerAppointmentsController {

    private final AppointmentService appointmentService;
    private final TrainerService trainerService;

    public TrainerAppointmentsController(
            AppointmentService appointmentService,
            TrainerService trainerService
    ) {
        this.appointmentService = appointmentService;
        this.trainerService = trainerService;
    }

    @PostMapping("/{id}/respond")
    public String respondToAppointment(
            @PathVariable Long id,
            @RequestParam boolean accept,
            Principal principal
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        TrainerView trainer = trainerService.getTrainerProfileByEmail(email);

        if (accept) {
            appointmentService.approveAppointment(id, trainer.personId());
        } else {
            appointmentService.rejectAppointment(id, trainer.personId());
        }

        return "redirect:/trainer/profile";
    }


}
