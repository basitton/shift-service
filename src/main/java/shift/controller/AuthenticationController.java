package shift.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import shift.domain.h2.Role.Role;
import shift.domain.h2.User.User;
import shift.domain.security.AuthToken;
import shift.domain.security.Registration;
import shift.domain.security.UserLogin;
import shift.exception.ShiftIllegalArgumentException;
import shift.security.JwtTokenProvider;
import shift.service.User.UserService;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private AuthenticationManager authenticationManager;
    private JwtTokenProvider tokenProvider;
    private UserService userService;
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
                                    JwtTokenProvider tokenProvider,
                                    UserService userService,
                                    PasswordEncoder bCryptPasswordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/token/generate-token")
    public ResponseEntity login(@RequestBody UserLogin userLogin) throws AuthenticationException {

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLogin.getUsername(),
                        userLogin.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new AuthToken(token));
    }

    @PostMapping("/register")
    public String register(@Valid @RequestBody Registration registration) throws SecurityException {
        String username = registration.getUsername();
        String password = bCryptPasswordEncoder.encode(registration.getPassword());

        try {
            userService.getUserByUsername(registration.getUsername());
            throw new SecurityException("A user already exists with this username.");
        } catch (UsernameNotFoundException ex) {

            registration.setPassword(password);

            Set<Role> roles = registration.getAuthorities().stream()
                    .map(authority -> new Role(authority.name()))
                    .collect(Collectors.toSet());

            userService.createUser(getUser(username, password, roles));
        }

        return "User has successfully been created. username = " + username;
    }

    private User getUser(String username, String password, Set<Role> roles) {
        return new User(username, password, roles);
    }
}
