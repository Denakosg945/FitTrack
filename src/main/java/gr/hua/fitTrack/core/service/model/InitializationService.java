package gr.hua.fitTrack.core.service.model;

import gr.hua.fitTrack.core.model.*;
import gr.hua.fitTrack.core.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@Profile("dev")
@Transactional
public class InitializationService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(InitializationService.class);

    private final PersonRepository personRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final AppointmentRepository appointmentRepository;
    private final TrainerClientNotesRepository trainerClientNotesRepository;
    private final TrainerWeeklyAvailabilityRepository weeklyAvailabilityRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public InitializationService(
            PersonRepository personRepository,
            TrainerProfileRepository trainerProfileRepository,
            ClientProfileRepository clientProfileRepository,
            AppointmentRepository appointmentRepository,
            TrainerClientNotesRepository trainerClientNotesRepository,
            TrainerWeeklyAvailabilityRepository weeklyAvailabilityRepository
    ) {
        this.personRepository = personRepository;
        this.trainerProfileRepository = trainerProfileRepository;
        this.clientProfileRepository = clientProfileRepository;
        this.appointmentRepository = appointmentRepository;
        this.trainerClientNotesRepository = trainerClientNotesRepository;
        this.weeklyAvailabilityRepository = weeklyAvailabilityRepository;
    }

    public void populateDatabase() {

        if (personRepository.count() > 0) {
            LOGGER.info("Database already populated — skipping initialization.");
            return;
        }

        LOGGER.info("Starting DEV database initialization...");

        TrainerProfile testTrainer = createFixedTestTrainer();
        ClientProfile testClient = createFixedTestClient();
        createFixedTestAppointments(testClient, testTrainer);

        List<TrainerProfile> trainers = createRandomTrainers(100);
        List<ClientProfile> clients = createRandomClients(100);

        createDevAppointmentsForNext7Days(testTrainer, clients);

        LOGGER.info("DEV DATABASE INITIALIZED SUCCESSFULLY");
    }

    // ================= FIXED TRAINER =================
    private TrainerProfile createFixedTestTrainer() {

        Person p = new Person();
        p.setFirstName("Test");
        p.setLastName("Trainer");
        p.setAge(35);
        p.setGender(GenderType.MALE);
        p.setEmailAddress("test.trainer@fittrack.com");
        p.setPhoneNumber("+306900000000");
        p.setPasswordHash(passwordEncoder.encode("test1234"));
        p.setType(PersonType.TRAINER);

        personRepository.save(p);

        TrainerProfile tp = new TrainerProfile();
        tp.setPerson(p);
        tp.setLocation("Athens");
        tp.setSpecialization("General Fitness");

        trainerProfileRepository.save(tp);

        Map<Weekday, String> start = Map.of(
                Weekday.MONDAY, "09:00",
                Weekday.TUESDAY, "09:00",
                Weekday.WEDNESDAY, "09:00",
                Weekday.THURSDAY, "09:00",
                Weekday.FRIDAY, "09:00"
        );

        Map<Weekday, String> end = Map.of(
                Weekday.MONDAY, "17:00",
                Weekday.TUESDAY, "17:00",
                Weekday.WEDNESDAY, "17:00",
                Weekday.THURSDAY, "17:00",
                Weekday.FRIDAY, "17:00"
        );

        List<TrainerWeeklyAvailability> av =
                buildWeeklyAvailability(tp, start, end);

        weeklyAvailabilityRepository.saveAll(av);
        tp.setWeeklyAvailability(av);

        return tp;
    }

    // ================= FIXED CLIENT =================
    private ClientProfile createFixedTestClient() {

        Person p = new Person();
        p.setFirstName("Test");
        p.setLastName("Client");
        p.setAge(28);
        p.setGender(GenderType.FEMALE);
        p.setEmailAddress("test.client@fittrack.com");
        p.setPhoneNumber("+306911111111");
        p.setPasswordHash(passwordEncoder.encode("test1234"));
        p.setType(PersonType.CLIENT);

        personRepository.save(p);

        ClientProfile cp = new ClientProfile();
        cp.setPerson(p);
        cp.setHeight(168);
        cp.setWeight(65);

        clientProfileRepository.save(cp);
        return cp;
    }

    // ================= FIXED APPOINTMENTS =================
    private void createFixedTestAppointments(ClientProfile c, TrainerProfile t) {

        appointmentRepository.saveAll(List.of(
                new Appointment(c, t, LocalDate.now().plusDays(1),
                        LocalTime.of(10, 0), LocalTime.of(11, 0),
                        "CONFIRMED", false, "Upper body"),
                new Appointment(c, t, LocalDate.now().plusDays(3),
                        LocalTime.of(18, 0), LocalTime.of(19, 0),
                        "CONFIRMED", false, "Cardio"),
                new Appointment(c, t, LocalDate.now().plusDays(6),
                        LocalTime.of(12, 0), LocalTime.of(13, 0),
                        "PENDING", false, "Leg day")
        ));
    }

    // ================= HELPERS =================
    private List<TrainerWeeklyAvailability> buildWeeklyAvailability(
            TrainerProfile t,
            Map<Weekday, String> s,
            Map<Weekday, String> e
    ) {
        List<TrainerWeeklyAvailability> list = new ArrayList<>();
        for (Weekday d : Weekday.values()) {
            if (!s.containsKey(d)) continue;
            TrainerWeeklyAvailability wa = new TrainerWeeklyAvailability();
            wa.setTrainerProfile(t);
            wa.setWeekday(d);
            wa.setStartTime(LocalTime.parse(s.get(d)));
            wa.setEndTime(LocalTime.parse(e.get(d)));
            list.add(wa);
        }
        return list;
    }

    private Optional<LocalTime[]> resolveWeeklyWorkingHours(
            TrainerProfile t, LocalDate date
    ) {
        Weekday wd = Weekday.from(date);
        return t.getWeeklyAvailability().stream()
                .filter(w -> w.getWeekday() == wd)
                .findFirst()
                .map(w -> new LocalTime[]{w.getStartTime(), w.getEndTime()});
    }
}
