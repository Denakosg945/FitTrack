package gr.hua.fitTrack.web.rest;

import gr.hua.fitTrack.core.service.TrainerService;
import gr.hua.fitTrack.core.service.model.TrainerView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class TrainersController {

    private final TrainerService trainerService;

    public TrainersController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @GetMapping("/trainers")
    public String showTrainers(
            //Parameters used for search
            @RequestParam(required= false) String lname,
            @RequestParam(required=false) String location,
            @RequestParam(required=false) String specialization,
            Model model
        ){


        List<String> lastNames = trainerService.getDistinctLastNames();
        List<String> locations = trainerService.getDistinctLocations();
        List<String> specializations = trainerService.getDistinctSpecializations();

        //Search Results:
        //If search has not happened or all search fields are null, all trainerProfiles are returned.
        List<TrainerView> trainers = trainerService.search(lname,location,specialization);

        model.addAttribute("trainers", trainers);
        model.addAttribute("lastNames", lastNames);
        model.addAttribute("locations", locations);
        model.addAttribute("specializations", specializations);

        model.addAttribute("selectedLastName", lname);
        model.addAttribute("selectedLocation", location);
        model.addAttribute("selectedSpecialization", specialization);



        return "trainers";
    }
}
