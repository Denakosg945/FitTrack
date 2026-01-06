package gr.hua.fitTrack.core.service.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record TrainerAppointmentView(
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String clientName,
        String notes
) {}
