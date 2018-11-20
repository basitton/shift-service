package shift.domain.security;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Data input object for registering a user with the application
 */
@Getter
@Setter
@Data
public class Registration {
    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    @NotNull
    private Set<Authority> authorities;
}
