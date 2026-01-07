package gr.hua.fitTrack.core.security;

import gr.hua.fitTrack.core.model.Person;
import gr.hua.fitTrack.core.repository.PersonRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;

    public ApplicationUserDetailsService(PersonRepository personRepository) {
        if (personRepository == null) throw new NullPointerException();
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        if (username == null) throw new NullPointerException();
        if (username.isBlank()) throw new IllegalArgumentException();

        final Person person = this.personRepository
                .findByEmailAddressIgnoreCase(username)
                .orElse(null);
        if (person == null) {
            throw new UsernameNotFoundException("person with emailAddress" + username + " does not exist");
        }

        ApplicationUserDetails testLogin= new ApplicationUserDetails(
                person.getId(),
                person.getEmailAddress(),
                person.getPasswordHash(),
                person.getType()
        );

        System.out.println("----------------LOGIN HAPPENING-----------------: The user is: " + testLogin.getUsername() + " and the hash is: " + testLogin.getPassword()); // print to see if the user's login

        return testLogin;

    }
}