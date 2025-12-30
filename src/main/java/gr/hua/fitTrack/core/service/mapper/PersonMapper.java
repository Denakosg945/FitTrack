package gr.hua.fitTrack.core.service.mapper;

import gr.hua.fitTrack.core.model.Person;
import gr.hua.fitTrack.core.service.model.PersonView;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {

    public PersonView convertPersonToPersonView(final Person person) {
        if(person == null) {
            return null;
        }
        final PersonView personView = new PersonView(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getAge(),
                person.getGender(),
                person.getEmailAddress(),
                person.getPhoneNumber(),
                person.getType()
        );
        return personView;
    }
}
