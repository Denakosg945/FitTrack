package gr.hua.fitTrack.core.service.model;

import gr.hua.fitTrack.core.model.TrainerOverrideAvailability;
import gr.hua.fitTrack.core.model.TrainerWeeklyAvailability;
import gr.hua.fitTrack.core.model.Weekday;

import java.util.List;
import java.util.Map;

public record TrainerView(
        Long trainerProfileId,
        Long personId,
        String firstName,
        String lastName,
        String location,
        String specialization,
        Map<Weekday, WeeklyAvailabilityView> weeklyAvailability
       ) {
}
