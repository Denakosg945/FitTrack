package gr.hua.fitTrack.core.port;

import gr.hua.fitTrack.core.port.impl.dto.GeolocationResult;
import gr.hua.fitTrack.core.port.impl.dto.WeatherResponse;
import gr.hua.fitTrack.core.port.impl.dto.WeatherUsefulData;

public interface WeatherServicePort {
    public WeatherResponse getDailyWeatherPrediction(GeolocationResult geolocationResult);

    public WeatherUsefulData toUsefulData(WeatherResponse weatherResponse);
}
