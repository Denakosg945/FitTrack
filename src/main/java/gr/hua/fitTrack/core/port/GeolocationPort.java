package gr.hua.fitTrack.core.port;

import gr.hua.fitTrack.core.port.impl.dto.GeolocationResult;

public interface GeolocationPort {
    public GeolocationResult getCoordinates(String location);
}
