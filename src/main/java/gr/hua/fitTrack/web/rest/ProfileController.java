package gr.hua.fitTrack.web.rest;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {
    @GetMapping("/profile")
    public String profilePage(Authentication authentication) {

        if (authentication == null) {
            return "redirect:/login";
        }

        boolean isClient = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));

        boolean isTrainer = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TRAINER"));

        if (isClient) {
            return "redirect:/client/profile";
        }

        if (isTrainer) {
            return "redirect:/trainer/profile";
        }

        return "redirect:/loginHomepage";
    }

}
