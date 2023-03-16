package org.perscholas.mbs.dao;

import org.perscholas.mbs.models.Doctor;
import org.perscholas.mbs.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepoI extends JpaRepository<Patient, Integer> {
    Optional<Patient> findByEmailAllIgnoreCase(String email);
}