package gr.hua.fitTrack.core.service.model;

import java.time.Instant;

public record ProgressView(
        Instant date,
        float weight,
        int runningTimeSeconds,
        int bodyFatPercentage,
        float waterIntake
) {
}

