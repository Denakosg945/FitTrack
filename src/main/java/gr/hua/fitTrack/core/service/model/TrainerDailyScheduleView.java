package gr.hua.fitTrack.core.service.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class TrainerDailyScheduleView {

    private LocalDate date;
    private String weekdayLabel;   // π.χ. "Monday"
    private boolean available;

    private LocalTime startTime;   // null αν not available
    private LocalTime endTime;     // null αν not available

    private boolean overridden;    // true αν προέρχεται από override

    public TrainerDailyScheduleView(
            LocalDate date,
            String weekdayLabel,
            boolean available,
            LocalTime startTime,
            LocalTime endTime,
            boolean overridden
    ) {
        this.date = date;
        this.weekdayLabel = weekdayLabel;
        this.available = available;
        this.startTime = startTime;
        this.endTime = endTime;
        this.overridden = overridden;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getWeekdayLabel() {
        return weekdayLabel;
    }

    public boolean isAvailable() {
        return available;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public boolean isOverridden() {
        return overridden;
    }
}
