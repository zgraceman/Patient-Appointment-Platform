package org.perscholas.mbs.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.perscholas.mbs.dao.PatientRepoI;
import org.perscholas.mbs.models.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
