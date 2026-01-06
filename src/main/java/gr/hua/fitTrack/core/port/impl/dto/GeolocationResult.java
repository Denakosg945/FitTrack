package gr.hua.fitTrack.core.port.impl.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;


@JsonIgnoreProperties(ignoreUnknown = true)
public class GeolocationResult {
    @JsonProperty("lat")
    private double latitude;
    @JsonProperty("lon")
    private double longitude;

    public GeolocationResult() {

    }


    @JsonCreator
    public GeolocationResult(@JsonProperty("lat") double latitude,@JsonProperty("lon") double longitude) {
         this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String latitudeToString() {
        return Double.toString(latitude);
    }

    public String longitudeToString() {
        return Double.toString(longitude);
    }

    public String toString() {
        return latitude + "," + longitude;
    }
}
