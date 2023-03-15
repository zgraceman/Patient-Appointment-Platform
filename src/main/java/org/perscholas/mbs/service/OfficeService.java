package org.perscholas.mbs.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.perscholas.mbs.dao.DoctorRepoI;
import org.perscholas.mbs.dao.OfficeRepoI;
import org.perscholas.mbs.dto.OfficeDTO;
import org.perscholas.mbs.dto.PatientDTO;
import org.perscholas.mbs.models.Doctor;
import org.perscholas.mbs.models.Office;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OfficeService {

    OfficeRepoI officeRepoI;

    @Autowired
    public OfficeService(OfficeRepoI officeRepoI) {
        this.officeRepoI = officeRepoI;
    }

    @Transactional(rollbackOn = Exception.class)
    public List<Office> getAllOffices() throws Exception {
        List<Office> offices = officeRepoI.findAll();

        if(offices.isEmpty()) {
            log.debug("Empty list of Office!!");
            throw new Exception("Empty List!");
        }

        return offices;
    }


    public List<Office> filterOfficesWithSpecialty(List<Office> allOffices, List<Doctor> specialtyDoctors, String selectedSpecialty){

        List<Office> specialtyOffices = new ArrayList<>();

        for(int i = 0; i < allOffices.toArray().length; i++) {
            System.out.println("Office: " + allOffices.get(i).getId());
            System.out.println("Doctors in this office: " + allOffices.get(i).getDoctors());
            System.out.println("User Selected Specialty: " + selectedSpecialty);
            System.out.println("specialtyDoctors: " + specialtyDoctors);
            System.out.println("Similar: " + allOffices.get(i).getDoctors().stream().filter(specialtyDoctors::contains).collect(Collectors.toList()));

            if (allOffices.get(i).getDoctors().stream().filter(specialtyDoctors::contains).collect(Collectors.toList()).isEmpty()) {
                System.out.println("false, lists are not similar");
            }
            else {
                System.out.println("true, lists are similar");
                specialtyOffices.add(allOffices.get(i));
            }
            System.out.println();
        }

        /*
        for(int i = 0; i < allOffices.toArray().length; i++) {

            System.out.println(allOffices.get(i).getDoctors());
            Set<Doctor> officeDoctors = allOffices.get(i).getDoctors();

            for(int j = 0; j < specialtyDoctors.toArray().length; j++) {
                if (officeDoctors.contains(specialtyDoctors.get(j))) {

                    specialtyOffices.add(allOffices.get(i));
                    break;

                }
            }
        }*/

        return specialtyOffices;
    }

    public List<OfficeDTO> getOfficeEssentialInfo() {

        return officeRepoI.findAll().stream().map((oneOffice) -> {
            return new OfficeDTO(oneOffice.getId(), oneOffice.getName());
        }).collect(Collectors.toList());

    }


}
