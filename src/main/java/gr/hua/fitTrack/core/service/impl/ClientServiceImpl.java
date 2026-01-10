package gr.hua.fitTrack.core.service.impl;

import gr.hua.fitTrack.core.model.*;
import gr.hua.fitTrack.core.port.SmsNotificationPort;
import gr.hua.fitTrack.core.repository.*;
import gr.hua.fitTrack.core.service.ClientService;
import gr.hua.fitTrack.core.service.mapper.AppointmentMapper;
import gr.hua.fitTrack.core.service.mapper.ClientMapper;
import gr.hua.fitTrack.core.service.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientProfileRepository clientProfileRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final PersonRepository personRepository;
    private final ClientMapper clientMapper;
    private final SmsNotificationPort smsNotificationPort;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final ProgressRepository progressRepository;

    public ClientServiceImpl(
            ClientProfileRepository clientProfileRepository,
            TrainerProfileRepository trainerProfileRepository,
            PersonRepository personRepository,
            ClientMapper clientMapper,
            SmsNotificationPort smsNotificationPort,
            AppointmentRepository appointmentRepository,
            AppointmentMapper appointmentMapper,
            ProgressRepository progressRepository
    ) {
        if (clientProfileRepository == null) throw new NullPointerException("clientProfileRepository is null");
        if (trainerProfileRepository == null) throw new NullPointerException("trainerProfileRepository is null");
        if (personRepository == null) throw new NullPointerException("personRepository is null");
        if (clientMapper == null) throw new NullPointerException("clientMapper is null");
        if (smsNotificationPort == null) throw new NullPointerException("smsNotificationPort is null");
        if (appointmentRepository == null) throw new NullPointerException("appointmentRepository is null");
        if (appointmentMapper == null) throw new NullPointerException("appointmentMapper is null");
        if (progressRepository == null) throw new NullPointerException("progressRepository is null");

        this.clientProfileRepository = clientProfileRepository;
        this.trainerProfileRepository = trainerProfileRepository;
        this.personRepository = personRepository;
        this.clientMapper = clientMapper;
        this.smsNotificationPort = smsNotificationPort;
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
        this.progressRepository = progressRepository;
    }

    @Override
    @Transactional
    public CreateClientResult createClientProfile(final CreateClientRequest createClientRequest,
                                                  final boolean notify) {

        if (createClientRequest == null) {
            throw new NullPointerException("createClientRequest is null");
        }

        final Person person = personRepository.findById(createClientRequest.personId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Person not found with id " + createClientRequest.personId()
                ));

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setPerson(person);
        clientProfile.setWeight(createClientRequest.weight());
        clientProfile.setHeight(createClientRequest.height());
        clientProfile.setProgress(null);

        // goals μόνο αν υπάρχει κάτι
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
            smsNotificationPort.sendSms(
                    person.getPhoneNumber(),
                    "You have successfully registered for the Fit Track Application as a client."
            );
        }

        final ClientView clientView = clientMapper.converClientToClientView(clientProfile);
        return CreateClientResult.success(clientView);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientView getClientProfileByPersonId(Long personId) {
        ClientProfile client = clientProfileRepository
                .findByPersonId(personId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Client profile not found for personId=" + personId
                ));

        return clientMapper.converClientToClientView(client);
    }

    @Override
    public void updateGoals(String email, float weight, int running, int bodyFat) {
        ClientProfile client = clientProfileRepository
                .findByEmailWithProgress(email)
                .orElseThrow();

        Goals goals = client.getGoals();
        if (goals == null) {
            goals = new Goals();
            goals.setClient(client);
            client.setGoals(goals);
        }

        goals.setWeightGoal(weight);
        goals.setRunningTimeGoal(running);
        goals.setBodyFatPercentageGoal(bodyFat);

        clientProfileRepository.save(client);
    }

    @Override
    @Transactional
    public void addProgress(String email,EditProgressForm form) {

        ClientProfile client = clientProfileRepository
                .findByEmailWithProgress(email)
                .orElseThrow();

        Progress progress = new Progress();
        progress.setClient(client);

        progress.setWeight(form.getWeight().floatValue());
        progress.setRunningTimeSeconds(form.getRunningTimeSeconds());
        progress.setBodyFatPercentage(form.getBodyFatPercentage().intValue());
        progress.setWaterIntake(form.getWaterIntake().floatValue());

        progressRepository.save(progress);

        // update current client weight
        client.setWeight(form.getWeight().intValue());
        clientProfileRepository.save(client);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientProfile getByEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email is required");
        }

        Person person = personRepository.findByEmailAddress(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Person not found with email: " + email)
                );

        return clientProfileRepository.findByPersonId(person.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Client profile not found for email: " + email)
                );
    }

    @Override
    public ClientView getViewByEmail(String email) {

        ClientProfile clientProfile =
                clientProfileRepository.findByEmailWithProgress(email)
                        .orElseThrow(() -> new RuntimeException("Client not found"));

        return clientMapper.converClientToClientView(clientProfile);
    }

}
