package gr.hua.fitTrack.core.model;

import jakarta.persistence.*;

@Entity
@Table(name = "trainer_schedule_slot")
public class TrainerScheduleSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_trainer",nullable = false)
    private Person trainer;

    @Column(name = "start_date_time",nullable = false,length = 50)
    private String startDateTime;

    @Column(name = "end_date_time",nullable = false,length = 50)
    private String endDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false)
    private TrainerSlotAvailability status;

    @OneToOne(mappedBy = "trainerScheduleSlot")
    private Appointment appointment;

    public TrainerScheduleSlot(){

    }

    public TrainerScheduleSlot(Person trainer, String startDateTime, String endDateTime, TrainerSlotAvailability status) {
        this.trainer = trainer;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = status;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getTrainer() {
        return trainer;
    }

    public void setTrainer(Person trainer) {
        this.trainer = trainer;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public TrainerSlotAvailability getStatus() {
        return status;
    }

    public void setStatus(TrainerSlotAvailability status) {
        this.status = status;
    }
}
