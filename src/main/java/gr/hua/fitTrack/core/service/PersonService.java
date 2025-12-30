package gr.hua.fitTrack.core.service;

import gr.hua.fitTrack.core.model.Person;
import gr.hua.fitTrack.core.service.model.CreatePersonRequest;
import gr.hua.fitTrack.core.service.model.CreatePersonResult;
import gr.hua.fitTrack.core.service.model.PendingRegistration;
import gr.hua.fitTrack.core.service.model.PersonView;

public interface PersonService {

    CreatePersonResult createPerson(final CreatePersonRequest createPersonRequest, final boolean notify);

    default CreatePersonResult createPerson(final CreatePersonRequest createPersonRequest) {
        return this.createPerson(createPersonRequest, true);
    }

    Person getByEmail(final String email);

    boolean existsByEmail(String email);

    void deleteById(Long personId);

    PendingRegistration getPendingRegistration(String phone);

    void generate2FACode(CreatePersonRequest createPersonRequest);

    CreatePersonResult verify2FACode(String phone,String code);

}
