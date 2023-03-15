package org.perscholas.mbs.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.perscholas.mbs.dao.AppointmentRepoI;
import org.perscholas.mbs.dao.DoctorRepoI;
import org.perscholas.mbs.dao.OfficeRepoI;
import org.perscholas.mbs.dao.PatientRepoI;
import org.perscholas.mbs.models.Appointment;
import org.perscholas.mbs.models.Doctor;
import org.perscholas.mbs.models.Office;
import org.perscholas.mbs.models.Patient;
import org.perscholas.mbs.service.AppointmentService;
import org.perscholas.mbs.service.DoctorService;
import org.perscholas.mbs.service.OfficeService;
import org.perscholas.mbs.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Time;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
public class HomeController {

    private final PatientRepoI patientRepoI;
    private final DoctorRepoI doctorRepoI;
    private final OfficeRepoI officeRepoI;
    private final AppointmentRepoI appointmentRepoI;

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final OfficeService officeService;
    private final AppointmentService appointmentService;

    private String selectedSpecialty = "";
    private Office selectedOffice = new Office();
    private Doctor selectedDoctor = new Doctor();
    private Patient registeredPatient = new Patient();
    private Date selectedDate = new Date();
    private String selectedTime = null;
    private Appointment buildingAppointment = new Appointment();


    @Autowired
    public HomeController(DoctorRepoI doctorRepoI, OfficeRepoI officeRepoI, PatientRepoI patientRepoI, AppointmentRepoI appointmentRepoI,
                          PatientService patientService, DoctorService doctorService, OfficeService officeService, AppointmentService appointmentService) {
        this.doctorRepoI = doctorRepoI;
        this.officeRepoI = officeRepoI;
        this.patientRepoI = patientRepoI;
        this.appointmentRepoI = appointmentRepoI;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.officeService = officeService;
        this.appointmentService = appointmentService;
    }

    @GetMapping(value = {"/", "index"})
    public String homePage(Model model) {

        log.warn("I am in the index controller method");

        String specialty = "specialty placeholder";
        model.addAttribute("specialty", specialty);

        // Validation: Cannot "continue" unless a specialty is selected

        return "index";
    }

    @PostMapping("/post-index")
    public String indexProcess(@ModelAttribute("specialty") String specialty, Model model, RedirectAttributes redirectAttributes) {

        log.warn("I am in the indexProcess controller method");

        selectedSpecialty = specialty;

        if (selectedSpecialty.isEmpty()) {
            log.warn("Specialty is empty! Returning to index");
            redirectAttributes.addFlashAttribute("insertedDanger", "Please select a specialty!");
            return "redirect:index";
        }

        else {
            return "redirect:select-clinic";
        }
    }

    @GetMapping(value = "select-clinic")
    public String selectClinicPage(Model model, RedirectAttributes redirectAttributes) throws Exception {

        log.warn("I am in the select-clinic controller method");

        if (selectedSpecialty.isEmpty()) {
            log.warn("Specialty is empty! Returning to index");
            redirectAttributes.addFlashAttribute("insertedDanger", "Please select a specialty!");
            return "redirect:index";
        }

        List<Office> allOffices = officeService.getAllOffices();

        List<Doctor> specialtyDoctorsNorthdale = doctorService.specialtyDoctorsInOffice(allOffices.get(0), selectedSpecialty);
        List<Doctor> specialtyDoctorsEastview = doctorService.specialtyDoctorsInOffice(allOffices.get(1), selectedSpecialty);
        List<Doctor> specialtyDoctorsSouthtown = doctorService.specialtyDoctorsInOffice(allOffices.get(2), selectedSpecialty);
        List<Doctor> specialtyDoctorsWestshire = doctorService.specialtyDoctorsInOffice(allOffices.get(3), selectedSpecialty);

        model.addAttribute("infoSelectClinic", selectedSpecialty);
        model.addAttribute("specialtyDoctorsNorth", specialtyDoctorsNorthdale);
        model.addAttribute("specialtyDoctorsEast", specialtyDoctorsEastview);
        model.addAttribute("specialtyDoctorsSouth", specialtyDoctorsSouthtown);
        model.addAttribute("specialtyDoctorsWest", specialtyDoctorsWestshire);

        return "select-clinic";
    }

