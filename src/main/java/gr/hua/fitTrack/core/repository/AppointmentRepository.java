package gr.hua.fitTrack.core.repository;

import gr.hua.fitTrack.core.model.Appointment;
import gr.hua.fitTrack.core.model.ClientProfile;
import gr.hua.fitTrack.core.model.Person;
import gr.hua.fitTrack.core.model.TrainerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface
AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByClient(ClientProfile client);

    List<Appointment> findByTrainer(TrainerProfile trainer);

    // Jpa provides CRUD functions automatically
}