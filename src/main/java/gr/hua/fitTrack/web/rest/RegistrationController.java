package gr.hua.fitTrack.web.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * UI controller for managing client/trainer registration.
 */
@Controller
public class RegistrationController {

    /**
     * Serves the registration form (HTML).
     */
    //Prepei na trexw service afou ta ftiaksoume


    @GetMapping("/register")
    public String showRegistrationForm(/*final Model model*/) {
        //TODO if user is authenticated, redirect to
        //Initial data for the form.
        //model.addAttribute("person/user", new Person/User(null,"", etc...))

        return "register"; //the name of the thymeleaf/HTML template.

    }

    /**
     * Handles te registration form submission (POST HTTP request)
     */
    @PostMapping("/register")
    public String handleRegistrationFormSubmission(){
        //Des ergasthrio 2
        return "?";
    }
}
