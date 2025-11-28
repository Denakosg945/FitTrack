package gr.hua.fitTrack.core.service.model;


import gr.hua.fitTrack.core.model.Weekday;

import java.util.Map;

public record CreateTrainerRequest(
        Long personId,
        String location,
        String specialization,
        String Client_Notes,
        Map<Weekday, String> startTimes,
        Map<Weekday, String> endTimes)
 {
}
