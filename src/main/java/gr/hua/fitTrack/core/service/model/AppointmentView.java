package gr.hua.fitTrack.core.service.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentView(
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String trainerName,
        String status,
        boolean isOutdoor
) {}
