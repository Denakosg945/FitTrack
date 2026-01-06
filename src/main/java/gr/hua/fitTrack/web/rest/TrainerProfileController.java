package gr.hua.fitTrack.web.rest;

import gr.hua.fitTrack.core.model.Weekday;
import gr.hua.fitTrack.core.service.TrainerService;
import gr.hua.fitTrack.core.service.model.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalTime;
import java.util.*;

@Controller
@RequestMapping("/trainer")
public class TrainerProfileController {

    private final TrainerService trainerService;

    public TrainerProfileController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    // =========================
    // PROFILE VIEW
    // =========================
    @GetMapping("/profile")
    public String trainerProfile(Model model) {

        Long TEST_TRAINER_PERSON_ID = 1L;

        TrainerView trainer =
                trainerService.getTrainerProfileByPersonId(TEST_TRAINER_PERSON_ID);

        List<TrainerAppointmentView> appointments =
                trainerService.getTrainerAppointmentsNext7Days(TEST_TRAINER_PERSON_ID);

        model.addAttribute("trainer", trainer);
        model.addAttribute(
                "dailySchedule",
                trainerService.getTrainerScheduleForNext7Days(TEST_TRAINER_PERSON_ID)
        );
        model.addAttribute("appointments", appointments);


        return "trainer/profile";
    }

    // =========================
    // EDIT PROFILE
    // =========================
    @GetMapping("/profile/edit")
    public String editProfile(Model model) {

        Long TEST_TRAINER_PERSON_ID = 1L;

        TrainerView trainer =
                trainerService.getTrainerProfileByPersonId(TEST_TRAINER_PERSON_ID);

        UpdateTrainerProfileRequest form =
                new UpdateTrainerProfileRequest(
                        TEST_TRAINER_PERSON_ID,
                        trainer.location(),
                        trainer.specialization()
                );

        model.addAttribute("trainer", trainer);
        model.addAttribute("form", form);

        return "trainer/edit/profile";
    }

    @PostMapping("/profile/edit")
    public String saveProfile(@ModelAttribute("form") UpdateTrainerProfileRequest form) {

        trainerService.updateTrainerProfile(form);
        return "redirect:/trainer/profile";
    }

    // =========================
    // EDIT WEEKLY SCHEDULE (GET)
    // =========================
    @GetMapping("/profile/edit/schedule")
    public String editWeeklySchedule(Model model) {

        Long TEST_TRAINER_PERSON_ID = 1L;

        TrainerView trainer =
                trainerService.getTrainerProfileByPersonId(TEST_TRAINER_PERSON_ID);

        Map<String, WeeklyAvailabilityView> availability = new HashMap<>();

        trainer.weeklyAvailability().forEach(
                (day, value) -> availability.put(day.name(), value)
        );

        model.addAttribute("trainer", trainer);
        model.addAttribute("days", Weekday.values());
        model.addAttribute("availability", availability);
        model.addAttribute("timeOptions", timeOptions());

        return "trainer/edit/schedule";
    }
    private List<String> timeOptions() {
        List<String> times = new ArrayList<>();
        for (int h = 6; h <= 23; h++) {
            times.add(String.format("%02d:00", h));
        }
        return times;
    }



    // =========================
    // SAVE WEEKLY SCHEDULE (POST)
    // =========================
    @PostMapping("/profile/edit/schedule")
    public String saveWeeklySchedule(
            @RequestParam Long personId,
            @RequestParam Map<String, String> params
    ) {

        Map<Weekday, LocalTime> parsedStartTimes = new EnumMap<>(Weekday.class);
        Map<Weekday, LocalTime> parsedEndTimes   = new EnumMap<>(Weekday.class);

        for (Weekday day : Weekday.values()) {

            String startKey = "startTimes[" + day.name() + "]";
            String endKey   = "endTimes[" + day.name() + "]";

            String start = params.get(startKey);
            String end   = params.get(endKey);

            LocalTime startTime =
                    (start == null || start.isBlank()) ? null : LocalTime.parse(start);

            LocalTime endTime =
                    (end == null || end.isBlank()) ? null : LocalTime.parse(end);

            if (startTime != null && endTime != null && endTime.isBefore(startTime)) {
                throw new IllegalArgumentException(
                        "End time before start time for " + day
                );
            }

            parsedStartTimes.put(day, startTime);
            parsedEndTimes.put(day, endTime);
        }

        trainerService.updateWeeklyAvailability(
                personId,
                parsedStartTimes,
                parsedEndTimes
        );

        return "redirect:/trainer/profile";
    }

    @GetMapping("/profile/edit/override")
    public String createOverride(Model model) {

        Long TEST_TRAINER_PERSON_ID = 1L;

        TrainerOverrideRequest form =
                new TrainerOverrideRequest(
                        TEST_TRAINER_PERSON_ID,
                        null,
                        true,
                        null,
                        null
                );

        model.addAttribute("form", form);
        model.addAttribute("timeOptions", timeOptions());

        return "trainer/edit/override";
    }

    @PostMapping("/profile/edit/override")
    public String saveOverride(
            @ModelAttribute("form") TrainerOverrideRequest form
    ) {
        trainerService.createOrUpdateOverride(form);
        return "redirect:/trainer/profile";
    }




    // =========================
    // HELPER
    // =========================
    private List<String> buildTimeOptions() {
        List<String> times = new ArrayList<>();
        for (int hour = 6; hour <= 23; hour++) {
            times.add(String.format("%02d:00", hour));
        }
        return times;
    }
}
