package gr.hua.fitTrack.core.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "trainer_client_notes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"trainer_id", "client_id"})
        }
)
public class TrainerClientNotes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "trainer_id", nullable = false)
    private TrainerProfile trainer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientProfile client;

    @Column(name = "notes", length = 2000)
    private String notes;

    protected TrainerClientNotes() {}

    public TrainerClientNotes(
            TrainerProfile trainer,
            ClientProfile client,
            String notes
    ) {
        this.trainer = trainer;
        this.client = client;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public TrainerProfile getTrainer() {
        return trainer;
    }

    public ClientProfile getClient() {
        return client;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
