package gr.hua.fitTrack.core.service.model;

public record UpdateTrainerProfileRequest(
        Long personId,
        String location,
        String specialization
) {
}
