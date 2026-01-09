package gr.hua.fitTrack.core.service.impl;

import gr.hua.fitTrack.core.exception.MaxActiveAppointmentsExceededException;
import gr.hua.fitTrack.core.model.Appointment;
import gr.hua.fitTrack.core.model.ClientProfile;
import gr.hua.fitTrack.core.model.TrainerProfile;
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

    private static final int MAX_ACTIVE_APPOINTMENTS = 3;

    public AppointmentServiceImpl(
            AppointmentRepository appointmentRepository,
            TrainerProfileRepository trainerProfileRepository,
            ClientProfileRepository clientProfileRepository,
            TrainerService trainerService,
            TokenService tokenService
    ) {
        this.appointmentRepository = appointmentRepository;
        this.trainerProfileRepository = trainerProfileRepository;
        this.clientProfileRepository = clientProfileRepository;
        this.trainerService = trainerService;
        this.tokenService = tokenService;
    }

    @Override
    public void save(Appointment appointment) {

        long activeCount =
                appointmentRepository.countActiveAppointments(
                        appointment.getClient().getId()
                );


        if (activeCount >= MAX_ACTIVE_APPOINTMENTS) {
            throw new MaxActiveAppointmentsExceededException(
                    "You already have the maximum number of active appointments."
            );
        }

        // TODO [NOTIFICATION]:
        // - Send EMAIL to CLIENT: "Your appointment request has been submitted"
        // - Send SMS/EMAIL to TRAINER: "New appointment request pending approval"
        // Ty Dio eisai alani



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
    public boolean canClientCreateAppointment(Long clientId) {
        long activeCount =
                appointmentRepository.countActiveAppointments(
                        clientId
                );

        return activeCount < MAX_ACTIVE_APPOINTMENTS;
    }



}
