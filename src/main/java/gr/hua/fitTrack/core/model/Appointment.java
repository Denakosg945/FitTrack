package gr.hua.fitTrack.core.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "appointment"
)
//TODO CHANGE

public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    //TODO CHANGE TO CLIENTPROFILE
    @ManyToOne
    @JoinColumn(name = "fk_client",nullable = false)
    private ClientProfile client;

    @ManyToOne
    @JoinColumn(name = "fk_trainer", nullable = false)
    private TrainerProfile trainer;

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

    public Appointment(ClientProfile client, TrainerProfile trainer,TrainerScheduleSlot trainerScheduleSlot, String status, boolean isOutdoor,String notes) {
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
