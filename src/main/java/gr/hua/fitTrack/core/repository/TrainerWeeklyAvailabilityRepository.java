package gr.hua.fitTrack.core.repository;

import gr.hua.fitTrack.core.model.TrainerWeeklyAvailability;
import gr.hua.fitTrack.core.model.Weekday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainerWeeklyAvailabilityRepository
        extends JpaRepository<TrainerWeeklyAvailability, Integer> {

    // Find all weekly availability for a given TrainerProfile entity
    List<TrainerWeeklyAvailability> findByTrainerProfile_Id(Long trainerProfileId);

    // Find weekly availability for a trainer by weekday
    List<TrainerWeeklyAvailability> findByTrainerProfile_IdAndWeekday(
            Long trainerProfileId,
            Weekday weekday
    );
}
