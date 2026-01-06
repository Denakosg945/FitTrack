package gr.hua.fitTrack.core.service.model;

import gr.hua.fitTrack.core.model.Weekday;

import java.time.LocalTime;

public record WeeklyAvailabilityView(
        Long id,
        Weekday weekday,
        LocalTime startTime,
        LocalTime endTime
) {}