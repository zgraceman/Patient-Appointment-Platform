package org.perscholas.mbs.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.perscholas.mbs.dao.AppointmentRepoI;
import org.perscholas.mbs.models.Appointment;
import org.perscholas.mbs.models.Doctor;
import org.perscholas.mbs.models.Office;
import org.perscholas.mbs.models.Patient;
import org.perscholas.mbs.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@Slf4j
public class AppointmentController {

    // Fields to hold information throughout the flow of the application
    private String selectedSpecialty = "";
    private Office selectedOffice = new Office();
    private Doctor selectedDoctor = new Doctor();
    private Patient registeredPatient = new Patient();
    private Date selectedDate = new Date();
    private String selectedTime = null;
    private Appointment buildingAppointment = new Appointment();

    // Injecting the necessary services and repositories
    private final AppointmentRepoI appointmentRepoI;
    private final AppointmentService appointmentService;

    /**
     * Constructor for AppointmentController. Initializes all repository interfaces and service classes via dependency injection.
     * Dependency injection allows the Spring framework to automatically manage the lifecycle of the dependencies.
     *
     * @param appointmentRepoI The repository for handling database operations related to appointments.
     * @param appointmentService The service class encapsulating business logic related to appointments.
     */
    @Autowired
    public AppointmentController(AppointmentRepoI appointmentRepoI, AppointmentService appointmentService) {
        this.appointmentRepoI = appointmentRepoI;
        this.appointmentService = appointmentService;
    }

    @GetMapping(value = "book-appointment")
    public String bookAppointmentPage(Model model, HttpSession session, RedirectAttributes redirectAttributes) throws Exception {

        log.warn("I am in the book-appointment controller method");

        log.info("Retrieving selectedSpecialty from HttpSession. Casting returned value from Object to String");
        selectedSpecialty = (String) session.getAttribute("selectedSpecialty");
        System.out.println(selectedSpecialty);

        log.info("Retrieving selectedOffice from HttpSession. Casting returned value from Object to Office");
        selectedOffice = (Office) session.getAttribute("selectedOffice");

        log.info("Retrieving selectedDoctor from HttpSession. Casting returned value from Object to Doctor");
        selectedDoctor = (Doctor) session.getAttribute("selectedDoctor");

        log.info("Retrieving registeredPatient from HttpSession. Casting returned value from Object to Patient");
        registeredPatient = (Patient) session.getAttribute("registeredPatient");
        // TODO: retrieve registeredPatient from database instead of HttpSession - maybe

        // TODO: Create method "isSpecialtyNull", call method, replacing following 5 lines of code. Probably use service layer?
        if (selectedSpecialty == null) {
            log.warn("Specialty is empty! Returning to index");
            redirectAttributes.addFlashAttribute("insertedDanger", "Please select a specialty!");
            return "redirect:index";
        }

        // TODO: Define and fix selectedDoctor.getName() and selectedDoctor == null bug. then create method "isDoctorNull"
        if (selectedDoctor == null) {
            log.warn("Doctor is Empty! Returning to select-clinic");
            redirectAttributes.addFlashAttribute("insertedDangerClinic", "Please select a Clinic and Doctor!");
            return "redirect:select-clinic";
        }

        if (registeredPatient == null) {
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

        // TODO: Create method "isSpecialtyNull", call method, replacing following 5 lines of code. Probably use service layer?
        if (selectedSpecialty == null) {
            log.warn("Specialty is empty! Returning to index");
            redirectAttributes.addFlashAttribute("insertedDanger", "Please select a specialty!");
            return "redirect:index";
        }

        // TODO: Define and fix selectedDoctor.getName() and selectedDoctor == null bug. then create method "isDoctorNull"
        if (selectedDoctor == null) {
            log.warn("Doctor is Empty! Returning to select-clinic");
            redirectAttributes.addFlashAttribute("insertedDangerClinic", "Please select a Clinic and Doctor!");
            return "redirect:select-clinic";
        }

        if (registeredPatient == null) {
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
    // TODO: Implement verification so user's cannot see other patient's appointment details
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
    // TODO: Implement verification so user's cannot see other patient's appointment details
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
    // TODO: Prevent users from cancelling other patient's appointments
    @PostMapping(value = "/post-appointment-cancellation")
    public String appointmentCancellationProcess() {

        log.warn("I am in the appointmentCancellationProcess controller method");

        System.out.println(cancelID);

        appointmentRepoI.deleteById(cancelID);

        return "appointment-lookup";
    }

}
