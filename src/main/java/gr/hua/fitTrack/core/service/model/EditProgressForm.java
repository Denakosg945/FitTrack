package gr.hua.fitTrack.core.service.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class EditProgressForm {

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private Double weight;

    @NotNull(message = "Running time is required")
    @Positive(message = "Running time must be positive")
    private Integer runningTimeSeconds;

    @NotNull(message = "Body fat percentage is required")
    @Positive(message = "Body fat percentage must be positive")
    private Double bodyFatPercentage;

    @NotNull(message = "Water intake is required")
    @Positive(message = "Water intake must be positive")
    private Double waterIntake;

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getRunningTimeSeconds() {
        return runningTimeSeconds;
    }

    public void setRunningTimeSeconds(Integer runningTimeSeconds) {
        this.runningTimeSeconds = runningTimeSeconds;
    }

    public Double getBodyFatPercentage() {
        return bodyFatPercentage;
    }

    public void setBodyFatPercentage(Double bodyFatPercentage) {
        this.bodyFatPercentage = bodyFatPercentage;
    }

    public Double getWaterIntake() {
        return waterIntake;
    }

    public void setWaterIntake(Double waterIntake) {
        this.waterIntake = waterIntake;
    }
}
