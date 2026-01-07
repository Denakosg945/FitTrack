package gr.hua.fitTrack.web.rest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)){
            return "redirect:/loginHomepage";
        }
        if (request.getParameter("error") != null){
            model.addAttribute("error", "Either the email or password is invalid.");
        }
        if (request.getParameter("logout") != null){
            model.addAttribute("message", "You have been logged out!");
        }
        return "login";
    }

    /* Isn't needed
    @GetMapping("/logout")
    public String logout(){
        return "logout";
    }
     */
}
