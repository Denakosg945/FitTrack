package gr.hua.fitTrack.core.service.model;

public class EditGoalsForm {

    private float weightGoal;
    private int runningTimeGoal;
    private int bodyFatPercentageGoal;

    // getters / setters
    public float getWeightGoal() { return weightGoal; }
    public void setWeightGoal(float weightGoal) { this.weightGoal = weightGoal; }

    public int getRunningTimeGoal() { return runningTimeGoal; }
    public void setRunningTimeGoal(int runningTimeGoal) {
        this.runningTimeGoal = runningTimeGoal;
    }

    public int getBodyFatPercentageGoal() { return bodyFatPercentageGoal; }
    public void setBodyFatPercentageGoal(int bodyFatPercentageGoal) {
        this.bodyFatPercentageGoal = bodyFatPercentageGoal;
    }
}