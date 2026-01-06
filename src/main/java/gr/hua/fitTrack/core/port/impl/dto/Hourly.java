package gr.hua.fitTrack.core.port.impl.dto;

import java.util.List;

public record Hourly(List<String> time,
                     List<Double> temperature_2m,
                     List<Double> uv_index,
                     List<Integer> relative_humidity_2m,
                     List<Double> dew_point_2m,
                     List<Double> apparent_temperature,
                     List<Double> wind_speed_10m,
                     List<Integer> wind_direction_10m,
                     List<Integer> precipitation_probability,
                     List<Double> precipitation) {
}
