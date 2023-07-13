/*
TODO:
1. Create separate controllers for separate functionalities: Split the HomeController into separate controllers
like LoginController, RegistrationController, and AppointmentController to improve manageability and readability.
- Complete!

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
import lombok.extern.slf4j.Slf4j;
import org.perscholas.mbs.dao.DoctorRepoI;
import org.perscholas.mbs.dao.OfficeRepoI;
import org.perscholas.mbs.models.Doctor;
import org.perscholas.mbs.models.Office;
import org.perscholas.mbs.service.DoctorService;
import org.perscholas.mbs.service.OfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

/**
 * The HomeController class is a Spring MVC Controller that manages the flow of the application from the home page.
 * It guides users through the selection of a specialty, then the selection of a clinic and doctor that offer
 * the chosen specialty.
 *
 * This controller interacts with both the Doctor and Office domains and their respective repositories and services,
 * which encapsulate all the operations related to these domains.
 *
 * HomeController utilizes HttpSession for maintaining user's selections (like selected specialty, office, doctor)
 * across different requests. It also makes use of the Model and RedirectAttributes to send data to the views
 * and handle redirects.
 */
@Controller
@Slf4j
public class  HomeController {

    // Injecting the necessary services and repositories
    private final DoctorRepoI doctorRepoI;
    private final OfficeRepoI officeRepoI;

    private final DoctorService doctorService;
    private final OfficeService officeService;

    // Fields to hold information throughout the flow of the application
    private String selectedSpecialty = "";

    /**
     * Constructor for HomeController. Initializes all repository interfaces and service classes via dependency injection.
     * Dependency injection allows the Spring framework to automatically manage the lifecycle of the dependencies.
     *
     * @param doctorRepoI The repository for handling database operations related to doctors.
     * @param officeRepoI The repository for handling database operations related to offices.
     * @param doctorService The service class encapsulating business logic related to doctors.
     * @param officeService The service class encapsulating business logic related to offices.
     */
    @Autowired
    public HomeController(DoctorRepoI doctorRepoI, OfficeRepoI officeRepoI, DoctorService doctorService, OfficeService officeService) {
        this.doctorRepoI = doctorRepoI;
        this.officeRepoI = officeRepoI;
        this.doctorService = doctorService;
        this.officeService = officeService;
    }

    /**
     * HTTP GET handler for the "/" and "/index" endpoints.
     *
     * This method is used to display the home page. It adds an attribute "specialty" to the model with an initial
     * empty string value. This attribute is used in the view to bind form data.
     *
     * @param model The Model object is automatically provided by Spring and can be used to add attributes
     *              to the model, which are then accessible in the view.
     * @return The name of the view to be rendered - "index".
     */
    @GetMapping(value = {"/", "/index"})
    public String homePage(Model model) {

        log.warn("I am in the index controller method");

        model.addAttribute("specialty", "");

        // Validation: Cannot "continue" unless a specialty is selected

        return "index";
    }

    /**
     * HTTP POST handler for the "/post-index" endpoint.
     *
     * This method is used to process the selected specialty. It retrieves the selected specialty from the
     * form data and stores it in the session and in the `selectedSpecialty` instance variable. If no specialty
     * is selected, it redirects the user back to the index page with a warning message.
     *
     * @param specialty A String representing the selected specialty, annotated with @ModelAttribute to indicate
     *                  that it should be populated with form data.
     * @param model The Model object is automatically provided by Spring and can be used to add attributes
     *              to the model, which are then accessible in the view.
     * @param session The HttpSession object is used to store the selected specialty.
     * @param redirectAttributes The RedirectAttributes object is used to add attributes to the session that
     *                           can be used after a redirect. In this case, it's used to add a warning message
     *                           when no specialty has been selected.
     * @return A redirect instruction, either back to the index page if no specialty has been selected, or
     *         to the select-clinic page if a specialty was selected.
     */
    @PostMapping("/post-index")
    public String indexProcess(@ModelAttribute("specialty") String specialty, Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        log.warn("I am in the indexProcess controller method");
        // Debug
        System.out.println("specialty: " + specialty);
        System.out.println("selectedSpecialty: " + selectedSpecialty);

        session.setAttribute("selectedSpecialty", specialty);
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
     * HTTP GET handler for the "/select-clinic" endpoint.
     *
     * This method prepares data required to display the clinic selection page. It validates the selected
     * specialty and retrieves doctors belonging to the chosen specialty from all offices.
     * If the specialty is not selected, it redirects the user back to the index page with a warning message.
     *
     * @param session The HttpSession object is used to retrieve the selected specialty.
     * @param model The Model object is automatically provided by Spring and can be used to add attributes
     *              to the model, which are then accessible in the view.
     * @param redirectAttributes The RedirectAttributes object is used to add attributes to the session that
     *                           can be used after a redirect. In this case, it's used to add a warning message
     *                           when no specialty has been selected.
     * @return A redirect instruction back to the index page if no specialty has been selected, or
     *         the name of the view to be rendered - "select-clinic".
     * @throws Exception if an error occurs during execution.
     */
    @GetMapping(value = "/select-clinic")
    public String selectClinicPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) throws Exception {

        log.warn("I am in the select-clinic controller method");
        // Debug
        System.out.println("specialty: " + session.getAttribute("specialty"));
        System.out.println("selectedSpecialty: " + selectedSpecialty);

        // TODO: Create method "isSpecialtyNull", call method, replacing following 5 lines of code. Probably use service layer?
        if (selectedSpecialty.isEmpty()) {
            log.warn("Specialty is empty! Returning to index");
            redirectAttributes.addFlashAttribute("insertedDanger", "Please select a specialty!");
            return "redirect:index";
        }

        /* TODO: Fix query redirect problem.
         * Everytime this page gets redirected to, it re-queries everything below. This occurs when the user is using
         * the select-clinic page and redirects to a further page without first selecting a clinic & doctor. This is
         * inefficient and could cause possible memory leakages. Consider different ways to fix this, such as saving the
         * query results and retrieving them after every redirect instead of rerunning the queries. */

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
     * This method processes the selected clinic and doctor. It retrieves these values from the form data
     * and stores them in the session.
     *
     * @param clinic A String representing the selected clinic, annotated with @RequestParam to indicate
     *               that it should be populated with form data.
     * @param doctorName A String representing the selected doctor, annotated with @RequestParam to indicate
     *                   that it should be populated with form data.
     * @param session The HttpSession object is used to store the selected clinic and doctor.
     * @return A redirect instruction to the patient-registration page.
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