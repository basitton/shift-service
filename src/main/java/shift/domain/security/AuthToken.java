package shift.domain.security;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class AuthToken {
    private static final String TOKEN_TYPE = "Bearer";

    public AuthToken(String token) {
        this.token = token;
    }

    @NotEmpty
    private String token;

    private String tokenType = TOKEN_TYPE;
}
