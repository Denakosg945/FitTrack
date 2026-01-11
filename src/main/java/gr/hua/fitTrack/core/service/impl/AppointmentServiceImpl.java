package gr.hua.fitTrack.core.service.impl;

import gr.hua.fitTrack.core.exception.MaxActiveAppointmentsExceededException;
import gr.hua.fitTrack.core.model.Appointment;
import gr.hua.fitTrack.core.model.ClientProfile;
import gr.hua.fitTrack.core.model.TrainerProfile;
import gr.hua.fitTrack.core.port.SmsNotificationPort;
import gr.hua.fitTrack.core.repository.AppointmentRepository;
import gr.hua.fitTrack.core.repository.ClientProfileRepository;
import gr.hua.fitTrack.core.repository.TrainerProfileRepository;
import gr.hua.fitTrack.core.security.TokenUtils;
import gr.hua.fitTrack.core.service.AppointmentService;
import gr.hua.fitTrack.core.service.TokenService;
import gr.hua.fitTrack.core.service.TrainerService;
import gr.hua.fitTrack.core.service.model.RequestAppointmentForm;
import gr.hua.fitTrack.core.service.model.TrainerDailyScheduleView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final TrainerService trainerService;
    private final TokenService tokenService;
    private final SmsNotificationPort smsNotificationPort;

    private static final int MAX_ACTIVE_APPOINTMENTS = 5;

    public AppointmentServiceImpl(
            AppointmentRepository appointmentRepository,
            TrainerProfileRepository trainerProfileRepository,
            ClientProfileRepository clientProfileRepository,
            TrainerService trainerService,
            TokenService tokenService,
            SmsNotificationPort smsNotificationPort
    ) {
        this.appointmentRepository = appointmentRepository;
        this.trainerProfileRepository = trainerProfileRepository;
        this.clientProfileRepository = clientProfileRepository;
        this.trainerService = trainerService;
        this.tokenService = tokenService;
        this.smsNotificationPort = smsNotificationPort;
    }

    @Override
    public void save(Appointment appointment) {

        long activeCount =
                appointmentRepository.countActiveAppointments(
                        appointment.getClient().getPerson().getEmailAddress()
                );


        if (activeCount >= MAX_ACTIVE_APPOINTMENTS) {
            throw new MaxActiveAppointmentsExceededException(
                    "You already have the maximum number of active appointments."
            );
        }

        // - Send EMAIL to CLIENT: "Your appointment request has been submitted"
        smsNotificationPort.sendSms(
                appointment.getClient().getPerson().getPhoneNumber(),
                "Your appointment request has been submitted");
        // - Send SMS/EMAIL to TRAINER: "New appointment request pending approval"
        smsNotificationPort.sendSms(
                appointment.getTrainer().getPerson().getPhoneNumber(),
                "New appointment request pending approval");



        appointmentRepository.save(appointment);
    }
    @Override
    @Transactional(readOnly = true)
    public List<LocalTime> getAvailableTimeSlots(
            Long trainerPersonId,
            LocalDate date
    ) {

        //availability για τη συγκεκριμένη μέρα
        TrainerDailyScheduleView daily =
                trainerService.getTrainerScheduleForNext7Days(trainerPersonId)
                        .stream()
                        .filter(d -> d.getDate().equals(date))
                        .findFirst()
                        .orElse(null);

        if (daily == null || !daily.isAvailable()) {
            return List.of();
        }

        LocalTime start = daily.getStartTime();
        LocalTime end   = daily.getEndTime();

        // trainer entity
        TrainerProfile trainer =
                trainerProfileRepository
                        .findByPersonId(trainerPersonId)
                        .orElseThrow();

        //  υπάρχοντα appointments
        List<Appointment> appointments =
                appointmentRepository
                        .findByTrainerAndDateBetweenOrderByDateAscStartTimeAsc(
                                trainer,
                                date,
                                date
                        );

        Set<LocalTime> booked =
                appointments.stream()
                        .map(Appointment::getStartTime)
                        .collect(Collectors.toSet());

        // generate 1-hour slots
        List<LocalTime> result = new ArrayList<>();
        LocalTime cursor = start;

        while (!cursor.plusHours(1).isAfter(end)) {

            if (!booked.contains(cursor)) {
                result.add(cursor);
            }

            cursor = cursor.plusHours(1);
        }

        return result;
    }

    @Override
    public boolean canClientCreateAppointment(String email) {
        long activeCount =
                appointmentRepository.countActiveAppointments(email);

        return activeCount < MAX_ACTIVE_APPOINTMENTS;
    }


    @Override
    @Transactional
    public void respondToAppointment(
            Long appointmentId,
            Long trainerPersonId,
            boolean accept
    ) {
        Appointment appointment =
                appointmentRepository.findById(appointmentId)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Appointment not found")
                        );

        if (!appointment.getTrainer().getPerson().getId().equals(trainerPersonId)) {
            throw new SecurityException("Trainer not allowed");
        }

        if (!"PENDING".equals(appointment.getStatus())) {
            throw new IllegalStateException("Appointment already processed");
        }

        if (accept) {
            appointment.setStatus("CONFIRMED");
            /*
            smsNotificationPort.sendSms(
                    appointment.getClient().getPerson().getPhoneNumber(),
                    "Your appointment on " +
                            appointment.getDate() + " at " +
                            appointment.getStartTime() +
                            " has been CONFIRMED."
            );


        } else {

            appointment.setStatus("REJECTED");

            smsNotificationPort.sendSms(
                    appointment.getClient().getPerson().getPhoneNumber(),
                    "Your appointment on " +
                            appointment.getDate() + " at " +
                            appointment.getStartTime() +
                            " has been REJECTED."
            );

             */
        }



    }
    @Override
    public void rejectAppointment(Long appointmentId, Long trainerPersonId) {

        Appointment appointment =
                appointmentRepository.findById(appointmentId)
                        .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        if (!appointment.getTrainer().getPerson().getId().equals(trainerPersonId)) {
            throw new SecurityException("Not allowed to reject this appointment");
        }

        appointmentRepository.delete(appointment);
    }

    @Override
    public void approveAppointment(Long appointmentId, Long trainerPersonId) {

        Appointment appointment =
                appointmentRepository.findById(appointmentId)
                        .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        if (!appointment.getTrainer().getPerson().getId().equals(trainerPersonId)) {
            throw new SecurityException("Not allowed");
        }

        appointment.setStatus("CONFIRMED");
        /*
        smsNotificationPort.sendSms(
                appointment.getClient().getPerson().getPhoneNumber(),
                "Your appointment on " + appointment.getDate() + " at " +
                        appointment.getStartTime() + " has been confirmed."
        );
        */
    }





}
