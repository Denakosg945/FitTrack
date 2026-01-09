package gr.hua.fitTrack.web.rest;

import gr.hua.fitTrack.core.service.AppointmentDataService;
import gr.hua.fitTrack.core.service.model.AppointmentView;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentResource {

    private final AppointmentDataService appointmentDataService;

    public AppointmentResource(AppointmentDataService appointmentDataService) {
        if( appointmentDataService == null ) throw new NullPointerException("appointmentDataService is null");
        this.appointmentDataService = appointmentDataService;
    }

    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("")
    public List<AppointmentView> getAppointmentDataService() {
        return this.appointmentDataService.appointments();
    }

}
