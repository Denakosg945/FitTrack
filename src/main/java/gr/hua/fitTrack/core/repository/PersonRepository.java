package gr.hua.fitTrack.core.repository;

import gr.hua.fitTrack.core.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person,Long> {

    // Jpa provides CRUD functions automatically
    boolean existsByEmailAddress(String emailAddress);

    Person findByPhoneNumber(String phoneNumber);

    void deleteByPhoneNumber(String phoneNumber);

    Optional<Person> findByEmailAddress(String emailAddress);

    //Person findByPersonId(Long id);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<Person> findByEmailAddressIgnoreCase(String emailAddress);

}
