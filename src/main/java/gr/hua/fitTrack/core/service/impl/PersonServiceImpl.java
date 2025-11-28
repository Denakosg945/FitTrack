package gr.hua.fitTrack.core.service.impl;

import gr.hua.fitTrack.core.model.Person;
import gr.hua.fitTrack.core.model.PersonType;
import gr.hua.fitTrack.core.repository.PersonRepository;
import gr.hua.fitTrack.core.service.PersonService;
import gr.hua.fitTrack.core.service.mapper.PersonMapper;
import gr.hua.fitTrack.core.service.model.CreatePersonRequest;
import gr.hua.fitTrack.core.service.model.CreatePersonResult;
import gr.hua.fitTrack.core.service.model.PersonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *Default implementation of {@link PersonService}
 */

@Service
public class PersonServiceImpl implements PersonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonServiceImpl.class);

    private final PasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    //private final PhoneNumberPort;

    public PersonServiceImpl(final PasswordEncoder passwordEncoder,
                             final PersonRepository personRepository,
                             final PersonMapper mapper) {
        if (passwordEncoder == null) throw new IllegalArgumentException("passwordEncoder cannot be null");
        if (personRepository == null) throw new IllegalArgumentException("personRepository cannot be null");
        if (mapper == null) throw new IllegalArgumentException("mapper cannot be null");
        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
        this.personMapper = mapper;
    }
    @Override
    public CreatePersonResult createPerson(final CreatePersonRequest createPersonRequest,final boolean notify){
        if (createPersonRequest == null) throw new IllegalArgumentException("createPersonRequest cannot be null");

        final String fistName = createPersonRequest.firstName();
        final String lastName = createPersonRequest.lastName();
        final int age = createPersonRequest.age();
        final String emailAddress = createPersonRequest.emailAddress();
        final String phoneNumber = createPersonRequest.phoneNumber();
        final String rawPassword = createPersonRequest.rawPassword();
        final PersonType type =createPersonRequest.type();

        final String hashedPassword = passwordEncoder.encode(rawPassword);

        //Initiate Person.

        Person person = new Person();
        person.setId(null); //auto-generated
        person.setFirstName(fistName);
        person.setLastName(lastName);
        person.setAge(age);
        person.setEmailAddress(emailAddress);
        person.setPhoneNumber(phoneNumber);
        person.setPasswordHash(hashedPassword);
        person.setType(type);

        //Save person to the database

        person = this.personRepository.save(person);

        if(notify) {
            final String content = String.format("You have succesfully registered for the Fit Track Application. "  +
                    "Use your (%s) email to login. ", emailAddress);
            //TODO SMS BOOLEAN
        }
        final PersonView personView= this.personMapper.convertPersonToPersonView(person);

        return CreatePersonResult.success(personView);

    }

    @Override
    public void deleteById(Long personId) {
        if (personId==null) return;
        if (!personRepository.existsById(personId)) return;
        personRepository.deleteById(personId);
    }
    @Override
    public boolean existsByEmail(String email) {
        return personRepository.existsByEmailAddress(email);
    }

    @Override
    public Person getByEmail(String email) {
        return personRepository.findByEmailAddress(email);
    }








}
