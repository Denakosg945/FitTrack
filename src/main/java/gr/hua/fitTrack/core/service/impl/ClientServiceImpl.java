package gr.hua.fitTrack.core.service.impl;

import gr.hua.fitTrack.core.model.ClientProfile;
import gr.hua.fitTrack.core.model.Goals;
import gr.hua.fitTrack.core.model.Person;
import gr.hua.fitTrack.core.model.Progress;
import gr.hua.fitTrack.core.port.SmsNotificationPort;
import gr.hua.fitTrack.core.repository.AppointmentRepository;
import gr.hua.fitTrack.core.repository.ClientProfileRepository;
import gr.hua.fitTrack.core.repository.PersonRepository;
import gr.hua.fitTrack.core.repository.ProgressRepository;
import gr.hua.fitTrack.core.service.ClientService;
import gr.hua.fitTrack.core.service.mapper.AppointmentMapper;
import gr.hua.fitTrack.core.service.mapper.ClientMapper;
import gr.hua.fitTrack.core.service.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientProfileRepository clientProfileRepository;
    private final PersonRepository personRepository;
    private final ClientMapper clientMapper;
    private final SmsNotificationPort smsNotificationPort;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final ProgressRepository progressRepository;
    public ClientServiceImpl(ClientProfileRepository clientProfileRepository,
                             PersonRepository personRepository,
                             ClientMapper clientMapper,
                             SmsNotificationPort smsNotificationPort,
                             AppointmentRepository appointmentRepository,
                             AppointmentMapper appointmentMapper,
                             ProgressRepository progressRepository) {

        if (clientProfileRepository == null) throw new NullPointerException("clientProfileRepository is null");
        if (personRepository == null) throw new NullPointerException("personRepository is null");
        if (clientMapper == null) throw new NullPointerException("clientMapper is null");
        if (smsNotificationPort == null) throw new NullPointerException("smsNotificationPort is null");
        if (appointmentMapper == null) throw new NullPointerException("Appointment Mapper cannot be null");
        if (appointmentRepository == null) throw new NullPointerException("Appointer repository cannot be null");
        if (progressRepository == null) throw new NullPointerException("Progress repository cannot be null");

        this.clientProfileRepository = clientProfileRepository;
        this.personRepository = personRepository;
        this.clientMapper = clientMapper;
        this.smsNotificationPort = smsNotificationPort;
        this.appointmentMapper = appointmentMapper;
        this.appointmentRepository = appointmentRepository;
        this.progressRepository = progressRepository;
    }

    @Override
    @Transactional
    public CreateClientResult createClientProfile(final CreateClientRequest createClientRequest,
                                                  final boolean notify) {

        if (createClientRequest == null)
            throw new NullPointerException("createClientRequest is null");

        final Person person = personRepository.findById(createClientRequest.personId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Person not found with id " + createClientRequest.personId()
                        )
                );

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setPerson(person);
        clientProfile.setWeight(createClientRequest.weight());
        clientProfile.setHeight(createClientRequest.height());
        clientProfile.setProgress(null);

        // ✅ Δημιουργία Goals ΜΟΝΟ αν υπάρχει έστω ένα goal
        if (createClientRequest.targetWeight() != null
                || createClientRequest.targetBodyFat() != null
                || createClientRequest.runningTimeGoal() != null) {

            Goals goals = new Goals();
            goals.setClient(clientProfile);

            if (createClientRequest.targetWeight() != null) {
                goals.setWeightGoal(createClientRequest.targetWeight());
            }

            if (createClientRequest.targetBodyFat() != null) {
                goals.setBodyFatPercentageGoal(createClientRequest.targetBodyFat());
            }

            if (createClientRequest.runningTimeGoal() != null) {
                goals.setRunningTimeGoal(createClientRequest.runningTimeGoal());
            }

            clientProfile.setGoals(goals);
        } else {
            clientProfile.setGoals(null);
        }

        clientProfile = clientProfileRepository.save(clientProfile);

        if (notify) {
            final String content =
                    "You have succesfully registered for the Fit Track Application as a client.";
            smsNotificationPort.sendSms(person.getPhoneNumber(), content);
        }

        final ClientView clientView =
                clientMapper.converClientToClientView(clientProfile);

        return CreateClientResult.success(clientView);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientView getClientProfileByPersonId(Long personId) {

        ClientProfile client =
                clientProfileRepository
                        .findByPersonId(personId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Client profile not found for personId=" + personId
                                )
                        );

        return clientMapper.converClientToClientView(client);
    }

    public void updateGoalsForTestClient(
            float weight,
            int running,
            int bodyFat
    ) {
        ClientProfile client = clientProfileRepository
                .findByPersonId(2L)
                 .orElseThrow();

        Goals goals = client.getGoals();
        goals.setWeightGoal(weight);
        goals.setRunningTimeGoal(running);
        goals.setBodyFatPercentageGoal(bodyFat);

        clientProfileRepository.save(client);
    }

    @Override
    @Transactional
    public void addProgressForTestClient(EditProgressForm form) {

        ClientProfile client = clientProfileRepository
                .findByPersonId(2L) // προσωρινά hardcoded
                .orElseThrow();

        Progress progress = new Progress();
        progress.setClient(client);

        progress.setWeight(form.getWeight().floatValue());

        progress.setRunningTimeSeconds(form.getRunningTimeSeconds());

        progress.setBodyFatPercentage(
                form.getBodyFatPercentage().intValue()
        );

        progress.setWaterIntake(form.getWaterIntake().floatValue());

        progressRepository.save(progress);


        // Update current client weight
        client.setWeight(form.getWeight().intValue());
        clientProfileRepository.save(client);
    }





}

