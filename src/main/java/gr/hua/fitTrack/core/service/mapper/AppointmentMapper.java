package gr.hua.fitTrack.core.service.mapper;

import gr.hua.fitTrack.core.model.Appointment;
import gr.hua.fitTrack.core.service.model.AppointmentView;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentView toView(Appointment appt) {
        return new AppointmentView(
                appt.getDate(),
                appt.getStartTime(),
                appt.getEndTime(),
                appt.getTrainer().getPerson().getFirstName()
                        + " " +
                        appt.getTrainer().getPerson().getLastName(),
                appt.getStatus(),
                appt.isOutdoor()
        );
    }
}

