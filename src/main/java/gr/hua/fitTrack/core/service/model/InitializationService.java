package gr.hua.fitTrack.core.service.model;

import gr.hua.fitTrack.core.model.*;
import gr.hua.fitTrack.core.repository.*;
import gr.hua.fitTrack.core.security.APIClientDetails;
import gr.hua.fitTrack.core.security.APIClientDetailsService;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(InitializationService.class);

    private final PersonRepository personRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final AppointmentRepository appointmentRepository;
    private final TrainerClientNotesRepository trainerClientNotesRepository;
    private final TrainerWeeklyAvailabilityRepository weeklyAvailabilityRepository;
    private final APIClientRepository apiClientRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Random random = new Random();

    // ================== GREEK POOLS (λίστες) ==================
    private static final List<String> MALE_FIRST_NAMES = List.of(
            "Γιώργος","Νίκος","Κώστας","Δημήτρης","Γιάννης","Παναγιώτης","Βασίλης",
            "Χρήστος","Θανάσης","Αλέξανδρος","Σπύρος","Μανώλης","Αντώνης","Στέλιος"
    );

    private static final List<String> FEMALE_FIRST_NAMES = List.of(
            "Μαρία","Ελένη","Κατερίνα","Γεωργία","Σοφία","Ιωάννα","Αναστασία",
            "Δήμητρα","Χριστίνα","Ευαγγελία","Αθηνά","Αγγελική","Βασιλική"
    );

    private static final List<String> LAST_NAMES = List.of(
            "Παπαδόπουλος","Οικονόμου","Γεωργίου","Δημητρίου","Νικολάου","Παναγιωτίδης",
            "Χριστοδούλου","Κωνσταντίνου","Αναστασίου","Βασιλείου","Αλεξίου","Ιωαννίδης",
            "Σταμάτης","Καραγιάννης","Μητρόπουλος"
    );

    private static final List<String> LOCATIONS = List.of(
            "Athens", "Piraeus", "Thessaloniki", "Patras", "Heraklion"
    );

    private static final List<String> SPECIALIZATIONS = List.of(
            "General Fitness", "Strength Training", "Weight Loss", "Rehab", "Functional Training"
    );

    public InitializationService(
            PersonRepository personRepository,
            TrainerProfileRepository trainerProfileRepository,
            ClientProfileRepository clientProfileRepository,
            AppointmentRepository appointmentRepository,
            TrainerClientNotesRepository trainerClientNotesRepository,
            TrainerWeeklyAvailabilityRepository weeklyAvailabilityRepository,
            APIClientRepository apiClientRepository
    ) {
        this.personRepository = personRepository;
        this.trainerProfileRepository = trainerProfileRepository;
        this.clientProfileRepository = clientProfileRepository;
        this.appointmentRepository = appointmentRepository;
        this.trainerClientNotesRepository = trainerClientNotesRepository;
        this.weeklyAvailabilityRepository = weeklyAvailabilityRepository;
        this.apiClientRepository = apiClientRepository;
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
        createApiDummyData();

        List<TrainerProfile> trainers = createRandomTrainers(100);
        List<ClientProfile> clients = createRandomClients(100);

        createDevAppointmentsForNext7Days(testTrainer, clients);

        LOGGER.info("DEV DATABASE INITIALIZED SUCCESSFULLY");
    }

    // ================== PICKERS (επιλογείς) ==================
    private String pick(List<String> list) {
        return list.get(random.nextInt(list.size()));
    }

    private GenderType randomGender() {
        return random.nextBoolean() ? GenderType.MALE : GenderType.FEMALE;
    }

    private String pickFirstNameByGender(GenderType gender) {
        return gender == GenderType.MALE ? pick(MALE_FIRST_NAMES) : pick(FEMALE_FIRST_NAMES);
    }

    private int randomAge(int min, int max) {
        return min + random.nextInt((max - min) + 1);
    }

    private String uniqueEmail(String prefix, int i) {
        // emails must be ASCII-friendly -> keep it simple
        return prefix + String.format("%04d", i) + "@fittrack.dev";
    }

    private String uniquePhone(String prefix, int i) {
        // E164: +3069XXXXXXXX
        // prefix should already contain "+3069"
        return prefix + String.format("%06d", i); // makes +3069 + 6 digits = 10 digits after +30
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

        List<TrainerWeeklyAvailability> av = buildWeeklyAvailability(tp, start, end);
        weeklyAvailabilityRepository.saveAll(av);
        tp.setWeeklyAvailability(av);

        return tp;
    }

    // ================= ADDED DUMMY DATA FOR API CLIENTS ==============
    private void createApiDummyData() {
        final List<APIClient> clients = List.of(
                new APIClient(null, "bigSecret", "INTEGRATION_READ,INTEGRATION_WRITE", "client00"),
                new APIClient(null, "bigSecret", "INTEGRATION_READ", "client01"),
                new APIClient(null, "bigSecret", "INTEGRATION_WRITE", "client02")
        );

        apiClientRepository.saveAll(clients);
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

    // ================= FIXED APPOINTMENTS (with notes) =================
    private void createFixedTestAppointments(ClientProfile c, TrainerProfile t) {
        saveAppointmentWithNotes(new Appointment(
                c, t, LocalDate.now().plusDays(1),
                LocalTime.of(10, 0), LocalTime.of(11, 0),
                "CONFIRMED", false, "Upper body"
        ));

        saveAppointmentWithNotes(new Appointment(
                c, t, LocalDate.now().plusDays(3),
                LocalTime.of(18, 0), LocalTime.of(19, 0),
                "CONFIRMED", false, "Cardio"
        ));

        saveAppointmentWithNotes(new Appointment(
                c, t, LocalDate.now().plusDays(6),
                LocalTime.of(12, 0), LocalTime.of(13, 0),
                "PENDING", false, "Leg day"
        ));
    }

    // ================= APPOINTMENT + TRAINER CLIENT NOTES =================
    private Appointment saveAppointmentWithNotes(Appointment a) {
        Appointment saved = appointmentRepository.save(a);

        TrainerProfile trainer = saved.getTrainer();
        ClientProfile client = saved.getClient();

        trainerClientNotesRepository
                .findByTrainerAndClient(trainer, client)
                .orElseGet(() -> trainerClientNotesRepository.save(
                        new TrainerClientNotes(
                                trainer,
                                client,
                                "DEV init: first contact created on " + LocalDate.now()
                        )
                ));

        return saved;
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

    private Optional<LocalTime[]> resolveWeeklyWorkingHours(TrainerProfile t, LocalDate date) {
        Weekday wd = Weekday.from(date);
        return t.getWeeklyAvailability().stream()
                .filter(w -> w.getWeekday() == wd)
                .findFirst()
                .map(w -> new LocalTime[]{w.getStartTime(), w.getEndTime()});
    }

    // ================= RANDOM TRAINERS (Greek names) =================
    private List<TrainerProfile> createRandomTrainers(int count) {
        List<TrainerProfile> list = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            GenderType gender = randomGender();

            Person p = new Person();
            p.setFirstName(pickFirstNameByGender(gender));
            p.setLastName(pick(LAST_NAMES));
            p.setAge(randomAge(22, 50));
            p.setGender(gender);
            p.setPhoneNumber(uniquePhone("+3069", 100000 + i)); // avoids collision with clients
            p.setEmailAddress(uniqueEmail("trainer", i));
            p.setPasswordHash(passwordEncoder.encode("pass" + i));
            p.setType(PersonType.TRAINER);
            personRepository.save(p);

            TrainerProfile tp = new TrainerProfile();
            tp.setPerson(p);
            tp.setLocation(pick(LOCATIONS));
            tp.setSpecialization(pick(SPECIALIZATIONS));
            trainerProfileRepository.save(tp);

            // Optional but recommended: give each trainer weekly availability (διαθεσιμότητα)
            Map<Weekday, String> start = Map.of(
                    Weekday.MONDAY, "09:00",
                    Weekday.TUESDAY, "10:00",
                    Weekday.WEDNESDAY, "09:00",
                    Weekday.THURSDAY, "10:00",
                    Weekday.FRIDAY, "09:00"
            );
            Map<Weekday, String> end = Map.of(
                    Weekday.MONDAY, "17:00",
                    Weekday.TUESDAY, "18:00",
                    Weekday.WEDNESDAY, "17:00",
                    Weekday.THURSDAY, "18:00",
                    Weekday.FRIDAY, "16:00"
            );

            List<TrainerWeeklyAvailability> av = buildWeeklyAvailability(tp, start, end);
            weeklyAvailabilityRepository.saveAll(av);
            tp.setWeeklyAvailability(av);

            list.add(tp);
        }
        return list;
    }

    // ================= RANDOM CLIENTS (Greek names) =================
    private List<ClientProfile> createRandomClients(int count) {
        List<ClientProfile> list = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            GenderType gender = randomGender();

            Person p = new Person();
            p.setFirstName(pickFirstNameByGender(gender));
            p.setLastName(pick(LAST_NAMES));
            p.setAge(randomAge(18, 60));
            p.setGender(gender);
            p.setEmailAddress(uniqueEmail("client", i));
            p.setPhoneNumber(uniquePhone("+3069", 200000 + i)); // different range from trainers
            p.setPasswordHash(passwordEncoder.encode("pass" + i));
            p.setType(PersonType.CLIENT);
            personRepository.save(p);

            ClientProfile cp = new ClientProfile();
            cp.setPerson(p);
            cp.setHeight(155 + random.nextInt(40)); // 155-194
            cp.setWeight(50 + random.nextInt(60));  // 50-109
            clientProfileRepository.save(cp);

            list.add(cp);
        }
        return list;
    }

    // ================= DEV APPOINTMENTS (with notes) =================
    private void createDevAppointmentsForNext7Days(
            TrainerProfile trainer,
            List<ClientProfile> clients
    ) {
        LocalDate today = LocalDate.now();

        for (int d = 0; d < 7; d++) {
            LocalDate date = today.plusDays(d);

            Optional<LocalTime[]> hours = resolveWeeklyWorkingHours(trainer, date);
            if (hours.isEmpty()) continue;

            LocalTime start = hours.get()[0];

            ClientProfile client = clients.get(random.nextInt(clients.size()));

            Appointment a = new Appointment(
                    client,
                    trainer,
                    date,
                    start,
                    start.plusHours(1),
                    "CONFIRMED",
                    false,
                    "DEV session"
            );

            saveAppointmentWithNotes(a);
        }
    }
}
