package org.perscholas.mbs.controllers;

import lombok.extern.slf4j.Slf4j;
import org.perscholas.mbs.dao.AppointmentRepoI;
import org.perscholas.mbs.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class AppointmentController {

    // Injecting the necessary services and repositories
    private final AppointmentRepoI appointmentRepoI;
    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentRepoI appointmentRepoI, AppointmentService appointmentService) {
        this.appointmentRepoI = appointmentRepoI;
        this.appointmentService = appointmentService;
    }
}
