package gr.hua.fitTrack.core.repository;

import gr.hua.fitTrack.core.model.ClientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClientProfileRepository extends JpaRepository<ClientProfile, Integer> {
    Optional<ClientProfile> findByPersonId(Long personId);

    @Query("""
    select c
    from ClientProfile c
    left join fetch c.goals
    left join fetch c.progress
    where c.person.id = :personId
""")
    Optional<ClientProfile> findByPersonIdWithGoalsAndProgress(
            @Param("personId") Long personId
    );
}
