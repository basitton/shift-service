package shift.domain.h2.Role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

/**
 * Provides entity model for Role data
 */
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    public Role(String name) {
        this.name = name;
    }

    @JsonIgnore
    @Id
    @GeneratedValue
    private Long id;

    @Column
    @NotEmpty
    private String name;
}
