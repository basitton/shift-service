package shift.domain.h2.Role;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Role {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;
}
