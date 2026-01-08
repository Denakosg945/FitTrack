package gr.hua.fitTrack.core.repository;

import gr.hua.fitTrack.core.model.APIClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface APIClientRepository extends JpaRepository<APIClient, Long> {



    Optional<APIClient> findByName(String name);

}
