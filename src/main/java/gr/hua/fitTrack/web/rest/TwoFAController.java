package gr.hua.fitTrack.web.rest;

import gr.hua.fitTrack.core.exception.PersonException;
import gr.hua.fitTrack.core.model.PersonType;
import gr.hua.fitTrack.core.repository.PersonRepository;
import gr.hua.fitTrack.core.service.PersonService;
import gr.hua.fitTrack.core.service.model.Create2FARequest;
import gr.hua.fitTrack.core.service.model.CreatePersonRequest;
import gr.hua.fitTrack.core.service.model.CreatePersonResult;
import gr.hua.fitTrack.core.service.model.PendingRegistration;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TwoFAController {

    private final PersonRepository personRepository;
    PersonService personService;

    public TwoFAController(PersonService personService, PersonRepository personRepository) {
        if(personService == null) throw new IllegalArgumentException("personService is null");
        this.personService = personService;
        this.personRepository = personRepository;
    }

    @GetMapping("/verifyPhone")
    public String show2FAForm(@RequestParam String phone, Model model) {
        PendingRegistration pending = personService.getPendingRegistration(phone);

        if (pending == null) {
            return "redirect:/register?error=NoPending"; // redirect back to registration
        }
        
        Create2FARequest form = new Create2FARequest();
        form.setPhone(phone);
        model.addAttribute("create2FARequest", form);

        return "verify-phone";
    }

    @PostMapping("/verifyPhone")
    public String verify2FA(@ModelAttribute Create2FARequest form, Model model) {
        try{
            CreatePersonResult result = personService.verify2FACode(form.getPhone(),form.getCode());
            System.out.println(result.created());
            if(!result.created()){
                personRepository.deleteByPhoneNumber(form.getPhone());
                throw new PersonException("Could not verify 2FA code");
            }
            if(result.personView().type().equals(PersonType.CLIENT) ){
                return "redirect:/clientProfileCreation?personId=" + result.personView().id();
            }

            return "redirect:/trainerProfileCreation?personId=" + result.personView().id();
        }catch(Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("create2FARequest", form);
            return "verify-phone";
        }
    }

    @PostMapping("/deletePerson/{phone}")
    @Transactional
    public String deletePending(@PathVariable String phone) {
        personRepository.deleteByPhoneNumber(phone);
        return "redirect:/register";
    }
}
