package org.perscholas.mbs.dao;

import org.perscholas.mbs.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepoI extends JpaRepository<Doctor, Integer> {
    Optional<Doctor> findByName(String name);
}