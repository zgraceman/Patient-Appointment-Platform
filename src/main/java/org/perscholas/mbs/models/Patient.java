package org.perscholas.mbs.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;


@Entity
@Table(name = "Patients")
@Slf4j
@NoArgsConstructor
@Setter
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Patient {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @NonNull
    @Size(min = 4, max = 30, message = "Please enter a name with length between 4-30 characters")
    String fullName;

    @NonNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date dob;

    @NonNull
    String gender;

    @NonNull
    @Email(message = "Please provide a valid email address", regexp = ".+@.+\\..+")
    String email;

    @NonNull
    @Size(min = 6, max = 10, message = "Please enter a length between 6-10 numbers")
    String phoneNumber;

    @NonNull
    int ssn;

    public Patient(@NonNull String fullName, @NonNull @DateTimeFormat(pattern = "yyyy-MM-dd") Date dob, @NonNull String gender, @NonNull String email, @NonNull String phoneNumber, @NonNull int ssn) {
        this.fullName = fullName;
        this.dob = dob;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.ssn = ssn;
    }
}
