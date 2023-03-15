package org.perscholas.mbs.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Doctors")
@Slf4j
@NoArgsConstructor
@Setter
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Doctor {

    @Id
    @NonNull
    int id;

    @NonNull
    String name;

    @NonNull
    String email;

    @NonNull
    List<String> specialties = new ArrayList<>();

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "Doctor_Offices",
            joinColumns = @JoinColumn(name = "Doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "Office_id"))
    Set<Office> offices = new LinkedHashSet<>();

    public Doctor(@NonNull int id, @NonNull String name, @NonNull String email, @NonNull List<String> specialties) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.specialties = specialties;
    }
}
