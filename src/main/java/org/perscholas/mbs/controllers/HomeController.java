/*
TODO:
1. Create separate controllers for separate functionalities: Split the HomeController into separate controllers
like LoginController, RegistrationController, and AppointmentController to improve manageability and readability.

2. Refactor repetitive logic into methods: Extract repetitive checks (like specialty, doctor, patient null/empty
checks) into separate methods to reduce code duplication and enhance readability.

3. Avoid using `System.out.println`: Use the existing logging framework SLF4J for logging instead. It provides more
options and is more appropriate in a professional setting.

4. Use Lombok annotations to reduce boilerplate code: Implement Lombok's `@Getter`, `@Setter`, `@AllArgsConstructor`,
`@NoArgsConstructor`, etc., to reduce boilerplate code in classes.

5. Use services for business logic: Implement business logic in the service layer and access repositories through
services from the controller, instead of accessing repositories directly in the controller. This helps to keep the
controller lean and focused on directing HTTP requests.

6. Use Spring's ResponseEntity: Use `ResponseEntity` for more control over HTTP responses, instead of returning a
string value for redirection.

*/

package org.perscholas.mbs.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
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

/**
 * HomeController is the main controller for the medical booking system web application.
 * This class handles web requests and responses, manages data flow and business logic.
 * It has fields to hold information throughout the flow of the application, including selected specialty, selected office,
 * selected doctor, registered patient, selected date and time, and building appointment.
 */
@Controller
@Slf4j
public class  HomeController {

    // Injecting the necessary services and repositories
    private final DoctorRepoI doctorRepoI;
    private final OfficeRepoI officeRepoI;
    private final AppointmentRepoI appointmentRepoI;

    private final DoctorService doctorService;
    private final OfficeService officeService;
    private final AppointmentService appointmentService;

    // Fields to hold information throughout the flow of the application
    private String selectedSpecialty = "";
    private Office selectedOffice = new Office();
    private Doctor selectedDoctor = new Doctor();
    private Patient registeredPatient = new Patient();
    private Date selectedDate = new Date();
    private String selectedTime = null;
    private Appointment buildingAppointment = new Appointment();

    /**
     * Constructor for HomeController. Initializes all repository interfaces and service classes via dependency injection.
     * Dependency injection allows the Spring framework to automatically manage the lifecycle of the dependencies.
     *
     * @param doctorRepoI The repository for handling database operations related to doctors.
     * @param officeRepoI The repository for handling database operations related to offices.
     * patientRepoI The repository for handling database operations related to patients.
     * @param appointmentRepoI The repository for handling database operations related to appointments.
     * patientService The service class encapsulating business logic related to patients.
     * @param doctorService The service class encapsulating business logic related to doctors.
     * @param officeService The service class encapsulating business logic related to offices.
     * @param appointmentService The service class encapsulating business logic related to appointments.
     */
    @Autowired
    public HomeController(DoctorRepoI doctorRepoI, OfficeRepoI officeRepoI, AppointmentRepoI appointmentRepoI,
                          DoctorService doctorService, OfficeService officeService, AppointmentService appointmentService) {
        this.doctorRepoI = doctorRepoI;
        this.officeRepoI = officeRepoI;
        this.appointmentRepoI = appointmentRepoI;
        this.doctorService = doctorService;
        this.officeService = officeService;
        this.appointmentService = appointmentService;
    }

    /**
     * Method to display home page.
     *
     * @param model The model object to hold attributes that are used in the view.
     * @return The name of the home page view to be rendered.
     */
    @GetMapping(value = {"/", "/index"})
    public String homePage(Model model) {

        log.warn("I am in the index controller method");

        model.addAttribute("specialty", "");

        // Validation: Cannot "continue" unless a specialty is selected

        return "index";
    }

    /**
     * Method to handle post request from index page.
     *
     * @param specialty The specialty selected by the user.
     * @param model The model object to hold attributes that are used in the view.
     * @param redirectAttributes Object for specifying attributes for redirect scenarios.
     * @return The path to redirect to based on whether a specialty was selected or not.
     */
    // ! is this whole method redundant?
    @PostMapping("/post-index")
    public String indexProcess(@ModelAttribute("specialty") String specialty, Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        log.warn("I am in the indexProcess controller method");
        System.out.println("specialty: " + specialty);
        System.out.println("selectedSpecialty: " + selectedSpecialty);

        session.setAttribute("specialty", specialty);
        selectedSpecialty = specialty;

        // Redirects back to the index page if no specialty is selected.
        if (selectedSpecialty.isEmpty()) {
            log.warn("Specialty is empty! (No specialty selected) Returning to index");
            redirectAttributes.addFlashAttribute("insertedDanger", "Please select a specialty!");
            return "redirect:index";
        }

        else {
            return "redirect:select-clinic";
        }
    }

    /**
     * GET handler for "/select-clinic".
     *
     * Method to prepare and display the clinic selection page. If no specialty is selected, redirects to index.
     * Collects and adds doctors per office to the model using the selected specialty.
     *
     * @param model Spring-provided Model for adding attributes to be accessed in the view.
     * @param redirectAttributes Used for session attributes after redirect, e.g., warning message.
     * @throws Exception If there's an error retrieving the list of all offices.
     * @return Redirect instruction or name of the view to render.
     */
    @GetMapping(value = "/select-clinic")
    public String selectClinicPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) throws Exception {

        log.warn("I am in the select-clinic controller method");
        System.out.println("specialty: " + session.getAttribute("specialty"));
        System.out.println("selectedSpecialty: " + selectedSpecialty);

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

    /**
     * HTTP POST handler for the "/post-select-clinic" endpoint.
     *
     * Method to process the clinic and doctor chosen by the user from the clinic selection page.
     * The chosen clinic and doctor are stored in `selectedOffice` and `selectedDoctor` respectively
     * for later use.
     *
     * @param clinic The name of the selected clinic sent as a request parameter.
     * @param doctorName The name of the selected doctor sent as a request parameter.
     * @return A redirect instruction to the patient registration page.
     */
    @PostMapping("/post-select-clinic")
    public String selectClinicProcess(@RequestParam(name = "clinicChoice") String clinic, @RequestParam(name = "doctorChoice") String doctorName, HttpSession session) {

        log.warn("I am in the selectClinicProcess controller method");

        log.info("Setting selectedOffice in HttpSession");
        // Getting the entire Office object by searching it by name
        session.setAttribute("selectedOffice", officeRepoI.findByName(clinic).get());

        log.info("Setting selectedDoctor in HttpSession");
        // Getting the entire Doctor Object by searching it by name
        session.setAttribute("selectedDoctor", doctorRepoI.findByName(doctorName).get());

        return "redirect:patient-registration";
    }
}