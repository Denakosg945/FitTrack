package gr.hua.fitTrack.core.port.impl.dto;

import java.util.Map;

public record WeatherResponse(double latitude,
                              double longitude,
                              double generationtime_ms,
                              int utc_offset_seconds,
                              String timezone,
                              String timezone_abbreviation,
                              double elevation,
                              Map<String, String> hourly_units,
                              Hourly hourly) {
}
