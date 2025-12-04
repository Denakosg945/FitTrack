package gr.hua.fitTrack.core.repository;

import gr.hua.fitTrack.core.model.ClientProfile;
import gr.hua.fitTrack.core.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import gr.hua.fitTrack.core.model.Progress;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Integer> {

    // Jpa provides CRUD functions automatically

    public List<Progress> findByClientOrderByEntryDateDesc(ClientProfile client);

    void deleteByClientAndId(ClientProfile person, Long id);

}
