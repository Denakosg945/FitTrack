package gr.hua.fitTrack.core.port.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.hua.fitTrack.core.port.GeolocationPort;
import gr.hua.fitTrack.core.port.impl.dto.GeolocationResult;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class GeolocationPortImpl implements GeolocationPort {
    private final String geocodeBaseUrl = "https://nominatim.openstreetmap.org/search?q=";
    private final String geocodeSuffix = "&format=json&limit=1";

    private final RestTemplate restTemplate;

    public GeolocationPortImpl(final RestTemplate restTemplate) {
        if (restTemplate == null) throw new NullPointerException();
        this.restTemplate = restTemplate;
    }

    @Override
    public GeolocationResult getCoordinates(String location){
        if (location == null) throw new NullPointerException();
        if (location.isEmpty()) throw new IllegalArgumentException();

        final String url = geocodeBaseUrl + URLEncoder.encode(location, StandardCharsets.UTF_8) + geocodeSuffix;
        final ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if(response.getStatusCode().is2xxSuccessful()){
            String body = response.getBody();
            ObjectMapper mapper = new ObjectMapper();

            List<GeolocationResult> results;
            try {
                results = mapper.readValue(body, new TypeReference<List<GeolocationResult>>() {
                });
            }catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            if(!results.isEmpty()){
                GeolocationResult result = results.getFirst();
                return result;
            }else{
                return new GeolocationResult(0.0,0.0);
            }

        };

        throw new RuntimeException("External geolocation service responded with " + response.getStatusCode());

    }

}
