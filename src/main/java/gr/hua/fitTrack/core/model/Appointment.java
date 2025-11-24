package gr.hua.fitTrack.core.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "appointment"
)

public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_client",nullable = false)
    private Person client;

    @ManyToOne
    @JoinColumn(name = "fk_trainer", nullable = false)
    private Person trainer;

    @OneToOne
    @JoinColumn(name = "fk_trainer_schedule_slot",nullable = false, unique = true)
    private TrainerScheduleSlot trainerScheduleSlot;

    @Column(name = "status", nullable = false, length = 15)
    private String status;

    @Column(name = "is_outdoor",nullable = false)
    private boolean isOutdoor;

    @Column(name = "notes")
    private String notes;


    public Appointment(){

    }

    public Appointment(Person client, Person trainer,TrainerScheduleSlot trainerScheduleSlot, String status, boolean isOutdoor,String notes) {
        this.notes = notes;
        this.trainerScheduleSlot = trainerScheduleSlot;
        this.client = client;
        this.trainer = trainer;
        this.status = status;
        this.isOutdoor = isOutdoor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getClient() {
        return client;
    }

    public void setClient(Person client) {
        this.client = client;
    }

    public Person getTrainer() {
        return trainer;
    }

    public void setTrainer(Person trainer) {
        this.trainer = trainer;
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

    public TrainerScheduleSlot getTrainerScheduleSlot() {
        return trainerScheduleSlot;
    }

    public void setTrainerScheduleSlot(TrainerScheduleSlot trainerScheduleSlot) {
        this.trainerScheduleSlot = trainerScheduleSlot;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
