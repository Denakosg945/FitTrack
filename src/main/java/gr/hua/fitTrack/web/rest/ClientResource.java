package gr.hua.fitTrack.web.rest;

import gr.hua.fitTrack.core.service.ClientDataService;
import gr.hua.fitTrack.core.service.model.ClientView;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientResource {

    private final ClientDataService clientDataService;

    public ClientResource(ClientDataService clientDataService) {
        if (clientDataService == null) {
            throw new IllegalArgumentException("clientDataService must not be null");
        }
        this.clientDataService = clientDataService;
    }

    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("")
    public List<ClientView> clients(){
        return this.clientDataService.getAllClients();
    }

}
