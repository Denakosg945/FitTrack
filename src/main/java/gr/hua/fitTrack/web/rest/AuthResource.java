package gr.hua.fitTrack.web.rest;

import gr.hua.fitTrack.core.security.APIClientDetailsService;
import gr.hua.fitTrack.core.security.APIClientDetails;
import gr.hua.fitTrack.core.security.JwtService;
import gr.hua.fitTrack.web.rest.model.TokenRequest;
import gr.hua.fitTrack.web.rest.model.TokenResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthResource {

    private final APIClientDetailsService APIClientDetailsService;
    private final JwtService jwtService;

    public AuthResource(APIClientDetailsService APIClientDetailsService, JwtService jwtService) {
        if(APIClientDetailsService == null) throw new NullPointerException();
        if(jwtService == null) throw new NullPointerException();
        this.APIClientDetailsService = APIClientDetailsService;
        this.jwtService = jwtService;
    }

    @PostMapping("/client-tokens")
    public TokenResponse clientToken(@RequestBody @Valid TokenRequest tokenRequest){

        final String clientId = tokenRequest.clientId();
        final String clientSecret = tokenRequest.clientSecret();



        // Step 1: Find and authenticate Client.
        final APIClientDetails client = this.APIClientDetailsService.authenticate(clientId,clientSecret).orElse(null);
        if(client==null){
            System.out.println("Client not found");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid client credentials");
        }

        // Step 2: Issue token
        final String token = this.jwtService.issue("client:" + client.id(),client.roles());
        final TokenResponse tokenResponse = new TokenResponse(token,"Bearer", 3600);
        return tokenResponse;
    }
}
