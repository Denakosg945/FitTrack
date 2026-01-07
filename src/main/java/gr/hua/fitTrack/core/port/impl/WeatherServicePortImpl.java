package gr.hua.fitTrack.core.port.impl;

import gr.hua.fitTrack.core.model.UvIndexClassification;
import gr.hua.fitTrack.core.port.WeatherServicePort;
import gr.hua.fitTrack.core.port.impl.dto.GeolocationResult;
import gr.hua.fitTrack.core.port.impl.dto.Hourly;
import gr.hua.fitTrack.core.port.impl.dto.WeatherResponse;
import gr.hua.fitTrack.core.port.impl.dto.WeatherUsefulData;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
        String openMeteoUrl = "https://api.open-meteo.com/v1/forecast?latitude=" +
                URLEncoder.encode(geolocationResult.latitudeToString(), StandardCharsets.UTF_8) +
                "&longitude=" +
                URLEncoder.encode(geolocationResult.longitudeToString(), StandardCharsets.UTF_8) +
                "&hourly=temperature_2m,uv_index,relative_humidity_2m,dew_point_2m," +
                "apparent_temperature,wind_speed_10m,wind_direction_10m," +
                "precipitation_probability,precipitation";

        final ResponseEntity<WeatherResponse> responseEntity = restTemplate.getForEntity(openMeteoUrl, WeatherResponse.class);

        if(responseEntity.getStatusCode().is2xxSuccessful()) {
            WeatherResponse weather = responseEntity.getBody();
            if(weather != null) return weather;

            throw new RuntimeException("Weather API returned empty body");
        }
        throw new RuntimeException("Weather API responded with " + responseEntity.getStatusCode());
    }

    public WeatherUsefulData toUsefulData(WeatherResponse weatherResponse) {
        if(weatherResponse == null) throw new NullPointerException();

        Double maxTemp = findMax(weatherResponse.hourly().temperature_2m());
        Double minTemp = findMin(weatherResponse.hourly().temperature_2m());
        Double avgTemp = findAvg(weatherResponse.hourly().temperature_2m());

        Double maxApparentTemp = findMax(weatherResponse.hourly().apparent_temperature());
        Double minApparentTemp = findMin(weatherResponse.hourly().apparent_temperature());
        Double avgApparentTemp = findAvg(weatherResponse.hourly().apparent_temperature());

        Integer maxHumidity = findMaxInt(weatherResponse.hourly().relative_humidity_2m());
        Double avgHumidity = findAvgInt(weatherResponse.hourly().relative_humidity_2m());

        Double avgWindSpeed = findAvg(weatherResponse.hourly().wind_speed_10m());
        Double maxSustainedWindSpeed = findMax(weatherResponse.hourly().wind_speed_10m());

        Integer precipitationProbability = findMaxInt(weatherResponse.hourly().precipitation_probability());
        Boolean isVisible = precipitationProbability < 70;

        Double maxUvIndex = findMax(weatherResponse.hourly().uv_index());
        UvIndexClassification uvIndexClassification;
        if (maxUvIndex < 3) {uvIndexClassification = UvIndexClassification.LOW;}
        else if (maxUvIndex < 6) {uvIndexClassification = UvIndexClassification.MODERATE;}
        else if (maxUvIndex < 8) {uvIndexClassification = UvIndexClassification.HIGH;}
        else if (maxUvIndex < 11) {uvIndexClassification = UvIndexClassification.VERY_HIGH;}
        else {uvIndexClassification = UvIndexClassification.EXTREME;}

        return new WeatherUsefulData(
                minTemp,
                maxTemp,
                avgTemp,
                minApparentTemp,
                maxApparentTemp,
                avgApparentTemp,
                maxHumidity,
                avgHumidity,
                avgWindSpeed,
                maxSustainedWindSpeed,
                precipitationProbability,
                isVisible,
                uvIndexClassification
        );
    }

    private Double findAvgInt(List<Integer> tempList) {
        Double sum = 0.0;
        for(Integer temp : tempList){
            sum += temp;
        }
        double avg = sum/tempList.size();
        return BigDecimal.valueOf(avg)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private Double findAvg(List<Double> tempList){
        Double sum = 0.0;
        for(Double temp : tempList){
            sum += temp;
        }
        double avg = sum/tempList.size();
        return BigDecimal.valueOf(avg)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private Double findMin(List<Double> tempList){
        Double minTemp = tempList.getFirst();
        for(Double temp : tempList){
            if(temp < minTemp) minTemp = temp;
        }
        return BigDecimal.valueOf(minTemp)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private Double findMax(List<Double> tempList){
        Double maxTemp = tempList.getFirst();
        for(Double temp : tempList){
            if(temp > maxTemp) maxTemp = temp;
        }
        return BigDecimal.valueOf(maxTemp)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private Integer findMaxInt(List<Integer> tempList){
        Integer maxTemp = tempList.getFirst();
        for(Integer temp : tempList){
            if(temp > maxTemp) maxTemp = temp;
        }
        return maxTemp;
    }
}
