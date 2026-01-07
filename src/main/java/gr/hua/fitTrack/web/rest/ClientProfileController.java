package gr.hua.fitTrack.web.rest;

import gr.hua.fitTrack.core.service.ClientService;
import gr.hua.fitTrack.core.service.model.ClientView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/client")
public class ClientProfileController {

    private final ClientService clientService;

    public ClientProfileController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/profile")
    public String clientProfile(Model model) {

        Long TEST_CLIENT_PERSON_ID = 2L; // προσωρινά hardcoded

        ClientView client =
                clientService.getClientProfileByPersonId(
                        TEST_CLIENT_PERSON_ID
                );

        model.addAttribute("client", client);

        return "client/profile";
    }
}
