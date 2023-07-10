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
    private final PatientRepoI patientRepoI;
    private final DoctorRepoI doctorRepoI;
    private final OfficeRepoI officeRepoI;
    private final AppointmentRepoI appointmentRepoI;

    private final PatientService patientService;
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
     * @param patientRepoI The repository for handling database operations related to patients.
     * @param appointmentRepoI The repository for handling database operations related to appointments.
     * @param patientService The service class encapsulating business logic related to patients.
     * @param doctorService The service class encapsulating business logic related to doctors.
     * @param officeService The service class encapsulating business logic related to offices.
     * @param appointmentService The service class encapsulating business logic related to appointments.
     */
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

    /**
     * Method to display login page.
     *
     * @return The name of the login page view to be rendered.
     */
    @GetMapping(value = "/login")
    public String loginPage() {

        log.warn("I am in the loginPage controller method");

        return "login-page";
    }

    /**
     * Method to handle login request.
     *
     * @return The path to redirect to after successful login.
     */
    @PostMapping("/post-login")
    public String loginProcess() {
        log.warn("I am in the post-login controller method");

        return "redirect:index";
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

        String specialty = "specialty placeholder";
        model.addAttribute("specialty", specialty);

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
    @PostMapping("/post-index")
    public String indexProcess(@ModelAttribute("specialty") String specialty, Model model, RedirectAttributes redirectAttributes) {

        log.warn("I am in the indexProcess controller method");

        selectedSpecialty = specialty;

        // Redirects back to the index page if no specialty is selected.
        if (selectedSpecialty.isEmpty()) {
            log.warn("Specialty is empty! Returning to index");
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
    public String selectClinicProcess(@RequestParam(name = "clinicChoice") String clinic, @RequestParam(name = "doctorChoice") String doctorName) {

        log.warn("I am in the selectClinicProcess controller method");

        selectedOffice = officeRepoI.findByName(clinic).get();
        selectedDoctor = doctorRepoI.findByName(doctorName).get();

        return "redirect:patient-registration";
    }

    /**
     * HTTP GET handler for the "patient-registration" endpoint.
     *
     * Method to display the patient registration page. Also adds a new Patient object to the model under the attribute
     * "patient", which is used in the view to bind form data.
     *
     * @param model The Model object is automatically provided by Spring and can be used to add attributes
     *              to the model, which are then accessible in the view.
     * @param redirectAttributes The RedirectAttributes object is used to add attributes to the session that
     *                           can be used after a redirect. In this case, it's used to add a warning message
     *                           when no specialty or doctor has been selected.
     * @return The name of the view to be rendered, or redirect instruction.
     */
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

    /**
     * HTTP POST handler for the "/post-patient-registration" endpoint.
     *
     * Method for processing the form data of a new patient. The Patient object is automatically populated with the form data and
     * validated. If there are validation errors, the user is returned to the patient registration page.
     *
     * If the Patient object is valid, it's saved to the database and also stored in the `registeredPatient`
     * instance variable for later use.
     *
     * @param patient A Patient object, annotated with @Valid to enable validation and @ModelAttribute to indicate
     *                that it should be populated with form data.
     * @param bindingResult The BindingResult object contains the results of the validation. It's automatically
     *                      populated by Spring when the method is called.
     * @param model The Model object is automatically provided by Spring and can be used to add attributes to the
     *              model, which are then accessible in the view.
     * @return The name of the view to be rendered, or a redirect instruction if the Patient object is valid.
     */
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

    /**
     * HTTP GET handler for the "/book-appointment" endpoint.
     *le for
     * Method responsible for rendering the page to book an appointment.
     * It checks that a specialty, doctor, and patient have been selected, redirecting the user with a warning
     * message to the appropriate page if any are missing.
     *
     * An Appointment object is instantiated with the selected specialty, doctor, and office, as well as the registered patient,
     * and added to the model under the attribute "appointment". This can be accessed in the view to display the
     * relevant data to the user.
     *
     * @param model The Model object is automatically provided by Spring and can be used to add attributes to
     *              the model, which are then accessible in the view.
     * @param redirectAttributes The RedirectAttributes object is used to add attributes to the session that can be used
     *                           after a redirect. In this case, it's used to add a warning message when no specialty, doctor or patient has been selected.
     * @return The name of the view to be rendered, or a redirect instruction if no specialty, doctor or patient has been selected.
     * @throws Exception Throws an Exception if there's an issue building the Appointment object.
     */
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

    /**
     * HTTP POST handler for the "/post-book-appointment" endpoint.
     *
     * This method is responsible for processing the appointment booking form.
     * If the BindingResult has errors, it redirects the user back to the booking page with the incomplete Appointment object
     * and a warning message.
     * It also checks that the selected date is not before the current date, redirecting with a warning if it is.
     *
     * The method updates the buildingAppointment object with the builtAppointment's date and time, and stores these in separate variables
     * for further use. Once all checks have passed, the user is redirected to the appointment confirmation page.
     *
     * @param builtAppointment The built Appointment object, constructed from the form data by Spring.
     * @param bindingResult The BindingResult object that contains the result of the validation and binding from the form.
     * @param redirectAttributes The RedirectAttributes object is used to add attributes to the session that can be used after a redirect.
     *                           In this case, it's used to add a warning message and the incomplete Appointment object when there are errors.
     * @return The redirect instruction to the next page in the flow, or back to the booking page if there are errors.
     */
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

    /**
     * HTTP GET handler for the "/appointment-confirmation" endpoint.
     *
     * This method is responsible for displaying the appointment confirmation page.
     * Prior to rendering the confirmation page, the method performs several checks
     * to ensure that a specialty, doctor, patient, and appointment date & time have been selected.
     * If any of these elements are missing, the user is redirected back to the appropriate page with a warning message.
     *
     * If all the necessary elements are present, a final Appointment object is created and saved to the database.
     * This final Appointment object is added to the model and passed to the view for confirmation.
     *
     * @param model The Model object is automatically provided by Spring and can be used to add attributes
     *              to the model, which are then accessible in the view.
     * @param redirectAttributes The RedirectAttributes object is used to add attributes to the session that can be used
     *                           after a redirect. In this case, it's used to add a warning message when any necessary element is missing.
     * @return The name of the view to be rendered, or a redirect instruction if any necessary element is missing.
     * @throws Exception Throws an Exception if there's an issue creating the Appointment object or saving it to the database.
     */
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

    /**
     * HTTP POST handler for the "/post-appointment-confirmation" endpoint.
     *
     * This method is responsible for processing the confirmation of an appointment.
     * Currently, it doesn't have any specific functionality or validation and simply redirects to the "/post-appointment-confirmation" endpoint.
     *
     * Future enhancements might include sending an email confirmation, updating appointment status in the database, or other related actions.
     *
     * @return The redirect instruction to the "/post-appointment-confirmation" endpoint.
     */
    @PostMapping(value = "/post-appointment-confirmation")
    public String appointmentConfirmationProcess() {

        log.warn("I am in the appointmentConfirmationProcess controller method");

        return "/post-appointment-confirmation";
    }

    /**
     * HTTP GET handler for the "/appointment-lookup" endpoint.
     *
     * This method is responsible for displaying the appointment lookup page.
     * It creates a temporary Appointment object for debugging purposes and prints it to the console.
     * It also adds a temporary "id" attribute to the model with a value of 0. This id could be used in the view
     * to display a form field for user input or for other purposes.
     *
     * @param model The Model object is automatically provided by Spring and can be used to add attributes
     *              to the model, which are then accessible in the view.
     * @return The name of the view to be rendered, in this case "appointment-lookup".
     */
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

    /**
     * HTTP POST handler for the "/post-appointment-lookup" endpoint.
     *
     * This method is responsible for processing the appointment lookup form submission.
     * It first checks if the input (id) is null, if so, it redirects back to the appointment lookup page with a warning message.
     *
     * It retrieves all the appointments and validates the entered id. If the id is not valid (i.e. there is no appointment with that id),
     * it redirects back to the appointment lookup page with an error message.
     *
     * If the id is valid, it fetches the corresponding appointment and adds it as a flash attribute to be used after the redirect.
     * It also stores the id in a private field, 'cancelID', for further processing.
     *
     * @param id The id of the appointment the user wants to lookup, obtained from the form submission.
     * @param redirectAttributes The RedirectAttributes object is used to add attributes to the session that can be used
     *                           after a redirect. In this case, it's used to add a warning message when no id is entered or if no appointment exists for the entered id.
     * @return A redirect instruction back to the appointment lookup page, with the relevant flash attributes added.
     * @throws Exception Throws an Exception if there's an issue getting the list of all appointments.
     */
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

    /**
     * HTTP POST handler for the "/post-appointment-cancellation" endpoint.
     *
     * This method is responsible for handling the cancellation of appointments.
     * It uses the 'cancelID' field, which should have been previously set by a successful call to the 'appointmentLookupProcess' method,
     * to identify which appointment to delete.
     *
     * @return The name of the view to be rendered after the appointment is cancelled, in this case the 'appointment-lookup' page.
     */
    @PostMapping(value = "/post-appointment-cancellation")
    public String appointmentCancellationProcess() {

        log.warn("I am in the appointmentCancellationProcess controller method");

        System.out.println(cancelID);

        appointmentRepoI.deleteById(cancelID);

        return "appointment-lookup";
    }
}