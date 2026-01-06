package gr.hua.fitTrack.core.repository;

import gr.hua.fitTrack.core.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainerClientNotesRepository
        extends JpaRepository<TrainerClientNotes, Long> {

    List<TrainerClientNotes> findByTrainer(TrainerProfile trainer);

    Optional<TrainerClientNotes> findByTrainerAndClient(
            TrainerProfile trainer,
            ClientProfile client
    );
}
