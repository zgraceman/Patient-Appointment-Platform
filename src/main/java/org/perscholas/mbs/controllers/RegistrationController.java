package org.perscholas.mbs.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.perscholas.mbs.dao.PatientRepoI;
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

@Controller
@Slf4j
public class RegistrationController {

    private final PatientRepoI patientRepoI;
    private final PatientService patientService;


    //private HttpSession session;
    //private String selectedSpecialty = (String) session.getAttribute("specialty");

    @Autowired
    public RegistrationController(PatientRepoI patientRepoI, PatientService patientService) {
        this.patientRepoI = patientRepoI;
        this.patientService = patientService;
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
    public String patientRegistrationPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {

        log.warn("I am in the patient-registration controller method");
        String selectedSpecialty = (String) session.getAttribute("specialty");

        System.out.println("selectedSpecialty: " + selectedSpecialty);

        if (selectedSpecialty == null) {
            log.warn("Specialty is empty! Returning to index");
            redirectAttributes.addFlashAttribute("insertedDanger", "Please select a specialty!");
            return "redirect:index";
        }

        /*
        if (selectedDoctor.getName() == null) {
            log.warn("Doctor is Empty! Returning to select-clinic");
            redirectAttributes.addFlashAttribute("insertedDangerClinic", "Please select a Clinic and Doctor!");
            return "redirect:select-clinic";
        }*/

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
    public String patientProcess(@Valid @ModelAttribute("patient") Patient patient, BindingResult bindingResult, HttpSession session, Model model) {

        log.warn("I am in the patientProcess controller method");

        if (bindingResult.hasErrors()) {
            log.debug(bindingResult.getAllErrors().toString());
            return "patient-registration";
        }

        patientRepoI.saveAndFlush(patient);
        log.warn(patient.toString());

        session.setAttribute("registeredPatient", patient);

        return "redirect:book-appointment";
    }
}
