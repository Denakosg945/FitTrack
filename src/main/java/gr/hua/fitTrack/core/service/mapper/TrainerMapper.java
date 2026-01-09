package gr.hua.fitTrack.core.service.mapper;

import gr.hua.fitTrack.core.model.TrainerProfile;
import gr.hua.fitTrack.core.model.TrainerWeeklyAvailability;
import gr.hua.fitTrack.core.model.Weekday;
import gr.hua.fitTrack.core.security.TokenSigner;
import gr.hua.fitTrack.core.service.model.TrainerSelectableView;
import gr.hua.fitTrack.core.service.model.TrainerView;
import gr.hua.fitTrack.core.service.model.WeeklyAvailabilityView;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class TrainerMapper {

    TokenSigner tokenSigner ;

    public TrainerView convertTrainerToTrainerView(TrainerProfile trainerProfile) {

        Map<Weekday, WeeklyAvailabilityView> availabilityMap =
                new EnumMap<>(Weekday.class);


        for (TrainerWeeklyAvailability wa : trainerProfile.getWeeklyAvailability()) {
            availabilityMap.put(
                    wa.getWeekday(),
                    new WeeklyAvailabilityView(
                            wa.getId(),
                            wa.getWeekday(),
                            wa.getStartTime(),
                            wa.getEndTime()
                    )
            );
        }

        // 2️⃣ Ensure όλες οι ημέρες υπάρχουν (για Thymeleaf)
        for (Weekday day : Weekday.values()) {
            availabilityMap.putIfAbsent(
                    day,
                    new WeeklyAvailabilityView(
                            null,
                            day,
                            null,
                            null
                    )
            );
        }

        return new TrainerView(
                trainerProfile.getId(),
                trainerProfile.getPerson().getId(),
                trainerProfile.getPerson().getFirstName(),
                trainerProfile.getPerson().getLastName(),
                trainerProfile.getLocation(),
                trainerProfile.getSpecialization(),
                availabilityMap
        );
    }

    public TrainerSelectableView convertTrainerToSelectableTrainerView(TrainerProfile trainer, String token) {
        return new TrainerSelectableView(
                trainer.getPerson().getFirstName(),
                trainer.getPerson().getLastName(),
                trainer.getLocation(),
                trainer.getSpecialization(),
                token        );
    }

}
