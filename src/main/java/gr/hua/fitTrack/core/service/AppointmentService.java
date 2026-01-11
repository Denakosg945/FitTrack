package gr.hua.fitTrack.core.service;

import gr.hua.fitTrack.core.model.Appointment;
import gr.hua.fitTrack.core.model.ClientProfile;
import gr.hua.fitTrack.core.service.model.RequestAppointmentForm;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentService {

    void save(Appointment appointment);

    List<LocalTime> getAvailableTimeSlots(
            Long trainerPersonId,
            LocalDate date
    );

     boolean canClientCreateAppointment(String email);

    void respondToAppointment(
            Long appointmentId,
            Long trainerPersonId,
            boolean accept
    );
    void rejectAppointment(Long appointmentId, Long trainerPersonId);

    void approveAppointment(Long appointmentId ,Long trainerPersonId);


}