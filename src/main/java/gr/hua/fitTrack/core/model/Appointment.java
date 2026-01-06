package gr.hua.fitTrack.core.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "appointment_seq_gen"
    )
    @SequenceGenerator(
            name = "appointment_seq_gen",
            sequenceName = "appointment_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(optional = false)
    private ClientProfile client;

    @ManyToOne(optional = false)
    private TrainerProfile trainer;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    // 1 hour session (κανόνας)
    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private String status;

    private boolean isOutdoor;
    private String notes;

    protected Appointment() {}

    public Appointment(
            ClientProfile client,
            TrainerProfile trainer,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            String status,
            boolean isOutdoor,
            String notes
    ) {
        this.client = client;
        this.trainer = trainer;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.isOutdoor = isOutdoor;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClientProfile getClient() {
        return client;
    }

    public void setClient(ClientProfile client) {
        this.client = client;
    }

    public TrainerProfile getTrainer() {
        return trainer;
    }

    public void setTrainer(TrainerProfile trainer) {
        this.trainer = trainer;
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

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isOutdoor() {
        return isOutdoor;
    }

    public void setOutdoor(boolean outdoor) {
        isOutdoor = outdoor;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
