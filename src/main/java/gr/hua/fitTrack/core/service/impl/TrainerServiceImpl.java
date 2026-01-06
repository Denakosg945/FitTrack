package gr.hua.fitTrack.core.service.impl;

import gr.hua.fitTrack.core.model.*;
import gr.hua.fitTrack.core.port.SmsNotificationPort;
import gr.hua.fitTrack.core.repository.PersonRepository;
import gr.hua.fitTrack.core.repository.TrainerOverrideAvailabilityRepository;
import gr.hua.fitTrack.core.repository.TrainerProfileRepository;
import gr.hua.fitTrack.core.repository.TrainerWeeklyAvailabilityRepository;
import gr.hua.fitTrack.core.service.TrainerService;
import gr.hua.fitTrack.core.service.mapper.TrainerMapper;
import gr.hua.fitTrack.core.service.mapper.TrainerScheduleMapper;
import gr.hua.fitTrack.core.service.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrainerServiceImpl implements TrainerService {

    private final TrainerProfileRepository trainerProfileRepository;
    private final PersonRepository personRepository;
    private final TrainerMapper trainerMapper;
    private final TrainerWeeklyAvailabilityRepository weeklyAvailabilityRepository;
    private final SmsNotificationPort smsNotificationPort;
    private final TrainerOverrideAvailabilityRepository trainerOverrideAvailabilityRepository;


    public TrainerServiceImpl(
            TrainerProfileRepository trainerProfileRepository,
            PersonRepository personRepository,
            TrainerMapper trainerMapper,
            TrainerWeeklyAvailabilityRepository weeklyAvailabilityRepository,
            SmsNotificationPort smsNotificationPort,
            TrainerOverrideAvailabilityRepository trainerOverrideAvailabilityRepository
    ) {
        this.trainerProfileRepository = trainerProfileRepository;
        this.personRepository = personRepository;
        this.trainerMapper = trainerMapper;
        this.weeklyAvailabilityRepository = weeklyAvailabilityRepository;
        this.smsNotificationPort = smsNotificationPort;
        this.trainerOverrideAvailabilityRepository = trainerOverrideAvailabilityRepository;
    }

    // --------------------------------------------------
    // CREATE TRAINER
    // --------------------------------------------------
    @Override
    public CreateTrainerResult createTrainerProfile(
            CreateTrainerRequest request,
            boolean notify
    ) {
        Person person = personRepository.findById(request.personId())
                .orElseThrow(() -> new IllegalArgumentException("Person not found"));

        TrainerProfile profile = new TrainerProfile();
        profile.setPerson(person);
        profile.setLocation(request.location());
        profile.setSpecialization(request.specialization());
        profile.setClientNotes(request.Client_Notes());

        List<TrainerWeeklyAvailability> availability =
                TrainerScheduleMapper.mapWeeklyAvailability(
                        request.startTimes(),
                        request.endTimes(),
                        profile
                );

        profile.setWeeklyAvailability(availability);
        profile = trainerProfileRepository.save(profile);

        if (notify) {
            smsNotificationPort.sendSms(
                    person.getPhoneNumber(),
                    "You have successfully registered as a trainer in FitTrack."
            );
        }

        return CreateTrainerResult.success(
                trainerMapper.convertTrainerToTrainerView(profile)
        );
    }

    // --------------------------------------------------
    // GET TRAINER
    // --------------------------------------------------
    @Override
    public TrainerView getTrainerProfileByEmail(String email) {
        Person person = personRepository.findByEmailAddress(email)
                .orElseThrow(() -> new IllegalArgumentException("Person not found"));

        TrainerProfile profile =
                trainerProfileRepository.findByPersonIdWithWeeklyAvailability(person.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Trainer profile not found"));

        return trainerMapper.convertTrainerToTrainerView(profile);
    }

    @Override
    public TrainerView getTrainerProfileByPersonId(Long personId) {
        TrainerProfile profile =
                trainerProfileRepository.findByPersonIdWithWeeklyAvailability(personId)
                        .orElseThrow(() -> new IllegalArgumentException("Trainer profile not found"));

        return trainerMapper.convertTrainerToTrainerView(profile);
    }

    @Override
    public TrainerProfile getTrainerProfile(Long trainerProfileId) {
        return trainerProfileRepository.findById(trainerProfileId)
                .orElseThrow(() -> new IllegalArgumentException("Trainer profile not found"));
    }

    @Override
    public boolean existsByTrainerPersonId(Long personId) {
        return trainerProfileRepository.existsByPersonId(personId);
    }

    @Override
    public boolean existsByTrainerProfileId(Long trainerProfileId) {
        return trainerProfileRepository.existsById(trainerProfileId);
    }


    @Override
    public List<TrainerWeeklyAvailability> getWeeklyAvailability(Long trainerProfileId) {
        return weeklyAvailabilityRepository.findByTrainerProfileId(trainerProfileId);
    }

    @Override
    public void saveWeeklyAvailability(
            Long trainerProfileId,
            List<TrainerWeeklyAvailability> weeklyAvailability
    ) {
        TrainerProfile profile = getTrainerProfile(trainerProfileId);

        weeklyAvailability.forEach(wa -> wa.setTrainerProfile(profile));
        weeklyAvailabilityRepository.saveAll(weeklyAvailability);
    }

    @Override
    public void deleteWeeklyAvailability(
            Long trainerProfileId,
            List<TrainerWeeklyAvailability> weeklyAvailability
    ) {
        weeklyAvailabilityRepository.deleteAll(weeklyAvailability);
    }


    @Override
    @Transactional
    public void updateWeeklyAvailability(
            Long personId,
            Map<Weekday, LocalTime> startTimes,
            Map<Weekday, LocalTime> endTimes
    ) {
        TrainerProfile profile =
                trainerProfileRepository.findByPersonId(personId)
                        .orElseThrow(() -> new IllegalArgumentException("Trainer profile not found"));

        // υπάρχοντα availabilities
        Map<Weekday, TrainerWeeklyAvailability> existing =
                weeklyAvailabilityRepository
                        .findByTrainerProfileId(profile.getId())
                        .stream()
                        .collect(Collectors.toMap(
                                TrainerWeeklyAvailability::getWeekday,
                                wa -> wa
                        ));

        List<TrainerWeeklyAvailability> result = new ArrayList<>();

        for (Weekday day : Weekday.values()) {

            TrainerWeeklyAvailability wa =
                    existing.getOrDefault(day, new TrainerWeeklyAvailability());

            wa.setTrainerProfile(profile);
            wa.setWeekday(day);
            wa.setStartTime(startTimes.get(day));
            wa.setEndTime(endTimes.get(day));

            result.add(wa);
        }

        weeklyAvailabilityRepository.saveAll(result);
    }

    @Override
    public void updateTrainerProfile(UpdateTrainerProfileRequest form) {
        TrainerProfile profile =
                trainerProfileRepository.findByPersonId(form.personId())
                        .orElseThrow(() -> new IllegalStateException("Trainer profile not found"));

        profile.setLocation(form.location());
        profile.setSpecialization(form.specialization());
    }


    @Override
    public int countTrainerProfiles() {
        return (int) trainerProfileRepository.count();
    }

    @Override
    public List<TrainerView> getAllTrainers() {
        return trainerProfileRepository.findAll()
                .stream()
                .map(trainerMapper::convertTrainerToTrainerView)
                .toList();
    }

    @Override
    public List<String> getAllUniqueLastNames() {
        return trainerProfileRepository.findAll()
                .stream()
                .map(tp -> tp.getPerson().getLastName())
                .distinct()
                .sorted()
                .toList();
    }

    @Override
    public List<String> getAllUniqueLocations() {
        return trainerProfileRepository.findAll()
                .stream()
                .map(TrainerProfile::getLocation)
                .distinct()
                .sorted()
                .toList();
    }

    @Override
    public List<String> getAllUniqueSpecializations() {
        return trainerProfileRepository.findAll()
                .stream()
                .map(TrainerProfile::getSpecialization)
                .distinct()
                .sorted()
                .toList();
    }

    @Override
    public List<TrainerView> search(String name, String location, String specialization) {
        return trainerProfileRepository.findAll()
                .stream()
                .filter(tp -> name == null || name.isBlank()
                        || tp.getPerson().getLastName().equalsIgnoreCase(name))
                .filter(tp -> location == null || location.isBlank()
                        || tp.getLocation().equalsIgnoreCase(location))
                .filter(tp -> specialization == null || specialization.isBlank()
                        || tp.getSpecialization().equalsIgnoreCase(specialization))
                .map(trainerMapper::convertTrainerToTrainerView)
                .toList();
    }

    @Override
    public void createOrUpdateOverride(TrainerOverrideRequest request) {

        TrainerProfile trainer =
                trainerProfileRepository.findByPersonId(request.getPersonId())
                        .orElseThrow();

        LocalDate date = request.getDate();

        if (date == null) {
            throw new IllegalArgumentException("Date is required");
        }

        if (request.isAvailable()) {

            if (request.getStartTime() == null || request.getEndTime() == null) {
                throw new IllegalArgumentException("Start and end time required");
            }

            if (request.getEndTime().isBefore(request.getStartTime())) {
                throw new IllegalArgumentException("End before start");
            }
        }

        TrainerOverrideAvailability override =
                trainerOverrideAvailabilityRepository
                        .findByTrainerProfileAndDate(trainer, date)
                        .orElse(
                                new TrainerOverrideAvailability()
                        );

        override.setTrainer(trainer);
        override.setDate(date);
        override.setAvailable(request.isAvailable());
        override.setStartTime(request.isAvailable() ? request.getStartTime() : null);
        override.setEndTime(request.isAvailable() ? request.getEndTime() : null);

        trainerOverrideAvailabilityRepository.save(override);
    }

    @Override
    public List<TrainerDailyScheduleView>
    getTrainerScheduleForNext7Days(Long personId) {

        TrainerProfile trainer =
                trainerProfileRepository.findByPersonId(personId)
                        .orElseThrow();

        Map<Weekday, TrainerWeeklyAvailability> weeklyMap =
                trainer.getWeeklyAvailability()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        TrainerWeeklyAvailability::getWeekday,
                                        wa -> wa
                                )
                        );

        LocalDate today = LocalDate.now();

        List<TrainerDailyScheduleView> result = new ArrayList<>();

        for (int i = 0; i < 7; i++) {

            LocalDate date = today.plusDays(i);
            Weekday weekday = Weekday.from(date); // θα το δούμε πιο κάτω

            Optional<TrainerOverrideAvailability> overrideOpt =
                    trainerOverrideAvailabilityRepository.findByTrainerProfileAndDate(
                            trainer, date
                    );

            // ===== CASE 1: OVERRIDE EXISTS =====
            if (overrideOpt.isPresent()) {

                TrainerOverrideAvailability override =
                        overrideOpt.get();

                if (!override.isAvailable()) {

                    result.add(
                            new TrainerDailyScheduleView(
                                    date,
                                    weekday.getDisplayName(),
                                    false,
                                    null,
                                    null,
                                    true
                            )
                    );

                } else {

                    result.add(
                            new TrainerDailyScheduleView(
                                    date,
                                    weekday.getDisplayName(),
                                    true,
                                    override.getStartTime(),
                                    override.getEndTime(),
                                    true
                            )
                    );
                }

                continue;
            }

            // ===== CASE 2: FALLBACK TO WEEKLY =====
            TrainerWeeklyAvailability weekly =
                    weeklyMap.get(weekday);

            if (weekly == null ||
                    weekly.getStartTime() == null ||
                    weekly.getEndTime() == null) {

                result.add(
                        new TrainerDailyScheduleView(
                                date,
                                weekday.getDisplayName(),
                                false,
                                null,
                                null,
                                false
                        )
                );

            } else {

                result.add(
                        new TrainerDailyScheduleView(
                                date,
                                weekday.getDisplayName(),
                                true,
                                weekly.getStartTime(),
                                weekly.getEndTime(),
                                false
                        )
                );
            }
        }

        return result;
    }


}
