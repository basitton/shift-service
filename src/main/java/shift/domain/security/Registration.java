package shift.domain.security;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
public class Registration {
    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    @NotNull
    private Set<Authority> authorities;
}
