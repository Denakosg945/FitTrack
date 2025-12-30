package gr.hua.fitTrack.core.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "trainer_override_availability")
public class TrainerOverrideAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    private TrainerProfile trainerProfile;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    //TRUE: custom availability, hours changed
    //FALSE: trainer is NOT available
    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    public TrainerOverrideAvailability() {}

    public TrainerOverrideAvailability(LocalTime endTime,
                                       LocalTime startTime,
                                       boolean isAvailable,
                                       LocalDate date,
                                       TrainerProfile trainer,
                                       Long id) {
        this.endTime = endTime;
        this.startTime = startTime;
        this.isAvailable = isAvailable;
        this.date = date;
        this.trainerProfile = trainer;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TrainerProfile getTrainer() {
        return trainerProfile;
    }

    public void setTrainer(TrainerProfile trainer) {
        this.trainerProfile = trainer;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
