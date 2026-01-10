package gr.hua.fitTrack.web.rest;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomepageController {

    @GetMapping("/")
    public String getHomepage(Authentication authentication) {
        //if user is authenticated redirect to homepage after login
        if (authentication != null && authentication.isAuthenticated()) return "redirect:/loginHomepage";


        return "homepage";
    }
}
