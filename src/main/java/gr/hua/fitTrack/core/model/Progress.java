package gr.hua.fitTrack.core.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "progress"
)
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_client",nullable = false)
    private Person client;

    @CreationTimestamp
    @Column(name = "entry_date", nullable = false, updatable = false)
    private Instant entryDate;

    @Column(name = "weight",nullable = false)
    private float weight;

    @Column(name = "running_time")
    private int runningTimeSeconds;

    @Column(name = "body_fat_percentage", nullable = false)
    private int bodyFatPercentage;

    @Column(name = "water_intake",nullable = false)
    private float waterIntake;

    @Column(name = "trainer_notes")
    private String trainerNotes;

    public Progress(){

    }

    public Progress( Person client, float weight, Instant entryDate, int runningTimeSeconds, int bodyFatPercentage,float waterIntake,String trainerNotes) {
        this.trainerNotes = trainerNotes;
        this.waterIntake = waterIntake;
        this.client = client;
        this.weight = weight;
        this.entryDate = entryDate;
        this.runningTimeSeconds = runningTimeSeconds;
        this.bodyFatPercentage = bodyFatPercentage;
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

    public Instant getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Instant entryDate) {
        this.entryDate = entryDate;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getRunningTimeSeconds() {
        return runningTimeSeconds;
    }

    public void setRunningTimeSeconds(int runningTimeSeconds) {
        this.runningTimeSeconds = runningTimeSeconds;
    }

    public int getBodyFatPercentage() {
        return bodyFatPercentage;
    }

    public void setBodyFatPercentage(int bodyFatPercentage) {
        this.bodyFatPercentage = bodyFatPercentage;
    }

    public float getWaterIntake() {
        return waterIntake;
    }

    public void setWaterIntake(float waterIntake) {
        this.waterIntake = waterIntake;
    }

    public String getTrainerNotes() {
        return trainerNotes;
    }

    public void setTrainerNotes(String trainerNotes) {
        this.trainerNotes = trainerNotes;
    }
}
