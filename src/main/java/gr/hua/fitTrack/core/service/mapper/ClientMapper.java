package gr.hua.fitTrack.core.service.mapper;

import gr.hua.fitTrack.core.model.Appointment;
import gr.hua.fitTrack.core.model.ClientProfile;
import gr.hua.fitTrack.core.repository.AppointmentRepository;
import gr.hua.fitTrack.core.service.model.AppointmentView;
import gr.hua.fitTrack.core.service.model.ClientView;
import gr.hua.fitTrack.core.service.model.ProgressView;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ClientMapper {

    private final ProgressMapper progressMapper;
    private final AppointmentMapper appointmentMapper;
    private final AppointmentRepository appointmentRepository;


    public ClientMapper(ProgressMapper progressMapper,
                        AppointmentRepository appointmentRepository,
                        AppointmentMapper appointmentMapper
                        ) {
        this.progressMapper = progressMapper;
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;

    }

    public ClientView converClientToClientView(ClientProfile client) {

        List<ProgressView> progressViews =
                client.getProgress() == null
                        ? List.of()
                        : client.getProgress()
                        .stream()
                        .map(progressMapper::toView)
                        .toList();

        List<Appointment> appointments = appointmentRepository.findByClient_Person_EmailAddressOrderByDateAscStartTimeAsc(client.getPerson().getEmailAddress());
        List<AppointmentView> appointmentViews =
                appointments.stream()
                        .map(appointmentMapper::toView)
                        .toList();



        return new ClientView(
                client.getId(),
                client.getPerson().getFirstName(),
                client.getPerson().getLastName(),
                client.getPerson().getEmailAddress(),
                client.getWeight(),
                client.getHeight(),
                client.getGoals(),
                progressViews,
                appointmentViews
        );
    }
}

