package shift.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import shift.domain.h2.Role.Role;
import shift.domain.h2.User.User;
import shift.domain.security.AuthToken;
import shift.domain.security.Registration;
import shift.domain.security.UserLogin;
import shift.security.JwtTokenProvider;
import shift.service.User.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides endpoints for token generation and registering users
 */
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

    /**
     * Gets a generated token for the user logging in.
     * @param userLogin {@link UserLogin} object for signing in
     * @return A JWT token
     * @throws AuthenticationException If the username and/or password is invalid
     */
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

        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new AuthToken(token, authorities));
    }

    /**
     * Registers a user to use the application.
     * @param registration {@link Registration} object for registering the user
     * @return A success message
     * @throws SecurityException when the given username already exists
     */
    @PostMapping("/register")
    public String register(@Valid @RequestBody Registration registration) throws SecurityException {
        String username = registration.getUsername();

        try {
            userService.getUserByUsername(registration.getUsername());
            throw new SecurityException("A user already exists with this username.");
        } catch (UsernameNotFoundException ex) {
            String password = bCryptPasswordEncoder.encode(registration.getPassword());
            registration.setPassword(password);

            Set<Role> roles = registration.getAuthorities().stream()
                    .map(authority -> new Role(authority.name()))
                    .collect(Collectors.toSet());

            userService.createUser(getUser(username, password, roles));
        }

        return "User has successfully been created. username = " + username;
    }

    // encapsulation
    private User getUser(String username, String password, Set<Role> roles) {
        return new User(username, password, roles);
    }
}
