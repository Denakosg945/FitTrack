package gr.hua.fitTrack.web;

import gr.hua.fitTrack.core.model.*;
import gr.hua.fitTrack.core.repository.*;
import gr.hua.fitTrack.web.rest.ProfileController;
import jakarta.transaction.Transactional;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//Class for testing the app
@Transactional
@RestController
public class TestController {

    PersonRepository personRepository;
    AppointmentRepository appointmentRepository;
    GoalsRepository goalsRepository;
    ProgressRepository progressRepository;
    TrainerScheduleSlotRepository trainerScheduleSlotRepository;

    public TestController(
            PersonRepository personRepository,
            AppointmentRepository appointmentRepository,
            GoalsRepository goalsRepository,
            ProgressRepository progressRepository,
            TrainerScheduleSlotRepository trainerScheduleSlotRepository
    ) {
        this.personRepository = personRepository;
        this.appointmentRepository = appointmentRepository;
        this.goalsRepository = goalsRepository;
        this.progressRepository = progressRepository;
        this.trainerScheduleSlotRepository = trainerScheduleSlotRepository;
    }

    @GetMapping(path = "/test", produces = MediaType.TEXT_PLAIN_VALUE)
    public String test() {
        // trainer test (dummy data)
        Person trainer = new Person();
        trainer.setAge(31);
        trainer.setId(null);
        trainer.setFirstName("John");
        trainer.setLastName("Smith");
        trainer.setEmailAddress("john.smith@gmail.com");
        trainer.setPhoneNumber("1234567890");
        trainer.setPasswordHash("null");
        trainer.setType(PersonType.TRAINER);
        personRepository.save(trainer);

        // client test
        Person client = new Person();
        client.setAge(22);
        client.setFirstName("John2");
        client.setLastName("Smith2");
        client.setEmailAddress("john.smith2@gmail.com");
        client.setPhoneNumber("1234545671");
        client.setPasswordHash("null");
        client.setType(PersonType.CLIENT);
        personRepository.save(client);

        // trainer schedule slot test (available slot)
        TrainerScheduleSlot trainerScheduleSlot = new TrainerScheduleSlot();
        trainerScheduleSlot.setEndDateTime("dummy end time");
        trainerScheduleSlot.setStartDateTime("dummy start time");
        trainerScheduleSlot.setStatus(TrainerSlotAvailability.AVAILABLE);
        trainerScheduleSlot.setTrainer(trainer);
        trainerScheduleSlot.setAppointment(null);
        trainerScheduleSlotRepository.save(trainerScheduleSlot);

        // appointment test
        Appointment appointment = new Appointment();
        appointment.setClient(client);
        appointment.setTrainer(trainer);
        appointment.setStatus("PENDING");
        appointment.setNotes("dummy notes");
        appointment.setOutdoor(false);
        appointment.setTrainerScheduleSlot(trainerScheduleSlot);
        appointmentRepository.save(appointment);

        // trainer schedule slot test (taken slot)
        trainerScheduleSlot = new TrainerScheduleSlot();
        trainerScheduleSlot.setEndDateTime("dummy end time2");
        trainerScheduleSlot.setStartDateTime("dummy start time2");
        trainerScheduleSlot.setTrainer(trainer);
        trainerScheduleSlot.setStatus(TrainerSlotAvailability.BOOKED);
        trainerScheduleSlot.setAppointment(appointment);
        trainerScheduleSlotRepository.save(trainerScheduleSlot);

        // progress test
        Progress progress = new Progress();
        progress.setClient(client);
        progress.setBodyFatPercentage(14);
        progress.setWeight(65.2F);
        progress.setTrainerNotes("some notes");
        progress.setRunningTimeSeconds(7200);
        progress.setWaterIntake(1200);
        progressRepository.save(progress);

        // Goals test
        Goals goals = new Goals();
        goals.setClient(client);
        goals.setBodyFatPercentageGoal(7);
        goals.setWeightGoal(60F);
        goals.setRunningTimeGoal(9600);
        goalsRepository.save(goals);



        return "test";
    }
}
