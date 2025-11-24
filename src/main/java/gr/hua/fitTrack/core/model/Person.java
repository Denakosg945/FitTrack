package gr.hua.fitTrack.core.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "person",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_person_email_address", columnNames = "email_address"),
                @UniqueConstraint(name = "uk_person_phone_number", columnNames = "phone_number")
        },
        indexes = {
            @Index(name = "idx_person_type", columnList = "type"),
            @Index(name = "idx_person_last_name", columnList = "last_name")
        }
)


public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "age", nullable = false, length = 3)
    private int age;

    @Column(name = "email_address",nullable = false, length = 100)
    private String emailAddress;

    @Column(name = "phone_number", nullable = false, length = 18)
    private String phoneNumber; // E164 format

    @Column(name = "password_hash",nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private PersonType type;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> clientAppointmentList = new ArrayList<>();

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> trainerAppointmentList = new ArrayList<>();

    @OneToMany(mappedBy = "trainer",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrainerScheduleSlot> trainerScheduleSlots = new ArrayList<>();

    @OneToMany(mappedBy = "client",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Progress> progressList = new ArrayList<>();

    @OneToOne(mappedBy = "client",cascade = CascadeType.ALL, orphanRemoval = true)
    private Goals goal;


    public Person() {

    }

    public Person(String firstName, String lastName, int age, String emailAddress, String phoneNumber, String passwordHash, PersonType type, Instant createdAt) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
        this.type = type;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public PersonType getType() {
        return type;
    }

    public void setType(PersonType type) {
        this.type = type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<Appointment> getClientAppointmentList() {
        return clientAppointmentList;
    }

    public void setClientAppointmentList(List<Appointment> clientAppointmentList) {
        this.clientAppointmentList = clientAppointmentList;
    }

    public List<Appointment> getTrainerAppointmentList() {
        return trainerAppointmentList;
    }

    public void setTrainerAppointmentList(List<Appointment> trainerAppointmentList) {
        this.trainerAppointmentList = trainerAppointmentList;
    }

    public List<Progress> getProgressList() {
        return progressList;
    }

    public void setProgressList(List<Progress> progressList) {
        this.progressList = progressList;
    }

    public List<TrainerScheduleSlot> getTrainerScheduleSlots() {
        return trainerScheduleSlots;
    }

    public void setTrainerScheduleSlots(List<TrainerScheduleSlot> trainerScheduleSlots) {
        this.trainerScheduleSlots = trainerScheduleSlots;
    }

    public Goals getGoal() {
        return goal;
    }

    public void setGoal(Goals goal) {
        this.goal = goal;
    }

    @Override
    public String toString(){
        return "Person{id = " + this.id + "\nType = " + this.type + "\nCreated At = " + this.createdAt + "}";
    }
}
