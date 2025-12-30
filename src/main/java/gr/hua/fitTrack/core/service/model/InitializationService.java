package gr.hua.fitTrack.core.service.model;

import gr.hua.fitTrack.core.model.GenderType;
import gr.hua.fitTrack.core.model.PersonType;
import gr.hua.fitTrack.core.model.Weekday;
import gr.hua.fitTrack.core.service.PersonService;
import gr.hua.fitTrack.core.service.TrainerService;
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

    public InitializationService(PersonService personService,
                                 TrainerService trainerService) {
        this.personService = personService;
        this.trainerService = trainerService;
    }

    public void populateDatabase() {

        /**
         * Run only if table TrainerProfiles is emply
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
                "Chalandri", "Nea Smyrni", "Zografou", "Glyfada", "Peristeri",
                "Petroupoli", "Aigaleo", "Ilion", "Tavros", "Moschato",
                "Galatsi", "Ampelokipoi", "Holargos", "Metamorfosi", "Keratsini"
        );

        List<String> specializations = List.of(
                "Strength Training", "Body Building", "Fat Loss", "Yoga", "Pilates",
                "CrossFit", "Cardio Training", "Endurance Coaching",
                "Mobility Training", "Aerobic"
        );

        Random random = new Random();

        /*
                  Person is created before trainerProfile.
                  Every person's id is put in here so that
                  we can create a trainerProfile for them later.
         */
        List<Long> trainerPersonIdsNeedingProfile = new ArrayList<>();

        /*
          Create 100 person with random data from data pools
         */

        for (int i = 0; i < 100; i++) {

            String first = firstNames.get(random.nextInt(firstNames.size()));
            String last = lastNames.get(random.nextInt(lastNames.size()));

            String email = (first + "." + last + i + "@fittrack.com").toLowerCase();

            CreatePersonRequest request = new CreatePersonRequest(
                    first,
                    last,
                    25 + random.nextInt(20), // age 25–45
                    GenderType.MALE,
                    email,
                    "+3069000000" + String.format("%02d", i),
                    "pass" + i,
                    PersonType.TRAINER
            );

            CreatePersonResult createdPerson = personService.createPerson(request);
            //ad the ID of the person just created to create the trainerProfile next
            trainerPersonIdsNeedingProfile.add(createdPerson.personView().id());
        }

        LOGGER.info("Created {} Person records.", trainerPersonIdsNeedingProfile.size());

        /*
          Create trainerProfiles for the people just created
         */

        for (Long personId : trainerPersonIdsNeedingProfile) {

            String location = locations.get(random.nextInt(locations.size()));
            String specialization = specializations.get(random.nextInt(specializations.size()));

            CreateTrainerRequest trainerRequest = new CreateTrainerRequest(
                    personId,
                    location,
                    specialization,
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
        LOGGER.info("Database initialization completed.");
    }
}
