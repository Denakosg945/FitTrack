package gr.hua.fitTrack.core.service.impl;

import gr.hua.fitTrack.core.model.Person;
import gr.hua.fitTrack.core.model.TrainerOverrideAvailability;
import gr.hua.fitTrack.core.model.TrainerProfile;
import gr.hua.fitTrack.core.model.TrainerWeeklyAvailability;
import gr.hua.fitTrack.core.port.PhoneNumberPort;
import gr.hua.fitTrack.core.port.SmsNotificationPort;
import gr.hua.fitTrack.core.repository.PersonRepository;
import gr.hua.fitTrack.core.repository.TrainerProfileRepository;
import gr.hua.fitTrack.core.service.TrainerService;
import gr.hua.fitTrack.core.service.mapper.TrainerMapper;
import gr.hua.fitTrack.core.service.mapper.TrainerScheduleMapper;
import gr.hua.fitTrack.core.service.model.CreateTrainerRequest;
import gr.hua.fitTrack.core.service.model.CreateTrainerResult;
import gr.hua.fitTrack.core.service.model.TrainerView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class TrainerServiceImpl implements TrainerService {

    public final TrainerProfileRepository trainerProfileRepository;
    private final PersonRepository personRepository;
    private final TrainerMapper trainerMapper;
    private final TrainerScheduleMapper trainerScheduleMapper;
    private final SmsNotificationPort smsNotificationPort;

    public  TrainerServiceImpl(TrainerProfileRepository trainerProfileRepository,
                               PersonRepository personRepository,
                               TrainerMapper trainerMapper, TrainerScheduleMapper trainerScheduleMapper,
                               SmsNotificationPort smsNotificationPort
                               ) {
        if(trainerProfileRepository == null) throw new NullPointerException("trainerProfileRepository cannot be null");
        if(personRepository == null) throw new NullPointerException("personRepository cannot be null");
        if(trainerMapper == null)  throw new NullPointerException("trainerMapper cannot be null");
        if(trainerScheduleMapper == null)  throw new NullPointerException("trainerScheduleMapper cannot be null");
        if(smsNotificationPort == null) throw new NullPointerException("smsNotificationPort cannot be null");
        this.trainerProfileRepository = trainerProfileRepository;
        this.personRepository = personRepository;
        this.trainerMapper = trainerMapper;
        this.trainerScheduleMapper = trainerScheduleMapper;
        this.smsNotificationPort = smsNotificationPort;
    }

    @Override
    @Transactional
    public CreateTrainerResult createTrainerProfile(final CreateTrainerRequest createTrainerRequest, final boolean notify){
        if (createTrainerRequest == null) throw new NullPointerException("createTrainerRequest cannot be null");

        final Person person = personRepository.findById(createTrainerRequest.personId())
                .orElseThrow(() -> new IllegalArgumentException("Person not found"));

        final String location = createTrainerRequest.location();
        final String specialization  = createTrainerRequest.specialization();
        final String clientNotes = createTrainerRequest.Client_Notes();

        TrainerProfile trainerProfile = new TrainerProfile();

        trainerProfile.setPerson(person);
        trainerProfile.setLocation(location);
        trainerProfile.setSpecialization(specialization);
        trainerProfile.setClientNotes(clientNotes);

// 1. Create weekly availability BEFORE saving the trainer
        List<TrainerWeeklyAvailability> weeklyAvailability =
                TrainerScheduleMapper.mapWeeklyAvailability(
                        createTrainerRequest.startTimes(),
                        createTrainerRequest.endTimes(),
                        trainerProfile);

// 2. Attach the availability to the trainer
        trainerProfile.setWeeklyAvailability(weeklyAvailability);

// 3. Now save ONCE -> cascades save availability rows
        trainerProfile = trainerProfileRepository.save(trainerProfile);



        final String content =String.format("You have succesfully registered for the Fit Track Application as a trainer. "
            );
        smsNotificationPort.sendSms(person.getPhoneNumber(),content);

        final TrainerView trainerView = this.trainerMapper.convertTrainerToTrainerView(trainerProfile);

        return CreateTrainerResult.success(trainerView);
    }

    @Override
    public int countTrainerProfiles() {
        return (int) trainerProfileRepository.count();

    }

    @Override
    public boolean existsByTrainerPersonId(Long personId) {
        return trainerProfileRepository.existsByPersonId(personId);
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
                .map(tp ->tp.getPerson()
                        .getLastName())
                .distinct().
                sorted().
                toList();
    };

    @Override
    public List<String> getAllUniqueLocations(){
        return trainerProfileRepository.findAll()
                .stream()
                .map(TrainerProfile::getLocation)
                .distinct()
                .sorted()
                .toList();
    };

    @Override
    public List<String> getAllUniqueSpecializations(){
        return trainerProfileRepository.findAll()
                .stream()
                .map(TrainerProfile::getSpecialization)
                .distinct()
                .sorted()
                .toList();
    };

    @Override
    public List<TrainerView> search(String name, String location, String specialization){

        List<TrainerProfile> trainers = trainerProfileRepository.findAll();

        return trainers.stream()
                .filter(tp -> name == null || name.isBlank() ||
                        tp.getPerson().getLastName().equalsIgnoreCase(name))
                .filter(tp-> location ==null || location.isBlank() ||
                        tp.getLocation().equalsIgnoreCase(location))
                .filter(tp->specialization ==null || specialization.isBlank() ||
                        tp.getSpecialization().equalsIgnoreCase(specialization))
                .map(trainerMapper::convertTrainerToTrainerView)
                .toList();
    }

    //TODO IMPLEMENT
    @Override
    public TrainerProfile updateTrainerProfile(int trainerProfileId, String specialization, String bio, String location) {
        return null;
    }
    //TODO IMPLEMENT
    @Override
    public TrainerProfile getTrainerProfile(int trainerProfileId) {
        return null;
    }

    //TODO IMPLEMENT
    @Override
    public boolean existsByTrainerProfileId(int trainerProfileId) {
        return false;
    }

    //TODO IMPLEMENT
    @Override
    public List<TrainerWeeklyAvailability> getWeeklyAvailability(int trainerProfileId) {
        return List.of();
    }

    //TODO IMPLEMENT

    @Override
    public void saveWeeklyAvailability(int trainerProfileId, List<TrainerWeeklyAvailability> weeklyAvailability) {

    }

    //TODO IMPLEMENT
    @Override
    public void deleteWeeklyAvailability(int trainerProfileId, List<TrainerWeeklyAvailability> weeklyAvailability) {

    }
}