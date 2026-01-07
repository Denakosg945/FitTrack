package gr.hua.fitTrack.core.service.model;

import gr.hua.fitTrack.core.model.ClientProfile;
import gr.hua.fitTrack.core.model.Goals;
import gr.hua.fitTrack.core.model.Progress;
import gr.hua.fitTrack.core.service.mapper.ProgressMapper;

import java.util.List;

public record ClientView(
        Long clientId,
        String firstName,
        String lastName,
        int weight,
        int height,
        Goals goals,
        List<ProgressView> progress
) {
}


