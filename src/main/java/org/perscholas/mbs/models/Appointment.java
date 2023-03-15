package org.perscholas.mbs.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Entity
@Table(name = "Appointments")
@Slf4j
@NoArgsConstructor
@Setter
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Appointment {

    @Id
    @NonNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Please enter appointment Date")
    Date appointmentDate;  // validate so appointment is at/after current date.

    @NotNull(message = "Please enter an Appointment Time")
    String appointmentTime;

    String appointmentSpecialty;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    @NonNull
    Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "office_id")
    @NonNull
    Office office;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    @NonNull
    Patient patient;

    public Appointment(@DateTimeFormat(pattern = "yyyy-MM-dd") Date appointmentDate, @NotNull(message = "Please enter an Appointment Time") String appointmentTime, String appointmentSpecialty, @NonNull Doctor doctor, @NonNull Office office, @NonNull Patient patient) {
        this.id = id;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.appointmentSpecialty = appointmentSpecialty;
        this.doctor = doctor;
        this.office = office;
        this.patient = patient;
    }

    public Appointment(@NonNull int id, @DateTimeFormat(pattern = "yyyy-MM-dd") Date appointmentDate, @NotNull(message = "Please enter an Appointment Time") String appointmentTime, String appointmentSpecialty, @NonNull Doctor doctor, @NonNull Office office, @NonNull Patient patient) {
        this.id = id;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.appointmentSpecialty = appointmentSpecialty;
        this.doctor = doctor;
        this.office = office;
        this.patient = patient;
    }
}
