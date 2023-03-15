package org.perscholas.mbs.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.perscholas.mbs.dao.PatientRepoI;
import org.perscholas.mbs.dto.PatientDTO;
import org.perscholas.mbs.models.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PatientService {

    PatientRepoI patientRepoI;

    @Autowired
    public PatientService(PatientRepoI patientRepoI) {
        this.patientRepoI = patientRepoI;
    }

    @Transactional(rollbackOn = Exception.class)
    public List<Patient> getAllPatients() throws Exception {
        List<Patient> patients = patientRepoI.findAll();

        if(patients.isEmpty()) {
            log.debug("Empty list of Patients!!");
            throw new Exception("Empty List!");
        }

        return patients;
    }

    public List<PatientDTO> getPatientEssentialInfo() {

        return patientRepoI.findAll().stream().map((onePatient) -> {
            return new PatientDTO(onePatient.getId(), onePatient.getFullName(), onePatient.getDob());
        }).collect(Collectors.toList());

    }
}
