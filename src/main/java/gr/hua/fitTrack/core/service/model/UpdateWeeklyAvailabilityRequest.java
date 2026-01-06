package gr.hua.fitTrack.core.service.model;

import gr.hua.fitTrack.core.model.Weekday;

import java.time.LocalTime;
import java.util.Map;

public record UpdateWeeklyAvailabilityRequest(
        Long trainerProfileId,
        Map<Weekday, LocalTime> startTimes,
        Map<Weekday, LocalTime> endTimes
) {}