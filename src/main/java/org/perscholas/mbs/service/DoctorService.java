package org.perscholas.mbs.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.perscholas.mbs.dao.DoctorRepoI;
import org.perscholas.mbs.dao.PatientRepoI;
import org.perscholas.mbs.dto.DoctorDTO;
import org.perscholas.mbs.dto.OfficeDTO;
import org.perscholas.mbs.models.Doctor;
import org.perscholas.mbs.models.Office;
import org.perscholas.mbs.models.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DoctorService {

    DoctorRepoI doctorRepoI;

    @Autowired
    public DoctorService(DoctorRepoI doctorRepoI) {
        this.doctorRepoI = doctorRepoI;
    }

    @Transactional(rollbackOn = Exception.class)
    public List<Doctor> getAllDoctors() throws Exception {
        List<Doctor> doctors = doctorRepoI.findAll();

        if(doctors.isEmpty()) {
            log.debug("Empty list of Patients!!");
            throw new Exception("Empty List!");
        }

        return doctors;
    }

    public List<Doctor> specialtyDoctorsInOffice(Office office, String selectedSpecialty) {

        List<Doctor> specialtyDoctorsInOffice = new ArrayList<>();
        Set<Doctor> doctorsInOfficeSet = office.getDoctors();

        for (Doctor doctor : doctorsInOfficeSet) {
            if(doctor.getSpecialties().contains(selectedSpecialty)) {
                System.out.println("Doctor: " + doctor + " contains: " + selectedSpecialty);
                specialtyDoctorsInOffice.add(doctor);
            }
        }

        return specialtyDoctorsInOffice;
    }

    public List<Doctor> filterDoctorsWithSpecialty(List<Doctor> allDoctors, String selectedSpecialty){

        List<Doctor> specialtyDoctors = new ArrayList<>();

        for(int i = 0; i < allDoctors.toArray().length; i++) {

            if ( allDoctors.get(i).getSpecialties().contains(selectedSpecialty) ) {

                specialtyDoctors.add(allDoctors.get(i));

            }
        }

        return specialtyDoctors;
    }

    public List<DoctorDTO> getDoctorEssentialInfo() {

        return doctorRepoI.findAll().stream().map((oneDoctor) -> {
            return new DoctorDTO(oneDoctor.getId(), oneDoctor.getName());
        }).collect(Collectors.toList());
    }
}
