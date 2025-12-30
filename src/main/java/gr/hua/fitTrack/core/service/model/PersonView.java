package gr.hua.fitTrack.core.service.model;

import gr.hua.fitTrack.core.model.GenderType;
import gr.hua.fitTrack.core.model.PersonType;

public record PersonView(
        Long id,
        String firstName,
        String lastName,
        int age,
        GenderType gender,
        String emailAddress,
        String phoneNumber,
        PersonType type
) {
}
