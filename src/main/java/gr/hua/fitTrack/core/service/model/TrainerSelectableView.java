package gr.hua.fitTrack.core.service.model;


public record TrainerSelectableView(
        String firstName,
        String lastName,
        String location,
        String specialization,
        String token
) {}
