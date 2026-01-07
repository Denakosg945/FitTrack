package gr.hua.fitTrack.core.service.impl;

import gr.hua.fitTrack.core.model.ClientProfile;
import gr.hua.fitTrack.core.model.Goals;
import gr.hua.fitTrack.core.model.Person;
import gr.hua.fitTrack.core.port.SmsNotificationPort;
import gr.hua.fitTrack.core.repository.ClientProfileRepository;
import gr.hua.fitTrack.core.repository.PersonRepository;
import gr.hua.fitTrack.core.service.ClientService;
import gr.hua.fitTrack.core.service.mapper.ClientMapper;
import gr.hua.fitTrack.core.service.model.ClientView;
import gr.hua.fitTrack.core.service.model.CreateClientRequest;
import gr.hua.fitTrack.core.service.model.CreateClientResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientProfileRepository clientProfileRepository;
    private final PersonRepository personRepository;
    private final ClientMapper clientMapper;
    private final SmsNotificationPort smsNotificationPort;

    public ClientServiceImpl(ClientProfileRepository clientProfileRepository,
                             PersonRepository personRepository,
                             ClientMapper clientMapper,
                             SmsNotificationPort smsNotificationPort) {

        if (clientProfileRepository == null) throw new NullPointerException("clientProfileRepository is null");
        if (personRepository == null) throw new NullPointerException("personRepository is null");
        if (clientMapper == null) throw new NullPointerException("clientMapper is null");
        if (smsNotificationPort == null) throw new NullPointerException("smsNotificationPort is null");

        this.clientProfileRepository = clientProfileRepository;
        this.personRepository = personRepository;
        this.clientMapper = clientMapper;
        this.smsNotificationPort = smsNotificationPort;
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
                clientProfileRepository.findByPersonIdWithGoalsAndProgress(personId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Client not found for personId=" + personId
                                )
                        );

        return clientMapper.converClientToClientView(client);
    }


}

