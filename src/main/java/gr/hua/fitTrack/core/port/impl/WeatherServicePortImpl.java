package gr.hua.fitTrack.core.port.impl;

import gr.hua.fitTrack.core.port.WeatherServicePort;
import gr.hua.fitTrack.core.port.impl.dto.GeolocationResult;
import gr.hua.fitTrack.core.port.impl.dto.WeatherResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class WeatherServicePortImpl implements WeatherServicePort {
    private RestTemplate restTemplate;

    public WeatherServicePortImpl(RestTemplate restTemplate) {
        if (restTemplate == null) throw new NullPointerException();
        this.restTemplate = restTemplate;
    }

    @Override
    public WeatherResponse getDailyWeatherPrediction(GeolocationResult geolocationResult) {
        if(geolocationResult == null) throw new NullPointerException();
        if(geolocationResult.getLatitude() > 90 || geolocationResult.getLatitude() < -90) throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
        if(geolocationResult.getLongitude() > 90 || geolocationResult.getLongitude() < -90) throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
        String openMeteoUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + URLEncoder.encode(
                geolocationResult.latitudeToString(), StandardCharsets.UTF_8) +
                "&longitude=" +
                URLEncoder.encode(geolocationResult.longitudeToString(),StandardCharsets.UTF_8)
                +"&hourly=temperature_2m,uv_index,relative_humidity_2m,dew_point_2m,apparent_temperature," +
                ",wind_direction_10m,precipitation_probability,precipitation";

        final ResponseEntity<WeatherResponse> responseEntity = restTemplate.getForEntity(openMeteoUrl, WeatherResponse.class);

        if(responseEntity.getStatusCode().is2xxSuccessful()) {
            WeatherResponse weather = responseEntity.getBody();
            if(weather != null) return weather;

            throw new RuntimeException("Weather API returned empty body");
        }
        throw new RuntimeException("Weather API responded with " + responseEntity.getStatusCode());
    }
}
