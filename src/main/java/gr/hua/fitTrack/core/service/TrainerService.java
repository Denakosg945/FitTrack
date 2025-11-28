package gr.hua.fitTrack.core.service;

import gr.hua.fitTrack.core.model.TrainerOverrideAvailability;
import gr.hua.fitTrack.core.model.TrainerProfile;
import gr.hua.fitTrack.core.model.TrainerWeeklyAvailability;
import gr.hua.fitTrack.core.service.model.CreateTrainerRequest;
import gr.hua.fitTrack.core.service.model.CreateTrainerResult;
import gr.hua.fitTrack.core.service.model.TrainerView;

import java.util.List;

public interface TrainerService {

    CreateTrainerResult createTrainerProfile(final CreateTrainerRequest createTrainerRequest, final boolean notify);

    default CreateTrainerResult createTrainerProfile(final CreateTrainerRequest createTrainerRequest) {
        return this.createTrainerProfile(createTrainerRequest, false);
    }

    boolean existsByTrainerPersonId(Long personId);

    List<TrainerView> getAllTrainers();

    List<String> getAllUniqueLastNames();

    List<String> getAllUniqueLocations();

    List<String> getAllUniqueSpecializations();

    int countTrainerProfiles();

    List<TrainerView> search(String name, String location, String specialization);

    TrainerProfile updateTrainerProfile(int trainerProfileId, String specialization, String bio, String location);

    TrainerProfile getTrainerProfile(int trainerProfileId);

    boolean existsByTrainerProfileId(int trainerProfileId);

    List<TrainerWeeklyAvailability> getWeeklyAvailability(int trainerProfileId);

    void saveWeeklyAvailability(int trainerProfileId, List<TrainerWeeklyAvailability> weeklyAvailability);

    void deleteWeeklyAvailability(int trainerProfileId, List<TrainerWeeklyAvailability> weeklyAvailability);

}
