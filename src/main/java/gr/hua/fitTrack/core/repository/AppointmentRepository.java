package gr.hua.fitTrack.core.repository;

import gr.hua.fitTrack.core.model.Appointment;
import gr.hua.fitTrack.core.model.ClientProfile;
import gr.hua.fitTrack.core.model.TrainerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    List<Appointment> findByClient(ClientProfile client);

    List<Appointment> findByTrainer(TrainerProfile trainer);

    List<Appointment> findByTrainerAndDateBetweenOrderByDateAscStartTimeAsc(
            TrainerProfile trainer,
            LocalDate start,
            LocalDate end
    );

    List<Appointment> findByClient_Person_EmailAddressOrderByDateAscStartTimeAsc
            (String email);

    @Query("""
    select count(a)
    from Appointment a
    where a.client.person.emailAddress = :email
      and a.status = 'ACTIVE'
""")
    long countActiveAppointments(@Param("email") String email);


}
