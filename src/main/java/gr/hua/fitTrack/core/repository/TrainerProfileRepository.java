package gr.hua.fitTrack.core.repository;

import gr.hua.fitTrack.core.model.TrainerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerProfileRepository extends JpaRepository <TrainerProfile, Integer> {
    boolean existsByPersonId(Long personId);

}
