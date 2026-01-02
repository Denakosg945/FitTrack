package gr.hua.fitTrack.core.service.model;

import gr.hua.fitTrack.core.model.GenderType;
import gr.hua.fitTrack.core.model.PersonType;
import gr.hua.fitTrack.core.model.Weekday;
import gr.hua.fitTrack.core.service.PersonService;
import gr.hua.fitTrack.core.service.TrainerService;
import gr.hua.fitTrack.core.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class InitializationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitializationService.class);

    private final AtomicBoolean initialized = new AtomicBoolean(false);

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

        /**
         * Run only if table TrainerProfiles is empty
         */

        if (trainerService.countTrainerProfiles() > 0) {
            LOGGER.info("Trainer profiles already exist — skipping initialization.");
            return;
        }

        if (initialized.getAndSet(true)) {
            LOGGER.warn("Initialization skipped — already executed.");
            return;
        }

        LOGGER.info("Starting initial database population...");

        /**
         * Random data pools
         */

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
                "Strength Training", "Body Building", "Fat Loss", "Yoga",
                "CrossFit", "Cardio Training"
        );

        Random random = new Random();

        /*
          Person is created before trainerProfile.
          Every person's id is put in here so that
          we can create a trainerProfile for them later.
         */
        List<Long> trainerPersonIds = new ArrayList<>();

        /*
          Create 100 TRAINER persons
         */

        for (int i = 0; i < 100; i++) {

            String first = firstNames.get(random.nextInt(firstNames.size()));
            String last = lastNames.get(random.nextInt(lastNames.size()));

            String email = (first + "." + last + ".trainer" + i + "@fittrack.com").toLowerCase();

            CreatePersonRequest request = new CreatePersonRequest(
                    first,
                    last,
                    25 + random.nextInt(20), // age 25–45
                    GenderType.MALE,
                    email,
                    "+30690001" + String.format("%03d", i),
                    "pass" + i,
                    PersonType.TRAINER
            );

            CreatePersonResult result = personService.createPerson(request);

            if (!result.created()) {
                LOGGER.error("Trainer person creation failed for {} → {}", email, result.reason());
                continue;
            }

            trainerPersonIds.add(result.personView().id());
        }

        LOGGER.info("Created {} TRAINER persons.", trainerPersonIds.size());

        /*
          Create trainerProfiles for trainers
         */

        for (Long personId : trainerPersonIds) {

            CreateTrainerRequest trainerRequest = new CreateTrainerRequest(
                    personId,
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
            );

            CreateTrainerResult result = trainerService.createTrainerProfile(trainerRequest);

            if (!result.created()) {
                LOGGER.error("Trainer profile creation failed for person {} → {}", personId, result.reason());
            }
        }

        LOGGER.info("100 trainer profiles created successfully!");

        /*
          Create 100 CLIENT persons + client profiles
         */

        int clientsCreated = 0;

        for (int i = 0; i < 100; i++) {

            String first = firstNames.get(random.nextInt(firstNames.size()));
            String last = lastNames.get(random.nextInt(lastNames.size()));

            String email = (first + "." + last + ".client" + i + "@fittrack.com").toLowerCase();

            CreatePersonRequest personRequest = new CreatePersonRequest(
                    first,
                    last,
                    18 + random.nextInt(30), // age 18–48
                    GenderType.FEMALE,
                    email,
                    "+30691001" + String.format("%03d", i),
                    "pass" + i,
                    PersonType.CLIENT
            );

            CreatePersonResult personResult = personService.createPerson(personRequest);

            if (!personResult.created()) {
                LOGGER.error("Client person creation failed for {} → {}", email, personResult.reason());
                continue;
            }

            /*
              Create clientProfile for the client person
             */

            CreateClientRequest clientRequest = new CreateClientRequest(
                    personResult.personView().id(),
                    60 + random.nextInt(30),   // weight
                    155 + random.nextInt(25),  // height
                    null,
                    null,
                    null
            );

            CreateClientResult clientResult = clientService.createClientProfile(clientRequest);

            if (!clientResult.created()) {
                LOGGER.error("Client profile creation failed for person {} → {}",
                        personResult.personView().id(), clientResult.reason());
                continue;
            }

            clientsCreated++;
        }

        LOGGER.info("{} CLIENT persons + profiles created successfully!", clientsCreated);
        LOGGER.info("Database initialization completed.");
    }
}
