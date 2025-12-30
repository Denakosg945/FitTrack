package gr.hua.fitTrack.web.rest;

import gr.hua.fitTrack.core.model.PersonType;
import gr.hua.fitTrack.core.service.PersonService;
import gr.hua.fitTrack.core.service.model.CreatePersonRequest;
import gr.hua.fitTrack.core.service.model.CreatePersonResult;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * UI controller for managing client/trainer registration.
 */
@Controller
public class RegistrationController {

    /**
     * Serves the registration form (HTML).
     */
    private final PersonService personService;

    public RegistrationController(final PersonService personService) {

        if (personService == null) throw new NullPointerException("personService is null");
        this.personService = personService;
    }


    @GetMapping("/register")
    public String showRegistrationForm(final Model model) {

        //TODO if user is authenticated, redirect to
        //Initial data for the form.
        final CreatePersonRequest createPersonRequest = new CreatePersonRequest("","",-1,"","","", PersonType.CLIENT);
        model.addAttribute("createPersonRequest",createPersonRequest);

        return "register"; //the name of the thymeleaf/HTML template.

    }

    /**
     * Handles te registration form submission (POST HTTP request)
     */
    @PostMapping("/register")
    public String handleRegistrationFormSubmission(
            @ModelAttribute("createPersonRequest") final CreatePersonRequest createPersonRequest,
            final Model model
        ){

        //TODO: Validation, UI errors
        final CreatePersonResult createPersonResult = personService.createPerson(createPersonRequest);


        if (createPersonResult.created()){
            personService.generate2FACode(createPersonRequest);
            String phoneParameter = URLEncoder.encode(createPersonResult.personView().phoneNumber(), StandardCharsets.UTF_8);
            return "redirect:/verifyPhone?phone=" + phoneParameter + "&userId=" + createPersonResult.personView().id();
        }
        model.addAttribute("createPersonRequest",createPersonRequest);
        model.addAttribute("errorMessage",createPersonResult.reason());


        return "register";
    }
}
