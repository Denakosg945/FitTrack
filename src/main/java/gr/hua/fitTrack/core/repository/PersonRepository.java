package gr.hua.fitTrack.core.repository;

import gr.hua.fitTrack.core.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person,Long> {

    // Jpa provides CRUD functions automatically
    boolean existsByEmailAddress(String emailAddress);

    Person findByPhoneNumber(String phoneNumber);

    void deleteByPhoneNumber(String phoneNumber);

    Person findByEmailAddress(String emailAddress);
}
