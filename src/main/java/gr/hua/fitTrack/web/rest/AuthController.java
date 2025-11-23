package gr.hua.fitTrack.web.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(){
        //TODO IF USER IS AUTHENTICATED, REDIRECT TO DEFAULT VIEW
        return "login";
    }

    @GetMapping("/logout")
    public String logout(){
        //TODO IF USER IS NOT AUTHENTICATED, REDIRECT TO LOGIN

        return "logout";
    }
}
