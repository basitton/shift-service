package shift.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShiftController {
    @RequestMapping("/")
    public String index() {
        return "Welcome to the shift service";
    }
}
