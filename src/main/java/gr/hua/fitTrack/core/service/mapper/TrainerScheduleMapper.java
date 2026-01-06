package gr.hua.fitTrack.core.service.mapper;

import gr.hua.fitTrack.core.model.TrainerProfile;
import gr.hua.fitTrack.core.model.TrainerWeeklyAvailability;
import gr.hua.fitTrack.core.model.Weekday;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrainerScheduleMapper {

    private TrainerScheduleMapper() {
        // utility class
    }

    public static List<TrainerWeeklyAvailability> mapWeeklyAvailability(
            Map<Weekday, String> startTimes,
            Map<Weekday, String> endTimes,
            TrainerProfile trainerProfile
    ) {

        List<TrainerWeeklyAvailability> result = new ArrayList<>();

        for (Weekday day : Weekday.values()) {

            String start = startTimes.get(day);
            String end = endTimes.get(day);

            // skip ημέρες χωρίς πλήρες ωράριο
            if (start == null || end == null || start.isBlank() || end.isBlank()) {
                continue;
            }

            result.add(new TrainerWeeklyAvailability(
                    null,
                    trainerProfile,
                    day,
                    LocalTime.parse(start),
                    LocalTime.parse(end)
            ));
        }

        return result;
    }
}
