package gr.hua.fitTrack.web.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TrainersController {

    @GetMapping("/trainers")
    public String showTrainers(){
        return "trainers";
    }
}