    @PostMapping("/post-select-clinic")
    public String selectClinicProcess(@RequestParam(name = "clinicChoice") String clinic, @RequestParam(name = "doctorChoice") String doctorName) {

        log.warn("I am in the selectClinicProcess controller method");

        selectedOffice = officeRepoI.findByName(clinic).get();
        selectedDoctor = doctorRepoI.findByName(doctorName).get();

        return "redirect:patient-registration";
    }

    @GetMapping(value = "patient-registration")
    public String patientRegistrationPage(Model model, RedirectAttributes redirectAttributes) {

        log.warn("I am in the patient-registration controller method");

        if (selectedSpecialty.isEmpty()) {
            log.warn("Specialty is empty! Returning to index");
            redirectAttributes.addFlashAttribute("insertedDanger", "Please select a specialty!");
            return "redirect:index";
        }

        if (selectedDoctor.getName() == null) {
            log.warn("Doctor is Empty! Returning to select-clinic");
            redirectAttributes.addFlashAttribute("insertedDangerClinic", "Please select a Clinic and Doctor!");
            return "redirect:select-clinic";
        }

        model.addAttribute("patient", new Patient());

        return "patient-registration";
    }

    @PostMapping("/post-patient-registration")
    public String patientProcess(@Valid @ModelAttribute("patient") Patient patient, BindingResult bindingResult, Model model) {

        log.warn("I am in the patientProcess controller method");

        if (bindingResult.hasErrors()) {
            log.debug(bindingResult.getAllErrors().toString());
            return "patient-registration";
        }

        patientRepoI.saveAndFlush(patient);
        log.warn(patient.toString());

        registeredPatient = patient;


        return "redirect:book-appointment";
    }

    @GetMapping(value = "book-appointment")
    public String bookAppointmentPage(Model model, RedirectAttributes redirectAttributes) throws Exception {

        log.warn("I am in the book-appointment controller method");

        if (selectedSpecialty.isEmpty()) {
            log.warn("Specialty is empty! Returning to index");
            redirectAttributes.addFlashAttribute("insertedDanger", "Please select a specialty!");
            return "redirect:index";
        }

        if (selectedDoctor.getName() == null) {
            log.warn("Doctor is Empty! Returning to select-clinic");
            redirectAttributes.addFlashAttribute("insertedDangerClinic", "Please select a Clinic and Doctor!");
            return "redirect:select-clinic";
        }

        if (registeredPatient.getFullName() == null) {
            log.warn("Patient Not Registered!");
            redirectAttributes.addFlashAttribute("insertedDangerPatient", "Please Complete Registration!");
            return "redirect:patient-registration";
        }

        String time = null;
        Date date = null;
        buildingAppointment = new Appointment(0, date, time, selectedSpecialty, selectedDoctor, selectedOffice, registeredPatient);

        model.addAttribute("appointment", buildingAppointment);

        return "book-appointment";
    }

    @PostMapping(value = "/post-book-appointment")
    public String bookAppointmentProcess(@Valid @ModelAttribute("appointment") Appointment builtAppointment, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        log.warn("I am in the bookAppointmentProcess controller method");

        if (bindingResult.hasErrors()) {
            log.debug(bindingResult.getAllErrors().toString());
            redirectAttributes.addFlashAttribute("appointment", buildingAppointment);
            redirectAttributes.addFlashAttribute("insertedDangerDateTime", "Please choose appointment Date & Time!");
            return "redirect:book-appointment";
        }

        Date d1 = builtAppointment.getAppointmentDate();
        Date d2 = new java.util.Date();

        if (d1.compareTo(d2) < 0) {
            log.debug("Selected date is before today's date!");
            redirectAttributes.addFlashAttribute("appointment", buildingAppointment);
            redirectAttributes.addFlashAttribute("insertedDangerWrongDate", "Your appointment cannot be scheduled on or before today's date!");
            return "redirect:book-appointment";
        }

        buildingAppointment = builtAppointment; // Appends newly added Date and Time to appointment
        selectedDate = builtAppointment.getAppointmentDate();
        selectedTime = builtAppointment.getAppointmentTime();

        return "redirect:appointment-confirmation";
    }

