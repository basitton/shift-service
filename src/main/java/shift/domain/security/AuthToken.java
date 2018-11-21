package shift.domain.security;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Data object returned for token requests
 */
@Getter
@Setter
@Data
public class AuthToken {
    private static final String TOKEN_TYPE = "Bearer";

    public AuthToken(String token, List<String> authorities) {
        this.token = token;
        this.authorities = authorities;
    }

    @NotEmpty
    private String token;

    @NotEmpty
    private String tokenType = TOKEN_TYPE;

    @NotEmpty
    private List<String> authorities;
}
