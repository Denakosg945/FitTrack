package gr.hua.fitTrack.core.repository;

import gr.hua.fitTrack.core.model.Person;
import gr.hua.fitTrack.core.model.TrainerScheduleSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainerScheduleSlotRepository extends JpaRepository<TrainerScheduleSlot, Long> {

    // Jpa provides CRUD functions automatically

    public List<TrainerScheduleSlot> findByTrainer(Person trainer);

    public void deleteByTrainerAndId(Person trainer,Long id);

}
