package org.perscholas.mbs.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
public class LoginController {

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
}
