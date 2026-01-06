package gr.hua.fitTrack.core.model;

import java.time.LocalDate;

public enum Weekday {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    public static Weekday from(LocalDate date) {
        return Weekday.valueOf(date.getDayOfWeek().name());
    }

    public String getDisplayName() {
        return this.name().substring(0, 1)
                + this.name().substring(1).toLowerCase();
    }
}
