package gr.hua.fitTrack.core.service.impl;

import gr.hua.fitTrack.core.model.*;
import gr.hua.fitTrack.core.port.SmsNotificationPort;
import gr.hua.fitTrack.core.repository.*;
import gr.hua.fitTrack.core.service.TokenService;
import gr.hua.fitTrack.core.service.TrainerService;
import gr.hua.fitTrack.core.service.mapper.TrainerMapper;
import gr.hua.fitTrack.core.service.mapper.TrainerScheduleMapper;
import gr.hua.fitTrack.core.service.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
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
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public TrainerServiceImpl(
            TrainerProfileRepository trainerProfileRepository,
            PersonRepository personRepository,
            TrainerMapper trainerMapper,
            TrainerWeeklyAvailabilityRepository weeklyAvailabilityRepository,
            SmsNotificationPort smsNotificationPort,
            TrainerOverrideAvailabilityRepository trainerOverrideAvailabilityRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService
    ) {
        this.trainerProfileRepository = trainerProfileRepository;
        this.personRepository = personRepository;
        this.trainerMapper = trainerMapper;
        this.weeklyAvailabilityRepository = weeklyAvailabilityRepository;
        this.smsNotificationPort = smsNotificationPort;
        this.trainerOverrideAvailabilityRepository = trainerOverrideAvailabilityRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // --------------------------------------------------
    // CREATE TRAINER
    // --------------------------------------------------
    @Override
    public CreateTrainerResult createTrainerProfile(CreateTrainerRequest request, boolean notify) {

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
    @Transactional(readOnly = true)
    public TrainerView getTrainerProfileByEmail(String email) {

        Person person = personRepository.findByEmailAddress(email)
                .orElseThrow(() -> new IllegalArgumentException("Person not found"));

        TrainerProfile profile =
                trainerProfileRepository.findByPersonIdWithWeeklyAvailability(person.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Trainer profile not found"));

        return trainerMapper.convertTrainerToTrainerView(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerView getTrainerProfileByPersonId(Long personId) {

        TrainerProfile profile =
                trainerProfileRepository.findByPersonIdWithWeeklyAvailability(personId)
                        .orElseThrow(() -> new IllegalArgumentException("Trainer profile not found"));

        return trainerMapper.convertTrainerToTrainerView(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerProfile getTrainerProfile(Long trainerProfileId) {
        return trainerProfileRepository.findById(trainerProfileId)
                .orElseThrow(() -> new IllegalArgumentException("Trainer profile not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTrainerPersonId(Long personId) {
        return trainerProfileRepository.existsByPersonId(personId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTrainerProfileId(Long trainerProfileId) {
        return trainerProfileRepository.existsById(trainerProfileId);
    }

    @Override
    @Transactional(readOnly = true)
    public int countTrainerProfiles() {
        return (int) trainerProfileRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerView> getAllTrainers() {
        return trainerProfileRepository.findAll()
                .stream()
                .map(trainerMapper::convertTrainerToTrainerView)
                .toList();
    }

    // --------------------------------------------------
    // SEARCH (still in memory for now)
    // --------------------------------------------------
    @Override
    @Transactional(readOnly = true)
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

    // --------------------------------------------------
    // WEEKLY AVAILABILITY
    // --------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<TrainerWeeklyAvailability> getWeeklyAvailability(Long trainerProfileId) {
        return weeklyAvailabilityRepository.findByTrainerProfileId(trainerProfileId);
    }

    @Override
    public void saveWeeklyAvailability(Long trainerProfileId, List<TrainerWeeklyAvailability> weeklyAvailability) {

        TrainerProfile profile = getTrainerProfile(trainerProfileId);
        weeklyAvailability.forEach(wa -> wa.setTrainerProfile(profile));

        weeklyAvailabilityRepository.saveAll(weeklyAvailability);
    }

    @Override
    public void deleteWeeklyAvailability(Long trainerProfileId, List<TrainerWeeklyAvailability> weeklyAvailability) {
        weeklyAvailabilityRepository.deleteAll(weeklyAvailability);
    }

    @Override
    public void updateWeeklyAvailability(
            Long personId,
            Map<Weekday, LocalTime> startTimes,
            Map<Weekday, LocalTime> endTimes
    ) {

        TrainerProfile profile =
                trainerProfileRepository.findByPersonId(personId)
                        .orElseThrow(() -> new IllegalArgumentException("Trainer profile not found"));

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

    // --------------------------------------------------
    // UPDATE TRAINER PROFILE
    // --------------------------------------------------
    @Override
    public void updateTrainerProfile(UpdateTrainerProfileRequest form) {

        TrainerProfile profile =
                trainerProfileRepository.findByPersonId(form.personId())
                        .orElseThrow(() -> new IllegalStateException("Trainer profile not found"));

        profile.setLocation(form.location());
        profile.setSpecialization(form.specialization());
    }

    // --------------------------------------------------
    // OVERRIDE
    // --------------------------------------------------
    @Override
    public void createOrUpdateOverride(TrainerOverrideRequest request) {

        TrainerProfile trainer =
                trainerProfileRepository.findByPersonId(request.getPersonId())
                        .orElseThrow();

        LocalDate date = request.getDate();
        if (date == null) throw new IllegalArgumentException("Date is required");

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
                        .orElse(new TrainerOverrideAvailability());

        override.setTrainer(trainer);
        override.setDate(date);
        override.setAvailable(request.isAvailable());
        override.setStartTime(request.isAvailable() ? request.getStartTime() : null);
        override.setEndTime(request.isAvailable() ? request.getEndTime() : null);

        trainerOverrideAvailabilityRepository.save(override);
    }

    // --------------------------------------------------
    // SCHEDULE NEXT 7 DAYS
    // --------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<TrainerDailyScheduleView> getTrainerScheduleForNext7Days(Long personId) {

        TrainerProfile trainer =
                trainerProfileRepository.findByPersonId(personId)
                        .orElseThrow();

        Map<Weekday, TrainerWeeklyAvailability> weeklyMap =
                trainer.getWeeklyAvailability()
                        .stream()
                        .collect(Collectors.toMap(
                                TrainerWeeklyAvailability::getWeekday,
                                wa -> wa
                        ));

        LocalDate today = LocalDate.now();
        List<TrainerDailyScheduleView> result = new ArrayList<>();

        for (int i = 0; i < 7; i++) {

            LocalDate date = today.plusDays(i);
            Weekday weekday = Weekday.from(date);

            Optional<TrainerOverrideAvailability> overrideOpt =
                    trainerOverrideAvailabilityRepository.findByTrainerProfileAndDate(trainer, date);

            if (overrideOpt.isPresent()) {

                TrainerOverrideAvailability override = overrideOpt.get();

                if (!override.isAvailable()) {
                    result.add(new TrainerDailyScheduleView(
                            date, weekday.getDisplayName(), false, null, null, true
                    ));
                } else {
                    result.add(new TrainerDailyScheduleView(
                            date, weekday.getDisplayName(), true,
                            override.getStartTime(), override.getEndTime(), true
                    ));
                }
                continue;
            }

            TrainerWeeklyAvailability weekly = weeklyMap.get(weekday);

            if (weekly == null || weekly.getStartTime() == null || weekly.getEndTime() == null) {
                result.add(new TrainerDailyScheduleView(
                        date, weekday.getDisplayName(), false, null, null, false
                ));
            } else {
                result.add(new TrainerDailyScheduleView(
                        date, weekday.getDisplayName(), true,
                        weekly.getStartTime(), weekly.getEndTime(), false
                ));
            }
        }

        return result;
    }

    // --------------------------------------------------
    // APPOINTMENTS NEXT 7 DAYS
    // --------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<TrainerAppointmentView> getTrainerAppointmentsNext7Days(Long personId) {

        TrainerProfile trainer =
                trainerProfileRepository.findByPersonId(personId)
                        .orElseThrow();

        LocalDate today = LocalDate.now();
        LocalDate end = today.plusDays(6);

        return appointmentRepository
                .findByTrainerAndDateBetweenOrderByDateAscStartTimeAsc(trainer, today, end)
                .stream()
                .map(a -> new TrainerAppointmentView(
                        a.getDate(),
                        a.getStartTime(),
                        a.getEndTime(),
                        a.getClient().getPerson().getFirstName() + " " +
                                a.getClient().getPerson().getLastName(),
                        a.getNotes()
                ))
                .toList();
    }

    // --------------------------------------------------
    // TOKEN METHODS
    // --------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public TrainerProfile getTrainerByToken(String token) {

        Long trainerProfileId = tokenService.verifyAndExtractTrainerId(token);

        return trainerProfileRepository.findById(trainerProfileId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid trainer token"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerSelectableView> getAllSelectable() {

        return trainerProfileRepository.findAll()
                .stream()
                .map(trainer -> {

                    Long trainerId = trainer.getId();
                    String token = tokenService.allocateToken(trainerId);

                    return new TrainerSelectableView(
                            trainer.getPerson().getFirstName(),
                            trainer.getPerson().getLastName(),
                            trainer.getLocation(),
                            trainer.getSpecialization(),
                            token
                    );
                })
                .toList();
    }

    // --------------------------------------------------
    // DISTINCT VALUES (DB QUERIES)
    // --------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<String> getDistinctLastNames() {
        return personRepository.findDistinctTrainerLastNames();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getDistinctLocations() {
        return trainerProfileRepository.findDistinctLocations();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getDistinctSpecializations() {
        return trainerProfileRepository.findDistinctSpecializations();
    }
    @Override
    @Transactional(readOnly = true)
    public List<TrainerSelectableView> searchSelectable(
            String lastName,
            String location,
            String specialization
    ) {

        return trainerProfileRepository.findAll()
                .stream()
                .filter(tp ->
                        lastName == null || lastName.isBlank()
                                || tp.getPerson().getLastName().equalsIgnoreCase(lastName)
                )
                .filter(tp ->
                        location == null || location.isBlank()
                                || tp.getLocation().equalsIgnoreCase(location)
                )
                .filter(tp ->
                        specialization == null || specialization.isBlank()
                                || tp.getSpecialization().equalsIgnoreCase(specialization)
                )
                .map(tp -> {

                    String token = tokenService.allocateToken(tp.getId());

                    return new TrainerSelectableView(
                            tp.getPerson().getFirstName(),
                            tp.getPerson().getLastName(),
                            tp.getLocation(),
                            tp.getSpecialization(),
                            token
                    );
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerAvailabilitySelectableView>
    getSelectableWithAvailability(
            LocalDate date,
            String lastName,
            String location,
            String specialization
    ) {
        return trainerProfileRepository.findAll()
                .stream()
                .filter(tp -> lastName == null || lastName.isBlank()
                        || tp.getPerson().getLastName().equalsIgnoreCase(lastName))
                .filter(tp -> location == null || location.isBlank()
                        || tp.getLocation().equalsIgnoreCase(location))
                .filter(tp -> specialization == null || specialization.isBlank()
                        || tp.getSpecialization().equalsIgnoreCase(specialization))
                .map(tp -> {

                    Optional<TrainerDailyScheduleView> dailyOpt =
                            getTrainerAvailabilityForDate(
                                    tp.getPerson().getId(),
                                    date
                            );

                    boolean available =
                            dailyOpt.isPresent() && dailyOpt.get().isAvailable();

                    TrainerDailyScheduleView daily =
                            dailyOpt.orElse(null);

                    String token =
                            tokenService.allocateToken(tp.getId());

                    return new TrainerAvailabilitySelectableView(
                            tp.getPerson().getFirstName(),
                            tp.getPerson().getLastName(),
                            tp.getLocation(),
                            tp.getSpecialization(),
                            available,
                            available ? daily.getStartTime() : null,
                            available ? daily.getEndTime() : null,
                            token
                    );
                })
                .toList();

    }
    @Override
    @Transactional(readOnly = true)
    public Optional<TrainerDailyScheduleView>
    getTrainerAvailabilityForDate(Long personId, LocalDate date) {

        TrainerProfile trainer =
                trainerProfileRepository.findByPersonId(personId)
                        .orElseThrow();

        Weekday weekday = Weekday.from(date);

        // 1️⃣ override έχει ΠΑΝΤΑ προτεραιότητα
        Optional<TrainerOverrideAvailability> overrideOpt =
                trainerOverrideAvailabilityRepository
                        .findByTrainerProfileAndDate(trainer, date);

        if (overrideOpt.isPresent()) {
            TrainerOverrideAvailability o = overrideOpt.get();

            if (!o.isAvailable()) {
                return Optional.of(
                        new TrainerDailyScheduleView(
                                date,
                                weekday.getDisplayName(),
                                false,
                                null,
                                null,
                                true
                        )
                );
            }

            return Optional.of(
                    new TrainerDailyScheduleView(
                            date,
                            weekday.getDisplayName(),
                            true,
                            o.getStartTime(),
                            o.getEndTime(),
                            true
                    )
            );
        }

        // 2️⃣ weekly availability
        TrainerWeeklyAvailability weekly =
                trainer.getWeeklyAvailability()
                        .stream()
                        .filter(w -> w.getWeekday() == weekday)
                        .findFirst()
                        .orElse(null);

        if (weekly == null || weekly.getStartTime() == null) {
            return Optional.of(
                    new TrainerDailyScheduleView(
                            date,
                            weekday.getDisplayName(),
                            false,
                            null,
                            null,
                            false
                    )
            );
        }

        return Optional.of(
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
    @Override
    @Transactional(readOnly = true)
    public TrainerSelectableView getTrainerSelectableViewByToken(String trainerToken) {

        Long trainerProfileId =
                tokenService.verifyAndExtractTrainerId(trainerToken);

        TrainerProfile trainer =
                trainerProfileRepository.findById(trainerProfileId)
                        .orElseThrow();

        return new TrainerSelectableView(
                trainer.getPerson().getFirstName(),
                trainer.getPerson().getLastName(),
                trainer.getLocation(),
                trainer.getSpecialization(),
                trainerToken
        );
    }



}
