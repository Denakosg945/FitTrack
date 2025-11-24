package gr.hua.fitTrack.core.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "goals"
)
public class Goals {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "body_fat_percentage_goal",nullable = false)
    private int bodyFatPercentageGoal;

    @Column(name = "weight_goal",nullable = false)
    private float weightGoal;

    @Column(name = "running_time_goal",nullable = false)
    private int runningTimeGoal; // in seconds

    @OneToOne
    @JoinColumn(name = "fk_client",nullable = false)
    private Person client;

    public Goals() {
    }

    public Goals(float weightGoal, int bodyFatPercentageGoal, int runningTimeGoal, Person client) {
        this.weightGoal = weightGoal;
        this.bodyFatPercentageGoal = bodyFatPercentageGoal;
        this.runningTimeGoal = runningTimeGoal;
        this.client = client;
    }

    public Person getClient() {
        return client;
    }

    public void setClient(Person client) {
        this.client = client;
    }

    public int getRunningTimeGoal() {
        return runningTimeGoal;
    }

    public void setRunningTimeGoal(int runningTimeGoal) {
        this.runningTimeGoal = runningTimeGoal;
    }

    public float getWeightGoal() {
        return weightGoal;
    }

    public void setWeightGoal(float weightGoal) {
        this.weightGoal = weightGoal;
    }

    public int getBodyFatPercentageGoal() {
        return bodyFatPercentageGoal;
    }

    public void setBodyFatPercentageGoal(int bodyFatPercentageGoal) {
        this.bodyFatPercentageGoal = bodyFatPercentageGoal;
    }
}
