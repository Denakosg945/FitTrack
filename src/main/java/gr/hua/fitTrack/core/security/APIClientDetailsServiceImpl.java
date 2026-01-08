package gr.hua.fitTrack.core.security;

import gr.hua.fitTrack.core.model.APIClient;
import gr.hua.fitTrack.core.repository.APIClientRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link APIClientDetailsService ).
 **/

@Service
public class APIClientDetailsServiceImpl implements APIClientDetailsService {

    private APIClientRepository apiClientRepository;

    public APIClientDetailsServiceImpl(final APIClientRepository apiClientRepository) {
        if(apiClientRepository == null) throw new NullPointerException();
        this.apiClientRepository = apiClientRepository;
    }

    @Override
    public Optional<APIClientDetails> authenticate(String id, String secret) {
        if(id == null) throw new NullPointerException();
        if(id.isBlank()) throw new IllegalArgumentException();
        if(secret == null) throw new NullPointerException();
        if(secret.isBlank()) throw new IllegalArgumentException();


        final APIClient client = this.apiClientRepository.findByName(id).orElse(null);
        if(client == null) return Optional.empty(); // API client does not exist.



        if(Objects.equals(client.getSecret(),secret)){
            final Set<String> roles = Arrays.stream(client.getPermissionsCsv().split(",")).map(String::strip).collect(Collectors.toSet());
            final APIClientDetails clientDetails = new APIClientDetails(
                    id,
                    secret,
                    client.getPermissionsCsv() == null ? Collections.emptySet() : Arrays.stream(
                            client.getPermissionsCsv().split(",")).map(String::strip).collect(Collectors.toSet()
                    )
            );

            return Optional.of(clientDetails);
        }else{
            return Optional.empty(); //
        }
    }
}
