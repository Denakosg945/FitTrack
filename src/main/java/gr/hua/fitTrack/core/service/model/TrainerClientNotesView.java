package gr.hua.fitTrack.core.service.model;

import gr.hua.fitTrack.core.model.TrainerClientNotes;

public record TrainerClientNotesView(
        Long id,
        Long clientId,
        String clientFullName,
        String notes
) {

    public static TrainerClientNotesView from(
            TrainerClientNotes entity
    ) {
        return new TrainerClientNotesView(
                entity.getId(),
                entity.getClient().getId(),
                entity.getClient().getPerson().getFirstName()
                        + " "
                        + entity.getClient().getPerson().getLastName(),
                entity.getNotes()
        );
    }
}
