package gr.hua.fitTrack.core.repository;

import gr.hua.fitTrack.core.model.TrainerWeeklyAvailability;
import gr.hua.fitTrack.core.model.Weekday;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainerWeeklyAvailabilityRepository
        extends JpaRepository<TrainerWeeklyAvailability, Long> {

    // Find all weekly availability for a given TrainerProfile entity
    List<TrainerWeeklyAvailability> findByTrainerProfile_Id(Long trainerProfileId);

    @Modifying
    @Transactional
    @Query("""
        delete from TrainerWeeklyAvailability twa
        where twa.trainerProfile.id = :trainerProfileId
    """)
    void deleteByTrainerProfileId(@Param("trainerProfileId") Long trainerProfileId);

    List<TrainerWeeklyAvailability> findByTrainerProfileId(Long trainerProfileId);
}
