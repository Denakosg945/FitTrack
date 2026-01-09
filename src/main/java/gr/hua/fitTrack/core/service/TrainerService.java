package gr.hua.fitTrack.core.service;

import gr.hua.fitTrack.core.model.TrainerProfile;
import gr.hua.fitTrack.core.model.TrainerWeeklyAvailability;
import gr.hua.fitTrack.core.model.Weekday;
import gr.hua.fitTrack.core.service.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TrainerService {

    // -------------------------
    // CREATE / UPDATE
    // -------------------------
    CreateTrainerResult createTrainerProfile(CreateTrainerRequest createTrainerRequest, boolean notify);

    default CreateTrainerResult createTrainerProfile(CreateTrainerRequest createTrainerRequest) {
        return this.createTrainerProfile(createTrainerRequest, false);
    }

    void updateTrainerProfile(UpdateTrainerProfileRequest form);

    // -------------------------
    // GET TRAINER PROFILE
    // -------------------------
    TrainerView getTrainerProfileByEmail(String email);

    TrainerView getTrainerProfileByPersonId(Long personId);

    TrainerProfile getTrainerProfile(Long trainerProfileId);

    boolean existsByTrainerPersonId(Long personId);

    boolean existsByTrainerProfileId(Long trainerProfileId);

    int countTrainerProfiles();

    List<TrainerView> getAllTrainers();

    // -------------------------
    // SEARCH
    // -------------------------
    List<TrainerView> search(String name, String location, String specialization);

    // -------------------------
    // WEEKLY AVAILABILITY
    // -------------------------
    List<TrainerWeeklyAvailability> getWeeklyAvailability(Long trainerProfileId);

    void saveWeeklyAvailability(Long trainerProfileId, List<TrainerWeeklyAvailability> weeklyAvailability);

    void deleteWeeklyAvailability(Long trainerProfileId, List<TrainerWeeklyAvailability> weeklyAvailability);

    void updateWeeklyAvailability(
            Long personId,
            Map<Weekday, LocalTime> startTimes,
            Map<Weekday, LocalTime> endTimes
    );

    // -------------------------
    // OVERRIDE + SCHEDULE
    // -------------------------
    void createOrUpdateOverride(TrainerOverrideRequest request);

    List<TrainerDailyScheduleView> getTrainerScheduleForNext7Days(Long personId);

    List<TrainerAppointmentView> getTrainerAppointmentsNext7Days(Long personId);

    // -------------------------
    // TOKEN / SELECTABLE
    // -------------------------
    TrainerProfile getTrainerByToken(String token);

    List<TrainerSelectableView> getAllSelectable();

    TrainerSelectableView getTrainerSelectableViewByToken(String trainerToken);

    // -------------------------
    // DISTINCT VALUES (DB)
    // -------------------------
    List<String> getDistinctLastNames();

    List<String> getDistinctLocations();

    List<String> getDistinctSpecializations();
    List<TrainerSelectableView> searchSelectable(
            String lastName,
            String location,
            String specialization
    );

    List<TrainerAvailabilitySelectableView>
    getSelectableWithAvailability(
            LocalDate date,
            String lastName,
            String location,
            String specialization
    );

    Optional<TrainerDailyScheduleView>
    getTrainerAvailabilityForDate(Long personId, LocalDate date);








}
