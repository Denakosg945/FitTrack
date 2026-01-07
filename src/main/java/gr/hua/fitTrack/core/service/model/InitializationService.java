package gr.hua.fitTrack.core.service.model;

import gr.hua.fitTrack.core.model.*;
import gr.hua.fitTrack.core.repository.AppointmentRepository;
import gr.hua.fitTrack.core.repository.ClientProfileRepository;
import gr.hua.fitTrack.core.repository.TrainerClientNotesRepository;
import gr.hua.fitTrack.core.repository.TrainerProfileRepository;
import gr.hua.fitTrack.core.service.ClientService;
import gr.hua.fitTrack.core.service.PersonService;
import gr.hua.fitTrack.core.service.TrainerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@Profile("dev")
public class InitializationService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(InitializationService.class);

    private final PersonService personService;
    private final TrainerService trainerService;
    private final ClientService clientService;

    private final TrainerProfileRepository trainerProfileRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final AppointmentRepository appointmentRepository;
    private final TrainerClientNotesRepository trainerClientNotesRepository;

    private Optional<LocalTime[]> resolveWeeklyWorkingHours(
            TrainerProfile trainer,
            LocalDate date
    ) {
        Weekday weekday = Weekday.from(date);

        return trainer.getWeeklyAvailability().stream()
                .filter(w -> w.getWeekday() == weekday)
                .findFirst()
                .flatMap(w -> {
                    if (w.getStartTime() == null || w.getEndTime() == null) {
                        return Optional.empty();
                    }
                    return Optional.of(
                            new LocalTime[]{ w.getStartTime(), w.getEndTime() }
                    );
                });
    }

    public InitializationService(PersonService personService,
                                 TrainerService trainerService,
                                 ClientService clientService,
                                 TrainerProfileRepository trainerProfileRepository,
                                 ClientProfileRepository clientProfileRepository,
                                 AppointmentRepository appointmentRepository,
                                 TrainerClientNotesRepository trainerClientNotesRepository) {
        this.personService = personService;
        this.trainerService = trainerService;
        this.clientService = clientService;
        this.trainerProfileRepository = trainerProfileRepository;
        this.clientProfileRepository = clientProfileRepository;
        this.appointmentRepository = appointmentRepository;
        this.trainerClientNotesRepository = trainerClientNotesRepository;
    }

    public void populateDatabase() {

        /* -------------------------------------------------
           SAFETY CHECK – do not reinitialize
         ------------------------------------------------- */

        if (personService.countPersons() > 0) {
            LOGGER.info("Database already populated — skipping initialization.");
            return;
        }

        LOGGER.info("Starting DEV database initialization...");

        /* -------------------------------------------------
           FIXED TEST TRAINER
         ------------------------------------------------- */

        Optional<Person> existingTestTrainer =
                personService.getByEmail("test.trainer@fittrack.com");

        if (existingTestTrainer.isEmpty()) {

            CreatePersonResult testTrainer =
                    personService.createPerson(
                            new CreatePersonRequest(
                                    "Test",
                                    "Trainer",
                                    35,
                                    GenderType.MALE,
                                    "test.trainer@fittrack.com",
                                    "+306900000000",
                                    "test1234",
                                    PersonType.TRAINER
                            )
                    );

            if (testTrainer.created()) {

                trainerService.createTrainerProfile(
                        new CreateTrainerRequest(
                                testTrainer.personView().id(),
                                "Athens",
                                "General Fitness",
                                "Fixed test trainer profile",
                                Map.of(
                                        Weekday.MONDAY, "09:00",
                                        Weekday.TUESDAY, "09:00",
                                        Weekday.WEDNESDAY, "09:00",
                                        Weekday.THURSDAY, "09:00",
                                        Weekday.FRIDAY, "09:00"
                                ),
                                Map.of(
                                        Weekday.MONDAY, "17:00",
                                        Weekday.TUESDAY, "17:00",
                                        Weekday.WEDNESDAY, "17:00",
                                        Weekday.THURSDAY, "17:00",
                                        Weekday.FRIDAY, "17:00"
                                )
                        )
                );

                LOGGER.info("Fixed TEST TRAINER created.");
            }
        }


        /* -------------------------------------------------
   FIXED TEST CLIENT
------------------------------------------------- */

        Optional<Person> existingTestClient =
                personService.getByEmail("test.client@fittrack.com");

        if (existingTestClient.isEmpty()) {

            CreatePersonResult testClient =
                    personService.createPerson(
                            new CreatePersonRequest(
                                    "Test",
                                    "Client",
                                    28,
                                    GenderType.FEMALE,
                                    "test.client@fittrack.com",
                                    "+306911111111",
                                    "test1234",
                                    PersonType.CLIENT
                            )
                    );

            if (!testClient.created()) {
                throw new IllegalStateException("Test client person not created");
            }

            // 1️⃣ CREATE ClientProfile
            clientService.createClientProfile(
                    new CreateClientRequest(
                            testClient.personView().id(),
                            65,
                            168,
                            null,
                            null,
                            null
                    )
            );

            // 2️⃣ LOAD ClientProfile (NOW it exists)
            ClientProfile clientProfile =
                    clientProfileRepository
                            .findByPersonId(testClient.personView().id())
                            .orElseThrow(() ->
                                    new IllegalStateException("ClientProfile not created")
                            );

            // 3️⃣ CREATE Goals
            Goals goals = new Goals(
                    60f,
                    18,
                    25,
                    clientProfile
            );
            clientProfile.setGoals(goals);

            // 4️⃣ CREATE Progress
            List<Progress> progressList = List.of(
                    new Progress(
                            clientProfile,
                            68f,
                            Instant.now().minusSeconds(10 * 24 * 3600),
                            32 * 60,
                            22,
                            2.0f
                    ),
                    new Progress(
                            clientProfile,
                            66f,
                            Instant.now().minusSeconds(5 * 24 * 3600),
                            30 * 60,
                            21,
                            2.2f
                    )
            );

            clientProfile.setProgress(progressList);

            // 5️⃣ SAVE
            clientProfileRepository.save(clientProfile);

            LOGGER.info("Fixed TEST CLIENT created with goals & progress.");
        }

        /* -------------------------------------------------
   FIXED TEST CLIENT APPOINTMENTS (DEV)
------------------------------------------------- */

        Optional<Person> testClientPersonOpt =
                personService.getByEmail("test.client@fittrack.com");

        Optional<Person> testTrainerPersonOpt =
                personService.getByEmail("test.trainer@fittrack.com");

        if (testClientPersonOpt.isPresent() && testTrainerPersonOpt.isPresent()) {

            ClientProfile testClient =
                    clientProfileRepository
                            .findByPersonId(
                                    testClientPersonOpt.get().getId()
                            )
                            .orElseThrow();

            TrainerProfile testTrainer =
                    trainerProfileRepository
                            .findByPersonId(
                                    testTrainerPersonOpt.get().getId()
                            )
                            .orElseThrow();

            List<Appointment> testAppointments = List.of(

                    new Appointment(
                            testClient,
                            testTrainer,
                            LocalDate.now().plusDays(1),
                            LocalTime.of(10, 0),
                            LocalTime.of(11, 0),
                            "CONFIRMED",
                            false,
                            "Upper body training"
                    ),

                    new Appointment(
                            testClient,
                            testTrainer,
                            LocalDate.now().plusDays(3),
                            LocalTime.of(18, 0),
                            LocalTime.of(19, 0),
                            "CONFIRMED",
                            false,
                            "Cardio & endurance"
                    ),

                    new Appointment(
                            testClient,
                            testTrainer,
                            LocalDate.now().plusDays(6),
                            LocalTime.of(12, 0),
                            LocalTime.of(13, 0),
                            "PENDING",
                            false,
                            "Leg day session"
                    )
            );

            appointmentRepository.saveAll(testAppointments);

            LOGGER.info(
                    "Fixed DEV appointments created for test client"
            );
        }



        /* -------------------------------------------------
           RANDOM DATA POOLS
         ------------------------------------------------- */

        List<String> firstNames = List.of(
                "John", "Maria", "Nikos", "Sofia", "Giorgos", "Eleni",
                "Stavros", "Katerina", "Dimitris", "Anna",
                "Petros", "Irene", "Theo", "Giannis", "Eftihia",
                "Christos", "Kostas", "Vasiliki", "Alex", "Dora"
        );

        List<String> lastNames = List.of(
                "Papadopoulos", "Ioannou", "Kazantzis", "Alexiou",
                "Papas", "Kosta", "Manolakos", "Drosi", "Spanos", "Mavraki",
                "Koutris", "Marinou", "Stavrinidis", "Karalis", "Vlachos",
                "Theodorou", "Lianos", "Perakis", "Frangou", "Kyrkos"
        );

        List<String> locations = List.of(
                "Vironas", "Kallithea", "Kypseli", "Pagrati", "Marousi",
                "Chalandri", "Nea Smyrni", "Zografou", "Glyfada", "Peristeri"
        );

        List<String> specializations = List.of(
                "Strength Training",
                "Body Building",
                "Fat Loss",
                "Yoga",
                "CrossFit",
                "Cardio Training"
        );

        Random random = new Random();

        /* -------------------------------------------------
           CREATE 100 TRAINERS
         ------------------------------------------------- */

        List<Long> trainerIds = new ArrayList<>();

        for (int i = 0; i < 100; i++) {

            CreatePersonResult result =
                    personService.createPerson(
                            new CreatePersonRequest(
                                    firstNames.get(random.nextInt(firstNames.size())),
                                    lastNames.get(random.nextInt(lastNames.size())),
                                    25 + random.nextInt(20),
                                    GenderType.MALE,
                                    "trainer" + i + "@fittrack.com",
                                    "+30690002" + String.format("%04d", i),
                                    "pass" + i,
                                    PersonType.TRAINER
                            )
                    );

            if (result.created()) {
                trainerIds.add(result.personView().id());
            }
        }

        LOGGER.info("Created {} TRAINER persons.", trainerIds.size());

        for (Long trainerId : trainerIds) {

            trainerService.createTrainerProfile(
                    new CreateTrainerRequest(
                            trainerId,
                            locations.get(random.nextInt(locations.size())),
                            specializations.get(random.nextInt(specializations.size())),
                            "Auto-generated trainer profile",
                            Map.of(
                                    Weekday.MONDAY, "09:00",
                                    Weekday.WEDNESDAY, "10:00",
                                    Weekday.FRIDAY, "08:30"
                            ),
                            Map.of(
                                    Weekday.MONDAY, "17:00",
                                    Weekday.WEDNESDAY, "18:00",
                                    Weekday.FRIDAY, "16:00"
                            )
                    )
            );
        }

        /* -------------------------------------------------
           CREATE 100 CLIENTS
         ------------------------------------------------- */

        int clientsCreated = 0;

        for (int i = 0; i < 100; i++) {

            CreatePersonResult person =
                    personService.createPerson(
                            new CreatePersonRequest(
                                    firstNames.get(random.nextInt(firstNames.size())),
                                    lastNames.get(random.nextInt(lastNames.size())),
                                    18 + random.nextInt(30),
                                    GenderType.FEMALE,
                                    "client" + i + "@fittrack.com",
                                    "+30691001" + String.format("%04d", i),
                                    "pass" + i,
                                    PersonType.CLIENT
                            )
                    );

            if (!person.created()) continue;

            CreateClientResult client =
                    clientService.createClientProfile(
                            new CreateClientRequest(
                                    person.personView().id(),
                                    60 + random.nextInt(30),
                                    155 + random.nextInt(25),
                                    null,
                                    null,
                                    null
                            )
                    );

            if (client.created()) clientsCreated++;
        }

        createDevAppointments();

        /* -------------------------------------------------
           SUMMARY
         ------------------------------------------------- */

        LOGGER.info("======================================");
        LOGGER.info("DEV DATABASE INITIALIZED SUCCESSFULLY");
        LOGGER.info("Trainers  : {}", trainerIds.size());
        LOGGER.info("Clients   : {}", clientsCreated);
        LOGGER.info("Persons   : {}", personService.countPersons());
        LOGGER.info("======================================");
    }
    @Transactional
    public void createDevAppointments() {

        Optional<Person> personOpt =
                personService.getByEmail("test.trainer@fittrack.com");

        if (personOpt.isEmpty()) return;

        Optional<TrainerProfile> trainerOpt =
                trainerProfileRepository.findByPersonIdWithWeeklyAvailability(
                        personOpt.get().getId()
                );

        if (trainerOpt.isEmpty()) return;

        TrainerProfile trainer = trainerOpt.get();

        List<ClientProfile> clients =
                clientProfileRepository.findAll();

        if (clients.isEmpty()) return;

        Random random = new Random();
        LocalDate today = LocalDate.now();

        int created = 0;

        for (int d = 0; d < 7; d++) {

            LocalDate date = today.plusDays(d);

            Optional<LocalTime[]> hoursOpt =
                    resolveWeeklyWorkingHours(trainer, date);

            if (hoursOpt.isEmpty()) continue;

            LocalTime start = hoursOpt.get()[0];
            LocalTime end   = hoursOpt.get()[1];

            int totalSlots =
                    end.getHour() - start.getHour();

            if (totalSlots <= 0) continue;

            int perDay = random.nextInt(3); // 0–2 appointments/day

            Set<Integer> usedSlots = new HashSet<>();

            for (int i = 0; i < perDay; i++) {

                int slot;
                do {
                    slot = random.nextInt(totalSlots);
                } while (usedSlots.contains(slot));

                usedSlots.add(slot);

                LocalTime slotStart = start.plusHours(slot);
                LocalTime slotEnd   = slotStart.plusHours(1);

                ClientProfile client =
                        clients.get(random.nextInt(clients.size()));

                Appointment appointment =
                        new Appointment(
                                client,
                                trainer,
                                date,
                                slotStart,
                                slotEnd,
                                "CONFIRMED",
                                false,
                                "DEV session with " +
                                        client.getPerson().getFirstName()
                        );

                appointmentRepository.save(appointment);

                /* ----------------------------------------
                CREATE TRAINER–CLIENT NOTES (DEV)
                ----------------------------------------- */
                trainerClientNotesRepository
                        .findByTrainerAndClient(trainer, client)
                        .orElseGet(() ->
                                trainerClientNotesRepository.save(
                                        new TrainerClientNotes(
                                                trainer,
                                                client,
                                                "Initial DEV notes for " +
                                                        client.getPerson().getFirstName()
                                        )
                                )
                        );

                created++;


                appointmentRepository.save(appointment);
                created++;

            }
        }

        LOGGER.info(
                "Created {} DEV appointments for test trainer",
                created
        );
    }

}
