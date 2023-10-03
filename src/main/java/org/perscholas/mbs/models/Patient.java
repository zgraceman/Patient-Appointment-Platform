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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents a patient in the appointment platform.
 *
 * This entity contains details about a mock patient, including personal
 * information such as name, date of birth, gender, contact details,
 * and secure information like password and SSN.
 *
 * The password field is hashed using BCrypt for security reasons, and
 * standard validation rules are applied on email and phone number fields.
 * The class utilizes Lombok for generating boilerplate code and JPA annotations
 * for persistence.
 */
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
    int ssn; // Change to string, doesn't actually use ssn

    @Setter(AccessLevel.NONE)
    String password;

    public String setPassword(String password) {  // Return void, not string,
       return this.password = new BCryptPasswordEncoder().encode(password);  // move to service layer or utility
    }

    public Patient(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    public Patient(@NonNull String fullName, @NonNull @DateTimeFormat(pattern = "yyyy-MM-dd") Date dob, @NonNull String gender, @NonNull String email, @NonNull String phoneNumber, @NonNull int ssn, String password){
        this.fullName = fullName;
        this.dob = dob;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.ssn = ssn;
        this.password = setPassword(password);
    }


}
