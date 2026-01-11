package gr.hua.fitTrack.core.service.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record TrainerAppointmentView(
        Long appointmentId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String status,
        String clientName,
        String notes
) {}
