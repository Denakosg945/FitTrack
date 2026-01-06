package gr.hua.fitTrack.core.repository;

import gr.hua.fitTrack.core.model.TrainerOverrideAvailability;
import gr.hua.fitTrack.core.model.TrainerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface TrainerOverrideAvailabilityRepository extends JpaRepository<TrainerOverrideAvailability, Integer> {
    Optional<TrainerOverrideAvailability>
    findByTrainerProfileAndDate(
            TrainerProfile trainerProfile,
            LocalDate date
    );
}
