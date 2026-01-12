package gr.hua.fitTrack.web.rest;

import gr.hua.fitTrack.core.service.ClientService;
import gr.hua.fitTrack.core.service.PersonService;
import gr.hua.fitTrack.core.service.model.CreateClientRequest;
import gr.hua.fitTrack.core.service.model.CreateClientResult;
import gr.hua.fitTrack.core.service.model.CreateTrainerRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ClientProfileCreationController {
    private final ClientService clientService;
    private final PersonService personService;

    public ClientProfileCreationController(ClientService clientService, PersonService personService) {
        this.clientService = clientService;
        this.personService = personService;
    }

    @GetMapping("/clientProfileCreation")
    public String showClientForm(
            @RequestParam("personId") Long personId,
            Model model){

                final CreateClientRequest createClientRequest = new CreateClientRequest(
                        personId,
                        0,
                        0,
                        null,
                        null,
                        null
                );
                model.addAttribute("createClientRequest", createClientRequest);
                return "clientProfileCreation";
    }
    @PostMapping("/clientProfileCreation")
    public String handleClientForm(
            @ModelAttribute("createClientRequest") final CreateClientRequest createClientRequest,
            @RequestParam("personId") Long personId,
            final Model model, HttpServletResponse response
            ){

        System.out.println("clientProfileCreation");

        final CreateClientResult createClientResult = clientService.createClientProfile(createClientRequest);
        if(createClientResult.created()){
            //Delete the cookie - no longer needed
            Cookie cookie = new Cookie("token", null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return "redirect:/login";
        }
        personService.deleteById(personId);

        //Delete the cookie - no longer needed
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        model.addAttribute("createClientRequest", createClientRequest);
        model.addAttribute("errorMessage", createClientResult.reason());
        return "redirect:/register";

    }

}
