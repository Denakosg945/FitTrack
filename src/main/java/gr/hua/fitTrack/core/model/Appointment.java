package gr.hua.fitTrack.core.model;

import jakarta.persistence.*;

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
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_client", nullable = false)
    private ClientProfile client;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_trainer", nullable = false)
    private TrainerProfile trainer;

    @OneToOne(optional = false)
    @JoinColumn(
            name = "fk_trainer_schedule_slot",
            nullable = false,
            unique = true
    )
    private TrainerScheduleSlot trainerScheduleSlot;

    @Column(name = "status", nullable = false, length = 15)
    private String status;

    @Column(name = "is_outdoor", nullable = false)
    private boolean isOutdoor;

    @Column(name = "notes")
    private String notes;

    protected Appointment() {}

    public Appointment(
            ClientProfile client,
            TrainerProfile trainer,
            TrainerScheduleSlot trainerScheduleSlot,
            String status,
            boolean isOutdoor,
            String notes
    ) {
        this.client = client;
        this.trainer = trainer;
        this.trainerScheduleSlot = trainerScheduleSlot;
        this.status = status;
        this.isOutdoor = isOutdoor;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public ClientProfile getClient() {
        return client;
    }

    public TrainerProfile getTrainer() {
        return trainer;
    }

    public TrainerScheduleSlot getTrainerScheduleSlot() {
        return trainerScheduleSlot;
    }

    public String getStatus() {
        return status;
    }

    public boolean isOutdoor() {
        return isOutdoor;
    }

    public String getNotes() {
        return notes;
    }
}
