package org.perscholas.mbs.controllers;

import lombok.extern.slf4j.Slf4j;
import org.perscholas.mbs.dao.PatientRepoI;
import org.perscholas.mbs.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final PatientRepoI patientRepoI;
    private final PatientService patientService;

    @Autowired
    public UserController(PatientRepoI patientRepoI, PatientService patientService) {
        this.patientRepoI = patientRepoI;
        this.patientService = patientService;
    }



}
