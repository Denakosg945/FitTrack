package gr.hua.fitTrack.core.port.impl.dto;

import gr.hua.fitTrack.core.model.UvIndexClassification;

public record WeatherUsefulData (
        Double minTemp,
        Double maxTemp,
        Double avgTemp,
        Double minApparentTemp,
        Double maxApparentTemp,
        Double avgApparentTemp,
        Integer maxHumidity,
        Double avgHumidity,
        Double avgWindSpeed,
        Double maxSustainedWindSpeed,
        Integer precipitationProbability,
        Boolean isVisible,
        UvIndexClassification uvIndexClassification
){
}
