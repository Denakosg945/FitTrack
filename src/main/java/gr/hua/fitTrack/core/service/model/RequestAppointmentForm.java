package gr.hua.fitTrack.core.service.model;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class RequestAppointmentForm {

    private String trainerToken;
    // signed token
    @NotNull
    @FutureOrPresent(message = "Date cannot be in the past")
    private LocalDate date;
    private LocalTime startTime;
    private boolean outdoor;

    public String getTrainerToken() {
        return trainerToken;
    }

    public void setTrainerToken(String trainerToken) {
        this.trainerToken = trainerToken;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public boolean isOutdoor() {
        return outdoor;
    }

    public void setOutdoor(boolean outdoor) {
        this.outdoor = outdoor;
    }


}
