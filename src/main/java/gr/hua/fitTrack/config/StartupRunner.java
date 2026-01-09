package gr.hua.fitTrack.config;

import gr.hua.fitTrack.core.port.GeolocationPort;
import gr.hua.fitTrack.core.port.WeatherServicePort;
import gr.hua.fitTrack.core.port.impl.dto.GeolocationResult;
import gr.hua.fitTrack.core.port.impl.dto.WeatherResponse;
import gr.hua.fitTrack.core.service.model.InitializationService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class StartupRunner implements ApplicationListener<ContextRefreshedEvent> {

    private final InitializationService initializationService;
    private final WeatherServicePort weatherServicePort;
    private final GeolocationPort geolocationPort;

    public StartupRunner(
            InitializationService initializationService,
            WeatherServicePort weatherServicePort,
            GeolocationPort geolocationPort
    ) {
        this.initializationService = initializationService;
        this.weatherServicePort = weatherServicePort;
        this.geolocationPort = geolocationPort;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        // ✅ ΜΟΝΟ data seeding
        initializationService.populateDatabase();

        // ✅ external services (OK εδώ)
        GeolocationResult geolocationResult =
                geolocationPort.getCoordinates("Athens Greece");

        WeatherResponse weatherResponse =
                weatherServicePort.getDailyWeatherPrediction(geolocationResult);
    }
}
