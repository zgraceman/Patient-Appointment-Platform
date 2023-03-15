package org.perscholas.mbs.dao;

import org.perscholas.mbs.models.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfficeRepoI extends JpaRepository<Office, Integer> {
    Optional<Office> findByName(String name);
}
