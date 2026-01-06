package gr.hua.fitTrack.core.service.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class TrainerOverrideRequest {

    private Long personId;
    private LocalDate date;
    private boolean available;
    private LocalTime startTime;
    private LocalTime endTime;

    public TrainerOverrideRequest() {}

    public TrainerOverrideRequest(
            Long personId,
            LocalDate date,
            boolean available,
            LocalTime startTime,
            LocalTime endTime
    ) {
        this.personId = personId;
        this.date = date;
        this.available = available;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // ===== GETTERS =====
    public Long getPersonId() {
        return personId;
    }

    public LocalDate getDate() {
        return date;
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

    // ===== SETTERS (ΑΥΤΑ ΕΛΕΙΠΑΝ) =====
    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
