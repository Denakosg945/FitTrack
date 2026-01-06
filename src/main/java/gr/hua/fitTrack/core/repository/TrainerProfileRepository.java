package gr.hua.fitTrack.core.repository;

import gr.hua.fitTrack.core.model.TrainerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TrainerProfileRepository extends JpaRepository <TrainerProfile, Long> {
    boolean existsByPersonId(Long personId);

    Optional<TrainerProfile> findByPersonId(Long personId);

    @Query("""
        select distinct tp
        from TrainerProfile tp
        join fetch tp.person p
        left join fetch tp.weeklyAvailability wa
        where p.id = :personId
    """)
    Optional<TrainerProfile> findByPersonIdWithWeeklyAvailability(@Param("personId") Long personId);



}
