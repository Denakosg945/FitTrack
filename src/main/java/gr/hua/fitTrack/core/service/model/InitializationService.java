package gr.hua.fitTrack.core.service.model;

import gr.hua.fitTrack.core.model.GenderType;
import gr.hua.fitTrack.core.model.Person;
import gr.hua.fitTrack.core.model.PersonType;
import gr.hua.fitTrack.core.model.Weekday;
import gr.hua.fitTrack.core.service.ClientService;
import gr.hua.fitTrack.core.service.PersonService;
import gr.hua.fitTrack.core.service.TrainerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Profile("dev")
public class InitializationService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(InitializationService.class);

    private final PersonService personService;
    private final TrainerService trainerService;
    private final ClientService clientService;

    public InitializationService(PersonService personService,
                                 TrainerService trainerService,
                                 ClientService clientService) {
        this.personService = personService;
        this.trainerService = trainerService;
        this.clientService = clientService;
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
}
