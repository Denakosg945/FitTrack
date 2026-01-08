package gr.hua.fitTrack.core.security;

/**
 * Service for managing the REST API {@code Client}
 **/

import java.util.Optional;

public interface APIClientDetailsService {

    Optional<APIClientDetails> authenticate(final String id, final String secret);
}
