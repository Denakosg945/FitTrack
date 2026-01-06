package gr.hua.fitTrack.core.service;

import gr.hua.fitTrack.core.model.TrainerOverrideAvailability;
import gr.hua.fitTrack.core.model.TrainerProfile;
import gr.hua.fitTrack.core.model.TrainerWeeklyAvailability;
import gr.hua.fitTrack.core.model.Weekday;
import gr.hua.fitTrack.core.service.model.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface TrainerService {

    CreateTrainerResult createTrainerProfile(final CreateTrainerRequest createTrainerRequest, final boolean notify);

    default CreateTrainerResult createTrainerProfile(final CreateTrainerRequest createTrainerRequest) {
        return this.createTrainerProfile(createTrainerRequest, false);
    }

    void updateTrainerProfile(UpdateTrainerProfileRequest form);

    TrainerView getTrainerProfileByEmail(String email);

    TrainerView getTrainerProfileByPersonId(Long personId);

    boolean existsByTrainerPersonId(Long personId);

    List<TrainerView> getAllTrainers();

    List<String> getAllUniqueLastNames();

    List<String> getAllUniqueLocations();

    List<String> getAllUniqueSpecializations();

    int countTrainerProfiles();

    List<TrainerView> search(String name, String location, String specialization);

    TrainerProfile getTrainerProfile(Long trainerProfileId);

    boolean existsByTrainerProfileId(Long trainerProfileId);

    List<TrainerWeeklyAvailability> getWeeklyAvailability(Long trainerProfileId);

    void saveWeeklyAvailability(Long trainerProfileId, List<TrainerWeeklyAvailability> weeklyAvailability);

    void deleteWeeklyAvailability(Long trainerProfileId, List<TrainerWeeklyAvailability> weeklyAvailability);

    void updateWeeklyAvailability(
            Long personId,
            Map<Weekday, LocalTime> startTimes,
            Map<Weekday, LocalTime> endTimes
    );

    public void createOrUpdateOverride(TrainerOverrideRequest request);

    public List<TrainerDailyScheduleView>
    getTrainerScheduleForNext7Days(Long personId);


}
