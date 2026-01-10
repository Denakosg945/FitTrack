package gr.hua.fitTrack.web.rest;

import gr.hua.fitTrack.core.exception.MaxActiveAppointmentsExceededException;
import gr.hua.fitTrack.core.model.Appointment;
import gr.hua.fitTrack.core.model.ClientProfile;
import gr.hua.fitTrack.core.model.TrainerProfile;
import gr.hua.fitTrack.core.service.AppointmentService;
import gr.hua.fitTrack.core.service.ClientService;
import gr.hua.fitTrack.core.service.TrainerService;
import gr.hua.fitTrack.core.service.model.RequestAppointmentForm;
import gr.hua.fitTrack.core.service.model.TrainerSelectableView;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/client/appointments")
public class ClientAppointmentController {

    private final TrainerService trainerService;
    private final AppointmentService appointmentService;
    private final ClientService clientService;

    public ClientAppointmentController(TrainerService trainerService,
                                       AppointmentService appointmentService,
                                       ClientService clientService) {
        this.trainerService = trainerService;
        this.appointmentService = appointmentService;
        this.clientService = clientService;
    }

    @GetMapping("/request")
    public String showStep1(Model model) {
        model.addAttribute("form", new RequestAppointmentForm());
        return "client/appointments/request-step-1";
    }

    @PostMapping("/request")
    public String submitStep1(
            @Valid @ModelAttribute("form") RequestAppointmentForm form,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            return "client/appointments/request-step-1";
        }

        if (form.getDate() == null || form.getDate().isBefore(LocalDate.now())) {
            return "redirect:/client/appointments/request";
        }

        return "redirect:/client/appointments/search?date=" + form.getDate();
    }

    @GetMapping("/search")
    public String searchTrainers(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String specialization,
            Model model
    ) {
        if (date.isBefore(LocalDate.now())) {
            return "redirect:/client/appointments/request";
        }

        model.addAttribute("date", date);
        model.addAttribute("lastNames", trainerService.getDistinctLastNames());
        model.addAttribute("locations", trainerService.getDistinctLocations());
        model.addAttribute("specializations", trainerService.getDistinctSpecializations());
        model.addAttribute(
                "trainers",
                trainerService.getSelectableWithAvailability(date, lastName, location, specialization)
        );

        return "client/appointments/request-step-2";
    }

    @GetMapping("/request/step-3")
    public String selectTimeSlot(
            @RequestParam("trainerToken") String trainerToken,
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            Model model
    ) {
        if (date.isBefore(LocalDate.now())) {
            return "redirect:/client/appointments/request";
        }

        TrainerProfile trainerProfile = trainerService.getTrainerByToken(trainerToken);
        TrainerSelectableView trainerView = trainerService.getTrainerSelectableViewByToken(trainerToken);

        List<LocalTime> slots =
                appointmentService.getAvailableTimeSlots(
                        trainerProfile.getPerson().getId(),
                        date
                );

        model.addAttribute("trainer", trainerView);
        model.addAttribute("trainerToken", trainerToken);
        model.addAttribute("date", date);
        model.addAttribute("slots", slots);

        return "client/appointments/request-step-3";
    }

    @GetMapping("/request/confirm")
    public String confirmOverview(
            @RequestParam("trainerToken") String trainerToken,
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @RequestParam("startTime")
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            LocalTime startTime,
            Model model
    ) {
        if (date.isBefore(LocalDate.now())) {
            return "redirect:/client/appointments/request";
        }

        TrainerSelectableView trainerView = trainerService.getTrainerSelectableViewByToken(trainerToken);

        model.addAttribute("trainer", trainerView);
        model.addAttribute("trainerToken", trainerToken);
        model.addAttribute("date", date);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", startTime.plusHours(1));

        return "client/appointments/request-confirm";
    }

    @PostMapping("/request/confirm")
    public String submitAppointment(
            @RequestParam("trainerToken") String trainerToken,
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @RequestParam("startTime")
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            LocalTime startTime,
            @RequestParam(value = "outdoor", defaultValue = "false") boolean outdoor,
            RedirectAttributes redirectAttributes,
            Principal principal
    ) {
        ClientProfile client = clientService.getByEmail(principal.getName());
        TrainerProfile trainer = trainerService.getTrainerByToken(trainerToken);

        Appointment appointment = Appointment.pending(
                client,
                trainer,
                date,
                startTime,
                startTime.plusHours(1),
                outdoor
        );

        try {
            appointmentService.save(appointment);
        } catch (MaxActiveAppointmentsExceededException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/client/profile";
        }

        redirectAttributes.addFlashAttribute("success", "Appointment requested successfully.");
        return "redirect:/client/profile";
    }
}
