package gr.hua.fitTrack.web.rest;

import gr.hua.fitTrack.core.port.GeolocationPort;
import gr.hua.fitTrack.core.port.WeatherServicePort;
import gr.hua.fitTrack.core.port.impl.dto.WeatherUsefulData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LogInHomepageController {

    private final String DEFAULT_LOCATION = "Athens, Greece";

    private final WeatherServicePort weatherService;
    private final GeolocationPort geolocationPort;

    public LogInHomepageController(WeatherServicePort weatherService,GeolocationPort geolocationPort) {
        this.weatherService = weatherService;
        this.geolocationPort = geolocationPort;
    }

    @GetMapping("/loginHomepage")
    public String getLoginHomepage(Model model){

        String location = DEFAULT_LOCATION;
        if(model.containsAttribute("location")){
            location = (String) model.getAttribute("location");
        }

        WeatherUsefulData data = weatherService.toUsefulData(
                weatherService.getDailyWeatherPrediction(geolocationPort.getCoordinates(location))
        );



        model.addAttribute("data",data);
        model.addAttribute("location",location);
        String condition = "Sunny";
        if(!data.isVisible()){
            condition = "Rainy";
        }
        model.addAttribute("condition",condition);
        model.addAttribute("weatherIcon",condition.toLowerCase());

        return "loggedInHomepage";
    }

    @PostMapping("/loginHomepage")
    public String updateLoginHomepage(
            @ModelAttribute("location")final String location, Model model,RedirectAttributes redirectAttributes
    ){
        if(location == null){
            return "redirect:/loginHomepage";
        }

        if(location.isEmpty()){
            return "redirect:/loginHomepage";
        }

        redirectAttributes.addFlashAttribute("location",location);


        return "redirect:/loginHomepage";
    }
}
