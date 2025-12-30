package gr.hua.fitTrack.core.service.impl;

import gr.hua.fitTrack.core.exception.SendSmsException;
import gr.hua.fitTrack.core.model.Person;
import gr.hua.fitTrack.core.model.PersonType;
import gr.hua.fitTrack.core.port.PhoneNumberPort;
import gr.hua.fitTrack.core.port.SmsNotificationPort;
import gr.hua.fitTrack.core.port.impl.SmsNotificationPortImpl;
import gr.hua.fitTrack.core.repository.PersonRepository;
import gr.hua.fitTrack.core.service.PersonService;
import gr.hua.fitTrack.core.service.mapper.PersonMapper;
import gr.hua.fitTrack.core.service.model.CreatePersonRequest;
import gr.hua.fitTrack.core.service.model.CreatePersonResult;
import gr.hua.fitTrack.core.service.model.PendingRegistration;
import gr.hua.fitTrack.core.service.model.PersonView;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *Default implementation of {@link PersonService}
 */

@Service
@Transactional
public class PersonServiceImpl implements PersonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonServiceImpl.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final PasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final SmsNotificationPort smsNotificationPort;
    private final PhoneNumberPort phoneNumberPort;
    private final Map<String, PendingRegistration> pendingRegistrations = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public PersonServiceImpl(final PasswordEncoder passwordEncoder,
                             final PersonRepository personRepository,
                             final PersonMapper mapper,final SmsNotificationPort smsNotificationPort,
                             final PhoneNumberPort phoneNumberPort) {
        if (passwordEncoder == null) throw new IllegalArgumentException("passwordEncoder cannot be null");
        if (personRepository == null) throw new IllegalArgumentException("personRepository cannot be null");
        if (mapper == null) throw new IllegalArgumentException("mapper cannot be null");
        if (smsNotificationPort == null) throw new IllegalArgumentException("smsNotificationPort cannot be null");
        if (phoneNumberPort == null) throw new IllegalArgumentException("phoneNumberPort cannot be null");
        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
        this.personMapper = mapper;
        this.smsNotificationPort = smsNotificationPort;
        this.phoneNumberPort = phoneNumberPort;
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
        final PersonType type = createPersonRequest.type();

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

        //Validate phone number
        if(!phoneNumberPort.validate(phoneNumber).isValid()) throw new IllegalArgumentException("Phone number is not valid");

        //Save person to the database

        person = this.personRepository.save(person);


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


    public void generate2FACode(CreatePersonRequest createPersonRequest) {
        //Check if phone number is valid
        if(!phoneNumberPort.validate(createPersonRequest.phoneNumber()).isValid()){
            throw new IllegalArgumentException("Phone number is not valid");
        }

        //Generate 5-Digit code
        String code = String.format("%05d", RANDOM.nextInt(100_000));

        //Send code with SMS
        smsNotificationPort.sendSms(createPersonRequest.phoneNumber(),"Your verification code: " + code);

        //Store pending registration and add a 5-Minute expiration timer
        pendingRegistrations.put(createPersonRequest.phoneNumber(),new PendingRegistration(
                createPersonRequest, code, Instant.now().plusSeconds(300))
        );
    }



    public CreatePersonResult verify2FACode(String phone,String code){
        PendingRegistration pendingRegistration = pendingRegistrations.get(phone);
        if(pendingRegistration==null){
            throw new IllegalStateException("No pending registration for this phone number.");
        }

        if(Instant.now().isAfter(pendingRegistration.expiresAt())){
            pendingRegistrations.remove(phone);
            throw new RuntimeException("Code expired");
        }

        if(!code.equals(pendingRegistration.code())){
            throw new IllegalArgumentException("Code mismatch! Invalid code!");
        }

        pendingRegistrations.remove(phone);
        return CreatePersonResult.success(personMapper.convertPersonToPersonView(
                personRepository.findByPhoneNumber(phone)
        ));
    }


    public PendingRegistration getPendingRegistration(String phone){
        return pendingRegistrations.get(phone);
    }





}
