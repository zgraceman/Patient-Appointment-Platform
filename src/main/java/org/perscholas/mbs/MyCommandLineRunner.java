package org.perscholas.mbs;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.perscholas.mbs.dao.AppointmentRepoI;
import org.perscholas.mbs.dao.DoctorRepoI;
import org.perscholas.mbs.dao.OfficeRepoI;
import org.perscholas.mbs.dao.PatientRepoI;
import org.perscholas.mbs.models.Appointment;
import org.perscholas.mbs.models.Doctor;
import org.perscholas.mbs.models.Office;
import org.perscholas.mbs.models.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalTime;
import java.util.*;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MyCommandLineRunner implements CommandLineRunner {

    DoctorRepoI doctorRepoI;
    OfficeRepoI officeRepoI;
    PatientRepoI patientRepoI;
    AppointmentRepoI appointmentRepoI;

    @Autowired
    public MyCommandLineRunner(DoctorRepoI doctorRepoI, OfficeRepoI officeRepoI, PatientRepoI patientRepoI, AppointmentRepoI appointmentRepoI) {
        this.doctorRepoI = doctorRepoI;
        this.officeRepoI = officeRepoI;
        this.patientRepoI = patientRepoI;
        this.appointmentRepoI = appointmentRepoI;
    }

    @PostConstruct
    void created() {
        log.warn("==================== MyCommandLineRunner Got Created ====================");
    }

    @Override
    public void run(String... args) throws Exception {

        List<String> specialtiesD1 = new ArrayList<>(Arrays.asList("Cardiology", "Dermatology", "Endocrinology"));
        Doctor d1 = new Doctor(1, "Dr. Bill Nye", "Bill@gmail.com", specialtiesD1);
        doctorRepoI.saveAndFlush(d1);

        List<String> specialtiesD2 = new ArrayList<>(Arrays.asList("Family Medicine", "Gastroenterology", "Neurology"));
        Doctor d2 = new Doctor(2, "Dr. Janet Whitlock", "Janet@gmail.com", specialtiesD2);
        doctorRepoI.saveAndFlush(d2);

        List<String> specialtiesD3 = new ArrayList<>(Arrays.asList("Ophthalmology", "Pediatrics", "Podiatry"));
        Doctor d3 = new Doctor(3, "Dr. Phil McGraw", "Phil@gmail.com", specialtiesD3);
        doctorRepoI.saveAndFlush(d3);

        List<String> specialtiesD4 = new ArrayList<>(Arrays.asList("Dermatology", "Neurology"));
        Doctor d4 = new Doctor(4, "Dr. Gustavo Fring", "Gus@gmail.com", specialtiesD4);
        doctorRepoI.saveAndFlush(d4);

        List<String> specialtiesD5 = new ArrayList<>(Arrays.asList("Cardiology", "Endocrinology", "Family Medicine", "Gastroenterology", "Pediatrics", "Podiatry", "Sleep Medicine"));
        Doctor d5 = new Doctor(5, "Dr. Eduardo Gonzalez", "Eduardo@gmail.com", specialtiesD5);
        doctorRepoI.saveAndFlush(d5);

        List<String> specialtiesD6 = new ArrayList<>(Arrays.asList("Dermatology", "Neurology", "Ophthalmology", "Sleep Medicine"));
        Doctor d6 = new Doctor(6, "Dr. Aisha Amari", "Aisha@gmail.com", specialtiesD6);
        doctorRepoI.saveAndFlush(d6);

        List<String> specialtiesD7 = new ArrayList<>(Arrays.asList("Dermatology"));
        Doctor d7 = new Doctor(7, "Dr. Deborah Barlowe", "Deborah@gmail.com", specialtiesD7);
        doctorRepoI.saveAndFlush(d7);

        List<String> specialtiesD8 = new ArrayList<>(Arrays.asList("Dermatology", "Pediatrics", "Sleep Medicine"));
        Doctor d8 = new Doctor(8, "Dr. Jones Bones", "Jones@gmail.com", specialtiesD8);
        doctorRepoI.saveAndFlush(d8);



        Office o1 = new Office(1, "Northdale", "Wilson");
        o1.addDoctor(d1);
        o1.addDoctor(d2);
        officeRepoI.saveAndFlush(o1);

        Office o2 = new Office(2, "Eastview", "Melody");
        o2.addDoctor(d3);
        o2.addDoctor(d4);
        o2.addDoctor(d5);
        o2.addDoctor(d8);
        officeRepoI.saveAndFlush(o2);

        Office o3 = new Office(3, "Southtown", "Clair");
        o3.addDoctor(d6);
        o3.addDoctor(d7);
        officeRepoI.saveAndFlush(o3);

        Office o4 = new Office(4, "Westshire", "Brandy");
        o4.addDoctor(d1);
        officeRepoI.saveAndFlush(o4);





        Date dobP1 = new Date(1999, 8, 14);
        Patient p1 = new Patient("Zachary Graceman", dobP1, "male", "zgman@gmail.com", "6129106192", 123456789);
        patientRepoI.saveAndFlush(p1);

        Date appointmentDateA1 = new Date(2023, 4, 1);
        String time = "";
        Appointment a1 = new Appointment(1, appointmentDateA1, "Cardiology", time, d1, o1, p1);
        appointmentRepoI.saveAndFlush(a1);

        Appointment a2 = new Appointment(2, appointmentDateA1, "Cardiology", time, d2, o2, p1);
        appointmentRepoI.saveAndFlush(a2);



    }
}
