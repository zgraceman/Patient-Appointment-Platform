package org.perscholas.mbs.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.perscholas.mbs.dao.PatientRepoI;
import org.perscholas.mbs.models.Doctor;
import org.perscholas.mbs.models.Office;
import org.perscholas.mbs.models.Patient;
import org.perscholas.mbs.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * RegistrationController is a Spring MVC Controller that handles the patient registration process
 * within the MedcareBookingSystem. This process includes handling the selection of specialty, office,
 * and doctor, and managing patient data before and during the registration.
 *
 * Internally, the controller uses a PatientRepoI for interacting with the database, and a PatientService
 * for handling business rules related to patients. It maintains the state of the selected specialty, office,
 * and doctor between requests using class-level fields.
 */
@Controller
@Slf4j
public class RegistrationController {

    // Fields to hold information throughout the flow of the application
    private String selectedSpecialty = null;
    private Office selectedOffice = new Office();
    private Doctor selectedDoctor = new Doctor();

    // Injecting the necessary services and repositories
    private final PatientRepoI patientRepoI;
    private final PatientService patientService;

    /**
     * Constructor for RegistrationController. Initializes all repository interfaces and service classes via dependency injection.
     * Dependency injection allows the Spring framework to automatically manage the lifecycle of the dependencies.
     *
     * @param patientRepoI The repository for handling database operations related to patients.
     * @param patientService The service class encapsulating business logic related to patients.
     */
    @Autowired
    public RegistrationController(PatientRepoI patientRepoI, PatientService patientService) {
        this.patientRepoI = patientRepoI;
        this.patientService = patientService;
    }

    /**
     * HTTP GET handler for the "/patient-registration" endpoint.
     *
     * This method prepares the data required to display the patient registration page. It validates the selected
     * specialty and doctor from the session. If either of these values are not set, it redirects the user back to the
     * appropriate page with a warning message. Additionally, it adds a new Patient object to the model for form binding.
     *
     * @param session The HttpSession object is used to retrieve the selected specialty and doctor.
     * @param model The Model object is automatically provided by Spring and can be used to add attributes
     *              to the model, which are then accessible in the view.
     * @param redirectAttributes The RedirectAttributes object is used to add attributes to the session that
     *                           can be used after a redirect. In this case, it's used to add warning messages
     *                           when either no specialty or no doctor has been selected.
     * @return A redirect instruction back to the index page if no specialty has been selected, or
     *         back to the select-clinic page if no doctor has been selected, or
     *         the name of the view to be rendered - "patient-registration".
     */
    @GetMapping(value = "patient-registration")
    public String patientRegistrationPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {

        log.warn("I am in the patient-registration controller method");
        selectedSpecialty = (String) session.getAttribute("selectedSpecialty");

        System.out.println("selectedSpecialty: " + selectedSpecialty);
        System.out.println("selectedDoctor: " + selectedDoctor);

        // TODO: Create method "isSpecialtyNull", call method, replacing following 5 lines of code. Probably use service layer?
        if (selectedSpecialty == null) {
            log.warn("Specialty is empty! Returning to index");
            redirectAttributes.addFlashAttribute("insertedDanger", "Please select a specialty!");
            return "redirect:index";
        }
        /* TODO: Define and fix selectedDoctor.getName() and selectedDoctor == null bug. then create method "isDoctorNull".
         *  User currently can navigate from select clinic to patient registration without making a selection.*/
        if (selectedDoctor == null) {
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
     * This method processes the form data submitted from the patient registration page. It validates the Patient object
     * and if there are errors, it returns back to the registration page. If the form data is valid, it saves the Patient
     * object in the database and sets it in the session as "registeredPatient". The user is then redirected to the
     * book-appointment page.
     *
     * @param patient A Patient object, annotated with @Valid and @ModelAttribute, which is populated with form data and validated.
     * @param bindingResult The BindingResult object that holds the result of the validation and binding and contains errors if any.
     * @param session The HttpSession object is used to store the registered patient.
     * @param model The Model object is automatically provided by Spring and can be used to add attributes
     *              to the model, which are then accessible in the view.
     * @return A redirect instruction to the book-appointment page if the form data is valid, or
     *         back to the patient-registration page if there are errors.
     */
    @PostMapping("/post-patient-registration")
    public String patientProcess(@Valid @ModelAttribute("patient") Patient patient, BindingResult bindingResult, HttpSession session, Model model) {

        log.warn("I am in the patientProcess controller method");

        if (bindingResult.hasErrors()) {
            log.debug(bindingResult.getAllErrors().toString());
            return "patient-registration";
        }

        patientRepoI.saveAndFlush(patient);
        System.out.println(patient.toString());

        // TODO: use database for registeredPatient instead of using HttpSession - maybe
        session.setAttribute("registeredPatient", patient);

        return "redirect:book-appointment";
    }
}
