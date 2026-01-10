package gr.hua.fitTrack.web.rest;

import gr.hua.fitTrack.core.model.GenderType;
import gr.hua.fitTrack.core.model.PersonType;
import gr.hua.fitTrack.core.repository.PersonRepository;
import gr.hua.fitTrack.core.service.PersonService;
import gr.hua.fitTrack.core.service.model.CreatePersonRequest;
import gr.hua.fitTrack.core.service.model.CreatePersonResult;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
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
    private final PersonRepository personRepository;

    public RegistrationController(final PersonService personService, PersonRepository personRepository) {

        if (personService == null) throw new NullPointerException("personService is null");
        this.personService = personService;
        this.personRepository = personRepository;
    }


    @GetMapping("/register")
    public String showRegistrationForm(final Model model, Authentication authentication) {
        //if user is authenticated redirect to homepage after login
        if (authentication != null && authentication.isAuthenticated()) return "redirect:/loginHomepage";


        //Initial data for the form.
        final CreatePersonRequest createPersonRequest = new CreatePersonRequest("","",-1, GenderType.MALE,"","","", PersonType.CLIENT);
        model.addAttribute("createPersonRequest",createPersonRequest);

        return "register"; //the name of the thymeleaf/HTML template.

    }

    /**
     * Handles te registration form submission (POST HTTP request)
     */
    @PostMapping("/register")
    public String handleRegistrationFormSubmission(
            @ModelAttribute("createPersonRequest") final CreatePersonRequest createPersonRequest,
            final Model model,
            HttpServletResponse response
        ){

        final CreatePersonResult createPersonResult;

        try {
             createPersonResult = personService.createPerson(createPersonRequest);
        }catch(DataIntegrityViolationException ex) {
            model.addAttribute("errorMessage", "Phone number or email already exists");
            return "register"; // stay on the same page
        }

        if (createPersonResult.created()){
            //Generate cookie to authorize verifyPhone
            Cookie phoneCookie = new Cookie("token",createPersonResult.personView().phoneNumber());
            phoneCookie.setPath("/");
            phoneCookie.setHttpOnly(true);
            phoneCookie.setMaxAge(300); // Cookie expiration in 5 minutes
            response.addCookie(phoneCookie);

            personService.generate2FACode(createPersonRequest);

            String phoneParameter = URLEncoder.encode(createPersonResult.personView().phoneNumber(), StandardCharsets.UTF_8);
            return "redirect:/verifyPhone?phone=" + phoneParameter + "&userId=" + createPersonResult.personView().id();
        }
        model.addAttribute("createPersonRequest",createPersonRequest);
        model.addAttribute("errorMessage",createPersonResult.reason());

        return "register";
    }
}
