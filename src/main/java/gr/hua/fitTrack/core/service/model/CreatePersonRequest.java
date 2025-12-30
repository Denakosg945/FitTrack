package gr.hua.fitTrack.core.service.model;

import gr.hua.fitTrack.core.model.GenderType;
import gr.hua.fitTrack.core.model.Person;
import gr.hua.fitTrack.core.model.PersonType;

public record CreatePersonRequest(
        String firstName,
        String lastName,
        int age,
        GenderType gender,
        String emailAddress,
        String phoneNumber,
        String rawPassword,
        PersonType type
) {
}