    @GetMapping(value = "appointment-confirmation")
    public String appointmentConfirmationPage(Model model, RedirectAttributes redirectAttributes) throws Exception {

        log.warn("I am in the appointment-confirmation controller method");

        if (selectedSpecialty.isEmpty()) {
            log.warn("Specialty is empty! Returning to index");
            redirectAttributes.addFlashAttribute("insertedDanger", "Please select a specialty!");
            return "redirect:index";
        }

        if (selectedDoctor.getName() == null) {
            log.warn("Doctor is Empty! Returning to select-clinic");
            redirectAttributes.addFlashAttribute("insertedDangerClinic", "Please select a Clinic and Doctor!");
            return "redirect:select-clinic";
        }

        if (registeredPatient.getFullName() == null) {
            log.warn("Patient Not Registered!");
            redirectAttributes.addFlashAttribute("insertedDangerPatient", "Please Complete Registration!");
            return "redirect:patient-registration";
        }

        if (buildingAppointment.getAppointmentDate() == null) {
            log.warn("Appointment Date & Time not set!");
            redirectAttributes.addFlashAttribute("insertedDangerDateTime", "Please choose appointment Date & Time!");
            return "redirect:book-appointment";
        }

        if (buildingAppointment.getAppointmentTime().isBlank()) {
            log.warn("Appointment Date & Time not set!");
            redirectAttributes.addFlashAttribute("insertedDangerDateTime", "Please choose appointment Date & Time!");
            return "redirect:book-appointment";
        }

        Appointment finalAppointment = new Appointment(selectedDate, selectedTime, selectedSpecialty, selectedDoctor, selectedOffice, registeredPatient);
        appointmentRepoI.saveAndFlush(finalAppointment);

        model.addAttribute("appointment", finalAppointment);

        return "appointment-confirmation";
    }

    @PostMapping(value = "/post-appointment-confirmation")
    public String appointmentConfirmationProcess() {

        log.warn("I am in the appointmentConfirmationProcess controller method");

        return "/post-appointment-confirmation";
    }

    @GetMapping(value = "appointment-lookup")
    public String appointmentLookupPage(Model model) {

        log.warn("I am in the appointmentLookupPage controller method");

        Appointment tempAppointment = new Appointment();
        System.out.println(tempAppointment);

        Integer id = 0;

        model.addAttribute("id", id);

        return "appointment-lookup";
    }

    private Integer cancelID;
    @PostMapping(value = "/post-appointment-lookup")
    public String appointmentLookupProcess(@RequestParam(name = "12345", required = false) Integer id, RedirectAttributes redirectAttributes) throws Exception {

        log.warn("I am in the appointmentLookupProcess controller method");

        if(id == null) {
            log.warn("User did not enter any value!");
            redirectAttributes.addFlashAttribute("noValueDanger", "Please enter a value before searching!");
            return "redirect:appointment-lookup";
        }

        System.out.println(id);

        List<Appointment> appointments = appointmentService.getAllAppointments();
        List<Integer> validIDs = new ArrayList<>();

        for (Appointment appointment : appointments) {
            validIDs.add(appointment.getId());
        }

        if (!validIDs.contains(id)) {
            log.warn("No Such Appointment Exists!");
            redirectAttributes.addFlashAttribute("noAppointmentDanger", "Sorry, no Appointment exists by this ID.");
            return "redirect:appointment-lookup";
        }

        Appointment flashAppointment = appointmentRepoI.findById(id).get();
        redirectAttributes.addFlashAttribute("appointment", flashAppointment);

        cancelID = id;

        return "redirect:appointment-lookup";
    }

    @PostMapping(value = "/post-appointment-cancellation")
    public String appointmentCancellationProcess() {

        log.warn("I am in the appointmentCancellationProcess controller method");

        System.out.println(cancelID);

        appointmentRepoI.deleteById(cancelID);

        return "appointment-lookup";
    }

}
