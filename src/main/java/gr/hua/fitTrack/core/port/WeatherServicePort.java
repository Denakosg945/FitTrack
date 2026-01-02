package gr.hua.fitTrack.core.port;

import gr.hua.fitTrack.core.port.impl.dto.GeolocationResult;
import gr.hua.fitTrack.core.port.impl.dto.WeatherResponse;

public interface WeatherServicePort {
    public WeatherResponse getDailyWeatherPrediction(GeolocationResult geolocationResult);
}
