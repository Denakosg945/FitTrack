package gr.hua.fitTrack.core.exception;


import gr.hua.fitTrack.core.exception.MaxActiveAppointmentsExceededException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxActiveAppointmentsExceededException.class)
    public String handleMaxAppointments(
            MaxActiveAppointmentsExceededException ex,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute(
                "errorMessage",
                ex.getMessage()
        );

        return "redirect:/client/appointments/request";
    }
}
