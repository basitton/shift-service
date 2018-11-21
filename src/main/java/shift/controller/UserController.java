package shift.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shift.domain.h2.User.User;
import shift.service.User.UserService;

import java.util.List;

/**
 * Provides endpoints for managing users
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Gets all users in the database. Only managers can access this endpoint
     * @return a list of {@link User} that are in the database
     */
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
