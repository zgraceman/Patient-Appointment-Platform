package org.perscholas.mbs.controllers;

import lombok.extern.slf4j.Slf4j;
import org.perscholas.mbs.dao.OfficeRepoI;
import org.perscholas.mbs.dao.PatientRepoI;
import org.perscholas.mbs.service.OfficeService;
import org.perscholas.mbs.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    PatientRepoI patientRepoI;
    OfficeRepoI officeRepoI;
    PatientService patientService;
    OfficeService officeService;

    @Autowired
    public AdminController(PatientRepoI patientRepoI, OfficeRepoI officeRepoI, PatientService patientService, OfficeService officeService) {
        this.patientRepoI = patientRepoI;
        this.officeRepoI = officeRepoI;
        this.patientService = patientService;
        this.officeService = officeService;
    }



}
