package gr.hua.fitTrack.core.service.model;

import java.time.LocalTime;

public record TrainerAvailabilitySelectableView(
        String firstName,
        String lastName,
        String location,
        String specialization,
        boolean available,
        LocalTime startTime,
        LocalTime endTime,
        String token
) {}

