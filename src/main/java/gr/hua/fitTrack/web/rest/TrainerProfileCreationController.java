package gr.hua.fitTrack.web.rest;

import gr.hua.fitTrack.core.model.Weekday;
import gr.hua.fitTrack.core.service.PersonService;
import gr.hua.fitTrack.core.service.TrainerService;
import gr.hua.fitTrack.core.service.model.CreateTrainerRequest;
import gr.hua.fitTrack.core.service.model.CreateTrainerResult;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.EnumMap;

@Controller
public class TrainerProfileCreationController {

    private final TrainerService trainerService;
    private final PersonService personService;

    public  TrainerProfileCreationController(PersonService personService, TrainerService trainerService) {
        if (personService == null) throw new NullPointerException("personService is null");
        if (trainerService == null) throw new NullPointerException("trainerService is null");
        this.personService = personService;
        this.trainerService = trainerService;

    }

    @GetMapping("/trainerProfileCreation")
    public String showTrainerForm(
        @RequestParam("personId") Long personId,
                Model model
    ) {

        final CreateTrainerRequest createTrainerRequest = new CreateTrainerRequest(
                personId,
                "",
                "",
                "",
                new EnumMap<>(Weekday.class),
                new EnumMap<>(Weekday.class));
        model.addAttribute("createTrainerRequest",  createTrainerRequest);



        return "trainerProfileCreation";
    }

    @PostMapping("/trainerProfileCreation")
    public String handleTrainerForm(
            @ModelAttribute("createTrainerRequest") final CreateTrainerRequest createTrainerRequest,
            @RequestParam("personId") Long personId,
            final Model model,
            HttpServletResponse response
    ){
        final CreateTrainerResult createTrainerResult = trainerService.createTrainerProfile(createTrainerRequest);
        if (createTrainerResult.created()){
            //Delete the cookie - no longer needed
            Cookie cookie = new Cookie("token", null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return "redirect:/login";
        }

        //Delete the person just created from db and redirect to register
        //So that there are no person entities in db that are not assigned a profile
        personService.deleteById(personId);
        //Delete the cookie - no longer needed
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        model.addAttribute("createTrainerRequest", createTrainerRequest);
        model.addAttribute("errorMessage", createTrainerResult.reason());
        return "redirect:/register";
        //TODO LET THEM KNOW ABOUT THE ERROR AND ASK TO START FROM BEGINNING
    }


}
