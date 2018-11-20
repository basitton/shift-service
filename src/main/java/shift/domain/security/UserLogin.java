package shift.domain.security;

import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * Data input object for user login when requesting a token
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserLogin {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

}
