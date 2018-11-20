package shift.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @RequestMapping("/")
    public String index() {
        return "Welcome to the shift service. " +
                "To use this app, please register.";
    }

    @PreAuthorize("hasAnyRole('ROLE_USER, ROLE_MANAGER, ROLE_EMPLOYEE')")
    @RequestMapping("/app/help")
    public String help() {
        return "Visit our official site for help becoming an employee! (or read README)";
    }

    @PreAuthorize("hasAnyRole('ROLE_USER, ROLE_MANAGER, ROLE_EMPLOYEE')")
    @RequestMapping("/app/info")
    public String info() {
        return "You may sign up by reading the 'Sign Up' section below!";
    }
}
