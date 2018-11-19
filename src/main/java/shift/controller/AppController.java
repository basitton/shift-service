package shift.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @RequestMapping("/")
    public String index() {
        return "Welcome to the shift service. " +
                "Please check the README to find out all the cool features this API can do!";
    }

    @RequestMapping("/app/help")
    public String help() {
        return "Visit https://help.wheniwork.com/ for help!";
    }

    @RequestMapping("/app/info")
    public String info() {
        return "You may sign up by reading the 'Sign Up' section below!" +
                "Also, you may visit https://help.wheniwork.com/article-categories/getting-started/ to officially get started!";
    }
}
